package com.ld.chatgptcopilot.persistent;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import com.ld.chatgptcopilot.model.ChatGPTCopilotServer;
import com.ld.chatgptcopilot.util.IdeaUtil;
import com.ld.chatgptcopilot.util.SimpleSelectableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ChatGPTCopilotServerManager", storages = @Storage("ChatGPTCopilotServerManager.xml"))
public class ChatGPTCopilotServerManager implements PersistentStateComponent<ChatGPTCopilotServerManager.Config> {

    private final List<Runnable> listeners = new ArrayList<>();
    private SimpleSelectableList<ChatGPTCopilotServer> chatGPTCopilotServers = new SimpleSelectableList<>();
    private final Config config = new Config();

    @Nullable
    @Override
    public Config getState() {
        config.selected = chatGPTCopilotServers.getSelectedItemIndex();
        config.copilots = chatGPTCopilotServers.getItems();
        return config;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, this.config);

        chatGPTCopilotServers.clear();
        List<ChatGPTCopilotServer> servers = config.copilots;
        if (servers != null) {
            chatGPTCopilotServers.addAll(servers);
        }
        chatGPTCopilotServers.selectItem(config.selected);
    }

    public void addConfigurationServerChangedListener(Runnable runnable) {
        listeners.add(runnable);
    }

    public List<ChatGPTCopilotServer> getChatGPTCopilotServers() {
        return chatGPTCopilotServers.getItems();
    }

    public int getSelectedChatGPTCopilotServerIndex() {
        return chatGPTCopilotServers.getSelectedItemIndex();
    }

    public boolean hasChatGPTCopilotServerConfigured() {
        return chatGPTCopilotServers.hasSelectedItem();
    }

    public ChatGPTCopilotServer getCurrentChatGPTCopilotServer() {
        return chatGPTCopilotServers.hasSelectedItem() ? chatGPTCopilotServers.getItems().get(getSelectedChatGPTCopilotServerIndex()) : null;
    }

    public void setChatGPTCopilotServers(SimpleSelectableList<ChatGPTCopilotServer> servers) {
        this.chatGPTCopilotServers = servers;
        onServersChanged();
    }


    private void onServersChanged() {
        listeners.forEach(Runnable::run);
    }

    public static ChatGPTCopilotServerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(ChatGPTCopilotServerManager.class);
    }

    public String getAPIToken() {
        if (getCurrentChatGPTCopilotServer() == null || getCurrentChatGPTCopilotServer().getApiToken() == null) {
            IdeaUtil.showNotification("configure", "Please configure your ChatGPT Copilot API Token");
            return null;
        }
        return getCurrentChatGPTCopilotServer().getApiToken();
    }

    public static class Config {
        @Tag("selected")
        public int selected;
        @XCollection(propertyElementName = "copilots")
        public List<ChatGPTCopilotServer> copilots;
    }

}
