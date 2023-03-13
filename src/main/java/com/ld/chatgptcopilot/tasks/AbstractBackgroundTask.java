package com.ld.chatgptcopilot.tasks;

import static java.util.Objects.nonNull;

import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.ld.chatgptcopilot.component.ChatGPTCopilotNotificationManager;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBackgroundTask extends Task.Backgroundable {

    public AbstractBackgroundTask(@NotNull Project project, @NotNull String title) {
        super(project, title, false, ALWAYS_BACKGROUND);
    }


    public void showNotification(String title, String content) {
        Notification notification = ChatGPTCopilotNotificationManager.getInstance().createNotification(title, content);
        //自动消失

        Notifications.Bus.notify(notification);
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        String content = nonNull(error.getCause()) ? error.getCause().getMessage() : "";
        Notifications.Bus.notify(ChatGPTCopilotNotificationManager.getInstance().createNotificationError(error.getMessage(), content));
    }


}
