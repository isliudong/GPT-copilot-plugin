package com.ld.chatgptcopilot.actions;

import static java.util.Objects.isNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.ui.dialog.ConfigureChatGptDialog;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import org.jetbrains.annotations.NotNull;

public class ConfigureChatGptAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {

    private static final ActionProperties properties = ActionProperties.of("Configure Copilot...",  AllIcons.General.Settings);

    public ConfigureChatGptAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        ConfigureChatGptDialog dlg = new ConfigureChatGptDialog(project);
        dlg.show();
    }




}
