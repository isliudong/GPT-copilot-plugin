package com.ld.chatgptcopilot.ui.panel;

import java.awt.*;
import java.util.List;

import com.intellij.ui.components.JBPanel;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import lombok.Getter;

@Getter
public abstract class AbstractChatDisplayPanel extends JBPanel {

    public final List<Message> messageList;
    public final ChatChannel chatChannel;
    public final AiCopilotChatPanel aiCopilotChatPanel;


    public AbstractChatDisplayPanel(ChatChannel chatChannel, AiCopilotChatPanel aiCopilotChatPanel) {
        super(new BorderLayout());
        this.messageList = chatChannel.getMessages();
        this.chatChannel = chatChannel;
        this.aiCopilotChatPanel = aiCopilotChatPanel;
    }

    public void addMessage(Message message){}

    public void loading(Message message) {
    }

    public void removeLoading() {
    }

    public void removeMessage(Message messageItem) {
        messageList.remove(messageItem);
    }

    public void appendMessage(Message message) {

    }

    public void appendContent(Message message, boolean newContent) {
    }
}
