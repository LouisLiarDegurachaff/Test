package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main Backport settings")
@Config.LangKey(Reference.CONFIG + "backportConfig")
@Config(modid = Reference.MOD_ID, category = "backports", configSubDirectory = Reference.MOD_ID, filename = "backports")
public class BackportConfigs {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableChickens;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMultiBlock;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCows;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableIDs;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDML;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(BackportConfigs.class);
        MultiBlockConfigs.registerConfig();
        ConfigurationManager.registerConfig(ChickenConfig.class);
        ConfigurationManager.registerConfig(CowConfig.class);
        ConfigurationManager.registerConfig(IDsConfig.class);
        ConfigurationManager.registerConfig(DMLConfig.class);
        ConfigurationManager.registerConfig(WorldGenConfig.class);
    }
}
