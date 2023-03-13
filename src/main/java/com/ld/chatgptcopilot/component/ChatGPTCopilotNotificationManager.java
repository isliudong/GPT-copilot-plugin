package com.ld.chatgptcopilot.component;

import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;

public class ChatGPTCopilotNotificationManager {

    private static final NotificationGroup BALLON_NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup("ChatGPTCopilot.BALLOON");
    private static final NotificationGroup STICKY_BALLOON_NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup("ChatGPTCopilot.STICKY_BALLOON");


    public static ChatGPTCopilotNotificationManager getInstance() {
        return ApplicationManager.getApplication().getComponent(ChatGPTCopilotNotificationManager.class);
    }

    public Notification createNotification(String title, String content) {
        return BALLON_NOTIFICATION_GROUP.createNotification(title, content, INFORMATION);
    }

    public Notification createNotificationError(String title, String content) {
        return STICKY_BALLOON_NOTIFICATION_GROUP.createNotification(title, content, ERROR);
    }

    public Notification createSilentNotification(String title, String content) {
        return BALLON_NOTIFICATION_GROUP.createNotification(title, content, INFORMATION);
    }

}
