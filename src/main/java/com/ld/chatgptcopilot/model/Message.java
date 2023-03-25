package com.ld.chatgptcopilot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    private String content;

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
