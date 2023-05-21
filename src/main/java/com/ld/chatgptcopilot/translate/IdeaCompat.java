package com.ld.chatgptcopilot.translate;

import com.intellij.openapi.application.ApplicationInfo;

@SuppressWarnings({"SpellCheckingInspection", "WeakerAccess", "unused"})
public final class IdeaCompat {

    public static final int BUILD_NUMBER = ApplicationInfo.getInstance().getBuild().getBaselineVersion();

    public static final class Version {
        public static final int IDEA15 = 143;
        public static final int IDEA2016_1 = 145;
        public static final int IDEA2016_2 = 162;
        public static final int IDEA2016_3 = 163;
        public static final int IDEA2017_1 = 171;
        public static final int IDEA2021_3 = 213;

        private Version() {
        }
    }

    private IdeaCompat() {
    }

}
