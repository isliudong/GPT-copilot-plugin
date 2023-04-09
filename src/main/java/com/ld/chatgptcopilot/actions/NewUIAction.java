package com.ld.chatgptcopilot.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.panel.AiCopilotChatPanel;
import com.ld.chatgptcopilot.util.MyResourceBundleUtil;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

public class NewUIAction extends ChatGPTCopilotComponentAction<AiCopilotChatPanel> implements DumbAware {

    private static final ActionProperties properties = ActionProperties.of("New UI", ChatGPTCopilotIcons.format);

    public NewUIAction(AiCopilotChatPanel aiCopilotChatPanel) {
        super(properties);
        registerComponent(aiCopilotChatPanel);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        ChatGPTCopilotChannelManager.State state = project.getComponent(ChatGPTCopilotChannelManager.class).getState();
        state.setNewUI(!BooleanUtils.isTrue(state.getNewUI()));
        getComponent().getAiCopilotDetailsPanel().loadFirst(state.getNewUI());

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Project project = e.getProject();
        if (project == null) return;
        ChatGPTCopilotChannelManager.State state = project.getComponent(ChatGPTCopilotChannelManager.class).getState();

        if (BooleanUtils.isTrue(state.getNewUI())) {
            if (UIUtil.isUnderDarcula()){
                e.getPresentation().setIcon(ChatGPTCopilotIcons.format_hover);
            }else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.format);
            }
            e.getPresentation().setText(MyResourceBundleUtil.getKey("Turn_Off_New_UI"));
        } else {
            if (UIUtil.isUnderDarcula()){
                e.getPresentation().setIcon(ChatGPTCopilotIcons.format);
            }else {
                e.getPresentation().setIcon(ChatGPTCopilotIcons.format_hover);
            }
            e.getPresentation().setText(MyResourceBundleUtil.getKey("Enable_New_UI"));
        }
    }

    //版本不同，ActionUpdateThread 可能不存在
    /*@Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }*/
}
