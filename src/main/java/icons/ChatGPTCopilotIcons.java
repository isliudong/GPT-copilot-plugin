package icons;

import java.net.URL;
import java.util.Optional;
import javax.swing.*;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.roots.ScalableIconComponent;
import com.intellij.util.ui.AnimatedIcon;

public interface ChatGPTCopilotIcons {

    Icon pluginIcon = IconLoader.getIcon("icons/chatGPT-copilot-logo-square.svg", ChatGPTCopilotIcons.class);
    Icon update = IconLoader.getIcon("icons/update.svg", ChatGPTCopilotIcons.class);
    Icon format = IconLoader.getIcon("icons/format.svg", ChatGPTCopilotIcons.class);
    Icon format_hover = IconLoader.getIcon("icons/format-hover.svg", ChatGPTCopilotIcons.class);
    Icon muti_comment = IconLoader.getIcon("icons/muti-comment.svg", ChatGPTCopilotIcons.class);
    Icon muti_comment_hover = IconLoader.getIcon("icons/muti-comment-hover.svg", ChatGPTCopilotIcons.class);
    ProcessIcon processIcon = new ProcessIcon();
    ImageIcon loading = new ImageIcon(getResource("icons/loading.gif"));
    ScalableIconComponent imageIconCom_loading = new ScalableIconComponent(ChatGPTCopilotIcons.loading);


    static URL getResource(String resourceName) {
        return Optional
                .ofNullable(ChatGPTCopilotIcons.class.getClassLoader()
                        .getResource(resourceName))
                .or(() -> Optional.ofNullable(ChatGPTCopilotIcons.class.getClassLoader()
                        .getResource(resourceName.startsWith("/") ? resourceName.replaceFirst("/", "") : "/" + resourceName)))
                .orElseThrow();
    }


    class ProcessIcon extends AnimatedIcon {
        private static final int CYCLE_LENGTH = 800;

        private static final Icon[] ICONS = {
                IconLoader.getIcon("/process/fs/step_1@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_2@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_3@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_4@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_5@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_6@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_7@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_8@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_9@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_10@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_11@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_12@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_13@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_14@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_15@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_16@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_17@2x.png", ProcessIcon.class),
                IconLoader.getIcon("/process/fs/step_18@2x.png", ProcessIcon.class)
        };

        private static final Icon STEP_PASSIVE = IconLoader.getIcon("/process/fs/step_passive@2x.png", ProcessIcon.class);

        public ProcessIcon() {
            super("Querying Process", ICONS, STEP_PASSIVE, CYCLE_LENGTH);
        }
    }

    interface Gutter {
        Icon Empty = IconLoader.getIcon("/icons/1px.svg", ChatGPTCopilotIcons.class);
        Icon Comment = IconLoader.getIcon("/icons/gutter-comment.svg", ChatGPTCopilotIcons.class);
        Icon Comments = IconLoader.getIcon("/icons/gutter-comments.svg", ChatGPTCopilotIcons.class);
        Icon AddComment = IconLoader.getIcon("/icons/gutter-plus-small.svg", ChatGPTCopilotIcons.class);
        Icon WritingComment = IconLoader.getIcon("/icons/gutter-writing-comment.svg", ChatGPTCopilotIcons.class);
        Icon HasDraft = IconLoader.getIcon("/icons/edit.svg", ChatGPTCopilotIcons.class);

    }
}
