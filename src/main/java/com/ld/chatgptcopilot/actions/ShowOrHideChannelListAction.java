package com.ld.chatgptcopilot.actions;

import static java.util.Objects.isNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import org.jetbrains.annotations.NotNull;

public class ShowOrHideChannelListAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {

    private static final ActionProperties properties = ActionProperties.of("hide Channels", AllIcons.General.TbHidden);

    public ShowOrHideChannelListAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (isNull(project)) {
            return;
        }
        AiCopilotPanel copilotPanel = getComponent();
        copilotPanel.showOrHideChannelList();
    }
}
