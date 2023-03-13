package com.ld.chatgptcopilot.util;

import static java.util.Objects.isNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.ImageUtil;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.Nullable;

public class ChatGPTCopilotIconUtil {

    private static final int SMALL_ICON = 16;


    public static Icon getIcon(@Nullable String iconUrl) {
        if (StringUtil.isEmpty(iconUrl)) {
            return null;
        }

        try {
            return IconLoader.findIcon(new URL(iconUrl));
        } catch (MalformedURLException e) {
            return null;
        }
    }


    public static Icon getSmallIcon(@Nullable String iconUrl) {
        Icon icon = getIcon(iconUrl);
        if (isNull(icon)) {
            return null;
        }

        Image image = IconLoader.toImage(icon);
        BufferedImage bufferedImage = ImageUtil.toBufferedImage(image);
        BufferedImage resizeImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, SMALL_ICON);
        //设置圆角
        resizeImage = ImageUtil.createRoundedImage(resizeImage, 5);
        return new ImageIcon(resizeImage);
    }

}
