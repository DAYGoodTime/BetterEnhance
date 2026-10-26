package com.day.betterexperience;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultString;
import com.gtnewhorizon.gtnhlib.config.Config.Ignore;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@com.gtnewhorizon.gtnhlib.config.Config(modid = BetterGTExperience.MODID, category = "")
@Comment("")
@RequiresMcRestart
public class Config {

    public static void synchronizeConfiguration() {
        try {
            ConfigurationManager.registerConfig(Config.class);
        } catch (ConfigException exception) {
            throw new IllegalStateException("Cannot load BetterGTExperience configuration", exception);
        }
    }

    // Source-only switch: never persist it in the configuration being reset, and never reset in an obfuscated game.
    @Ignore
    public static boolean useDevelopmentDefaults = true;

    @Comment("通用配置。")
    @LangKey("betterexperience.config.general")
    public static final General general = new General();

    public static class General {

        @Comment("启动时写入日志的问候语，不影响机器机制。")
        @DefaultString("轻而易举呀")
        public String greeting;
    }
}
