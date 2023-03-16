package com.ld.chatgptcopilot.util;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.util.text.DateFormatUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotChatPanel;
import com.ld.chatgptcopilot.ui.panel.MessagesPanel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatGPTCopilotUtil {
    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getPrettyDateTime(Date date) {
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : "";
    }

    public static void postToAi(@NotNull ChatChannel chatChannel, @Nullable ChatChannel.Message newMessage, @NotNull String apiToken, @Nullable Runnable runnable) {
        ChatChannel data = new ChatChannel();
        BeanUtil.copyProperties(chatChannel, data);
        data.setName(null);
        if (newMessage != null) {
            data.getMessages().add(newMessage);
        }

        HttpRequest request = HttpRequest.post("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .timeout(60000)
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

    public static void postToAiAndUpdateUi(MessagesPanel message, @NotNull ChatChannel chatChannel, @Nullable ChatChannel.Message newMessage, @NotNull String apiToken, @Nullable Runnable runnable) {
        ChatChannel data = new ChatChannel();
        BeanUtil.copyProperties(chatChannel, data);
        data.setName(null);
        data.setStream(true);
        if (newMessage != null) {
            data.getMessages().add(newMessage);
        }
        //使用OkHttp的EventSourceListener实时接收AI Copilot的回复
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .method("POST", RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(data)))
                .post(RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(data)))
                .build();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();


        AiCopilotChatPanel.MessageItem user = new AiCopilotChatPanel.MessageItem(newMessage, message);
        ChatChannel.Message assistantMess = new ChatChannel.Message("assistant", "");
        AiCopilotChatPanel.MessageItem assistant = new AiCopilotChatPanel.MessageItem(assistantMess, message);
        message.addMessage(user);
        message.addMessage(assistant);
        assistant.loading();

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                super.onClosed(eventSource);
                assistant.loadingEnd();
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                System.out.println(data);
                super.onEvent(eventSource, id, type, data);

                if ("[DONE]".equals(data)) {
                    return;
                }
                //反序列化 ChatChannel
                ObjectMapper mapper = new ObjectMapper();
                try {
                    ChatChannel chatChannel1 = mapper.readValue(data, ChatChannel.class);
                    assistant.appendContent(chatChannel1.getChoices().get(0).getDelta().getContent());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                super.onFailure(eventSource, t, response);
                if (t != null) {
                    assistant.appendContent(t.getMessage());
                }

                if (response != null && response.body() != null) {
                    try {
                        assistant.appendContent(response.body().string());
                    } catch (IOException e) {
                        assistant.appendContent(e.getMessage());
                    }
                }
                assistant.loadingEnd();
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                super.onOpen(eventSource, response);
                assistant.loadingEnd();
            }
        };

        EventSource.Factory factory = EventSources.createFactory(httpClient);
        factory.newEventSource(request, listener);


    }

    private static void summaryTitle(ChatChannel chatChannel, String apiToken) {
        if (chatChannel.getMessages().size() == 6) {
            ChatChannel target = new ChatChannel();
            BeanUtil.copyProperties(chatChannel, target);
            ChatChannel.Message message = new ChatChannel.Message();
            message.setContent("What is the topic of our chat?");
            message.setRole("user");
            postToAi(target, message, apiToken, null);
            chatChannel.setName(target.getMessages().get(target.getMessages().size() - 1).getContent());
        }
    }


}
