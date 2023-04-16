package com.ld.chatgptcopilot.util;

import java.util.ArrayList;
import java.util.List;

import com.intellij.application.options.colors.ColorAndFontOptions;
import com.intellij.application.options.colors.SimpleEditorPreview;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.commen.CodeBlockNodeRenderer;
import com.ld.chatgptcopilot.component.ChatGPTCopilotNotificationManager;
import com.ld.chatgptcopilot.model.Message;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.ins.InsExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import org.jetbrains.annotations.NotNull;

public class ChatGPTCopilotCommonUtil {

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


    public static ChatTipUIUtil.Browser getHtmlPanel(String markdown) {

        ChatTipUIUtil.Browser myBrowser = ChatTipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        myBrowser.setText(md2html(markdown));
        return myBrowser;
    }

    public static ChatTipUIUtil.Browser getHtmlPanel() {
        ChatTipUIUtil.Browser myBrowser = ChatTipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        return myBrowser;
    }

    public static String md2html(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        return HtmlRenderer.builder().build().render(document);
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


    public static String appendPageHtml(List<Message> messages) {
        String messagesHtml = getMessagesHtml(messages);
        String html = ChatGPTCopilotUIUtil.getIndexHtml();
        html = html.replace("{{content}}", messagesHtml);

        return html;
    }

    @NotNull
    public static String getMessagesHtml(List<Message> messages) {
        StringBuilder messagesHtml = new StringBuilder();

        for (Message message : messages) {
            String messageHtml = getMessageHtml(message);
            messagesHtml.append(messageHtml);
        }
        return messagesHtml.toString();
    }

    @Deprecated
    public static String appendHtml(Message message, String html) {
        MutableDataSet options = new MutableDataSet();
        List<Extension> extensionList = new ArrayList<>();
        extensionList.add(TablesExtension.create());
        extensionList.add(StrikethroughExtension.create());
        extensionList.add(TaskListExtension.create());
        extensionList.add(AutolinkExtension.create());
        extensionList.add(AnchorLinkExtension.create());
        extensionList.add(EmojiExtension.create());
        extensionList.add(InsExtension.create());
        extensionList.add(SuperscriptExtension.create());
        extensionList.add(FootnoteExtension.create());
        extensionList.add(DefinitionExtension.create());
        extensionList.add(AbbreviationExtension.create());
        extensionList.add(TypographicExtension.create());
        extensionList.add(YamlFrontMatterExtension.create());
        extensionList.add(WikiLinkExtension.create());

        options.set(Parser.EXTENSIONS, extensionList);
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();


        String contentHtml = renderer.render(parser.parse(message.getContent()));
        message.setContentHtml(contentHtml);

        if (message.isUser()) {
            String userHtml = "<div class=\"right\">\n" +
                    "        <div class=\"content\">" + contentHtml + "</div>\n" +
                    "    </div>";

            //在html中<bottom></bottom>之前插入
            html = html.replace("<bottom></bottom>", userHtml + "<bottom></bottom>");

        } else {
            String robotHtml = "<div class=\"left\">\n" +
                    "        <div class=\"content\">" + contentHtml + "</div>\n" +
                    "    </div>";

            html = html.replace("<bottom></bottom>", robotHtml + "<bottom></bottom>");
        }
        return html;


        /*<div class="right">
        <img src="https://i.imgur.com/9YQYqXs.jpg" alt="头像">
        <p>你好，有什么可以帮助你的吗有什么可以帮助你的吗有什么可以帮助你的吗有什么可以帮助你的吗有什么可以帮助你的吗？</p>
        </div>

        <div class="left">
        <img src="https://i.imgur.com/9YQYqXs.jpg" alt="头像">
        <p>我想了解一下产品的详细信息有什么可以帮助你的吗有什么可以帮助你的吗有什么可以帮助你的吗。</p>
        </div>

        */

    }

    public static String getMessageHtml(Message message) {
        String contentHtml = getContentHtml(message);
        if (message.isUser()) {

            return "<div class=\"right\" id=\"" + message.getId() + "\">\n" +
                    "        <div class='content'>" + contentHtml + "</div>" +
                    "    </div>";
        } else {
            return "<div class=\"left\" id=\"" + message.getId() + "\">\n" +
                    "        <div class='content'>" + contentHtml + "</div>" +
                    "    </div>";
        }
    }

    @NotNull
    public static String getContentHtml(Message message) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).nodeRendererFactory(new CodeBlockNodeRenderer.Factory(true)).build();


        String contentHtml = renderer.render(parser.parse(message.getContent() == null ? "" : message.getContent()));

        //消息标签统一使用<p>包裹
        if (!contentHtml.startsWith("<p>") && !contentHtml.endsWith("</p>")) {
            contentHtml = "<p>" + contentHtml + "</p>";
        }

        message.setContentHtml(contentHtml);
        return contentHtml;
    }



    /*
     * UIUtil.isUnderDarcula()
     *
     * HTMLEditorProvider.openEditor
     * */
}
