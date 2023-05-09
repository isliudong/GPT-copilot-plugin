package com.ld.chatgptcopilot.ui.panel;

import java.awt.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import lombok.Getter;

public class AiCopilotChatPanel extends JBPanel {
    @Getter
    private AiCopilotDetailsPanel aiCopilotDetailsPanel;
    private boolean newUI;
    private Project project;
    @Getter
    private ChatChannel chatChannel;
    @Getter
    public AbstractChatDisplayPanel messageListPanel;

    JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


    public AiCopilotChatPanel(ChatChannel chatChannel, Project project, AiCopilotDetailsPanel aiCopilotDetailsPanel, boolean newUI) {
        this.chatChannel = chatChannel;
        this.project = project;
        this.aiCopilotDetailsPanel = aiCopilotDetailsPanel;
        this.newUI = newUI;
        setContent();
    }

    public void refreshContent(boolean newUI) {
        if (this.newUI == newUI) {
            return;
        }
        this.newUI = newUI;
        setContent();
    }

    public void setContent() {
        removeAll();
        setLayout(new BorderLayout());
        messageListPanel = newUI ? new HtmlMessageListDisplayPanel(project, chatChannel, this) : new MessageListDisplayPanel(project, chatChannel, this);
        add(messageListPanel, BorderLayout.CENTER);
        updateUI();
    }


    public void loading() {
        messageListPanel.add(loadingPanel);
    }

    public void loadingEnd() {
        messageListPanel.remove(loadingPanel);
    }


}
