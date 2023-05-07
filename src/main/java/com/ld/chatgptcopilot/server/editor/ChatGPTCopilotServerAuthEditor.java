package com.ld.chatgptcopilot.server.editor;


import static com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil.MARGIN_BOTTOM;

import java.awt.*;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.textCompletion.TextFieldWithCompletion;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.actions.coffee.SupportView;
import com.ld.chatgptcopilot.model.ChatGPTCopilotServer;
import com.ld.chatgptcopilot.tasks.TestChatGPTCopilotServerConnectionTask;
import org.jetbrains.annotations.NotNull;

public abstract class ChatGPTCopilotServerAuthEditor {

    protected final static int DEFAULT_WIDTH = 450;
    protected final static int DEFAULT_HEIGHT = 24;

    protected final Project project;
    protected final ChatGPTCopilotServer server;
    protected final boolean selectedServer;

    protected final BiConsumer<ChatGPTCopilotServer, Boolean> changeListener;
    protected final Consumer<ChatGPTCopilotServer> changeUrlListener;

    protected JLabel nameLabel;
    protected JTextField nameField;


    protected JCheckBox defaultServerCheckbox;

    protected JPanel testPanel;
    protected JButton testButton;

    public ChatGPTCopilotServerAuthEditor(Project project, ChatGPTCopilotServer server, boolean selected, BiConsumer<ChatGPTCopilotServer, Boolean> changeListener, Consumer<ChatGPTCopilotServer> changeUrlListener) {
        this.project = project;
        this.server = server;
        this.selectedServer = selected;
        this.changeListener = changeListener;
        this.changeUrlListener = changeUrlListener;
        init();
    }

    public abstract JPanel getPanel();

    public void installListeners() {
        installListener(nameField);
        installListener(defaultServerCheckbox);
        installListener(testButton);
    }

    private void init() {
        this.nameLabel = new JBLabel("Copilot name:", 4);
        this.nameField = new JBTextField();
        this.nameField.setText(server.getName());
        this.nameField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));


        this.defaultServerCheckbox = new JCheckBox();
        this.defaultServerCheckbox.setBorder(JBUI.Borders.emptyRight(4));
        this.defaultServerCheckbox.setSelected(selectedServer);

        this.testPanel = new JPanel(new BorderLayout());
        testPanel.setBorder(MARGIN_BOTTOM);
        JButton support = new JButton("Support");
        support.addActionListener((event) -> SwingUtilities.invokeLater(() -> {
            SupportView supportView = new SupportView();
            supportView.show();
        }));
        this.testButton = new JButton("Test");
        this.testPanel.add(support, BorderLayout.WEST);
        this.testPanel.add(testButton, BorderLayout.EAST);
    }


    protected void installListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> apply());
            }
        });
    }

    protected void installListener(TextFieldWithCompletion textField) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
                DocumentListener.super.documentChanged(event);
                ApplicationManager.getApplication().invokeLater(() -> apply());
            }
        });
    }

    private void installListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> defaultServerChanged());
    }

    private void installListener(JButton button) {
        button.addActionListener((event) -> SwingUtilities.invokeLater(() -> {
            TestChatGPTCopilotServerConnectionTask task = new TestChatGPTCopilotServerConnectionTask(project, server);
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

    protected void apply() {
        this.changeUrlListener.accept(server);
    }

    private void defaultServerChanged() {
        this.changeListener.accept(server, defaultServerCheckbox.isSelected());
    }

}
