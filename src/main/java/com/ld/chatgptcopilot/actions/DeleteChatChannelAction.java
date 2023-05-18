package com.ld.chatgptcopilot.actions;

import java.util.Optional;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import org.jetbrains.annotations.NotNull;

public class DeleteChatChannelAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {
    private static final ActionProperties properties = ActionProperties.of("Delete Channel", AllIcons.General.Remove);

    public DeleteChatChannelAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional.ofNullable(getComponent())
                .ifPresent(AiCopilotPanel::deleteSelectedChannel);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        AiCopilotPanel component = getComponent();
        e.getPresentation().setEnabled(component.isShowList);
    }
}
