package com.ld.chatgptcopilot.actions;

import static java.util.Objects.isNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import icons.ChatGPTCopilotIcons;
import org.jetbrains.annotations.NotNull;

public class AskChatGPTAction extends AbsChatGetMenuAction {

    private static final ActionProperties properties = ActionProperties.of("Ask Copilot", ChatGPTCopilotIcons.pluginIcon);

    public AskChatGPTAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        ChatChannel chatChannel = ChatChannel.newChannel();
        Message message1 = Message.builder().role("user").content("解释这段文本内容：").build();
        Message message2 = Message.builder().role("user").content(selectedText).build();
        chatChannel.getMessages().add(message1);
        chatChannel.getMessages().add(message2);
        askCopilot(project, editor, chatChannel);

    }


}
