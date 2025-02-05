package com.ld.chatgptcopilot.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.util.MultilingualUtil;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

public class ChannelContinuousAction extends AnAction implements DumbAware {

    final ChatChannel chatChannel;
    private static final ActionProperties properties = ActionProperties.of(MultilingualUtil.getKey("continuous_conversation"), ChatGPTCopilotIcons.muti_comment);

    public ChannelContinuousAction(ChatChannel chatChannel) {
        super(properties.getText(), properties.getDescription(), properties.getIcon());
        this.chatChannel = chatChannel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        chatChannel.setContinuousFlag(!BooleanUtils.isTrue(chatChannel.getContinuousFlag()));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        if (BooleanUtils.isTrue(chatChannel.getContinuousFlag())) {
            if (UIUtil.isUnderDarcula()) {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.muti_comment_hover);
            } else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.muti_comment);
            }
            e.getPresentation().setText(MultilingualUtil.getKey("continuous_conversation"));
        } else {
            if (UIUtil.isUnderDarcula()) {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.muti_comment);
            } else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.muti_comment_hover);
            }
            e.getPresentation().setText(MultilingualUtil.getKey("continuous_conversation"));
        }
    }

    //版本不同，ActionUpdateThread 可能不存在
    /*@Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }*/
}
