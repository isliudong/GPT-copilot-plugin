package com.ld.chatgptcopilot.actions;

import java.util.ArrayList;
import java.util.Optional;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import org.jetbrains.annotations.NotNull;

public class AddChatChannelAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {
    private static final ActionProperties properties = ActionProperties.of("Add Channel", AllIcons.General.Add);

    public AddChatChannelAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional.ofNullable(getComponent())
                .ifPresent(component -> {
                    ChatChannel chatChannel = new ChatChannel();
                    chatChannel.setMessages(new ArrayList<>());
                    component.add(chatChannel);
                });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        AiCopilotPanel component = getComponent();
        e.getPresentation().setEnabled(component.isShowList);
    }
}
