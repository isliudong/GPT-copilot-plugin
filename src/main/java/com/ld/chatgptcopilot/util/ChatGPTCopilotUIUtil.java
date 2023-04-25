package com.ld.chatgptcopilot.util;

import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public class ChatGPTCopilotUIUtil {
    private static String indexHtml = null;


    @NotNull
    public static String getIndexHtml() {
        if (indexHtml != null) {
            return indexHtml;
        }
        indexHtml = ChatGPTCopilotFileUtils.getResource("/html/index.html");
        indexHtml = indexHtml.replace("{{mainCss}}", getCss());
        indexHtml = indexHtml.replace("{{mainJs}}", getJs());
        return indexHtml;
    }

    @NotNull
    private static String getCss() {
        String highlightCSS;
        if (UIUtil.isUnderDarcula()) {
            highlightCSS = ChatGPTCopilotFileUtils.getResource("/html/css/highlight_androidstudio.css");
        } else {
            highlightCSS = ChatGPTCopilotFileUtils.getResource("/html/css/highlight_tomorrow.min.css");
        }
        String mainCss;
        if (UIUtil.isUnderDarcula()) {
            mainCss = ChatGPTCopilotFileUtils.getResource("/html/css/main_dark.css");
        } else {
            mainCss = ChatGPTCopilotFileUtils.getResource("/html/css/main_light.css");
        }
        return highlightCSS + mainCss;
    }

    private static String getJs() {
        //注意依赖顺序
        return ChatGPTCopilotFileUtils.getResource("/html/js/highlight.min.js")
                + ChatGPTCopilotFileUtils.getResource("/html/js/main.js");
    }
}
