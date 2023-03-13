package com.ld.chatgptcopilot.ui.table;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

import java.awt.*;
import java.util.List;
import javax.swing.table.TableColumnModel;

import com.intellij.ui.table.TableView;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.table.model.AiCopilotListTableModel;


public class AiCopilotListTableView extends TableView<ChatChannel> {

    private final AiCopilotListTableModel model;

    public AiCopilotListTableView(List<ChatChannel> chatChannels) {
        super();
        model = new AiCopilotListTableModel(chatChannels);
        setModelAndUpdateColumns(model);
        setSelectionMode(SINGLE_SELECTION);
        setIntercellSpacing(new Dimension());
        setShowGrid(false);
        //不显示表头
        setTableHeader(null);
        //setRowHeight(100);
    }


    @Override
    protected TableColumnModel createDefaultColumnModel() {
        TableColumnModel columnModel = super.createDefaultColumnModel();
        columnModel.setColumnMargin(0);
        return columnModel;
    }

    public void updateModel(List<ChatChannel> channels) {
        model.setItems(channels);
    }

    @Override
    public AiCopilotListTableModel getModel() {
        return model;
    }
}
