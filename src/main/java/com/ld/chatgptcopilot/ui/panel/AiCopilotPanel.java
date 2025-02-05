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
import com.ld.chatgptcopilot.actions.ToggleAction;
import com.ld.chatgptcopilot.commen.ChatGPTCopilotComponentActionGroup;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.table.AiCopilotListTableView;
import icons.ChatGPTCopilotIcons;
import org.apache.commons.collections.CollectionUtils;

public class AiCopilotPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final ChatGPTCopilotChannelManager channelManager;

    private AiCopilotListTableView copilotListTableView;
    private AiCopilotDetailsPanel detailsPanel;

    JBSplitter splitter;

    public boolean isShowList = false;

    public AiCopilotPanel(Project project) {
        super(false, true);
        this.project = project;
        channelManager = project.getComponent(ChatGPTCopilotChannelManager.class);
        splitter = new JBSplitter(false, 0.4f);
        init();
        setContentWithoutList();
    }

    private void init() {
        setToolbar();
    }


    private void setContentWithList() {
        ChatChannel lastChatChannel = null;
        if (detailsPanel != null) {
            lastChatChannel = detailsPanel.chatChannel;
        }
        detailsPanel = new AiCopilotDetailsPanel(project);
        detailsPanel.showChannel(lastChatChannel, channelManager.getState().newUI);
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
        channelListPanel.setPreferredSize(new Dimension(50, 0));
        channelListPanel.setMinimumSize(new Dimension(10, 0));
        channelListPanel.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(copilotListTableView);
        channelListPanel.add(scrollPane, BorderLayout.CENTER);


        splitter.setFirstComponent(channelListPanel);
        splitter.setSecondComponent(detailsPanel);
        splitter.setShowDividerIcon(false);
        splitter.setDividerWidth(1);


        super.setContent(splitter);

    }


    private void setToolbar() {
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, createActionGroup(), false);
        actionToolbar.setTargetComponent(this);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        super.setToolbar(toolBarBox);
    }

    private ActionGroup createActionGroup() {
        ChatGPTCopilotComponentActionGroup<AiCopilotPanel> group = new ChatGPTCopilotComponentActionGroup<>(this);
        group.add(new AddChatChannelAction());
        group.add(new DeleteChatChannelAction());
        group.add(new Separator());

        ToggleAction<AiCopilotPanel> panelToggleAction = new ToggleAction<>("Channel List", "Show/Hide Channel List",
                isShowList,
                ChatGPTCopilotIcons.recovery,
                ChatGPTCopilotIcons.spread,
                this::showOrHideChannelList,
                this::showOrHideChannelList);
        group.add(panelToggleAction);
        group.add(new ConfigureChatGptAction());
        //group.add(new TestAction());
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

    public void showOrHideChannelList() {
        if (isShowList) {
            setContentWithoutList();
            isShowList = false;
        } else {
            setContentWithList();
            isShowList = true;
        }
        this.revalidate();
        this.updateUI();
    }

    private void setContentWithoutList() {
        ChatChannel lastChatChannel = null;
        if (detailsPanel != null) {
            lastChatChannel = detailsPanel.chatChannel;
        } else if (CollectionUtils.isNotEmpty(channelManager.getChatChannels())) {
            lastChatChannel = channelManager.getChatChannels().get(channelManager.getChatChannels().size() - 1);
        }
        if (isNull(lastChatChannel)) {
            lastChatChannel = new ChatChannel();
        }
        detailsPanel = new AiCopilotDetailsPanel(project);
        detailsPanel = new AiCopilotDetailsPanel(project);
        detailsPanel.showChannel(lastChatChannel, channelManager.getState().newUI);
        super.setContent(detailsPanel);
    }
}
