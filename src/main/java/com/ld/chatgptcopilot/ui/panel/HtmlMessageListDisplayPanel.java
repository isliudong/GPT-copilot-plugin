package com.ld.chatgptcopilot.ui.panel;


import javax.swing.*;

import com.intellij.ui.JBColor;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.util.IdeaUtil;
import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;

@Getter
public class HtmlMessageListDisplayPanel extends AbstractChatDisplayPanel {

    public static final JCEFHtmlPanel messageHtmlPanel = new JCEFHtmlPanel(null);
    public static volatile boolean messageHtmlPanelFlag = false;


    public HtmlMessageListDisplayPanel(ChatChannel chatChannel, AiCopilotChatPanel aiCopilotChatPanel) {
        super(chatChannel, aiCopilotChatPanel);
        messageHtmlPanel.getComponent().setBackground(UIUtil.getPanelBackground());
        messageHtmlPanel.getCefBrowser().getUIComponent().setBackground(UIUtil.getPanelBackground());
        setBackground(UIUtil.getPanelBackground());
        setContent();
    }

    public void setContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        synchronized (HtmlMessageListDisplayPanel.class) {
            if (!messageHtmlPanelFlag) {
                String html = IdeaUtil.appendPageHtml(messageList);
                messageHtmlPanel.setHtml(html);
                messageHtmlPanelFlag = true;
            } else {
                browserShowChannelMessages();
            }
            add(messageHtmlPanel.getComponent());
            browserToBottom();
        }
    }


    @Override
    public void addMessage(Message message) {
        browserAppendMessage(message);
        messageList.add(message);
    }

    @Override
    public void removeMessage(Message message) {
        messageList.remove(message);
        String code = "var divs = document.getElementsByClassName('chat-container')[0];var div = document.getElementById('" + message.getId() + "');divs.removeChild(div);";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    @Override
    public void appendContent(Message message, boolean newContent) {
        if (message.getContent() == null) {
            message.setContent("");
        }
        browserReplaceTextWithLoading(message);
        browserToBottom();
    }


    @Override
    public void appendMessage(Message message) {
        browserReplaceLastMessage(message);
        browserHighlightAll();
        browserToBottom();
    }


    @Override
    public void loading(Message message) {
        AiCopilotDetailsPanel.InputPanel inputPanel = this.getAiCopilotChatPanel().getAiCopilotDetailsPanel().getInputPanel();
        inputPanel.setText("");
        inputPanel.button.setEnabled(false);
        inputPanel.button.setText("Sending...");
        if (message.getId() != null) {
            browserAddCursor(message.getId());
        } else {
            browserAddCursorLast();
        }
        browserToBottom();
    }


    @Override
    public void removeLoading() {
        AiCopilotDetailsPanel.InputPanel inputPanel = this.getAiCopilotChatPanel().getAiCopilotDetailsPanel().getInputPanel();
        inputPanel.button.setEnabled(true);
        inputPanel.button.setText("Send");
        browserRemoveCursor();
    }

    private void browserShowChannelMessages() {
        String messagesHtml = IdeaUtil.getMessagesHtml(messageList);
        //在倒数第一个class为content的div中的第一个p标签中追加内容
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(messagesHtml);
        String code = "var div = document.getElementsByClassName('chat-container')[0];div.innerHTML = \"" + escapeMessageHtml + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
        browserHighlightAll();
    }

    private void browserReplaceTextWithLoading(Message message) {
        //替换换行符为br标签
        message.setContent(message.getContent().replace("\n", "<br/>"));
        //在倒数第一个class为content的div中的第一个p标签中追加内容
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(message.getContent()) + "<span class=\"cursor\"></span>");

        String code = "var divs = document.getElementsByClassName('content');var div = divs[divs.length-1];div.getElementsByTagName('p')[0].innerHTML = \"" + escapeMessageHtml + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private static void browserReplaceLastMessage(Message message) {
        String contentHtml = IdeaUtil.getContentHtml(message);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(contentHtml);
        //去掉开头和结尾的<p></p>
        escapeMessageHtml = escapeMessageHtml.substring(3, escapeMessageHtml.length() - 5);

        //替换倒数第一个class为content的div中的第一个p标签中的内容
        String code = "var divs = document.getElementsByClassName('content');var div = divs[divs.length-1];div.getElementsByTagName('p')[0].innerHTML = \"" + escapeMessageHtml + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserAppendMessage(Message message) {
        String messageHtml = IdeaUtil.getMessageHtml(message);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(messageHtml);
        String code = "var div = document.getElementsByClassName('chat-container')[0];div.innerHTML += \"" + escapeMessageHtml + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
        browserHighlightAll();
        browserToBottom();
    }

    private static void browserRemoveCursor() {
        String code = "removeCursor()";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private static void browserAddCursor(String id) {
        String code = "addCursor(\"" + id + "\")";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private static void browserAddCursorLast() {
        String code1 = "var divs = document.getElementsByClassName('content');var div = divs[divs.length-1];div.getElementsByTagName('p')[0].innerHTML += \"" + StringEscapeUtils.escapeJavaScript("<span class=\"cursor\"></span>") + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code1, "", 0);
    }

    private void browserHighlightAll() {
        String code = "hljs.highlightAll();";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserToBottom() {
        String code = "window.scrollTo(0,document.body.scrollHeight)";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }
}
