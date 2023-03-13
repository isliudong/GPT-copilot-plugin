package com.ld.chatgptcopilot.commen;

import static java.util.Objects.isNull;

import javax.swing.*;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import org.jetbrains.annotations.Nullable;

public class ActionProperties {

    private final String text;
    private final String description;
    private final Icon icon;
    private final CustomShortcutSet shortcut;

    public static ActionProperties of(String text){
        return new ActionProperties(text, null, null, null);
    }

    public static ActionProperties of(String text, Icon icon){
        return new ActionProperties(text, null, icon, null);
    }

    public static ActionProperties of(String text, String description, Icon icon){
        return new ActionProperties(text, description, icon, null);
    }

    public static ActionProperties of(String text, Icon icon, String shortcut){
        return new ActionProperties(text, null, icon, shortcut);
    }

    private ActionProperties(@Nullable String text, @Nullable String description, @Nullable Icon icon, @Nullable String shortcut) {
        this.text = text;
        this.description = description;
        this.icon = icon;
        this.shortcut = isNull(shortcut) ? CustomShortcutSet.EMPTY : CustomShortcutSet.fromString(shortcut);
    }


    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public Icon getIcon() {
        return icon;
    }

    public CustomShortcutSet getShortcut() {
        return shortcut;
    }
}
