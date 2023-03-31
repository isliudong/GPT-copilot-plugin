package com.ld.chatgptcopilot.util;

import com.intellij.application.options.colors.ColorAndFontOptions;
import com.intellij.application.options.colors.SimpleEditorPreview;
import com.intellij.ide.util.TipUIUtil;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.component.ChatGPTCopilotNotificationManager;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;

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

    public static TipUIUtil.Browser getMarkdownComponent() {
        TipUIUtil.Browser myBrowser = TipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        return myBrowser;
    }

    public static String md2html(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        return html;
    }

    public static SimpleEditorPreview createEditorPreview() {
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        ColorAndFontOptions options = new ColorAndFontOptions();
        options.reset();
        options.selectScheme(scheme.getName());
        ColorSettingsPage[] pages = ColorSettingsPages.getInstance().getRegisteredPages();
        int index;
        int attempt = 0;
        do {
            index = (int) Math.round(Math.random() * (pages.length - 1));
        }
        while (StringUtil.countNewLines(pages[index].getDemoText()) < 8 && ++attempt < 10);
        SimpleEditorPreview simpleEditorPreview = new SimpleEditorPreview(options, pages[index], false);
        return simpleEditorPreview;
    }
}
