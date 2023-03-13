package com.ld.chatgptcopilot.ui;


import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.DACULA_DEFAULT_COLOR;
import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.WHITE;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import com.intellij.util.ui.UIUtil;

public class ChatGPTCopilotTextPane extends JTextPane {

    private static final HTMLEditorKit HTML_EDITOR_KIT = new HTMLEditorKit();
    private static final String HTML_CONTENT_TYPE = "text/html";

    static {
        String rule = UIUtil.displayPropertiesToCSS(UIUtil.getLabelFont(), UIUtil.getLabelForeground());
        HTML_EDITOR_KIT.getStyleSheet().addRule(rule);
    }

    public ChatGPTCopilotTextPane() {
        this(false);
    }

    public ChatGPTCopilotTextPane(boolean editable) {
        setEditorKit(HTML_EDITOR_KIT);
        setContentType(HTML_CONTENT_TYPE);
        setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);
        setEditable(editable);
    }

    public void setHTMLText(String text) {
        setText("<html><body>"+ text +"</body></html>");
    }

}
