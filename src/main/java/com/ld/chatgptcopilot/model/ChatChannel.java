package com.ld.chatgptcopilot.model;

import java.util.ArrayList;
import java.util.List;

import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import com.intellij.util.xmlb.annotations.XCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

@Tag("chatChannel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatChannel {
    private String id;
    private String name;
    private String object;
    private Long created;
    private String model;
    private Usage usage;
    @Transient
    private String html;

    @Builder.Default
    private Boolean continuousFlag = true;

    private Boolean stream;
    private List<Choice> choices;
    @XCollection
    private List<Message> messages;

    public void clearOther() {
        this.setName(null);
        this.setHtml(null);
        this.setContinuousFlag(null);
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(Message::clearOther);
        }
    }

    public boolean isContinuing() {
        return BooleanUtils.isTrue(continuousFlag);
    }

    public boolean isNotContinuing() {
        return BooleanUtils.isNotTrue(continuousFlag);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Message message;
        private String finish_reason;
        private Integer index;
        private Message delta;
    }

    public static ChatChannel newChannel() {
        ChatChannel chatChannel = new ChatChannel();
        chatChannel.setModel("gpt-3.5-turbo");
        chatChannel.setMessages(new ArrayList<>());
        return chatChannel;
    }

    //最后一条消息内容
    public String getLastMessageContent() {
        if (messages == null || messages.isEmpty()) {
            return "no message";
        }
        return messages.get(messages.size() - 1).getContent();
    }

    //最后一条消息
    public Message getLastMessage() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
}
