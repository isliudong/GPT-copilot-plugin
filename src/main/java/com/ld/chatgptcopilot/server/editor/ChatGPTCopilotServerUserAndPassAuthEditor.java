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
import com.ld.chatgptcopilot.model.ChatGPTCopilotServer;

public class ChatGPTCopilotServerUserAndPassAuthEditor extends ChatGPTCopilotServerAuthEditor {

    private JLabel usernameLabel;
    private JTextField usernameField;

    private JLabel passwordLabel;
    private JPasswordField passwordField;

    public ChatGPTCopilotServerUserAndPassAuthEditor(Project project, ChatGPTCopilotServer server, boolean selected, BiConsumer<ChatGPTCopilotServer, Boolean> changeListener, Consumer<ChatGPTCopilotServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {

        this.usernameLabel = new JBLabel("Username:", 4);
        this.usernameField = new JBTextField();
        this.usernameField.setText(server.getUsername());
        this.usernameField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        this.passwordLabel = new JBLabel("Password:", 4);
        this.passwordField = new JPasswordField();
        this.passwordField.setText(server.getPassword());
        this.passwordField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.nameLabel, this.nameField)
                .addLabeledComponent(this.usernameLabel, this.usernameField)
                .addLabeledComponent(this.passwordLabel, this.passwordField)
                .addComponent(this.defaultServerCheckbox)
                .addComponentToRightColumn(this.testPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(usernameField);
        installListener(passwordField);
    }

    @Override
    protected void apply() {
        String url = trim(nameField.getText());
        String username = trim(usernameField.getText());
        String password = trim(valueOf(passwordField.getPassword()));

        this.server.withUserAndPass(url, username, password);

        super.apply();
    }


}
