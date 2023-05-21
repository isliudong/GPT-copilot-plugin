package com.ld.chatgptcopilot.translate.actions;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupEvent;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupListener;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.ld.chatgptcopilot.translate.AutoSelectionMode;
import com.ld.chatgptcopilot.translate.edge.EdgeTranslator;
import com.ld.chatgptcopilot.translate.SelectWordUtilCompat;
import com.ld.chatgptcopilot.translate.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectedTranslateAndReplaceAction extends AnAction implements DumbAware {
    private static final String PATTERN_FIX = "^(\\[[\\u4E00-\\u9FBF]+])+ ";
    private final boolean mCheckSelection=true;
    private final SelectWordUtil.CharCondition mWordPartCondition=SelectWordUtilCompat.HANZI_CONDITION;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Get all the required data from data keys
        if (e.getProject() == null) {
            return;
        }
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        Document document = editor.getDocument();

        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        if (start == end) {
            return;
        }

        ThreadUtil.execAsync(()->{
            String text = document.getText().substring(start, end);
            String translate = new EdgeTranslator().translate(text, "zh-CHS", "en");

            final List<LookupElement> replaceLookup = getReplaceLookupElements(Collections.singletonList(translate));
            if (replaceLookup.isEmpty()) {
                return;
            }

            ApplicationManager.getApplication().invokeLater(() -> doReplace(editor, getSelectionRange(e), text, replaceLookup));
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Project project = e.getProject();
        if (isNull(project)) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String selectedText = null;
        if (editor != null) {
            selectedText = editor.getSelectionModel().getSelectedText();
        }
        if (StringUtils.isNotBlank(selectedText)) {
            e.getPresentation().setEnabledAndVisible(true);
            return;
        }
        e.getPresentation().setEnabledAndVisible(false);
    }

    private TextRange getSelectionRange(AnActionEvent e) {
        TextRange selectionRange = null;

        Editor editor = getEditor(e);
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (mCheckSelection && selectionModel.hasSelection()) {
                selectionRange = new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
            } else {
                final ArrayList<TextRange> ranges = new ArrayList<>();
                final int offset = editor.getCaretModel().getOffset();

                final AutoSelectionMode selectionMode = Utils.requireNonNull(AutoSelectionMode.INCLUSIVE,
                        "Method getAutoSelectionMode() can not return null.");
                final boolean exclusiveMode = selectionMode == AutoSelectionMode.EXCLUSIVE;

                SelectWordUtilCompat.addWordOrLexemeSelection(exclusiveMode, editor, offset, ranges, mWordPartCondition);

                if (!ranges.isEmpty()) {
                    if (exclusiveMode) {
                        selectionRange = ranges.get(0);
                    } else {
                        TextRange maxRange = null;
                        for (TextRange range : ranges) {
                            if (maxRange == null || range.contains(maxRange)) {
                                maxRange = range;
                            }
                        }

                        selectionRange = maxRange;
                    }
                }
            }
        }

        return selectionRange;
    }

    @Nullable
    protected static Editor getEditor(AnActionEvent e) {
        return CommonDataKeys.EDITOR.getData(e.getDataContext());
    }


    private static void doReplace(@NotNull final Editor editor,
                                  @NotNull final TextRange selectionRange,
                                  @NotNull final String targetText,
                                  @NotNull final List<LookupElement> replaceLookup) {
        if (editor.isDisposed() || editor.getProject() == null ||
                !targetText.equals(editor.getDocument().getText(selectionRange)) ||
                !selectionRange.containsOffset(editor.getCaretModel().getOffset())) {
            return;
        }

        final SelectionModel selectionModel = editor.getSelectionModel();
        final int startOffset = selectionRange.getStartOffset();
        final int endOffset = selectionRange.getEndOffset();
        if (selectionModel.hasSelection()) {
            if (selectionModel.getSelectionStart() != startOffset || selectionModel.getSelectionEnd() != endOffset) {
                return;
            }
        } else {
            selectionModel.setSelection(startOffset, endOffset);
        }

        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        editor.getCaretModel().moveToOffset(endOffset);

        LookupElement[] items = replaceLookup.toArray(new LookupElement[0]);
        final LookupEx lookup = LookupManager.getInstance(editor.getProject()).showLookup(editor, items);

        if (lookup == null) {
            return;
        }

        final HighlightManager highlightManager = HighlightManager.getInstance(editor.getProject());
        final List<RangeHighlighter> highlighters = addHighlight(highlightManager, editor, selectionRange);

        lookup.addLookupListener(new LookupListener() {
            @Override
            public void itemSelected(@NotNull LookupEvent event) {
                disposeHighlight(highlighters);
            }

            @Override
            public void lookupCanceled(@NotNull LookupEvent event) {
                selectionModel.removeSelection();
                disposeHighlight(highlighters);
            }
        });
    }

    @NotNull
    private static List<RangeHighlighter> addHighlight(@NotNull HighlightManager highlightManager,
                                                       @NotNull Editor editor,
                                                       @NotNull TextRange selectionRange) {
        final ArrayList<RangeHighlighter> highlighters = new ArrayList<RangeHighlighter>();
        highlightManager.addOccurrenceHighlight(editor, selectionRange.getStartOffset(), selectionRange.getEndOffset(),
                TextAttributesKey.createTextAttributesKey("TRANSLATION_HIGHLIGHT"), 0, highlighters);

        for (RangeHighlighter highlighter : highlighters) {
            highlighter.setGreedyToLeft(true);
            highlighter.setGreedyToRight(true);
        }

        return highlighters;
    }

    private static void disposeHighlight(@NotNull List<RangeHighlighter> highlighters) {
        for (RangeHighlighter highlighter : highlighters) {
            highlighter.dispose();
        }
    }

    private static List<LookupElement> getReplaceLookupElements(List<String> explains) {
        if (explains == null || explains.size() == 0)
            return Collections.emptyList();

        final Set<LookupElement> camel = new LinkedHashSet<>();
        final Set<LookupElement> pascal = new LinkedHashSet<>();
        final Set<LookupElement> lowerWithUnder = new LinkedHashSet<>();
        final Set<LookupElement> capsWithUnder = new LinkedHashSet<>();
        final Set<LookupElement> withSpace = new LinkedHashSet<>();

        final StringBuilder camelBuilder = new StringBuilder();
        final StringBuilder pascalBuilder = new StringBuilder();
        final StringBuilder lowerWithUnderBuilder = new StringBuilder();
        final StringBuilder capsWithUnderBuilder = new StringBuilder();
        final StringBuilder withSpaceBuilder = new StringBuilder();

        for (String explain : explains) {
            assert explain != null;
            List<String> words = fixAndSplitForVariable(explain);
            if (words == null || words.isEmpty()) {
                continue;
            }

            camelBuilder.setLength(0);
            pascalBuilder.setLength(0);
            lowerWithUnderBuilder.setLength(0);
            capsWithUnderBuilder.setLength(0);
            withSpaceBuilder.setLength(0);

            build(words, camelBuilder, pascalBuilder, lowerWithUnderBuilder, capsWithUnderBuilder, withSpaceBuilder);

            camel.add(LookupElementBuilder.create(camelBuilder.toString()));
            pascal.add(LookupElementBuilder.create(pascalBuilder.toString()));
            lowerWithUnder.add(LookupElementBuilder.create(lowerWithUnderBuilder.toString()));
            capsWithUnder.add(LookupElementBuilder.create(capsWithUnderBuilder.toString()));
            withSpace.add(LookupElementBuilder.create(withSpaceBuilder.toString()));
        }

        final Set<LookupElement> result = new LinkedHashSet<>();
        result.addAll(camel);
        result.addAll(pascal);
        result.addAll(lowerWithUnder);
        result.addAll(capsWithUnder);
        result.addAll(withSpace);

        return List.copyOf(result);
    }


    private static void build(@NotNull final List<String> words,
                              @NotNull final StringBuilder camel,
                              @NotNull final StringBuilder pascal,
                              @NotNull final StringBuilder lowerWithUnder,
                              @NotNull final StringBuilder capsWithUnder,
                              @NotNull final StringBuilder withSpace) {
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);

            if (i > 0) {
                lowerWithUnder.append('_');
                capsWithUnder.append('_');
                withSpace.append(' ');
            }

            withSpace.append(word);

            if (i == 0) {
                word = sanitizeJavaIdentifierStart(word);
            }

            String capitalized = StringUtil.capitalizeWithJavaBeanConvention(word);
            String lowerCase = word.toLowerCase();

            camel.append(i == 0 ? lowerCase : capitalized);
            pascal.append(capitalized);
            lowerWithUnder.append(lowerCase);
            capsWithUnder.append(word.toUpperCase());
        }
    }

    private static String sanitizeJavaIdentifierStart(@NotNull String name) {
        return Character.isJavaIdentifierStart(name.charAt(0)) ? name : "_" + name;
    }
    @Nullable
    private static List<String> fixAndSplitForVariable(@NotNull String explains) {
        String explain = Utils.splitExplain(explains)[1];
        if (Utils.isEmptyOrBlankString(explain)) {
            return null;
        }

        String fixed = explain.replaceFirst(PATTERN_FIX, "");
        return StringUtil.getWordsIn(fixed);
    }

}
