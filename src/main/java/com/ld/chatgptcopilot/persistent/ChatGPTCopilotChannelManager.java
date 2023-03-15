package com.ld.chatgptcopilot.persistent;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XCollection;
import com.ld.chatgptcopilot.model.ChatChannel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ChatGPTCopilotChannelManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class ChatGPTCopilotChannelManager implements PersistentStateComponent<ChatGPTCopilotChannelManager.State> {

    private final State state = new State();

    @NotNull
    @Override
    public State getState() {
        if (state.getChatChannels().isEmpty()) {
            state.getChatChannels().add(ChatChannel.newChannel());
        }
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, this.state);

        //反序列化时，如果没有消息，不会初始化数组，需要手动初始化
        this.state.chatChannels.forEach(chatChannel -> {
            if (chatChannel.getMessages() == null) {
                chatChannel.setMessages(new ArrayList<>());
            }
        });

        if (state.getDynamicCommends() == null) {
            state.setDynamicCommends(new ArrayList<>());
        }
    }

    public List<ChatChannel> getChatChannels() {
        return getState().chatChannels;
    }
    public List<String> getDynamicCommends() {
        return getState().dynamicCommends;
    }

    public void add(ChatChannel chatChannel) {
        state.chatChannels.add(chatChannel);
    }

    public void delete(ChatChannel chatChannel) {
        state.chatChannels.remove(chatChannel);
    }

    @Data
    public static class State {
        @XCollection(propertyElementName = "dynamicCommends")
        public List<String> dynamicCommends = new ArrayList<>();
        @XCollection(propertyElementName = "chatChannels")
        public List<ChatChannel> chatChannels = new ArrayList<>();
    }
}
