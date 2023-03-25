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
    private Project project;
    private ChatChannel chatChannel;
    MessageListPanel messageListPanel;

    JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


    public AiCopilotChatPanel(ChatChannel chatChannel, Project project, AiCopilotDetailsPanel aiCopilotDetailsPanel) {
        this.chatChannel = chatChannel;
        this.project = project;
        this.aiCopilotDetailsPanel = aiCopilotDetailsPanel;
        messageListPanel = new MessageListPanel(chatChannel.getMessages(), this);
        setContent();
    }

    private void setContent() {
        setLayout(new BorderLayout());
        add(messageListPanel, BorderLayout.CENTER);
        //内容空白区域弹性填充
        //add(Box.createVerticalGlue(), BorderLayout.CENTER);
    }


    public void loading() {
        messageListPanel.add(loadingPanel);
    }

    public void loadingEnd() {
        messageListPanel.remove(loadingPanel);
    }


}
