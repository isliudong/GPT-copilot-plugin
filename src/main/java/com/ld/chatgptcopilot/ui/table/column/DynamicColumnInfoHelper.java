package com.ld.chatgptcopilot.ui.table.column;

import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getBgRowColor;
import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getFgRowColor;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ImmutableColumnInfo;
import com.ld.chatgptcopilot.ui.table.render.DefaultChatGPTCopilotNoteTableCellRenderer;
import com.ld.chatgptcopilot.ui.table.render.DefaultChatGPTCopilotTableCellRenderer;
import icons.ChatGPTCopilotIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicColumnInfoHelper {


    private static final String COMMEND_COLUMN = "commend";

    private static DynamicColumnInfoHelper helper;

    private DynamicColumnInfoHelper() {
    }

    @NotNull
    public static DynamicColumnInfoHelper getHelper() {
        if (helper == null) {
            helper = new DynamicColumnInfoHelper();
        }

        return helper;
    }

    @NotNull
    public ColumnInfo<String, String>[] generateColumnsInfo() {
        return new ColumnInfo[]{
                new DynamicColumnInfoHelper.TitleColumnInfo()
        };
    }


    private abstract static class ChatGPTCopilotNoteColumnInfo extends ColumnInfo<String, String> {
        ChatGPTCopilotNoteColumnInfo(@NotNull String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(String note) {
            return new DefaultChatGPTCopilotTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                    setBackground(getBgRowColor(isSelected));
                    setForeground(getFgRowColor(isSelected));
                    setIcon(ChatGPTCopilotIcons.pluginIcon);
                    return this;
                }
            };
        }
    }


    private static class TitleColumnInfo extends DynamicColumnInfoHelper.ChatGPTCopilotNoteColumnInfo {

        TitleColumnInfo() {
            super(COMMEND_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(String item) {
            return item;
        }

        @Override
        public void setValue(String s, String value) {
            super.setValue(s, value);
        }
    }


}
