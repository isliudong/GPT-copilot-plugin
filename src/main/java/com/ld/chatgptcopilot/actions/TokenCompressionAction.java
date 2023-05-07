package com.ld.chatgptcopilot.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotChatPanel;
import com.ld.chatgptcopilot.util.MultilingualUtil;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

public class TokenCompressionAction extends ChatGPTCopilotComponentAction<AiCopilotChatPanel> {

    final ChatChannel chatChannel;
    private static final ActionProperties properties = ActionProperties.of(MultilingualUtil.getKey("token_compression"), ChatGPTCopilotIcons.compression);

    public TokenCompressionAction(AiCopilotChatPanel aiCopilotChatPanel) {
        super(properties);
        this.chatChannel = aiCopilotChatPanel.getChatChannel();
        registerComponent(aiCopilotChatPanel);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        chatChannel.setTokenCompression(!chatChannel.getTokenCompression());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        if (BooleanUtils.isTrue(chatChannel.getTokenCompression())) {
            if (UIUtil.isUnderDarcula()) {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.compressionHover);
            } else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.compression);
            }
        } else {
            if (UIUtil.isUnderDarcula()) {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.compression);
            } else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.compressionHover);
            }
        }
    }
}
