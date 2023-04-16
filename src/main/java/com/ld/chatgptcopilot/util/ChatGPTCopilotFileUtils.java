package com.ld.chatgptcopilot.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGPTCopilotFileUtils {

    public static String getFileExtension(String filename) {
        Pattern pattern = Pattern.compile("[^.]+$");
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static String getResource(String path) {
        try (var stream = Objects.requireNonNull(ChatGPTCopilotFileUtils.class.getResourceAsStream(path))) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource", e);
        }
    }
}
