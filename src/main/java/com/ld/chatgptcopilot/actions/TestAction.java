package com.ld.chatgptcopilot.actions;

import static java.util.Objects.isNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import org.jetbrains.annotations.NotNull;

/**
 * 123(123,"123")
 */
public class TestAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {

    private static final ActionProperties properties = ActionProperties.of("Test", AllIcons.Actions.Download);
    public static TestAction instance = new TestAction();


    public TestAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (isNull(project)) {
            return;
        }
        //获取当前编辑器
        FileEditor editor = FileEditorManager.getInstance(project).getSelectedEditor();
        if (editor == null) {
            return;
        }
        //获取当前编辑器光标位置
    }
}
