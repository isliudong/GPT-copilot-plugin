package com.ld.chatgptcopilot.translate;

import java.util.List;

import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public final class SelectWordUtilCompat {

    private SelectWordUtilCompat() {
    }

    public static final SelectWordUtil.CharCondition DEFAULT_CONDITION = SelectWordUtil.JAVA_IDENTIFIER_PART_CONDITION;

    public static final SelectWordUtil.CharCondition HANZI_CONDITION = ch -> ch >= '\u4E00' && ch <= '\u9FBF';

    public static void addWordOrLexemeSelection(boolean camel,
                                                @NotNull Editor editor,
                                                int cursorOffset,
                                                @NotNull List<TextRange> ranges,
                                                @NotNull SelectWordUtil.CharCondition isWordPartCondition) {
        if (IdeaCompat.BUILD_NUMBER >= IdeaCompat.Version.IDEA2016_2) {
            SelectWordUtil.addWordOrLexemeSelection(camel, editor, cursorOffset, ranges, isWordPartCondition);
        } else {
            CharSequence editorText = editor.getDocument().getImmutableCharSequence();
            SelectWordUtil.addWordSelection(camel, editorText, cursorOffset, ranges, isWordPartCondition);
        }

    }

}
