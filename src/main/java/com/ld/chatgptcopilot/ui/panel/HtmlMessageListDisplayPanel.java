package com.ld.chatgptcopilot.ui.panel;


import static com.intellij.ui.jcef.JBCefClient.Properties.JS_QUERY_POOL_SIZE;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.util.ChatGPTCopilotCommonUtil;
import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;

@Getter
public class HtmlMessageListDisplayPanel extends AbstractChatDisplayPanel {

    //共享browser
    public static final JCEFHtmlPanel messageHtmlPanel = new JCEFHtmlPanel(null);
    public static volatile boolean messageHtmlPanelFlag = false;

    private JBCefJSQuery copyCodeJsQuery;
    private JBCefJSQuery replaceCodeJsQuery;


    public HtmlMessageListDisplayPanel(Project project, ChatChannel chatChannel, AiCopilotChatPanel aiCopilotChatPanel) {
        super(project, chatChannel, aiCopilotChatPanel);
        messageHtmlPanel.getComponent().setBackground(UIUtil.getPanelBackground());
        messageHtmlPanel.getCefBrowser().getUIComponent().setBackground(UIUtil.getPanelBackground());
        setBackground(UIUtil.getPanelBackground());
        setContent();
        addBrowserListener();
    }

    private void addBrowserListener() {
        //监听控制台输出
        CefBrowser cefBrowser = messageHtmlPanel.getCefBrowser();
        cefBrowser.getClient().addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public void onAfterCreated(CefBrowser browser) {
                super.onAfterCreated(browser);
                browser.getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
                    @Override
                    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                        System.out.println("onConsoleMessage: " + message);
                        return super.onConsoleMessage(browser, level, message, source, line);
                    }
                });
                //监听页面加载完成
                cefBrowser.getClient().addLoadHandler(new CefLoadHandlerAdapter() {
                    @Override
                    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                        super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward);
                        System.out.println("onLoadingStateChange: " + isLoading);
                    }

                    @Override
                    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                        super.onLoadStart(browser, frame, transitionType);
                        System.out.println("onLoadStart: " + transitionType);
                    }

                    @Override
                    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                        super.onLoadEnd(browser, frame, httpStatusCode);
                        System.out.println("onLoadEnd: " + httpStatusCode);
                    }

                    @Override
                    public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                        super.onLoadError(browser, frame, errorCode, errorText, failedUrl);
                        System.out.println("onLoadError: " + errorCode + " " + errorText + " " + failedUrl);
                    }
                });
            }
        });


    }

    private void addBrowserJavaBridge() {
        messageHtmlPanel.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if (httpStatusCode == 200) {
                    messageHtmlPanel.getCefBrowser().executeJavaScript(
                            "window.JavaBridge = {" +
                                    "copyCode : function(code) {" +
                                    copyCodeJsQuery.inject("code") +
                                    "}," +
                                    "replaceCode : function(code) {" +
                                    replaceCodeJsQuery.inject("code") +
                                    "}," +
                                    "};",
                            "", 0);
                }
            }
        }, messageHtmlPanel.getCefBrowser());
    }

    public void setContent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        synchronized (HtmlMessageListDisplayPanel.class) {
            if (!messageHtmlPanelFlag) {
                String html = ChatGPTCopilotCommonUtil.appendPageHtml(messageList);
                messageHtmlPanel.setHtml(html);
                messageHtmlPanel.getJBCefClient().setProperty(JS_QUERY_POOL_SIZE, 20);
                messageHtmlPanelFlag = true;

                this.copyCodeJsQuery = JBCefJSQuery.create((JBCefBrowserBase) messageHtmlPanel);
                this.copyCodeJsQuery.addHandler((text) -> {
                    StringSelection stringSelection = new StringSelection(text);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    return null;
                });

                this.replaceCodeJsQuery = new ReplaceInEditorQuery(this.getProject(), messageHtmlPanel, editor -> {
                }).getQuery();

                addBrowserJavaBridge();
            } else {
                browserShowChannelMessages(messageList);
            }
            add(messageHtmlPanel.getComponent());
            browserToBottom();
        }
    }


    @Override
    public void addMessage(Message message) {
        browserAddMessage(message);
        messageList.add(message);
    }

    @Override
    public void removeMessage(Message message) {
        messageList.remove(message);
        String code = String.format("removeMessage('%s')", message.getId());
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    @Override
    public void appendContent(Message message, boolean newContent) {
        if (message.getContent() == null) {
            message.setContent("");
        }
        browserLoadingMessage(message);
        //browserHighlightAll(); 实时高亮导致页面闪烁
        browserToBottom();
    }


    @Override
    public void appendMessage(Message message) {
        browserUpdateMessage(message);
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
    public void dispose() {
        messageHtmlPanel.dispose();
    }


    @Override
    public void removeLoading() {
        AiCopilotDetailsPanel.InputPanel inputPanel = this.getAiCopilotChatPanel().getAiCopilotDetailsPanel().getInputPanel();
        inputPanel.button.setEnabled(true);
        inputPanel.button.setText("Send");
        browserRemoveCursor();
    }

    private void browserShowChannelMessages(List<Message> messages) {
        String messagesHtml = ChatGPTCopilotCommonUtil.getMessagesHtml(messages);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(messagesHtml);
        String code = "var div = document.getElementsByClassName('chat-container')[0];div.innerHTML = \"" + escapeMessageHtml + "\";";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
        browserHighlightAll();
    }

    private void browserLoadingMessage(Message message) {
        //在倒数第一个class为content的div中的第一个p标签中追加内容
        String contentHtml = ChatGPTCopilotCommonUtil.getContentHtml(message);
        //去掉结尾的</p>
        contentHtml = contentHtml.substring(0, contentHtml.length() - 5);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(contentHtml + "<span class='cursor'></span>" + "</p>");

        String code = String.format("replaceMessageContent('%s','%s')", message.getId(), escapeMessageHtml);
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserUpdateMessage(Message message) {
        String contentHtml = ChatGPTCopilotCommonUtil.getContentHtml(message);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(contentHtml);
        //去掉开头和结尾的<p></p>
        escapeMessageHtml = escapeMessageHtml.substring(3, escapeMessageHtml.length() - 5);

        String code = String.format("replaceMessageContent('%s','%s')", message.getId(), escapeMessageHtml);
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserAddMessage(Message message) {
        String messageHtml = ChatGPTCopilotCommonUtil.getMessageHtml(message);
        String escapeMessageHtml = StringEscapeUtils.escapeJavaScript(messageHtml);
        String code = String.format("addMessage('%s')", escapeMessageHtml);
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
        browserHighlightAll();
        browserToBottom();
    }

    private void browserRemoveCursor() {
        String code = "removeCursor()";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserAddCursor(String id) {
        String code = String.format("addCursor('%s')", id);
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserAddCursorLast() {
        String code1 = "addCursorLast()";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code1, "", 0);
    }

    private void browserHighlightAll() {
        String code = "hljs.highlightAll();";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    private void browserToBottom() {
        String code = "scrollToBottom();";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }

    public void displayLandingView() {
        String code = "displayLandingView()";
        messageHtmlPanel.getCefBrowser().executeJavaScript(code, "", 0);
    }
}
