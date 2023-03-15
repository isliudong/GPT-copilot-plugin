package com.ld.chatgptcopilot.util;

import static java.util.Objects.nonNull;

import java.util.Date;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.util.text.DateFormatUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatGPTCopilotUtil {
    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getPrettyDateTime(Date date) {
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : "";
    }

    public static void postToAi(@NotNull ChatChannel chatChannel, @Nullable ChatChannel.Message newMessage, @NotNull String apiToken,@Nullable Runnable runnable) {
        ChatChannel data = new ChatChannel();
        BeanUtil.copyProperties(chatChannel, data);
        data.setName(null);
        if (newMessage != null) {
            data.getMessages().add(newMessage);
        }
        HttpRequest request = HttpRequest.post("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .timeout(20000)
                .body(JSONUtil.toJsonStr(data));
        try (HttpResponse response = request.execute()) {
            String body = response.body();
            //反序列化 ChatChannel
            ObjectMapper mapper = new ObjectMapper();
            ChatChannel chatChannel1 = mapper.readValue(body, ChatChannel.class);
            chatChannel.getMessages().add(newMessage);
            chatChannel.getMessages().add(chatChannel1.getChoices().get(0).getMessage());
            if (runnable != null) {
                runnable.run();
            }
            //归纳聊天主题
            ThreadUtil.execAsync(() -> summaryTitle(chatChannel, apiToken));

        } catch (Exception e) {
            IdeaUtil.showFailedNotification("AI Copilot is sick：" + e.getMessage());
        }
    }

    private static void summaryTitle(ChatChannel chatChannel, String apiToken) {
        if (chatChannel.getMessages().size() == 6) {
            ChatChannel target = new ChatChannel();
            BeanUtil.copyProperties(chatChannel, target);
            ChatChannel.Message message = new ChatChannel.Message();
            message.setContent("What is the topic of our chat?");
            message.setRole("user");
            postToAi(target, message, apiToken,null);
            chatChannel.setName(target.getMessages().get(target.getMessages().size() - 1).getContent());
        }
    }


}
