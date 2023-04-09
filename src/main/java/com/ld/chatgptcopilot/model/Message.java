package com.ld.chatgptcopilot.model;

import com.intellij.util.xmlb.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String role;
    private String content;
    @Transient
    private String contentHtml;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
        this.id = String.valueOf(System.currentTimeMillis());
    }

    public void clearOther() {
        setContentHtml(null);
        setId(null);
    }

    public enum Role {
        USER("user"),
        ASSISTANT("assistant");
        final String value;

        Role(String value) {
            this.value = value;
        }
    }

    public boolean isUser() {
        return Role.USER.value.equals(role);
    }
}
