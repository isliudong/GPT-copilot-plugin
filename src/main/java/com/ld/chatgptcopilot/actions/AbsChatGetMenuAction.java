package com.ld.chatgptcopilot.actions;

import java.awt.*;
import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.ChatGPTCopilotUtil;
import com.ld.chatgptcopilot.util.IdeaUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AbsChatGetMenuAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {
    public AbsChatGetMenuAction(@NotNull ActionProperties actionProperties) {
        super(actionProperties);
    }

    protected static void askCopilot(Project project, Editor editor, ChatChannel chatChannel) {
        String apiToken = ChatGPTCopilotServerManager.getInstance().getAPIToken();
        JBPanel<JBPanel> jbPanelJBPanel = new JBPanel<>(new BorderLayout());
        jbPanelJBPanel.setPreferredSize(new Dimension(400, 300));
        JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();
        loadingPanel.setPreferredSize(new Dimension(300, 500));
        jbPanelJBPanel.add(loadingPanel, BorderLayout.CENTER);
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(jbPanelJBPanel, null)
                .createPopup();
        popup.showInBestPositionFor(editor);

        ThreadUtil.execAsync(() -> {
            ChatGPTCopilotUtil.postToAi(chatChannel, null, apiToken, () -> {
                JComponent markdownComponent = IdeaUtil.getMarkdownComponent(chatChannel.getLastMessageContent()).getComponent();
                //创建一个大小合适的面板来显示回复信息
                SwingUtilities.invokeLater(() -> {
                    JBScrollPane jbScrollPane = new JBScrollPane(markdownComponent);
                    jbPanelJBPanel.removeAll();
                    jbPanelJBPanel.add(jbScrollPane);
                    jbPanelJBPanel.revalidate();
                    jbPanelJBPanel.repaint();
                });
            });
        });
    }
}
