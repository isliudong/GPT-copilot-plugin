package com.ld.chatgptcopilot.ui.panel;

import static java.util.Objects.isNull;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.ChatGPTCopilotUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AiCopilotDetailsPanel extends SimpleToolWindowPanel {
    private static final String TAB_KEY = "selectedTab";

    private final Project project;
    private final Map<String, Integer> data = new HashMap<>();

    @Getter
    AiCopilotChatPanel chatPanel;
    @Getter
    InputPanel inputPanel;
    JScrollPane chatScrollPane;

    public AiCopilotDetailsPanel(Project project) {
        super(true);
        this.project = project;
        setEmptyContent();
    }

    public void showChannel(@Nullable ChatChannel chatChannel) {
        if (isNull(chatChannel)) {
            setEmptyContent();
            return;
        }
        //创建一个聊天面板来显示聊天信息
        chatPanel = new AiCopilotChatPanel(chatChannel, project, this);
        chatScrollPane = ScrollPaneFactory.createScrollPane(chatPanel);
        //禁止水平滚动
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //滚动到底部
        scrollToBottom(chatScrollPane);
        //分割面板
        Splitter splitter = new Splitter(true, 1f);
        splitter.setFirstComponent(chatScrollPane);
        inputPanel = new InputPanel(chatChannel, chatPanel);
        splitter.setSecondComponent(inputPanel);
        setContent(splitter);
    }

    private void scrollToBottom(JScrollPane scrollPane) {
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        };
        scrollPane.getVerticalScrollBar().addAdjustmentListener(downScroller);
    }


    public class InputPanel extends JBPanel {

        JBTextArea textArea = new JBTextArea();
        String lastText = "";

        public InputPanel(@NotNull ChatChannel chatChannel, AiCopilotChatPanel chatPanel) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setMinimumSize(new Dimension(0, 100));
            //设置黑边
            setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, JBColor.WHITE));

            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            //移除默认的下边框
            textArea.setBorder(BorderFactory.createEmptyBorder());
            JBScrollPane scrollPane = new JBScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            add(scrollPane);
            JButton button = new JButton("Send");

            //按下ctrl+enter换行，按下enter发送消息
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                        textArea.append("\n");
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isControlDown()) {
                        e.consume();
                        button.doClick();
                    }
                }
            });
            button.addActionListener(e -> {
                String text = textArea.getText();
                setText("");
                if (text != null && !text.isEmpty()) {
                    Message message = new Message("user", text);
                    scrollToBottom(chatScrollPane);
                    chatScrollPane.revalidate();
                    chatScrollPane.repaint();
                    ThreadUtil.execAsync(() -> {
                        String apiToken = ChatGPTCopilotServerManager.getInstance().getAPIToken();
                        if (apiToken == null) {
                            return;
                        }
                        ChatGPTCopilotUtil.postToAiAndUpdateUi(chatPanel.messagesPanel, chatChannel, message, apiToken, () -> scrollToBottom(chatScrollPane));
                    });


                }
            });
            JBPanel buttonJBPanel = new JBPanel<>(new BorderLayout());
            buttonJBPanel.setBackground(textArea.getBackground());
            button.setBackground(textArea.getBackground());
            buttonJBPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            buttonJBPanel.add(button, BorderLayout.EAST);
            buttonJBPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
            add(buttonJBPanel);
        }

        //获取输入框的内容
        public String getText() {
            return textArea.getText();
        }

        //设置输入框的内容
        public void setText(String text) {
            lastText = textArea.getText();
            textArea.setText(text);
        }

        //上次输入的内容
        public String getLastText() {
            return lastText;
        }

        //恢复上次输入的内容
        public void restoreLastText() {
            textArea.setText(lastText);
        }
    }


    public void setEmptyContent() {
        setContent(ChatGPTCopilotPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

}
