package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main MultiBlock WorldGen Settings")
@Config.LangKey(Reference.CONFIG + "worldGenConfig")
@Config(modid = Reference.MOD_ID, category = "worldGen", configSubDirectory = Reference.MOD_ID, filename = "worldgen")
public class WorldGenConfig {

    @Config.Comment("Hardened Stone generation settings")
    @Config.LangKey(Reference.CONFIG + "hardenedStoneGen")
    public static final StoneGenSettings hardenedStone = new StoneGenSettings(true, 15, 6, 0, 6);

    @Config.Comment("Alabaster generation settings")
    @Config.LangKey(Reference.CONFIG + "alabasterGen")
    public static final StoneGenSettings alabaster = new StoneGenSettings(true, 30, 22, 40, 200);

    @Config.Comment("Basalt generation settings")
    @Config.LangKey(Reference.CONFIG + "basaltGen")
    public static final StoneGenSettings basalt = new StoneGenSettings(true, 28, 14, 8, 32);

    public static class StoneGenSettings {

        @Config.Comment("Enable generation")
        @Config.DefaultBoolean(true)
        public boolean enable;

        @Config.Comment("Size of each node (blocks)")
        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0)
        public int nodeSize;

        @Config.Comment("Number of nodes per chunk")
        @Config.DefaultInt(12)
        @Config.RangeInt(min = 0)
        public int nodes;

        @Config.Comment("Minimum generation height")
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0)
        public int minHeight;

        @Config.Comment("Maximum generation height")
        @Config.DefaultInt(64)
        @Config.RangeInt(min = 0)
        public int maxHeight;

        public StoneGenSettings() {
            this(true, 30, 12, 0, 64);
        }

        public StoneGenSettings(boolean enable, int nodeSize, int nodes, int minHeight, int maxHeight) {
            this.enable = enable;
            this.nodeSize = nodeSize;
            this.nodes = nodes;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }

    @Config.Comment("Helium gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "heliumGasGen")
    public static final GasPocketGenSettings helium = new GasPocketGenSettings(true, 20, 0.01f, 10, 60);

    @Config.Comment("Chlorine gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "chlorineGasGen")
    public static final GasPocketGenSettings chlorine = new GasPocketGenSettings(true, 15, 0.01f, 5, 30);

    @Config.Comment("Fluorine gas pocket generation settings")
    @Config.LangKey(Reference.CONFIG + "fluorineGasGen")
    public static final GasPocketGenSettings fluorine = new GasPocketGenSettings(true, 10, 0.01f, 5, 20);

    public static class GasPocketGenSettings {

        @Config.Comment("Enable gas pocket generation")
        @Config.DefaultBoolean(true)
        public boolean enable;

        @Config.Comment("Size of each gas pocket (blocks)")
        @Config.DefaultInt(15)
        @Config.RangeInt(min = 0)
        public int pocketSize;

        @Config.Comment("Number of pockets per chunk")
        @Config.DefaultFloat(0.01f)
        @Config.RangeFloat(min = 0f)
        public float pocketsPerChunk;

        @Config.Comment("Minimum generation height")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0)
        public int minHeight;

        @Config.Comment("Maximum generation height")
        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0)
        public int maxHeight;

        public GasPocketGenSettings() {
            this(true, 15, 0.01f, 5, 40);
        }

        public GasPocketGenSettings(boolean enable, int pocketSize, float pocketsPerChunk, int minHeight,
            int maxHeight) {
            this.enable = enable;
            this.pocketSize = pocketSize;
            this.pocketsPerChunk = pocketsPerChunk;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }
}
