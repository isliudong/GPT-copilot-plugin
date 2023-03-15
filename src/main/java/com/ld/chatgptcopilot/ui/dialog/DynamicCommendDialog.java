package com.ld.chatgptcopilot.ui.dialog;

import static com.intellij.openapi.ui.Messages.getCancelButton;
import static com.intellij.openapi.ui.Messages.getOkButton;

import java.awt.*;
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
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotChannelManager;
import com.ld.chatgptcopilot.ui.table.column.DynamicColumnInfoHelper;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.MyResourceBundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicCommendDialog extends DialogWrapper {

    private final static JPanel EMPTY_PANEL = ChatGPTCopilotPanelUtil.createPlaceHolderPanel("No server selected.").withMinimumWidth(450).withMinimumHeight(100);
    private final static String EMPTY_PANEL_NAME = "empty.panel";

    private final Project project;
    private final ChatGPTCopilotChannelManager chatGPTCopilotChannelManager;

    private EditeAbleList<String> commendList;

    ListTableModel<String> listModel = new ListTableModel<>(DynamicColumnInfoHelper.getHelper().generateColumnsInfo());


    public DynamicCommendDialog(@NotNull Project project) {
        super(project, false);
        this.project = project;
        this.chatGPTCopilotChannelManager = project.getComponent(ChatGPTCopilotChannelManager.class);
        init();
    }


    @Override
    protected void init() {
        //editor

        // servers
        for (String server : chatGPTCopilotChannelManager.getDynamicCommends()) {
            listModel.addRow(server);
        }

        commendList = new EditeAbleList<>(listModel);
        commendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setTitle(MyResourceBundleUtil.getKey("Configure_Commends"));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return createCommendsPanel();
    }


    @Override
    protected void doOKAction() {
        if (!validateServers()) {
            return;
        }
        chatGPTCopilotChannelManager.getDynamicCommends().clear();
        chatGPTCopilotChannelManager.getDynamicCommends().addAll(listModel.getItems());

        super.doOKAction();
    }

    private boolean validateServers() {
        for (String item : listModel.getItems()) {
            if (StringUtils.isBlank(item)) {
                Messages.showErrorDialog("Commend is required", MyResourceBundleUtil.getKey("Error"));
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
        listModel.addRow("");
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

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            //super.setValueAt(aValue, row, column);//父类方法无效
            listTableModel.setItem(row,(T)aValue.toString());
        }
    }
}
