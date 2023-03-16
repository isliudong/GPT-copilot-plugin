package com.ld.chatgptcopilot.ui.panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.accessibility.AccessibleContext;
import javax.swing.*;

import com.intellij.ide.util.TipUIUtil;
import com.intellij.notification.impl.ui.NotificationsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.FontUtil;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.webSymbols.utils.HtmlMarkdownUtils;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.IdeaUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel;
import org.intellij.plugins.markdown.ui.preview.MarkdownPreviewFileEditorProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class AiCopilotChatPanel extends JBPanel {
    private Project project;
    private ChatChannel chatChannel;
    MessagesPanel messagesPanel;

    JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


    public AiCopilotChatPanel(ChatChannel chatChannel, Project project) {
        this.chatChannel = chatChannel;
        this.project = project;
        messagesPanel = new MessagesPanel(chatChannel.getMessages(),this);
        setContent();
    }

    private void setContent() {
        setLayout(new BorderLayout());
        add(messagesPanel, BorderLayout.CENTER);
        //内容空白区域弹性填充
        //add(Box.createVerticalGlue(), BorderLayout.CENTER);
    }


    public void loading() {
        messagesPanel.add(loadingPanel);
    }

    public void loadingEnd() {
        messagesPanel.remove(loadingPanel);
    }

    public static class MessageItem extends JBPanel {
        @Getter
        private final ChatChannel.Message message;
        @Getter
        private final MessagesPanel messagesPanel;
        @Getter
        private MessageHtmlPanel htmlPanel;
        @Getter
        private TipUIUtil.Browser browser;

        JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


        private List<Runnable> refreshListeners = new ArrayList<>();

        public MessageItem(ChatChannel.Message message, MessagesPanel messagesPanel) {
            this.message = message;
            this.messagesPanel = messagesPanel;
            initUi();
            addComponentHover(this);
        }

        public void loading() {
            this.add(loadingPanel);
        }

        public void loadingEnd() {
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

            //增加顶部内边距
            this.setBorder(JBUI.Borders.empty(20, 0, 6, 0));

            //内容部分,最大高度20，超出自动滚动

            String noteBody = message.getContent();
            //按markdown格式解析为html panel
            browser = IdeaUtil.getMarkdownComponent(noteBody);
            browser.getComponent().setFont(JBUI.Fonts.smallFont());

            add(browser.getComponent());
        }

        //更新内容
        public void appendContent(String content) {
            String collect = Stream.of(message.getContent(), content).filter(Objects::nonNull).collect(Collectors.joining());
            if (StringUtils.isBlank(collect)) {
                return;
            }
            SwingUtilities.invokeLater(() -> {

                message.setContent(collect);

                browser.setText(HtmlMarkdownUtils.toHtml(collect));
                browser.getComponent().revalidate();
                browser.getComponent().repaint();
                browser.getComponent().updateUI();



                this.revalidate();
                this.repaint();
                this.updateUI();

                messagesPanel.revalidate();
                messagesPanel.repaint();
                messagesPanel.updateUI();

                messagesPanel.getParent().revalidate();
                messagesPanel.getParent().repaint();


            });
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
