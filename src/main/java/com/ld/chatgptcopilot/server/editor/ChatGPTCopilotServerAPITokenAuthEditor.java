package com.ld.chatgptcopilot.server.editor;

import static com.intellij.openapi.util.text.StringUtil.trim;
import static java.lang.String.valueOf;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.textCompletion.TextCompletionProvider;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.model.ChatGPTCopilotServer;
import com.ld.chatgptcopilot.tasks.TestChatGPTCopilotServerConnectionTask;

public class ChatGPTCopilotServerAPITokenAuthEditor extends ChatGPTCopilotServerAuthEditor {

    private JLabel apiKeyLabel;
    private JPasswordField apiTokenField;
    private TextFieldWithCompletion modelField;

    public ChatGPTCopilotServerAPITokenAuthEditor(Project project, ChatGPTCopilotServer server, boolean selected, BiConsumer<ChatGPTCopilotServer, Boolean> changeListener, Consumer<ChatGPTCopilotServer> changeUrlListener) {
        super(project, server, selected, changeListener, changeUrlListener);
    }

    @Override
    public JPanel getPanel() {
        this.apiKeyLabel = new JBLabel("API Key:", SwingConstants.RIGHT);
        this.apiTokenField = new JPasswordField();
        this.apiTokenField.setText(server.getApiToken());
        this.apiTokenField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        //model选择下拉框
        TextCompletionProvider provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(Arrays.asList("gpt-3.5-turbo", "gpt-4"), AllIcons.Debugger.AddToWatch);
        modelField = new TextFieldWithCompletion(project, provider, server.getModel(), true, true, true, true);
        modelField.setBackground(UIUtil.getTextFieldBackground());
        installListeners();

        return FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addLabeledComponent(this.nameLabel, this.nameField)
                .addLabeledComponent(this.apiKeyLabel, this.apiTokenField)
                .addLabeledComponent(new JBLabel("Model:"), modelField)
                .addLabeledComponent(new JBLabel("Active:"), this.defaultServerCheckbox)
                .addComponent(this.testPanel)
                .getPanel();
    }

    @Override
    public void installListeners() {
        super.installListeners();
        installListener(apiTokenField);
        installListener(modelField);
    }

    @Override
    protected void apply() {
        String name = trim(nameField.getText());
        String apiToken = trim(valueOf(apiTokenField.getPassword()));
        String model = modelField.getText();
        this.server.withApiToken(name, apiToken, model);
        super.apply();
    }

    public void installListener(JButton button) {
        button.addActionListener((event) -> SwingUtilities.invokeLater(() -> {
            TestChatGPTCopilotServerConnectionTask task = new TestChatGPTCopilotServerConnectionTask(project, server, modelField);
            ProgressManager.getInstance().run(task);
            Exception e = task.getException();
            if (e == null) {
                Messages.showMessageDialog(project, "Connection is successful", "Connection", Messages.getInformationIcon());
            } else if (!(e instanceof ProcessCanceledException)) {
                String message = e.getMessage();
                if (e instanceof UnknownHostException) {
                    message = "Unknown host: " + message;
                }
                if (message == null) {
                    message = "Unknown error";
                }
                Messages.showErrorDialog(project, StringUtil.capitalize(message), "Error");
            }
        }));
    }


}
