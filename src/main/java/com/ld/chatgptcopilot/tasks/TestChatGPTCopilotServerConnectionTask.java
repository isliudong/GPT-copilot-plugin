package com.ld.chatgptcopilot.tasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.server.ChatGPTCopilotServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestChatGPTCopilotServerConnectionTask extends Task.Modal {

    private final Project project;
    private Exception exception;
    private final TaskRepository.CancellableConnection connection;
    private final ChatGPTCopilotServer ChatGPTCopilotServer;

    public TestChatGPTCopilotServerConnectionTask(@Nullable Project project, @NotNull ChatGPTCopilotServer server) {
        super(project, "Test Connection", true);
        this.project = project;
        this.ChatGPTCopilotServer = server;
        this.connection = new TaskRepository.CancellableConnection() {
            protected void doTest() throws Exception {
                ChatChannel chatChannel = ChatChannel.newChannel();
                chatChannel.getMessages().add(ChatChannel.Message.builder().role("user").content("hello").build());
                HttpRequest request = HttpRequest.post("https://api.openai.com/v1/chat/completions")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + ChatGPTCopilotServer.getApiToken())
                        .timeout(20000)
                        .body(JSONUtil.toJsonStr(chatChannel));
                String body;
                try (HttpResponse response = request.execute()) {
                    if (response.getStatus() != 200) {
                        throw new Exception("Error: " + response.getStatus() + " " + response.body());
                    }
                    body = response.body();
                }
                //反序列化 ChatChannel
                ObjectMapper mapper = new ObjectMapper();
                ChatChannel chatChannel1 = mapper.readValue(body, ChatChannel.class);
                chatChannel.getMessages().add(chatChannel1.getChoices().get(0).getMessage());
            }

            public void cancel() {
            }
        };
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setText("Connecting to " + ChatGPTCopilotServer.getName() + "...");
        indicator.setFraction(0);
        indicator.setIndeterminate(true);
        try {

            Future<Exception> future = ApplicationManager.getApplication().executeOnPooledThread(connection);
            while (true) {
                try {
                    exception = future.get(100, TimeUnit.MILLISECONDS);
                    return;
                } catch (TimeoutException ignore) {
                    try {
                        indicator.checkCanceled();
                    } catch (ProcessCanceledException e) {
                        exception = e;
                        connection.cancel();
                        return;
                    }
                } catch (Exception e) {
                    exception = e;
                    return;
                }
            }
        } catch (Exception e) {
            exception = e;
        }
    }

    @Override
    public void onCancel() {
        if (connection != null) {
            connection.cancel();
        }
    }

    public Exception getException() {
        return exception;
    }
}
