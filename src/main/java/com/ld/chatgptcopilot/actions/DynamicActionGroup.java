package com.ld.chatgptcopilot.actions;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.dialog.DynamicCommendDialog;
import com.ld.chatgptcopilot.util.MyResourceBundleUtil;
import icons.ChatGPTCopilotIcons;
import org.jetbrains.annotations.NotNull;

public class DynamicActionGroup extends ActionGroup {
    public final List<String> defaultActionsCommend = new ArrayList<>(Arrays
            .asList(MyResourceBundleUtil.getKey("explain"),
                    MyResourceBundleUtil.getKey("translate_to_chinese"),
                    MyResourceBundleUtil.getKey("translate_to_english"),
                    MyResourceBundleUtil.getKey("check_bugs"),
                    MyResourceBundleUtil.getKey("optimize_code")));

    @Override
    public AnAction @NotNull [] getChildren(AnActionEvent event) {
        if (event == null || isNull(event.getProject())) {
            return new AnAction[0];
        }
        List<String> dynamicCommends = event.getProject().getComponent(ChatGPTCopilotChannelManager.class).getDynamicCommends();
        if (dynamicCommends.isEmpty()) {
            dynamicCommends.addAll(defaultActionsCommend);
        }

        List<AnAction> actions = dynamicCommends.stream().map(TranslateAction::new).collect(Collectors.toList());
        actions.add(new AnAction("Edit Commend", "Edit commend", ChatGPTCopilotIcons.pluginIcon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (isNull(e.getProject())) {
                    return;
                }
                new DynamicCommendDialog(e.getProject()).show();
            }
        });
        return actions.toArray(AnAction[]::new);
    }

    static class TranslateAction extends AbsChatGetMenuAction {
        String commend;


        public TranslateAction(String commend) {
            super(ActionProperties.of(commend, ChatGPTCopilotIcons.pluginIcon));
            this.commend = commend;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();
            if (isNull(project)) {
                return;
            }
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (Objects.isNull(editor)) {
                return;
            }
            String selectedText = editor.getSelectionModel().getSelectedText();
            ChatChannel chatChannel = ChatChannel.newChannel();
            Message message1 = new Message("user", commend + ":");
            Message message2 = new Message("user", selectedText);
            chatChannel.getMessages().add(message1);
            chatChannel.getMessages().add(message2);
            askCopilot(project, e.getRequiredData(CommonDataKeys.EDITOR), chatChannel);
        }
    }
}
