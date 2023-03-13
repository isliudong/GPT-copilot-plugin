package com.ld.chatgptcopilot.ui.table.column;

import javax.swing.table.TableCellRenderer;

import com.intellij.util.ui.ColumnInfo;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.ui.table.render.DefaultChatGPTCopilotNoteTableCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AiCopilotColumnInfoHelper {

    private static final String CHAT_COLUMN = "chat";

    private static AiCopilotColumnInfoHelper helper;

    private AiCopilotColumnInfoHelper() {
    }

    @NotNull
    public static AiCopilotColumnInfoHelper getHelper() {
        if (helper == null) {
            helper = new AiCopilotColumnInfoHelper();
        }

        return helper;
    }

    @NotNull
    public ColumnInfo<ChatChannel, String>[] generateColumnsInfo() {
        return new ColumnInfo[]{
                new TitleColumnInfo()
        };
    }


    private abstract static class ChatGPTCopilotNoteColumnInfo extends ColumnInfo<ChatChannel, String> {
        ChatGPTCopilotNoteColumnInfo(@NotNull String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(ChatChannel note) {
            return DefaultChatGPTCopilotNoteTableCellRenderer.DEFAULT_CELL_RENDERER;
        }
    }


    private static class TitleColumnInfo extends ChatGPTCopilotNoteColumnInfo {

        TitleColumnInfo() {
            super(CHAT_COLUMN);
        }

        @Nullable
        @Override
        public String valueOf(ChatChannel chatChannel) {
            return chatChannel.getName() == null ? "New Chat" : chatChannel.getName();
        }
    }


}
