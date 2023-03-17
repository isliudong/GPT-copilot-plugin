package com.ld.chatgptcopilot.ui.table.column;

import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getBgRowColor;
import static com.ld.chatgptcopilot.util.ChatGPTCopilotLabelUtil.getFgRowColor;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import com.intellij.util.ui.ColumnInfo;
import com.ld.chatgptcopilot.model.DynamicCommend;
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
    public ColumnInfo<DynamicCommend, String>[] generateColumnsInfo() {
        return new ColumnInfo[]{
                new DynamicColumnInfoHelper.TitleColumnInfo()
        };
    }


    private abstract static class ChatGPTCopilotColumnInfo extends ColumnInfo<DynamicCommend, String> {
        ChatGPTCopilotColumnInfo(@NotNull String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(DynamicCommend DynamicCommend) {
            return new DefaultChatGPTCopilotTableCellRenderer() {
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


    private static class TitleColumnInfo extends ChatGPTCopilotColumnInfo {

        TitleColumnInfo() {
            super(COMMEND_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(DynamicCommend item) {
            return item.getContent();
        }

        @Override
        public void setValue(DynamicCommend s, String value) {
            s.setContent(value);
        }
    }


}
