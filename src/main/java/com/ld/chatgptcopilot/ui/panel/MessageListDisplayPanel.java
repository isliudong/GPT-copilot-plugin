package com.ld.chatgptcopilot.ui.panel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;

public class MessageListDisplayPanel extends AbstractChatDisplayPanel {

    private final List<MessageItemPanel> messageItemList = new ArrayList<>();
    private MessageItemPanel loadingMessageItem;

    public MessageListDisplayPanel(Project project, ChatChannel chatChannel, AiCopilotChatPanel aiCopilotChatPanel) {
        super(project, chatChannel, aiCopilotChatPanel);
        setContent();
    }

    public void setContent() {
        //消息列表
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        messageList.forEach(message -> {
            MessageItemPanel item = new MessageItemPanel(message, this);
            messageItemList.add(item);
            add(item);
        });
    }

    @Override
    public void appendContent(Message message, boolean newContent) {
        messageItemList.stream().filter(item -> item.getMessage().equals(message)).findAny().ifPresent(MessageItemPanel::appendContent);
    }

    @Override
    public void addMessage(Message message) {
        messageList.add(message);
        MessageItemPanel itemPanel = new MessageItemPanel(message, this);
        messageItemList.add(itemPanel);
        add(itemPanel);
    }

    @Override
    public void loading(Message message) {
        messageItemList.stream().filter(item -> item.getMessage().equals(message)).findAny().ifPresent(item -> {
            item.loading();
            loadingMessageItem = item;
        });
    }

    @Override
    public void removeLoading() {
        if (loadingMessageItem != null) {
            loadingMessageItem.removeLoading();
            loadingMessageItem = null;
        }
    }

    @Override
    public void removeMessage(Message message) {
        messageList.remove(message);
        messageItemList.stream().filter(item -> item.getMessage().equals(message)).findAny().ifPresent(item -> {
            messageItemList.remove(item);
            remove(item);
        });
    }
}
