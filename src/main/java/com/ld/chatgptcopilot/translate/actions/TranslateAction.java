package com.ld.chatgptcopilot.translate.actions;

import static java.util.Objects.isNull;

import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBDimension;
import com.ld.chatgptcopilot.actions.AbsChatGetMenuAction;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.translate.edge.EdgeTranslator;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class TranslateAction extends AbsChatGetMenuAction {

    private static final ActionProperties properties = ActionProperties.of("Translate", ChatGPTCopilotIcons.pluginIcon);

    public TranslateAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (isNull(project)) {
            return;
        }
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        translateAndShow(editor, selectedText);
    }

    private static void translateAndShow(Editor editor, String selectedText) {
        JPanel content = new JPanel();
        content.add(new JLabel("Translating..."));
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(content, content)
                .setTitle("Translation")
                .setMovable(true)
                .setResizable(true)
                .setShowShadow(true)
                .setRequestFocus(true)
                .setCancelOnWindowDeactivation(false)
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnClickOutside(false)
                .setCancelKeyEnabled(true)
                .setCancelButton(new IconButton("Close", AllIcons.Actions.CloseHovered))
                .createPopup();
        popup.setSize(new JBDimension(400, 300));
        popup.showInBestPositionFor(editor);

        ThreadUtil.execAsync(() -> {
            String translate = new EdgeTranslator().translate(selectedText, "en", "zh-CHS");
            SwingUtilities.invokeLater(() -> {
                content.removeAll();
                JTextArea textArea = new JTextArea(translate);
                textArea.setFont(new JLabel().getFont());
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                JBScrollPane scrollPane = new JBScrollPane(textArea);
                scrollPane.setPreferredSize(new JBDimension(400, 300));
                content.add(scrollPane);
                content.revalidate();
                content.repaint();
            });

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

}
