package com.ld.chatgptcopilot.server.editor;

import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.lang.String.valueOf;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.server.ChatGPTCopilotServer;

public class ChatGPTCopilotServerAPITokenAuthEditor extends ChatGPTCopilotServerAuthEditor {

    private JLabel apiTokenLabel;
    private JPasswordField apiTokenField;

    public ChatGPTCopilotServerAPITokenAuthEditor(Project project, ChatGPTCopilotServer server, boolean selected, BiConsumer<ChatGPTCopilotServer, Boolean> changeListener, Consumer<ChatGPTCopilotServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {
        this.apiTokenLabel = new JBLabel("API Token:", 4);
        this.apiTokenField = new JPasswordField();
        this.apiTokenField.setText(server.getApiToken());
        this.apiTokenField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.nameLabel, this.nameField)
                .addLabeledComponent(this.apiTokenLabel, this.apiTokenField)
                .addComponent(this.defaultServerCheckbox)
                .addComponent(this.testPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(apiTokenField);
    }

    @Override
    protected void apply() {
        String name = trim(nameField.getText());
        String apiToken = trim(valueOf(apiTokenField.getPassword()));
        this.server.withApiToken(name, apiToken);
        super.apply();
    }

}
