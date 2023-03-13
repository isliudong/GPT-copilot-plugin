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
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AiCopilotDetailsPanel extends SimpleToolWindowPanel {
    private static final String TAB_KEY = "selectedTab";

    private final Project project;
    private final Map<String, Integer> data = new HashMap<>();

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
        AiCopilotChatPanel chatPanel = new AiCopilotChatPanel(chatChannel,project);
        chatScrollPane = ScrollPaneFactory.createScrollPane(chatPanel);
        //禁止水平滚动
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //滚动到底部
        scrollToBottom(chatScrollPane);
        //分割面板
        Splitter splitter = new Splitter(true, 1f);
        splitter.setFirstComponent(chatScrollPane);
        JBPanel inputPanel = getInputPanel(chatChannel, chatPanel);
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


    @NotNull
    private JBPanel getInputPanel(@NotNull ChatChannel chatChannel, AiCopilotChatPanel chatPanel) {
        JBPanel inputPanel = new JBPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setMinimumSize(new Dimension(0, 100));
        //设置黑边
        inputPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, JBColor.WHITE));

        JBTextArea textArea = new JBTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //移除默认的下边框
        textArea.setBorder(BorderFactory.createEmptyBorder());
        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        inputPanel.add(scrollPane);
        JButton button = new JButton("发送");
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    button.doClick();
                }
            }
        });
        button.addActionListener(e -> {
            String text = textArea.getText();
            if (text != null && !text.isEmpty()) {
                ChatChannel.Message message = new ChatChannel.Message("user", text);
                chatChannel.getMessages().add(message);
                chatPanel.loading();
                scrollToBottom(chatScrollPane);
                chatScrollPane.revalidate();
                chatScrollPane.repaint();
                ThreadUtil.execAsync(() -> {
                    chatPanel.postToAi(chatChannel);
                    SwingUtilities.invokeLater(() -> {
                        showChannel(chatChannel);
                        chatScrollPane.revalidate();
                    });
                });


            }
        });
        JBPanel buttonJBPanel = new JBPanel<>(new BorderLayout());
        buttonJBPanel.setBackground(textArea.getBackground());
        button.setBackground(textArea.getBackground());
        buttonJBPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        buttonJBPanel.add(button, BorderLayout.EAST);
        buttonJBPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
        inputPanel.add(buttonJBPanel);
        return inputPanel;
    }

    public void setEmptyContent() {
        setContent(ChatGPTCopilotPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

}
