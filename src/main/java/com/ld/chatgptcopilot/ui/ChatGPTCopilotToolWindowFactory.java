package com.ld.chatgptcopilot.ui;


import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.ui.dialog.ConfigureChatGptDialog;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import org.jetbrains.annotations.NotNull;

public class ChatGPTCopilotToolWindowFactory implements ToolWindowFactory, DumbAware {

    public static final String TOOL_WINDOW_ID = "ChatGPT Copilot";
    public static final String TAB_AI_COPILOT = "Chat";
    public ToolWindow toolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        createContent(project);
        toolWindow.setType(ToolWindowType.DOCKED, null);
        ChatGPTCopilotServerManager.getInstance().addConfigurationServerChangedListener(() -> {
            SwingUtilities.invokeLater(() -> createContent(project));
        });
    }

    private void createContent(Project project) {
        if (ChatGPTCopilotServerManager.getInstance().getAPIToken() == null) {
            createNewConfigContent(project);
            return;
        }


        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        ContentFactory factory = contentManager.getFactory();

        AiCopilotPanel aiCopilotPanel = new AiCopilotPanel(project);
        Content aiContent = factory.createContent(aiCopilotPanel, TAB_AI_COPILOT, false);
        contentManager.addDataProvider(aiCopilotPanel);
        contentManager.addContent(aiContent);

        //放在编辑器左侧
        toolWindow.setAnchor(ToolWindowAnchor.LEFT, null);
    }

    private void createNewConfigContent(Project project) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        ContentFactory factory = contentManager.getFactory();
        JBPanel holderPanel = ChatGPTCopilotPanelUtil.createPlaceHolderPanel("Please add Open Ai API token");
        //添加刷新按钮
        JButton button = new JButton(AllIcons.General.Add);
        button.addActionListener(event -> new ConfigureChatGptDialog(project).show());

        holderPanel.add(button);
        contentManager.addContent(factory.createContent(holderPanel, "Config", false));
    }
}
