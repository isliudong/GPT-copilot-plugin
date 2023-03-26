/*
 * Copyright 2021 isliudong(486545887@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ld.chatgptcopilot.actions.coffee;

import java.awt.*;
import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.ld.chatgptcopilot.ui.labels.ChatGPTCopilotLinkLabel;
import org.jetbrains.annotations.Nullable;


public class SupportView extends DialogWrapper {

    private JLabel alipayLabel;
    private JLabel wechatLabel;
    private JPanel buyMeACoffeePanel;

    public SupportView() {
        super(false);
        init();
        setTitle("Thank You For Support ! Please Note Your Email or Other Information");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        createUIComponents();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(alipayLabel);
        panel.add(wechatLabel);
        panel.add(buyMeACoffeePanel);
        return panel;
    }

    private void createUIComponents() {
        ImageIcon alipayIcon = new ImageIcon(this.getClass().getResource("/pay/alipay.jpg"));
        double scale = 400d / alipayIcon.getIconHeight();
        alipayIcon.setImage(alipayIcon.getImage().getScaledInstance((int) (alipayIcon.getIconWidth() * scale), 400, Image.SCALE_DEFAULT));
        alipayLabel = new JLabel(alipayIcon);
        alipayLabel.setVisible(true);

        ImageIcon wechatIcon = new ImageIcon(this.getClass().getResource("/pay/wechat.jpg"));
        scale = 400d / wechatIcon.getIconHeight();
        wechatIcon.setImage(wechatIcon.getImage().getScaledInstance((int) (wechatIcon.getIconWidth() * scale), 400, Image.SCALE_DEFAULT));
        wechatLabel = new JLabel(wechatIcon);
        wechatLabel.setVisible(true);

        ImageIcon buymeacoffeeIcon = new ImageIcon(this.getClass().getResource("/pay/buymeacoffee.png"));
        scale = 400d / buymeacoffeeIcon.getIconHeight();
        buymeacoffeeIcon.setImage(buymeacoffeeIcon.getImage().getScaledInstance((int) (buymeacoffeeIcon.getIconWidth() * scale), 382, Image.SCALE_DEFAULT));
        buyMeACoffeePanel = new JPanel();
        buyMeACoffeePanel.setLayout(new BoxLayout(buyMeACoffeePanel, BoxLayout.Y_AXIS));
        buyMeACoffeePanel.add(new JLabel(buymeacoffeeIcon));
        JLabel url = new ChatGPTCopilotLinkLabel("buy me a coffee", "https://www.buymeacoffee.com/liudong");
        buyMeACoffeePanel.add(url);
    }
}
