package com.ld.chatgptcopilot.commen;

import javax.swing.*;

import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class ChatGPTCopilotComponentActionGroup<T extends JComponent> extends DefaultActionGroup {

    private final T parent;

    public ChatGPTCopilotComponentActionGroup(T component) {
        super();
        this.parent = component;
    }

    public void add(ChatGPTCopilotComponentAction<T> action) {
        action.registerComponent(parent);
        super.add(action);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
