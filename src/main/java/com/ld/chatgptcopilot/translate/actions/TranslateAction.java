package com.ld.chatgptcopilot.translate.actions;

import static java.util.Objects.isNull;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
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
        PopupPanel popupPanel = new PopupPanel(new BorderLayout());
        JLabel loading = new JLabel("Translating");
        loading.setPreferredSize(new JBDimension(80, 30));
        AtomicBoolean loadingFlag = new AtomicBoolean(true);
        //半秒钟增加一个点，到达三个点后，清空，重新开始
        ThreadUtil.execAsync(() -> {
            int i = 0;
            while (loadingFlag.get()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i >3) {
                    i = 0;
                    SwingUtilities.invokeLater(() -> {
                        loading.setText("Translating");
                        popupPanel.revalidate();
                        loading.updateUI();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        loading.setText(loading.getText() + ".");
                        popupPanel.revalidate();
                        loading.updateUI();
                    });
                }
            }
        });
        popupPanel.add(loading);
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(popupPanel, popupPanel)
                .setMovable(true)
                .setResizable(true)
                .setShowShadow(true)
                .setRequestFocus(true)
                .setCancelOnWindowDeactivation(false)
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnClickOutside(true)
                .setCancelKeyEnabled(true)
                .createPopup();
        popup.showInBestPositionFor(editor);

        ThreadUtil.execAsync(() -> {
            String translate = new EdgeTranslator().translate(selectedText, "en", "zh-CHS");
            SwingUtilities.invokeLater(() -> {
                popupPanel.removeAll();
                loadingFlag.set(false);
                JTextArea textArea = new JTextArea(translate);
                textArea.setFont(new JLabel().getFont());
                textArea.setLineWrap(true);
                textArea.setEditable(false);
                JBScrollPane scrollPane = new JBScrollPane(textArea);
                popupPanel.add(scrollPane);
                popupPanel.revalidate();
                popupPanel.repaint();
                popup.setSize(new JBDimension(200, 150));
                popup.getContent().revalidate();
                popup.getContent().repaint();
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
