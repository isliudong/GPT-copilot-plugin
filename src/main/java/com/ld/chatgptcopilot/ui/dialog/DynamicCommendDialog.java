package com.ld.chatgptcopilot.ui.dialog;


import static com.intellij.openapi.ui.Messages.getCancelButton;
import static com.intellij.openapi.ui.Messages.getOkButton;

import java.awt.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.ld.chatgptcopilot.model.DynamicCommend;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.table.column.DynamicColumnInfoHelper;
import com.ld.chatgptcopilot.util.ChatGPTCopilotMessageBundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicCommendDialog extends DialogWrapper {

    private final Project project;
    private final ChatGPTCopilotChannelManager chatGPTCopilotChannelManager;

    private EditeAbleList<DynamicCommend> commendList;

    ListTableModel<DynamicCommend> listModel = new ListTableModel<>(DynamicColumnInfoHelper.getHelper().generateColumnsInfo());


    public DynamicCommendDialog(@NotNull Project project) {
        super(project, false);
        this.project = project;
        this.chatGPTCopilotChannelManager = project.getComponent(ChatGPTCopilotChannelManager.class);
        this.setSize(600, 400);
        init();
    }


    @Override
    protected void init() {
        //editor

        // servers
        for (String server : chatGPTCopilotChannelManager.getDynamicCommends()) {
            listModel.addRow(new DynamicCommend(server));
        }

        commendList = new EditeAbleList<>(listModel);
        commendList.setTableHeader(null);
        commendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setTitle(ChatGPTCopilotMessageBundleUtil.getKey("Configure_Commends"));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return createCommendsPanel();
    }


    @Override
    protected void doOKAction() {
        //无法触发失焦自动保存，手动保存表格
        if (commendList.getCellEditor() != null) {
            commendList.getCellEditor().stopCellEditing();
        }
        if (!validateServers()) {
            return;
        }
        chatGPTCopilotChannelManager.getDynamicCommends().clear();
        chatGPTCopilotChannelManager.getDynamicCommends().addAll(listModel.getItems().stream().map(DynamicCommend::getContent).collect(Collectors.toList()));

        super.doOKAction();
    }

    private boolean validateServers() {
        for (DynamicCommend item : listModel.getItems()) {
            if (StringUtils.isBlank(item.getContent())) {
                Messages.showErrorDialog("Commend is required", ChatGPTCopilotMessageBundleUtil.getKey("Error"));
                return false;
            }
        }
        return true;

    }

    private JComponent createCommendsPanel() {
        JBPanel myPanel = new JBPanel(new BorderLayout());
        myPanel.setMinimumSize(JBUI.size(-1, 200));
        myPanel.add(ToolbarDecorator.createDecorator(commendList)
                .setAddAction(button -> {
                    addCommend();
                })
                .setRemoveAction(button -> {
                    if (Messages.showOkCancelDialog(project, "You are going to delete this commend, are you sure?", "Delete Commend", getOkButton(), getCancelButton(), Messages.getQuestionIcon()) == Messages.OK) {
                        removeString();
                    }
                })
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }


    private void addCommend() {
        listModel.addRow(new DynamicCommend(""));
    }


    private void removeString() {
        int selectedServer = commendList.getSelectedRow();
        if (selectedServer >= 0) {
            listModel.removeRow(selectedServer);
        }
    }

    @Nullable


    static class EditeAbleList<T> extends JBTable {
        ListTableModel<T> listTableModel;

        public EditeAbleList(ListTableModel<T> listTableModel) {
            super(listTableModel);
            this.listTableModel = listTableModel;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            return super.getCellEditor(row, column);
        }
    }
}
