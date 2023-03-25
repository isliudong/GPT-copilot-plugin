package com.ld.chatgptcopilot.ui.panel;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.ui.components.JBPanel;
import com.ld.chatgptcopilot.model.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageListPanel extends JBPanel {

    private final List<Message> messageList;
    private final AiCopilotChatPanel aiCopilotChatPanel;
    private final List<MessageItemPanel> messageItemList = new ArrayList<>();

    public MessageListPanel(List<Message> messageList, AiCopilotChatPanel aiCopilotChatPanel) {
        super(new BorderLayout());
        this.messageList = messageList;
        this.aiCopilotChatPanel = aiCopilotChatPanel;
        setContent();
    }

    private void setContent() {
        //消息列表
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        messageList.forEach(message -> {
            MessageItemPanel item = new MessageItemPanel(message, this);
            messageItemList.add(item);
            add(item);
        });
    }

    public void addMessage(MessageItemPanel message) {
        messageItemList.add(message);
        messageList.add(message.getMessage());
        add(message);
    }

    public void removeMessage(MessageItemPanel messageItem) {
        messageList.remove(messageItem.getMessage());
        messageItemList.removeIf(item -> item.getMessage().equals(messageItem.getMessage()));
        remove(messageItem);
    }
}
