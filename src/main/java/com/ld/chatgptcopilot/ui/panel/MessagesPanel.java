package com.ld.chatgptcopilot.ui.panel;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.ui.components.JBPanel;
import com.ld.chatgptcopilot.model.ChatChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessagesPanel extends JBPanel {

    private final List<ChatChannel.Message> messageList;
    private final AiCopilotChatPanel aiCopilotChatPanel;
    private final List<AiCopilotChatPanel.MessageItem> messageItemList=new ArrayList<>();

    public MessagesPanel(List<ChatChannel.Message> messageList, AiCopilotChatPanel aiCopilotChatPanel) {
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
            AiCopilotChatPanel.MessageItem item = new AiCopilotChatPanel.MessageItem(message,this);
            messageItemList.add(item);
            add(item);
        });
    }

    public void addMessage(AiCopilotChatPanel.MessageItem message){
        messageItemList.add(message);
        messageList.add(message.getMessage());
        add(message);
    }

    public void removeMessage(AiCopilotChatPanel.MessageItem messageItem) {
        messageList.remove(messageItem.getMessage());
        messageItemList.removeIf(item -> item.getMessage().equals(messageItem.getMessage()));
        remove(messageItem);
    }
}
