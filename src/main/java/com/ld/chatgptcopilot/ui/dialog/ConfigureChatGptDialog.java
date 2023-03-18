package com.ld.chatgptcopilot.ui.dialog;

import static com.intellij.openapi.ui.Messages.getCancelButton;
import static com.intellij.openapi.ui.Messages.getOkButton;
import static java.util.Objects.nonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.containers.ConcurrentFactoryMap;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.model.ChatGPTCopilotServer;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.server.auth.AuthType;
import com.ld.chatgptcopilot.server.editor.ChatGPTCopilotServerEditor;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.MyResourceBundleUtil;
import com.ld.chatgptcopilot.util.SimpleSelectableList;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigureChatGptDialog extends DialogWrapper {

    private final static JPanel EMPTY_PANEL = ChatGPTCopilotPanelUtil.createPlaceHolderPanel("No server selected.").withMinimumWidth(450).withMinimumHeight(100);
    private final static String EMPTY_PANEL_NAME = "empty.panel";

    private final Project project;
    private final ChatGPTCopilotServerManager chatGPTCopilotServerManager;

    private SimpleSelectableList<ChatGPTCopilotServer> servers;
    private JBList<ChatGPTCopilotServer> serversList;
    private final List<ChatGPTCopilotServerEditor> editors = new ArrayList<>();

    private JPanel ChatGPTCopilotServerEditor;
    private Splitter splitter;

    private int count;
    private final Map<ChatGPTCopilotServer, String> serverNamesMap = ConcurrentFactoryMap.createMap(server -> Integer.toString(count++));

    private BiConsumer<ChatGPTCopilotServer, Boolean> changeListener;
    private Consumer<ChatGPTCopilotServer> changeUrlListener;


    public ConfigureChatGptDialog(@NotNull Project project) {
        super(project, false);
        this.project = project;
        this.chatGPTCopilotServerManager = ChatGPTCopilotServerManager.getInstance();
        init();
    }


    @Override
    protected void init() {
        //editor
        ChatGPTCopilotServerEditor = new JPanel(new CardLayout());
        ChatGPTCopilotServerEditor.add(EMPTY_PANEL, EMPTY_PANEL_NAME);

        // servers
        servers = new SimpleSelectableList<>();
        CollectionListModel listModel = new CollectionListModel(new ArrayList());
        for (ChatGPTCopilotServer server : chatGPTCopilotServerManager.getChatGPTCopilotServers()) {
            ChatGPTCopilotServer clone = server.clone();
            listModel.add(clone);
            servers.add(clone);
        }

        servers.selectItem(chatGPTCopilotServerManager.getSelectedChatGPTCopilotServerIndex());

        //listeners
        this.changeListener = (server, selected) -> servers.updateSelectedItem(server, selected);
        this.changeUrlListener = (server) -> ((CollectionListModel) serversList.getModel()).contentsChanged(server);


        IntStream.range(0, servers.getItems().size())
                .forEach(i -> addChatGPTCopilotServerEditor(servers.getItems().get(i), i == chatGPTCopilotServerManager.getSelectedChatGPTCopilotServerIndex()));


        serversList = new JBList();
        serversList.setEmptyText(MyResourceBundleUtil.getKey("No_servers"));
        serversList.setModel(listModel);
        serversList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        serversList.addListSelectionListener(e -> {
            ChatGPTCopilotServer selectedServer = getSelectedServer();
            if (nonNull(selectedServer)) {
                String name = serverNamesMap.get(selectedServer);
                updateEditorPanel(name);
            }
        });

        serversList.setCellRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                ChatGPTCopilotServer server = (ChatGPTCopilotServer) value;
                setIcon(ChatGPTCopilotIcons.pluginIcon);
                append(server.getPresentableName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });

        setTitle(MyResourceBundleUtil.getKey("Configure_Servers"));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        splitter = new JBSplitter(true, 0.6f);
        splitter.setFirstComponent(createServersPanel());
        splitter.setSecondComponent(createDetailsServerPanel());

        return splitter;
    }


    @Override
    protected void doOKAction() {
        if (!validateServers()) {
            return;
        }
        chatGPTCopilotServerManager.setChatGPTCopilotServers(servers);
        if (!servers.isEmpty()) {
            updateIssues();
        }
        super.doOKAction();
    }

    private boolean validateServers() {
        List<ChatGPTCopilotServer> servers = this.servers.getItems();

        for (ChatGPTCopilotServer server : servers) {
            if (StringUtils.isBlank(server.getName())) {
                Messages.showErrorDialog(MyResourceBundleUtil.getKey("Server_url_is_required"), MyResourceBundleUtil.getKey("Error"));
                return false;
            }
            if (AuthType.USER_PASS.equals(server.getType())) {
                if (StringUtils.isBlank(server.getUsername())) {
                    Messages.showErrorDialog(MyResourceBundleUtil.getKey("Username_is_required"), MyResourceBundleUtil.getKey("Error"));
                    return false;
                }
                if (StringUtils.isBlank(server.getPassword())) {
                    Messages.showErrorDialog(MyResourceBundleUtil.getKey("Password_is_required"), MyResourceBundleUtil.getKey("Error"));
                    return false;
                }
            } else if (AuthType.API_TOKEN.equals(server.getType())) {
                if (StringUtils.isBlank(server.getApiToken())) {
                    Messages.showErrorDialog(MyResourceBundleUtil.getKey("Personal_Access_Token_is_required"), MyResourceBundleUtil.getKey("Error"));
                    return false;
                }
            }

        }
        return true;
    }

    private JComponent createServersPanel() {

        if (servers.hasSelectedItem()) {
            serversList.setSelectedValue(servers.getSelectedItem(), true);
        }

        JBPanel myPanel = new JBPanel(new BorderLayout());
        myPanel.setMinimumSize(JBUI.size(-1, 200));
        myPanel.add(ToolbarDecorator.createDecorator(serversList)
                .setAddAction(button -> {
                    addChatGPTCopilotServer();
                })
                .setRemoveAction(button -> {
                    if (Messages.showOkCancelDialog(project, "You are going to delete this server, are you sure?", "Delete Server", getOkButton(), getCancelButton(), Messages.getQuestionIcon()) == Messages.OK) {
                        removeChatGPTCopilotServer();
                    }
                })
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }


    private void addChatGPTCopilotServer() {
        ChatGPTCopilotServer server = new ChatGPTCopilotServer();
        servers.add(server);
        ((CollectionListModel) serversList.getModel()).add(server);
        addChatGPTCopilotServerEditor(server, false);
        serversList.setSelectedIndex(serversList.getModel().getSize() - 1);
    }

    private void addChatGPTCopilotServerEditor(ChatGPTCopilotServer server, boolean selected) {
        ChatGPTCopilotServerEditor editor = new ChatGPTCopilotServerEditor(project, server, selected, changeListener, changeUrlListener);
        editors.add(editor);
        String name = serverNamesMap.get(server);
        ChatGPTCopilotServerEditor.add(editor.getPanel(), name);
        ChatGPTCopilotServerEditor.doLayout();
    }


    private void removeChatGPTCopilotServer() {
        int selectedServer = serversList.getSelectedIndex();
        if (selectedServer > -1) {
            ((CollectionListModel) serversList.getModel()).remove(selectedServer);
            servers.remove(selectedServer);
            serversList.setSelectedIndex(servers.getSelectedItemIndex());
        }

        if (servers.isEmpty()) {
            updateEditorPanel(EMPTY_PANEL_NAME);
        }


    }

    private void updateEditorPanel(String name) {
        ((CardLayout) ChatGPTCopilotServerEditor.getLayout()).show(ChatGPTCopilotServerEditor, name);
        splitter.doLayout();
        splitter.repaint();
    }

    private void updateIssues() {
    }


    private JComponent createDetailsServerPanel() {
        return ChatGPTCopilotServerEditor;
    }

    @Nullable
    private ChatGPTCopilotServer getSelectedServer() {
        return serversList.getSelectedValue();
    }
}
