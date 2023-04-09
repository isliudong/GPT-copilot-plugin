package com.ld.chatgptcopilot.ui.panel;

import static com.ld.chatgptcopilot.ui.ChatGPTCopilotToolWindowFactory.TOOL_WINDOW_ID;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.actions.AddChatChannelAction;
import com.ld.chatgptcopilot.actions.ConfigureChatGptAction;
import com.ld.chatgptcopilot.actions.DeleteChatChannelAction;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentActionGroup;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.table.AiCopilotListTableView;
import org.apache.commons.collections.CollectionUtils;

public class AiCopilotPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final ChatGPTCopilotChannelManager channelManager;

    private AiCopilotListTableView copilotListTableView;
    private AiCopilotDetailsPanel detailsPanel;

    public AiCopilotPanel(Project project) {
        super(false, true);
        this.project = project;
        channelManager = project.getComponent(ChatGPTCopilotChannelManager.class);
        init();
        setContent();
    }

    private void init() {
        setToolbar();
    }


    private void setContent() {
        JComponent content;
        detailsPanel = new AiCopilotDetailsPanel(project);
        List<ChatChannel> chatChannels = channelManager.getChatChannels();
        List<ChatChannel> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(chatChannels)) {
            for (int i = chatChannels.size() - 1; i >= 0; i--) {
                list.add(chatChannels.get(i));
            }
        }

        copilotListTableView = new AiCopilotListTableView(list);
        copilotListTableView
                .getSelectionModel()
                .addListSelectionListener(event -> {
                    if (event.getValueIsAdjusting()) {
                        return;
                    }
                    this.detailsPanel.showChannel(copilotListTableView.getSelectedObject(), channelManager.getState().newUI);
                });


        JPanel channelListPanel = new JPanel(new BorderLayout());
        channelListPanel.setMinimumSize(new Dimension(100, 0));
        channelListPanel.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(copilotListTableView);
        channelListPanel.add(scrollPane, BorderLayout.CENTER);

        JBSplitter splitter = new JBSplitter();
        splitter.setProportion(0.4f);
        splitter.setFirstComponent(channelListPanel);
        splitter.setSecondComponent(detailsPanel);
        splitter.setShowDividerIcon(false);
        splitter.setDividerWidth(1);

        content = splitter;

        super.setContent(content);

    }


    private void setToolbar() {
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, createActionGroup(), false);
        actionToolbar.setTargetComponent(this);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        super.setToolbar(toolBarBox);
    }

    private ActionGroup createActionGroup() {
        ChatGPTCopilotComponentActionGroup group = new ChatGPTCopilotComponentActionGroup(this);
        group.add(new AddChatChannelAction());
        group.add(new DeleteChatChannelAction());
        group.add(new Separator());
        group.add(new ConfigureChatGptAction());
        return group;
    }

    public void add(ChatChannel chatChannel) {
        SwingUtilities.invokeLater(() -> {
            if (nonNull(copilotListTableView)) {
                copilotListTableView.getModel().insertRow(0, chatChannel);
                copilotListTableView.addSelection(chatChannel);
                detailsPanel.showChannel(copilotListTableView.getSelectedObject(), channelManager.getState().newUI);
                channelManager.add(chatChannel);
            }
        });
    }

    //删除当前选中的channel
    public void deleteSelectedChannel() {
        SwingUtilities.invokeLater(() -> {
            if (nonNull(copilotListTableView)) {
                ChatChannel chatChannel = copilotListTableView.getSelectedObject();
                if (isNull(chatChannel)) {
                    return;
                }
                copilotListTableView.getModel().removeRow(copilotListTableView.getModel().indexOf(chatChannel));
                detailsPanel.showChannel(copilotListTableView.getSelectedObject(), channelManager.getState().newUI);
                channelManager.delete(chatChannel);
            }
        });
    }
}
