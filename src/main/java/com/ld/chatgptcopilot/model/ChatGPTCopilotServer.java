package com.ld.chatgptcopilot.model;

import static com.intellij.openapi.util.text.StringUtil.trim;

import java.util.Objects;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import com.ld.chatgptcopilot.server.auth.AuthType;
import com.ld.chatgptcopilot.util.ChatGPTCopilotCommonUtil;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPasswordUtil;
import org.jetbrains.annotations.NotNull;

@Tag("ChatGPTCopilotServer")
public class ChatGPTCopilotServer {

    public static final AuthType DEFAULT_AUTH_TYPE = AuthType.USER_PASS;

    private String name;

    private String username;

    private String password;

    private String userEmail;
    private String apiToken;

    private AuthType type;

    public ChatGPTCopilotServer() {
    }

    private ChatGPTCopilotServer(String name, String username, String password, String userEmail, String apiToken, AuthType type) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userEmail = userEmail;
        this.apiToken = apiToken;
        this.type = type;
    }

    private ChatGPTCopilotServer(ChatGPTCopilotServer other) {
        this(other.getName(), other.getUsername(), other.getPassword(), other.getUserEmail(), other.getApiToken(), other.getType());
    }

    public void withUserAndPass(String url, String username, String password) {
        setName(url);
        setUsername(username);
        setPassword(password);
        setUserEmail(null);
        setApiToken(null);
        setType(AuthType.USER_PASS);
    }

    public void withApiToken(String name, String apiToken) {
        setName(name);
        setUsername(null);
        setPassword(null);
        setUserEmail(userEmail);
        setApiToken(apiToken);
        setType(AuthType.API_TOKEN);
    }

    @Attribute("url")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Attribute("username")
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Transient
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Attribute("password")
    public String getEncodedPassword() {
        return ChatGPTCopilotPasswordUtil.encodePassword(this.getPassword());
    }

    public void setEncodedPassword(String password) {
        try {
            this.setPassword(ChatGPTCopilotPasswordUtil.decodePassword(password));
        } catch (Exception e) {
            ChatGPTCopilotCommonUtil.showFailedNotification("ChatGPTCopilot token error");
        }
    }

    @Attribute("useremail")
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Transient
    public String getApiToken() {
        return apiToken;
    }

    @Attribute("apiToken")
    public String getEncodedApiToken() {
        return ChatGPTCopilotPasswordUtil.encodePassword(this.getApiToken());
    }

    public void setEncodedApiToken(String apiToken) {
        try {
            this.setApiToken(ChatGPTCopilotPasswordUtil.decodePassword(apiToken));
        } catch (NumberFormatException var3) {
            ChatGPTCopilotCommonUtil.showFailedNotification("ChatGPTCopilot token error");
        }
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @Attribute("type")
    public AuthType getType() {
        return type == null ? DEFAULT_AUTH_TYPE : type;
    }

    public void setType(AuthType type) {
        this.type = type == null ? DEFAULT_AUTH_TYPE : type;
    }

    @Transient
    public boolean hasUserAndPassAuth() {
        return AuthType.USER_PASS == getType();
    }

    @Transient
    public String getPresentableName() {
        return StringUtil.isEmpty(trim(getName())) ? "<undefined>" : getName();
    }

    @NotNull
    @Override
    public ChatGPTCopilotServer clone() {
        return new ChatGPTCopilotServer(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatGPTCopilotServer that = (ChatGPTCopilotServer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    @Override
    public String toString() {
        return getPresentableName();
    }
}
