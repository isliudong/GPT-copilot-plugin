package com.ld.chatgptcopilot.commen;

import javax.swing.*;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public abstract class ChatGPTCopilotComponentAction<T extends JComponent> extends AnAction implements DumbAware {

    private final ActionProperties actionProperties;
    private T component;

    public ChatGPTCopilotComponentAction(@NotNull ActionProperties actionProperties) {
        super(actionProperties.getText(), actionProperties.getDescription(), actionProperties.getIcon());
        this.actionProperties = actionProperties;
    }

    public void registerComponent(T component){
        this.component = component;
        registerCustomShortcutSet(actionProperties.getShortcut(), component);
    }

    public T getComponent() {
        return component;
    }
}
