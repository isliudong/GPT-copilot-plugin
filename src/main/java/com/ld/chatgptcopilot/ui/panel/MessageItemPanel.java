package com.ld.chatgptcopilot.ui.panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.intellij.ide.util.TipUIUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.ld.chatgptcopilot.model.Message;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.IdeaUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MessageItemPanel extends JBPanel {
    @Getter
    private final Message message;
    @Getter
    private final MessageListDisplayPanel messageListDisplayPanel;
    @Getter
    private MessageHtmlPanel htmlPanel;
    @Getter
    private TipUIUtil.Browser browser;

    JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


    private List<Runnable> refreshListeners = new ArrayList<>();

    public MessageItemPanel(Message message, MessageListDisplayPanel messageListDisplayPanel) {
        this.message = message;
        this.messageListDisplayPanel = messageListDisplayPanel;
        initUi();
        addComponentHover(this);
    }

    public void loading() {
        AiCopilotDetailsPanel.InputPanel inputPanel = messageListDisplayPanel.getAiCopilotChatPanel().getAiCopilotDetailsPanel().getInputPanel();
        inputPanel.setText("");
        inputPanel.button.setEnabled(false);
        inputPanel.button.setText("Sending...");
        this.add(loadingPanel);
    }

    public void removeLoading() {
        AiCopilotDetailsPanel.InputPanel inputPanel = messageListDisplayPanel.getAiCopilotChatPanel().getAiCopilotDetailsPanel().getInputPanel();
        inputPanel.button.setEnabled(true);
        inputPanel.button.setText("Send");
        this.remove(loadingPanel);
    }


    //@Override
    //public void paint(Graphics g) {
    //    int fieldX = 0;
    //    int fieldY = 0;
    //    int fieldWeight = getSize().width;
    //    int fieldHeight = getSize().height;
    //    RoundRectangle2D rect = new RoundRectangle2D.Double(fieldX, fieldY, fieldWeight, fieldHeight, 20, 20);
    //    g.setClip(rect);
    //    super.paint(g);
    //}

    //构建panel内容
    private void initUi() {
        //不自动填充父容器，刚好装下内部组件
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        if (message.isUser()) {
            //用户消息，右对齐
            this.setBorder(JBUI.Borders.empty(10, 0, 4, 0));
        } else {
            //机器人消息，左对齐
            this.setBorder(JBUI.Borders.empty(10, 0, 4, 0));
        }


        //内容部分,最大高度20，超出自动滚动

        String noteBody = message.getContent();
        //按markdown格式解析为html panel
        browser = IdeaUtil.getBrowser();
        browser.setText(IdeaUtil.md2html(noteBody));
        //使用微软雅黑高亮字体
        browser.getComponent().setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        //纯白字体，纯黑色背景
        browser.getComponent().setForeground(JBColor.foreground());
        //设置背景色,使用控制台日志背景色

        JBColor background = new JBColor(UIUtil.getTextFieldBackground(), Gray._43);
        browser.getComponent().setBackground(background);
        loadingPanel.setBackground(background);
        add(browser.getComponent());
    }

    //更新内容
    public void appendContent() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                browser.setText(IdeaUtil.md2html(this.message.getContent()));
                browser.getComponent().revalidate();
                browser.getComponent().repaint();
                browser.getComponent().updateUI();


                this.revalidate();
                this.repaint();
                this.updateUI();

                messageListDisplayPanel.revalidate();
                messageListDisplayPanel.repaint();
                messageListDisplayPanel.updateUI();

                messageListDisplayPanel.getParent().revalidate();
                messageListDisplayPanel.getParent().repaint();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            IdeaUtil.showFailedNotification(e.getMessage());
        }
        refreshListeners.forEach(Runnable::run);
    }

    // 鼠标hover监听
    private void addComponentHover(JComponent comp) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
                //下划线
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public static class MessageHtmlPanel extends HtmlPanel {

        private String message = "";

        @Override
        protected @NotNull @Nls String getBody() {
            return StringUtils.isEmpty(message) ? "" : message;
        }

        @Override
        protected @NotNull Font getBodyFont() {
            return UIUtil.getLabelFont();
        }

        public void updateMessage(String updateMessage) {
            this.message = updateMessage;
            update();
        }
    }
}
