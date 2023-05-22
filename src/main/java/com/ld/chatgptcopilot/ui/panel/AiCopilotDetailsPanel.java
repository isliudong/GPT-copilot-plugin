package com.ld.chatgptcopilot.ui.panel;

import static java.util.Objects.isNull;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import cn.hutool.core.thread.ThreadUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.ld.chatgptcopilot.actions.ChannelContinuousAction;
import com.ld.chatgptcopilot.actions.ClearChannelAction;
import com.ld.chatgptcopilot.actions.NewUIAction;
import com.ld.chatgptcopilot.actions.TokenCompressionAction;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.ChatGPTCopilotRequestUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AiCopilotDetailsPanel extends SimpleToolWindowPanel {

    private final Project project;
    Splitter splitter;

    final AdjustmentListener downScroller = e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum());

    @Getter
    AiCopilotChatPanel chatPanel;
    @Getter
    InputPanel inputPanel;
    JScrollPane chatScrollPane;
    public ChatChannel chatChannel;

    public AiCopilotDetailsPanel(Project project) {
        super(true);
        this.project = project;
        setEmptyContent();
        //分割面板
        splitter = new Splitter(true, 1f);
        //设置分割线的颜色
        splitter.setDividerWidth(2);
    }

    public void showChannel(@Nullable ChatChannel chatChannel, boolean newUI) {
        if (isNull(chatChannel)) {
            setEmptyContent();
            return;
        }
        this.chatChannel = chatChannel;
        //创建一个聊天面板来显示聊天信息

        chatPanel = new AiCopilotChatPanel(chatChannel, project, this, newUI);
        loadFirst(newUI);
        inputPanel = new InputPanel(chatChannel, chatPanel);

        splitter.setSecondComponent(inputPanel);
        setContent(splitter);
    }

    private void loadFirst(boolean newUI) {
        chatPanel.refreshContent(newUI);
        if (newUI) {
            splitter.setFirstComponent(chatPanel);
        } else {
            chatScrollPane = ScrollPaneFactory.createScrollPane(chatPanel);
            //禁止水平滚动
            chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            splitter.setFirstComponent(chatScrollPane);
            //滚动到底部 fixme: 一次直接滚到底部不完全
            addDownScroller();
            //移除监听器
            ThreadUtil.execute(() -> {
                ThreadUtil.sleep(200);
                removeDownScroller();
            });
        }


    }

    private void scrollToBottom(JScrollPane scrollPane) {
        if (chatScrollPane == null) {
            return;
        }
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollPane.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        };
        scrollPane.getVerticalScrollBar().addAdjustmentListener(downScroller);
    }

    //移除监听器
    public void removeDownScroller() {
        if (chatScrollPane == null) {
            return;
        }
        chatScrollPane.getVerticalScrollBar().removeAdjustmentListener(downScroller);
    }

    //添加监听器
    public void addDownScroller() {
        if (chatScrollPane == null) {
            return;
        }
        chatScrollPane.getVerticalScrollBar().addAdjustmentListener(downScroller);
    }


    public class InputPanel extends JBPanel {

        @Getter
        JBTextArea textArea = new JBTextArea();
        @Getter
        JButton button = new JButton("Send");

        String lastText = "";

        public InputPanel(@NotNull ChatChannel chatChannel, AiCopilotChatPanel chatPanel) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setMinimumSize(new Dimension(0, 100));
            //设置黑边
            setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, JBColor.WHITE));

            textArea.setLineWrap(true);
            textArea.setFont(new Font("Sans", Font.PLAIN, 14));
            textArea.setWrapStyleWord(true);
            //移除默认的下边框
            textArea.setBorder(BorderFactory.createEmptyBorder());
            //设置输入框顶部按钮
            DefaultActionGroup actionGroup = new DefaultActionGroup();
            actionGroup.add(new ChannelContinuousAction(chatChannel));
            actionGroup.add(new TokenCompressionAction(chatPanel));
            actionGroup.add(new NewUIAction(chatPanel));
            actionGroup.add(new ClearChannelAction(chatPanel));
            JComponent actionsToolbar = createActionsToolbar(actionGroup);
            add(actionsToolbar);

            JBScrollPane scrollPane = new JBScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            add(scrollPane);

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
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.addActionListener(e -> {
                String text = textArea.getText();
                if (text != null && !text.isEmpty()) {
                    Message message = new Message("user", text);
                    if (chatScrollPane != null) {
                        scrollToBottom(chatScrollPane);
                        chatScrollPane.revalidate();
                        chatScrollPane.repaint();
                    }

                    ThreadUtil.execAsync(() -> {
                        String apiToken = ChatGPTCopilotServerManager.getInstance().getAPIToken();
                        if (apiToken == null) {
                            return;
                        }
                        ChatGPTCopilotRequestUtil.postToAiAndUpdateUi(chatPanel, chatChannel, message, apiToken, () -> scrollToBottom(chatScrollPane));
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


        @NotNull
        private JComponent createActionsToolbar(DefaultActionGroup actionGroup) {
            ActionManager actionManager = ActionManager.getInstance();
            ActionToolbar toolbar = actionManager.createActionToolbar("ld.chat-gpt.toolbar", actionGroup, true);
            toolbar.setTargetComponent(this);


            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setBackground(textArea.getBackground());
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
            //GuiUtils.installVisibilityReferent(panel, toolbar.getComponent());
            JComponent toolbarComponent = toolbar.getComponent();
            toolbarComponent.setBackground(textArea.getBackground());
            panel.add(toolbarComponent);
            return panel;
        }
    }


    public void setEmptyContent() {
        setContent(ChatGPTCopilotPanelUtil.createPlaceHolderPanel("nothing here"));
    }

}
