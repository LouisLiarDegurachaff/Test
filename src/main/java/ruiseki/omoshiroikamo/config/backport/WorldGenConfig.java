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
}
