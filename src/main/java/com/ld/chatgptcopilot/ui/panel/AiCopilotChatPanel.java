package com.ld.chatgptcopilot.ui.panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.ld.chatgptcopilot.model.ChatChannel;
import com.ld.chatgptcopilot.persistent.ChatGPTCopilotServerManager;
import com.ld.chatgptcopilot.util.ChatGPTCopilotPanelUtil;
import com.ld.chatgptcopilot.util.IdeaUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class AiCopilotChatPanel extends JBPanel {
    private Project project;
    private ChatChannel chatChannel;
    JBPanel loadingPanel = ChatGPTCopilotPanelUtil.createLoadingPanel();


    JBPanel<JBPanel> messagesPanel = new JBPanel<>();


    public AiCopilotChatPanel(ChatChannel chatChannel, Project project) {
        this.chatChannel = chatChannel;
        this.project = project;
        setContent();
    }

    private void setContent() {
        setLayout(new BorderLayout());
        load(chatChannel.getMessages());

        add(messagesPanel, BorderLayout.CENTER);
        //内容空白区域弹性填充
        //add(Box.createVerticalGlue(), BorderLayout.CENTER);
    }

    public void load(List<ChatChannel.Message> messages) {
        //消息列表
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.removeAll();
        messages.forEach(message -> {
            MessageItem item = new MessageItem(message);
            messagesPanel.add(item);
        });
    }

    public void postToAi(ChatChannel chatChannel) {
        ChatChannel data = new ChatChannel();
        BeanUtil.copyProperties(chatChannel, data);
        data.setName(null);
        HttpRequest request = HttpRequest.post("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+project.getComponent(ChatGPTCopilotServerManager.class).getAPIToken())
                .timeout(20000)
                .body(JSONUtil.toJsonStr(data));
        try (HttpResponse response = request.execute()) {
            String body = response.body();
            //反序列化 ChatChannel
            ObjectMapper mapper = new ObjectMapper();
            ChatChannel chatChannel1 = mapper.readValue(body, ChatChannel.class);
            chatChannel.getMessages().add(chatChannel1.getChoices().get(0).getMessage());
            //归纳聊天主题
            ThreadUtil.execAsync(()->summaryTitle(chatChannel));

        } catch (Exception e) {
            IdeaUtil.showFailedNotification("AI Copilot is sick："+e.getMessage());
        }
    }

    private void summaryTitle(ChatChannel chatChannel) {
        if (chatChannel.getMessages().size() == 6) {
            ChatChannel target = new ChatChannel();
            BeanUtil.copyProperties(chatChannel, target);
            ChatChannel.Message message = new ChatChannel.Message();
            message.setContent("What is the topic of our chat?");
            message.setRole("user");
            target.getMessages().add(message);
            postToAi(target);
            chatChannel.setName(target.getMessages().get(target.getMessages().size()-1).getContent());
        }
    }

    public void loading() {
        messagesPanel.add(loadingPanel);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class MessageItem extends JBPanel {
        private final ChatChannel.Message message;

        private List<Runnable> refreshListeners = new ArrayList<>();

        public MessageItem(ChatChannel.Message message) {
            this.message = message;
            initUi();
            addComponentHover(this);
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
            JComponent markdownComponent = IdeaUtil.getMarkdownComponent(noteBody);

            JTextArea contentTextArea = new JTextArea(noteBody);
            contentTextArea.setBorder(JBUI.Borders.empty());
            //字体大小
            contentTextArea.setFont(JBUI.Fonts.label(14));
            //无法编辑
            contentTextArea.setEditable(false);
            //自动滚动的文本框
            contentTextArea.setLineWrap(true);
            contentTextArea.setWrapStyleWord(true);
            add(markdownComponent);
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

}
