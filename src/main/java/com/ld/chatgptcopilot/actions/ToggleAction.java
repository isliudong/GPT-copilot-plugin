package com.ld.chatgptcopilot.actions;

import javax.swing.*;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import org.jetbrains.annotations.NotNull;

public class ToggleAction<T extends JComponent> extends ChatGPTCopilotComponentAction<T> {
    private final Runnable selectedAction;
    private final Runnable unSelectedAction;
    private volatile boolean selected;
    private final Icon selectedIcon;
    private final Icon unSelectedIcon;

    public ToggleAction(String text,
                        String description,
                        boolean defaultSelected,
                        Icon selectedIcon,
                        Icon unSelectedIcon,
                        Runnable selectedAction,
                        Runnable unSelectedAction) {
        super(ActionProperties.of(text, description, defaultSelected ? selectedIcon : unSelectedIcon));
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
        this.selectedAction = selectedAction;
        this.unSelectedAction = unSelectedAction;
        this.selected = defaultSelected;
    }

    public boolean isSelected() {
        return selected;
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
    }

    public synchronized void toggle() {
        setSelected(!isSelected());
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        toggle();
        if (isSelected()) {
            if (selectedAction != null) {
                selectedAction.run();
            }
        } else {
            if (unSelectedAction != null) {
                unSelectedAction.run();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        if (isSelected()) {
            e.getPresentation().setIcon(selectedIcon);
        } else {
            e.getPresentation().setIcon(unSelectedIcon);
        }
    }
}
