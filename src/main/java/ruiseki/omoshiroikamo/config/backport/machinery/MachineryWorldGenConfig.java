package ruiseki.omoshiroikamo.config.backport.machinery;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main Modular Machinery Gas Pocket WorldGen Settings")
@Config.LangKey(Reference.CONFIG + "machineryGasConfig")
@Config(
    modid = Reference.MOD_ID,
    category = "worldGen",
    configSubDirectory = Reference.MOD_ID + "/machinery",
    filename = "gas_worldgen")
public class MachineryWorldGenConfig {

    @Config.Comment("Helium gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "heliumGasGen")
    public static final GasPocketGenSettings helium = new GasPocketGenSettings(true, 20, 1, 10, 60);

    @Config.Comment("Chlorine gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "chlorineGasGen")
    public static final GasPocketGenSettings chlorine = new GasPocketGenSettings(true, 15, 1, 5, 30);

    @Config.Comment("Fluorine gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "fluorineGasGen")
    public static final GasPocketGenSettings fluorine = new GasPocketGenSettings(true, 10, 1, 5, 20);

    public static class GasPocketGenSettings {

        @Config.Comment("Enable gas pocket generation")
        @Config.DefaultBoolean(true)
        public boolean enable;

        @Config.Comment("Size of each gas pocket (blocks)")
        @Config.DefaultInt(15)
        @Config.RangeInt(min = 0)
        public int pocketSize;

        @Config.Comment("Number of pockets per chunk")
        @Config.DefaultInt(1)
        @Config.RangeInt(min = 0)
        public int pocketsPerChunk;

        @Config.Comment("Minimum generation height")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0)
        public int minHeight;

        @Config.Comment("Maximum generation height")
        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0)
        public int maxHeight;

        public GasPocketGenSettings() {
            this(true, 15, 1, 5, 40);
        }

        public GasPocketGenSettings(boolean enable, int pocketSize, int pocketsPerChunk, int minHeight, int maxHeight) {
            this.enable = enable;
            this.pocketSize = pocketSize;
            this.pocketsPerChunk = pocketsPerChunk;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }
}
