package com.ld.chatgptcopilot.util;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.util.text.DateFormatUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.ui.panel.AbstractChatDisplayPanel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotChatPanel;
import com.ld.chatgptcopilot.ui.panel.AiCopilotDetailsPanel;
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

public class ChatGPTCopilotRequestUtil {
    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    public static String getPrettyDateTime(Date date) {
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : "";
    }

    public static void postToAi(@NotNull ChatChannel chatChannel, @Nullable Message newMessage, @NotNull String apiToken, @Nullable Runnable runnable) {
        ChatChannel data = new ChatChannel();
        BeanUtil.copyProperties(chatChannel, data);
        if (chatChannel.isNotContinuing()) {
            data.setMessages(new ArrayList<>());
        }
        data.clearOther();
        if (newMessage != null) {
            Message messageData = new Message();
            BeanUtil.copyProperties(newMessage, messageData);
            messageData.clearOther();
            data.getMessages().add(messageData);
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
            ChatGPTCopilotCommonUtil.showFailedNotification("AI Copilot is sick：" + e.getMessage());
        }
    }

    public static void postToAiAndUpdateUi(AiCopilotChatPanel copilotChatPanel, @NotNull ChatChannel chatChannel, @Nullable Message newMessage, @NotNull String apiToken, @Nullable Runnable runnable) {
        ChatChannel bodyData = ChatChannel.newChannel();
        BeanUtil.copyProperties(chatChannel, bodyData);

        if (chatChannel.isNotContinuing()) {
            bodyData.setMessages(new ArrayList<>());
        } else if (chatChannel.getTokenCompression()) {
            tokenCompression(bodyData, apiToken);
        }
        bodyData.clearOther();
        bodyData.setStream(true);
        if (newMessage != null) {
            Message messageData = new Message();
            BeanUtil.copyProperties(newMessage, messageData);
            messageData.clearOther();
            bodyData.getMessages().add(messageData);
        }
        //使用OkHttp的EventSourceListener实时接收AI Copilot的回复
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .method("POST", RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(bodyData)))
                .post(RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(bodyData)))
                .build();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();


        Message assistantMess = new Message("assistant", "");
        AiCopilotDetailsPanel aiCopilotDetailsPanel = copilotChatPanel.getAiCopilotDetailsPanel();
        AiCopilotDetailsPanel.InputPanel inputPanel = aiCopilotDetailsPanel.getInputPanel();

        AbstractChatDisplayPanel messageListPanel = copilotChatPanel.getMessageListPanel();


        messageListPanel.addMessage(newMessage);
        messageListPanel.addMessage(assistantMess);
        messageListPanel.loading(assistantMess);


        if (runnable != null) {
            runnable.run();
        }

        EventSourceListener listener = new EventSourceListener() {
            private boolean success = false;
            private boolean newContent = true;

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                super.onClosed(eventSource);
                messageListPanel.removeLoading();
                aiCopilotDetailsPanel.removeDownScroller();
                if (success) {
                    messageListPanel.appendMessage(assistantMess);
                    //归纳聊天主题
                    ThreadUtil.execAsync(() -> summaryTitle(chatChannel, apiToken));
                } else {
                    messageListPanel.removeMessage(newMessage);
                    messageListPanel.removeMessage(assistantMess);
                    inputPanel.restoreLastText();
                }
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                System.out.println(new Date() + data);
                super.onEvent(eventSource, id, type, data);

                if ("[DONE]".equals(data)) {
                    success = true;
                    return;
                }
                //反序列化 ChatChannel
                ObjectMapper mapper = new ObjectMapper();
                try {
                    ChatChannel chatChannel1 = mapper.readValue(data, ChatChannel.class);
                    Message delta = chatChannel1.getChoices().get(0).getDelta();
                    if (delta.getContent() != null) {
                        assistantMess.setContent(assistantMess.getContent() + delta.getContent());
                    }
                    messageListPanel.appendContent(assistantMess, newContent);
                    newContent = false;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    ChatGPTCopilotCommonUtil.showFailedNotification(data);
                    messageListPanel.removeMessage(newMessage);
                    messageListPanel.removeMessage(assistantMess);
                    inputPanel.restoreLastText();
                }
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                super.onFailure(eventSource, t, response);
                if (t != null) {
                    ChatGPTCopilotCommonUtil.showFailedNotification("netWork error：" + t.getMessage());
                }

                if (response != null && response.body() != null) {
                    try {
                        ChatGPTCopilotCommonUtil.showFailedNotification(response.body().string());
                    } catch (IOException e) {
                        ChatGPTCopilotCommonUtil.showFailedNotification(e.getMessage());
                    }
                }
                messageListPanel.removeLoading();
                aiCopilotDetailsPanel.removeDownScroller();
                if (!success) {
                    messageListPanel.removeMessage(newMessage);
                    messageListPanel.removeMessage(assistantMess);
                    inputPanel.restoreLastText();
                }
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                super.onOpen(eventSource, response);
                messageListPanel.removeLoading();
                aiCopilotDetailsPanel.addDownScroller();

            }
        };

        EventSource.Factory factory = EventSources.createFactory(httpClient);
        factory.newEventSource(request, listener);


    }

    private static void tokenCompression(ChatChannel tempChatChannel, String apiToken) {
        if (tempChatChannel.getMessages().stream().map(Message::getContent).collect(Collectors.joining()).length() < 3000) {
            return;
        }
        summaryTitle(tempChatChannel, apiToken);


        //保留最后四个元素，不够则全部保留
        List<Message> messages = tempChatChannel.getMessages();
        int size = messages.size();
        if (size > 4) {
            tempChatChannel.setMessages(messages.subList(size - 4, size));
        }

        AtomicBoolean chinese = new AtomicBoolean(false);
        messages.stream().filter(message -> "user".equals(message.getRole())).findAny().ifPresent(message -> {
            String content = message.getContent();
            chinese.set(content.chars().anyMatch(i -> i >= 0x4E00 && i <= 0x9FA5));
        });

        Message message1 = new Message();
        message1.setContent(chinese.get() ? "我们之前讨论了：" : "Before we talk about：" + tempChatChannel.getName());
        message1.setRole("user");
        Message message2 = new Message();
        message2.setContent(chinese.get() ? "是的，非常不错" : "sure,that is good");
        message2.setRole("assistant");
        tempChatChannel.getMessages().add(0, message2);
        tempChatChannel.getMessages().add(0, message1);
    }

    private static void summaryTitle(ChatChannel chatChannel, String apiToken) {
        if (chatChannel.getMessages().size() == 6) {
            ChatChannel target = new ChatChannel();
            BeanUtil.copyProperties(chatChannel, target);
            Message message = new Message();
            message.setContent("What is the topic of our chat?");
            message.setRole("user");
            Message message2 = new Message();
            message2.setContent("Ok here is my summary of the contents of the chat:");
            message2.setRole("assistant");
            chatChannel.getMessages().add(message2);
            target.getMessages().add(message);
            target.getMessages().add(message2);

            postToAi(target, null, apiToken, null);
            chatChannel.setName(target.getMessages().get(target.getMessages().size() - 1).getContent());
        }
    }


}
