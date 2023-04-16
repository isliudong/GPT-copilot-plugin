package com.ld.chatgptcopilot.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotChatPanel;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

public class ClearChannelAction extends ChatGPTCopilotComponentAction<AiCopilotChatPanel> {

    final ChatChannel chatChannel;
    private static final ActionProperties properties = ActionProperties.of("Clear", ChatGPTCopilotIcons.cleanDark);

    public ClearChannelAction(AiCopilotChatPanel aiCopilotChatPanel) {
        super(properties);
        this.chatChannel = aiCopilotChatPanel.getChatChannel();
        registerComponent(aiCopilotChatPanel);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        chatChannel.getMessages().clear();
        getComponent().setContent();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(CollectionUtils.isNotEmpty(chatChannel.getMessages()));
        if (UIUtil.isUnderDarcula()) {
            e.getPresentation().setIcon(ChatGPTCopilotIcons.cleanDark);
        } else {
            e.getPresentation().setIcon(ChatGPTCopilotIcons.clean);
        }

    }
}
