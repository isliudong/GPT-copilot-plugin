package com.ld.chatgptcopilot.ui.labels;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.JBLabel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil;

public class ChatGPTCopilotLinkLabel extends JBLabel {

    private final String url;

    public ChatGPTCopilotLinkLabel(String text, String url) {
        super(text);
        this.url = url;
        init();
    }

    private void init(){
        setIcon(AllIcons.Ide.External_link_arrow);
        setHorizontalAlignment(SwingUtilities.LEFT);
        setToolTipText(this.url);
        setCursor(ChatGPTCopilotLabelUtil.HAND_CURSOR);
        setForeground(ChatGPTCopilotLabelUtil.LINK_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> BrowserUtil.open(url));
            }
        });
    }

}
