package com.ld.chatgptcopilot.actions;

import java.awt.*;
import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.ld.chatgptcopilot.commen.ActionProperties;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.ui.panel.AiCopilotPanel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.ChatGPTCopilotUtil;
import com.ld.chatgptcopilot.util.IdeaUtil;
import icons.ChatGPTCopilotIcons;
import org.jetbrains.annotations.NotNull;

public abstract class AbsChatGetMenuAction extends ChatGPTCopilotComponentAction<AiCopilotPanel> {
    public AbsChatGetMenuAction(@NotNull ActionProperties actionProperties) {
        super(actionProperties);
    }

    protected static void askCopilot(Project project, Editor editor, ChatChannel chatChannel) {
        String apiToken = ChatGPTCopilotServerManager.getInstance().getAPIToken();
        PopupPanel jbPanelJBPanel = new PopupPanel(new BorderLayout());
        jbPanelJBPanel.setPreferredSize(new Dimension(400, 300));
        JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();
        loadingPanel.setPreferredSize(new Dimension(300, 500));
        jbPanelJBPanel.add(loadingPanel, BorderLayout.CENTER);
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(jbPanelJBPanel, jbPanelJBPanel)
                .setMovable(true)
                .setResizable(true)
                .setShowShadow(true)
                .setRequestFocus(true)
                .setCancelOnClickOutside(false)
                .setCancelKeyEnabled(true)
                .setCancelButton(new IconButton("Close", AllIcons.Actions.Close))
                .createPopup();
        popup.showInBestPositionFor(editor);

        ThreadUtil.execAsync(() -> {
            chatChannel.setContinuousFlag(true);
            ChatGPTCopilotUtil.postToAi(chatChannel, null, apiToken, () -> {
                JComponent markdownComponent = IdeaUtil.getMarkdownComponent(chatChannel.getLastMessageContent()).getComponent();
                //创建一个大小合适的面板来显示回复信息
                SwingUtilities.invokeLater(() -> {
                    JBScrollPane jbScrollPane = new JBScrollPane(markdownComponent);
                    jbPanelJBPanel.removeAll();
                    jbPanelJBPanel.add(jbScrollPane);
                    jbPanelJBPanel.revalidate();
                    jbPanelJBPanel.repaint();
                });
            });
        });
    }

    static class PopupPanel extends JBPanel {
        public PopupPanel(LayoutManager layout) {
            super(layout);
            setOpaque(true);
            int fieldWeight = getSize().width;
            int fieldHeight = getSize().height;
            setSize(fieldWeight + 5, fieldHeight + 5);

        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int fieldX = 0;
            int fieldY = 0;
            int fieldWeight = getSize().width;
            int fieldHeight = getSize().height;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getBackground());
            g.fillRoundRect(fieldX, fieldY, fieldWeight, fieldHeight, 20, 20);
            super.paintChildren(g);//可以正常显示该组件中添加的组件
        }
    }
}
