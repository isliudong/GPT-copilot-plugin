package com.ld.chatgptcopilot.actions.coffee;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.RightAlignedToolbarAction;
import com.ld.chatgptcopilot.util.MultilingualUtil;
import org.jetbrains.annotations.NotNull;

public class CoffeeMeAction extends AnAction implements RightAlignedToolbarAction {
    public CoffeeMeAction() {
        super(MultilingualUtil.getKey("CoffeeMe"), MultilingualUtil.getKey("CoffeeMe"), AllIcons.Ide.Gift);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        SupportView supportView = new SupportView();
        supportView.show();
    }
}
