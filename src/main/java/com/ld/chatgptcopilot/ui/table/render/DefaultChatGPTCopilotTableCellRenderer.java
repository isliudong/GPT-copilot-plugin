package com.ld.chatgptcopilot.ui.table.render;


import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getBgRowColor;
import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getFgRowColor;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultChatGPTCopilotTableCellRenderer extends DefaultTableCellRenderer {
    public static final DefaultChatGPTCopilotTableCellRenderer DEFAULT_CELL_RENDERER = new DefaultChatGPTCopilotTableCellRenderer();


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        setBackground(getBgRowColor(isSelected));
        setForeground(getFgRowColor(isSelected));

        return this;
    }
}
