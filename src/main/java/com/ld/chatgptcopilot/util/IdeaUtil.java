package com.ld.chatgptcopilot.util;

import com.intellij.ide.ui.text.StyledTextPane;
import com.intellij.ide.util.TipUIUtil;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.component.ChatGPTCopilotNotificationManager;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;

public class IdeaUtil {
    private static String gitProjectName = null;

    public static void showNotification(String title, String content) {
        Notifications.Bus.notify(ChatGPTCopilotNotificationManager.getInstance().createNotification(title, content));
    }

    public static void showSuccessNotification() {
        Notifications.Bus.notify(ChatGPTCopilotNotificationManager.getInstance().createNotification("ChatGPTCopilot", "Success"));
    }

    public static void showFailedNotification() {
        Notifications.Bus.notify(ChatGPTCopilotNotificationManager.getInstance().createNotificationError("ChatGPTCopilot", "Failed"));
    }

    public static void showFailedNotification(String content) {
        Notifications.Bus.notify(ChatGPTCopilotNotificationManager.getInstance().createNotificationError("ChatGPTCopilot", content));
    }


    public static TipUIUtil.Browser getMarkdownComponent(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        TipUIUtil.Browser myBrowser = TipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        myBrowser.setText(html);
        return myBrowser;
    }

    public static StyledTextPane getMarkdownComponent2(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        StyledTextPane myBrowser = new StyledTextPane();
        myBrowser.setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        myBrowser.setText(html);
        return myBrowser;
    }

    public static String md2html(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        return html;
    }
}
