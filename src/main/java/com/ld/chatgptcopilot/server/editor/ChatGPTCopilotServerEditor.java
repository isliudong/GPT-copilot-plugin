package com.ld.chatgptcopilot.server.editor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import com.ld.chatgptcopilot.server.ChatGPTCopilotServer;
import com.ld.chatgptcopilot.ui.ChatGPTCopilotTabbedPane;

public class ChatGPTCopilotServerEditor {

    private final Project project;
    private final ChatGPTCopilotServer server;
    private final boolean selectedServer;

    private final BiConsumer<ChatGPTCopilotServer, Boolean> changeListener;
    private final Consumer<ChatGPTCopilotServer> changeUrlListener;

    private ChatGPTCopilotTabbedPane ChatGPTCopilotTabbedPane;

    public ChatGPTCopilotServerEditor(Project project, ChatGPTCopilotServer server, boolean selected, BiConsumer<ChatGPTCopilotServer, Boolean> changeListener, Consumer<ChatGPTCopilotServer> changeUrlListener) {
        this.project = project;
        this.server = server;
        this.selectedServer = selected;
        this.changeListener = changeListener;
        this.changeUrlListener = changeUrlListener;
    }

    public JPanel getPanel() {
        ChatGPTCopilotServerAuthEditor userAndPassAuthEditor = new ChatGPTCopilotServerUserAndPassAuthEditor(project, server, selectedServer, changeListener, changeUrlListener);
        ChatGPTCopilotServerAuthEditor apiTokenAuthEditor = new ChatGPTCopilotServerAPITokenAuthEditor(project, server, selectedServer, changeListener, changeUrlListener);

        this.ChatGPTCopilotTabbedPane = new ChatGPTCopilotTabbedPane(JTabbedPane.NORTH);
        //this.ChatGPTCopilotTabbedPane.addTab("User And Pass", userAndPassAuthEditor.getPanel());
        this.ChatGPTCopilotTabbedPane.addTab("API Token", apiTokenAuthEditor.getPanel());

        //暂不支持用户名密码登录
        //if (AuthType.API_TOKEN == server.getType()) {
        //    this.ChatGPTCopilotTabbedPane.setSelectedIndex(1);
        //}

        return FormBuilder.createFormBuilder()
                .addComponent(this.ChatGPTCopilotTabbedPane)
                .getPanel();
    }

}
