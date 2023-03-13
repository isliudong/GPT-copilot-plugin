package com.ld.chatgptcopilot.commen;

import javax.swing.*;

import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class ChatGPTCopilotComponentActionGroup extends DefaultActionGroup {

    private final JComponent parent;

    public ChatGPTCopilotComponentActionGroup(JComponent component) {
        super();
        this.parent = component;
    }

    public void add(ChatGPTCopilotComponentAction action) {
        action.registerComponent(parent);
        super.add(action);
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
