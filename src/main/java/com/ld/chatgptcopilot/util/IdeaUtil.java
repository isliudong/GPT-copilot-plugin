package com.ld.chatgptcopilot.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.application.options.colors.ColorAndFontOptions;
import com.intellij.application.options.colors.SimpleEditorPreview;
import com.intellij.ide.util.TipUIUtil;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.options.colors.ColorSettingsPages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ResourceUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public class IdeaUtil {
    private static String template = null;



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


    public static TipUIUtil.Browser getBrowser(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        TipUIUtil.Browser myBrowser = TipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        myBrowser.setText(html);
        return myBrowser;
    }

    public static TipUIUtil.Browser getBrowser() {
        TipUIUtil.Browser myBrowser = TipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        return myBrowser;
    }

    public static String md2html(String markdown) {
        Document document = Parser.builder().build().parse(markdown);
        String html = HtmlRenderer.builder().build().render(document);
        return html;
    }

    @NotNull
    public static String getTemplate(String path) {
        if (template != null) {
            return template;
        }
        byte[] resourceAsBytes;
        try {
            resourceAsBytes = ResourceUtil.getResourceAsBytes(path, ChatTipUIUtil.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (resourceAsBytes != null) {
            template = new String(resourceAsBytes, StandardCharsets.UTF_8);
            template = template.replace("{{css}}", getCss());
        }
        return template;
    }

    @NotNull
    private static String getCss() {
        String highlight;
        if (UIUtil.isUnderDarcula()) {
            highlight = "   /*!\n" +
                    "      Theme: Railscasts railscasts.min.css\n" +
                    "      Author: Ryan Bates (http://railscasts.com)\n" +
                    "      License: ~ MIT (or more permissive) [via base16-schemes-source]\n" +
                    "      Maintainer: @highlightjs/core-team\n" +
                    "      Version: 2021.09.0\n" +
                    "    */pre code.hljs{display:block;overflow-x:auto;padding:1em}code.hljs{padding:3px 5px}.hljs{color:#e6e1dc;background:#2b2b2b}.hljs ::selection,.hljs::selection{background-color:#3a4055;color:#e6e1dc}.hljs-comment{color:#5a647e}.hljs-tag{color:#d4cfc9}.hljs-operator,.hljs-punctuation,.hljs-subst{color:#e6e1dc}.hljs-operator{opacity:.7}.hljs-bullet,.hljs-deletion,.hljs-name,.hljs-selector-tag,.hljs-template-variable,.hljs-variable{color:#da4939}.hljs-attr,.hljs-link,.hljs-literal,.hljs-number,.hljs-symbol,.hljs-variable.constant_{color:#cc7833}.hljs-class .hljs-title,.hljs-title,.hljs-title.class_{color:#ffc66d}.hljs-strong{font-weight:700;color:#ffc66d}.hljs-addition,.hljs-code,.hljs-string,.hljs-title.class_.inherited__{color:#a5c261}.hljs-built_in,.hljs-doctag,.hljs-keyword.hljs-atrule,.hljs-quote,.hljs-regexp{color:#519f50}.hljs-attribute,.hljs-function .hljs-title,.hljs-section,.hljs-title.function_,.ruby .hljs-property{color:#6d9cbe}.diff .hljs-meta,.hljs-keyword,.hljs-template-tag,.hljs-type{color:#b6b3eb}.hljs-emphasis{color:#b6b3eb;font-style:italic}.hljs-meta,.hljs-meta .hljs-keyword,.hljs-meta .hljs-string{color:#bc9458}.hljs-meta .hljs-keyword,.hljs-meta-keyword{font-weight:700}";

        } else {
            highlight = "pre code.hljs{display:block;overflow-x:auto;padding:1em}code.hljs{padding:3px 5px}.hljs{color:#383a42;background:#fafafa}.hljs-comment,.hljs-quote{color:#a0a1a7;font-style:italic}.hljs-doctag,.hljs-formula,.hljs-keyword{color:#a626a4}.hljs-deletion,.hljs-name,.hljs-section,.hljs-selector-tag,.hljs-subst{color:#e45649}.hljs-literal{color:#0184bb}.hljs-addition,.hljs-attribute,.hljs-meta .hljs-string,.hljs-regexp,.hljs-string{color:#50a14f}.hljs-attr,.hljs-number,.hljs-selector-attr,.hljs-selector-class,.hljs-selector-pseudo,.hljs-template-variable,.hljs-type,.hljs-variable{color:#986801}.hljs-bullet,.hljs-link,.hljs-meta,.hljs-selector-id,.hljs-symbol,.hljs-title{color:#4078f2}.hljs-built_in,.hljs-class .hljs-title,.hljs-title.class_{color:#c18401}.hljs-emphasis{font-style:italic}.hljs-strong{font-weight:700}.hljs-link{text-decoration:underline}";
        }
        String mainCss;
        if (UIUtil.isUnderDarcula()){
            mainCss = "body{\n" +
                    "        font-family: Arial, sans-serif;\n" +
                    "        background-color: #2B2B2B !important;\n" +
                    "        /*字体颜色*/\n" +
                    "        color: #A9B7C6 !important;\n" +
                    "        font-size: 12px !important;\n" +
                    "    }\n" +
                    "    /* 左侧内容 */\n" +
                    "    .left {\n" +
                    "        display: flex;\n" +
                    "        align-items: center;\n" +
                    "        margin-bottom: 10px;\n" +
                    "    }\n" +
                    "    .left img {\n" +
                    "        width: 50px;\n" +
                    "        height: 50px;\n" +
                    "        border-radius: 50%;\n" +
                    "        margin-right: 10px;\n" +
                    "    }\n" +
                    "    .left .content {\n" +
                    "        background-color: #3C3F41;\n" +
                    "        /*background-color: #3e4449;*/\n" +
                    "        padding: 10px;\n" +
                    "        border-radius: 10px;\n" +
                    "        max-width: 80%;\n" +
                    "        margin-top: 0 !important;\n" +
                    "        margin-bottom: 0 !important;\n" +
                    "        padding-top: 0 !important;\n" +
                    "        padding-bottom: 0 !important;\n" +
                    "        overflow: hidden;\n" +
                    "    }\n" +
                    "\n" +
                    "    /* 右侧内容 */\n" +
                    "    .right {\n" +
                    "        display: flex;\n" +
                    "        flex-direction: row-reverse;\n" +
                    "        align-items: center;\n" +
                    "        margin-bottom: 10px;\n" +
                    "    }\n" +
                    "    .right img {\n" +
                    "        width: 50px;\n" +
                    "        height: 50px;\n" +
                    "        border-radius: 50%;\n" +
                    "        margin-left: 10px;\n" +
                    "    }\n" +
                    "    .right .content {\n" +
                    "        background-color: #3C3F41;\n" +
                    "        padding: 10px;\n" +
                    "        border-radius: 10px;\n" +
                    "        max-width: 80%;\n" +
                    "        margin-top: 0 !important;\n" +
                    "        margin-bottom: 0 !important;\n" +
                    "        padding-top: 0 !important;\n" +
                    "        padding-bottom: 0 !important;\n" +
                    "        overflow: hidden;\n" +
                    "    }\n" +
                    "\n" +
                    "    /* Customize scrollbar for chatlogs */\n" +
                    "    ::-webkit-scrollbar {\n" +
                    "        width: 9px;\n" +
                    "    }\n" +
                    "\n" +
                    "    ::-webkit-scrollbar-track {\n" +
                    "        background-color: #2B2B2B;\n" +
                    "    }\n" +
                    "\n" +
                    "    ::-webkit-scrollbar-thumb {\n" +
                    "        background-color: #4E4E4E\n" +
                    "    }\n" +
                    "\n" +
                    "    ::-webkit-scrollbar-thumb:hover {\n" +
                    "        background-color: #636566;\n" +
                    "    }\n" +
                    "\n" +
                    "    /*cursor*/\n" +
                    "    .cursor {\n" +
                    "        display: inline-block;\n" +
                    "        width: 10px;\n" +
                    "        height: 20px;\n" +
                    "        margin-bottom: -4px;\n" +
                    "        background-color: black;\n" +
                    "        animation: blinkCursor 1s infinite;\n" +
                    "    }\n" +
                    "    @keyframes blinkCursor {\n" +
                    "        50% {\n" +
                    "            opacity: 0;\n" +
                    "        }\n" +
                    "    }\n";
        }else {
            mainCss = "body{\n" +
                    "    font-family: Arial, sans-serif;\n" +
                    "    background-color: #FFFFFF !important;\n" +
                    "    /*字体颜色*/\n" +
                    "    color: #070730 !important;\n" +
                    "    font-size: 12px !important;\n" +
                    "}\n" +
                    "/* 左侧内容 */\n" +
                    ".left {\n" +
                    "    display: flex;\n" +
                    "    align-items: center;\n" +
                    "    margin-bottom: 10px;\n" +
                    "}\n" +
                    ".left img {\n" +
                    "    width: 50px;\n" +
                    "    height: 50px;\n" +
                    "    border-radius: 50%;\n" +
                    "    margin-right: 10px;\n" +
                    "}\n" +
                    ".left .content {\n" +
                    "    background-color: #F2F2F2;\n" +
                    "    /*background-color: #3e4449;*/\n" +
                    "    padding: 10px;\n" +
                    "    border-radius: 10px;\n" +
                    "    max-width: 80%;\n" +
                    "    margin-top: 0 !important;\n" +
                    "    margin-bottom: 0 !important;\n" +
                    "    padding-top: 0 !important;\n" +
                    "    padding-bottom: 0 !important;\n" +
                    "    overflow: hidden;\n" +
                    "}\n" +
                    "\n" +
                    "/* 右侧内容 */\n" +
                    ".right {\n" +
                    "    display: flex;\n" +
                    "    flex-direction: row-reverse;\n" +
                    "    align-items: center;\n" +
                    "    margin-bottom: 10px;\n" +
                    "}\n" +
                    ".right img {\n" +
                    "    width: 50px;\n" +
                    "    height: 50px;\n" +
                    "    border-radius: 50%;\n" +
                    "    margin-left: 10px;\n" +
                    "}\n" +
                    ".right .content {\n" +
                    "    background-color: #F2F2F2;\n" +
                    "    padding: 10px;\n" +
                    "    border-radius: 10px;\n" +
                    "    max-width: 80%;\n" +
                    "    margin-top: 0 !important;\n" +
                    "    margin-bottom: 0 !important;\n" +
                    "    padding-top: 0 !important;\n" +
                    "    padding-bottom: 0 !important;\n" +
                    "    overflow: hidden;\n" +
                    "}\n" +
                    "\n" +
                    "/* Customize scrollbar for chatlogs */\n" +
                    "::-webkit-scrollbar {\n" +
                    "    width: 9px;\n" +
                    "}\n" +
                    "\n" +
                    "::-webkit-scrollbar-track {\n" +
                    "    background-color: #F5F5F5;\n" +
                    "    width: 9px;\n" +
                    "}\n" +
                    "\n" +
                    "::-webkit-scrollbar-thumb {\n" +
                    "    background-color: #E2E2E2;\n" +
                    "}\n" +
                    "\n" +
                    "::-webkit-scrollbar-thumb:hover {\n" +
                    "    background-color: #DBDBDB;\n" +
                    "}\n" +
                    "\n" +
                    "/*cursor*/\n" +
                    ".cursor {\n" +
                    "    display: inline-block;\n" +
                    "    width: 10px;\n" +
                    "    height: 20px;\n" +
                    "    margin-bottom: -4px;\n" +
                    "    background-color: #070730;\n" +
                    "    animation: blinkCursor 1s infinite;\n" +
                    "}\n" +
                    "@keyframes blinkCursor {\n" +
                    "    50% {\n" +
                    "        opacity: 0;\n" +
                    "    }\n" +
                    "}\n";
        }
        return highlight + mainCss;
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

    //使用highlightjs、flexmark工具将md转换为具有代码高亮的html文本
    public static String md2htmlWithCodeHighlight(String markdown) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options)
                .build();
        return renderer.render(parser.parse(markdown));
    }


    public static void test() {
        String markdown = "```java\npublic static void main(String[] args) {\n    System.out.println(\"Hello World!\");\n}\n```";
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
        String html = renderer.render(parser.parse(markdown));
        String template = getTemplate("html/md.html");
        template = template.replace("{{content}}", html);
        System.out.println(template);
        TipUIUtil.Browser myBrowser = TipUIUtil.createBrowser();
        myBrowser.getComponent().setBorder(JBUI.Borders.empty(0, 10, 8, 10));
        myBrowser.setText(template);
    }

    public static String appendPageHtml(List<Message> messages) {
        String messagesHtml = getMessagesHtml(messages);
        String html = getTemplate("html/md.html");
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
            String userHtml = "<div class=\"right\" id=\"" + message.getId() + "\">\n" +
                    "        <div class='content'>" + contentHtml + "</div>" +
                    "    </div>";

            return userHtml;
        } else {
            String robotHtml = "<div class=\"left\" id=\"" + message.getId() + "\">\n" +
                    "        <div class='content'>" + contentHtml + "</div>" +
                    "    </div>";
            return robotHtml;
        }


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

    @NotNull
    public static String getContentHtml(Message message) {
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


        String contentHtml = renderer.render(parser.parse(message.getContent() == null ? "" : message.getContent()));

        contentHtml = StringEscapeUtils.unescapeHtml(contentHtml);

        //修复不正常内容

        //1、没有pre标签的code标签处理
        String regex = "<code>(.*?)</code>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentHtml);
        while (matcher.find()) {
            String oldCode = matcher.group(1);
            String newCode = oldCode.replace("<br/>", "\n");
            contentHtml = contentHtml.replace("<code>" + oldCode + "</code>", "<pre><code>" + newCode + "</code></pre>");
        }


        //2、移除首尾换行
        String s = contentHtml.startsWith("\n") ? contentHtml.substring(1) : contentHtml;
        contentHtml = s.endsWith("\n") ? s.substring(0, s.length() - 1) : s;

        //3、转义无法显示的内容为文本
        if (contentHtml.startsWith("<!DOCTYPE html>") || contentHtml.startsWith("<!DOCTYPE HTML>")) {
            contentHtml = StringEscapeUtils.escapeHtml(contentHtml);
            //转义后替换\n为<br>，否则无法换行
            contentHtml = contentHtml.replace("\n", "<br/>");
        }

        //4、消息标签统一使用<p>包裹
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
