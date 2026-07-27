package ruiseki.omoshiroikamo.config.item;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main item settings")
@Config.LangKey(Reference.CONFIG + "itemConfig")
@Config(modid = Reference.MOD_ID, category = "general.items", configSubDirectory = Reference.MOD_ID)
public class ItemConfigs {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(ItemConfigs.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean renderPufferFish;

    @Config.DefaultBoolean(true)
    public static boolean renderBaubles;

    @Config.DefaultBoolean(true)
    public static boolean renderArmor;

    @Config.Comment("The maximum reach distance for structure wand preview rendering")
    @Config.DefaultFloat(16.0f)
    @Config.RangeFloat(min = 0.0f, max = 1024.0f)
    public static float wandPreviewReach;
}
