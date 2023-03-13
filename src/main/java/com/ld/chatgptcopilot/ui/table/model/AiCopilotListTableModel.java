package com.ld.chatgptcopilot.ui.table.model;

import java.util.List;

import com.intellij.util.ui.ListTableModel;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.table.column.AiCopilotColumnInfoHelper;
import org.jetbrains.annotations.NotNull;

public class AiCopilotListTableModel extends ListTableModel<ChatChannel> {

    public AiCopilotListTableModel(@NotNull List<ChatChannel> chatChannels) {
        super();
        setColumnInfos(AiCopilotColumnInfoHelper.getHelper().generateColumnsInfo());
        setItems(chatChannels);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


}
