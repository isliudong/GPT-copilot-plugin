package com.ld.chatgptcopilot.ui.table.render;

import java.awt.*;
import javax.swing.*;

import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil;

public class DefaultChatGPTCopilotNoteTableCellRenderer extends DefaultChatGPTCopilotTableCellRenderer {

    private final ChatChannel note;

    public DefaultChatGPTCopilotNoteTableCellRenderer(ChatChannel note) {
        super();
        this.note = note;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component label = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        JBPanel panel = new JBPanel<>(new BorderLayout()).withBackground(label.getBackground());
        if (!isSelected) {
            panel.withBackground(ChatGPTCopilotLabelUtil.getBgRowColor());
        }
        //ChatGPTCopilotMergeRequestCommentsPanel.NoteItem noteItem = new ChatGPTCopilotMergeRequestCommentsPanel.NoteItem(note, commentsPanel);
        panel.setBorder(JBUI.Borders.empty(8, 6));
        //panel.add(this, LINE_START);
        //panel.add(noteItem);

        return panel;
    }


}
