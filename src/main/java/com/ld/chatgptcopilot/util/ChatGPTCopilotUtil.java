package com.ld.chatgptcopilot.util;

import static java.util.Objects.nonNull;

import java.util.Date;
import java.util.regex.Pattern;

import com.intellij.util.text.DateFormatUtil;

public class ChatGPTCopilotUtil {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getPrettyDateTime(Date date){
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : "";
    }

}
