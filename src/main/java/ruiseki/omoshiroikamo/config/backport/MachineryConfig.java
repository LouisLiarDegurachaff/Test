package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Modular Machinery settings")
@Config.LangKey(Reference.CONFIG + "modularConfig")
@Config(
    modid = Reference.MOD_ID,
    category = "Modular",
    configSubDirectory = Reference.MOD_ID + "/modular",
    filename = "modular")
public class MachineryConfig {

    @Config.Comment("Default tint color for machine blocks (hex color code)")
    @Config.DefaultString("#FFFFFF")
    public static String defaultTintColor;

    @Config.Comment("Number of enabled tiers for ports and machinery (1-16)")
    @Config.DefaultInt(6)
    @Config.RangeInt(min = 1, max = 16)
    public static int enabledTierCount = 6;

    @Config.Comment("Item Input/Output Port Slots (Tier 1-16)")
    @Config.DefaultString("1,4,9,12,16,27,36,45,54,63,72,81,81,81,81,81")
    public static String itemPortSlots = "1,4,9,12,16,27,36,45,54,63,72,81,81,81,81,81";

    @Config.Comment("Fluid Input/Output Port Capacity in mB (Tier 1-16)")
    @Config.DefaultString("1000,4000,16000,64000,256000,1024000,4096000,16384000,65536000,262144000,1048576000,1048576000,1048576000,1048576000,1048576000,1048576000")
    public static String fluidPortCapacity = "1000,4000,16000,64000,256000,1024000,4096000,16384000,65536000,262144000,1048576000,1048576000,1048576000,1048576000,1048576000,1048576000";

    @Config.Comment("Gas Input/Output Port Capacity in mB (Tier 1-16)")
    @Config.DefaultString("1000,4000,16000,64000,256000,1024000,4096000,16384000,65536000,262144000,1048576000,1048576000,1048576000,1048576000,1048576000,1048576000")
    public static String gasPortCapacity = "1000,4000,16000,64000,256000,1024000,4096000,16384000,65536000,262144000,1048576000,1048576000,1048576000,1048576000,1048576000,1048576000";

    @Config.Comment("Energy Input/Output Port Capacity in RF (Tier 1-16)")
    @Config.DefaultString("2048,8192,32768,131072,524288,2097152,8388608,33554432,134217728,536870912,2147483647,2147483647,2147483647,2147483647,2147483647,2147483647")
    public static String energyPortCapacity = "2048,8192,32768,131072,524288,2097152,8388608,33554432,134217728,536870912,2147483647,2147483647,2147483647,2147483647,2147483647,2147483647";

    @Config.Comment("Energy Input/Output Port Transfer Rate in RF/t (Tier 1-16)")
    @Config.DefaultString("128,512,2048,8192,32768,131072,524288,2097152,8388608,33554432,134217728,134217728,134217728,134217728,134217728,134217728")
    public static String energyPortTransfer = "128,512,2048,8192,32768,131072,524288,2097152,8388608,33554432,134217728,134217728,134217728,134217728,134217728,134217728";

    @Config.Comment("Mana Input/Output Port Capacity (Tier 1-16)")
    @Config.DefaultString("10000,40000,160000,640000,2560000,10240000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000")
    public static String manaPortCapacity = "10000,40000,160000,640000,2560000,10240000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000,40960000";

    @Config.Comment("Mana Input/Output Port Transfer Rate (Tier 1-16)")
    @Config.DefaultString("1000,4000,16000,64000,256000,1024000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000")
    public static String manaPortTransfer = "1000,4000,16000,64000,256000,1024000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000,4096000";

    @Config.Comment("Essentia Input/Output Port Capacity per Aspect (Tier 1-16)")
    @Config.DefaultString("64,128,256,512,1024,2048,4096,8192,16384,32768,65536,65536,65536,65536,65536,65536")
    public static String essentiaPortCapacity = "64,128,256,512,1024,2048,4096,8192,16384,32768,65536,65536,65536,65536,65536,65536";

    @Config.Comment("Vis Input/Output Port Capacity per Aspect in centi-vis (Tier 1-16)")
    @Config.DefaultString("100,200,400,800,1600,3200,6400,12800,25600,51200,102400,102400,102400,102400,102400,102400")
    public static String visPortCapacity = "100,200,400,800,1600,3200,6400,12800,25600,51200,102400,102400,102400,102400,102400,102400";

    @Config.Ignore
    private static int[] _itemPortSlots;
    @Config.Ignore
    private static int[] _fluidPortCapacity;
    @Config.Ignore
    private static int[] _gasPortCapacity;
    @Config.Ignore
    private static int[] _energyPortCapacity;
    @Config.Ignore
    private static int[] _energyPortTransfer;
    @Config.Ignore
    private static int[] _manaPortCapacity;
    @Config.Ignore
    private static int[] _manaPortTransfer;
    @Config.Ignore
    private static int[] _essentiaPortCapacity;
    @Config.Ignore
    private static int[] _visPortCapacity;

    public static int[] getItemPortSlots() {
        if (_itemPortSlots == null) {
            _itemPortSlots = parseIntArray(itemPortSlots);
        }
        return _itemPortSlots;
    }

    public static int[] getFluidPortCapacity() {
        if (_fluidPortCapacity == null) {
            _fluidPortCapacity = parseIntArray(fluidPortCapacity);
        }
        return _fluidPortCapacity;
    }

    public static int[] getGasPortCapacity() {
        if (_gasPortCapacity == null) {
            _gasPortCapacity = parseIntArray(gasPortCapacity);
        }
        return _gasPortCapacity;
    }

    public static int[] getEnergyPortCapacity() {
        if (_energyPortCapacity == null) {
            _energyPortCapacity = parseIntArray(energyPortCapacity);
        }
        return _energyPortCapacity;
    }

    public static int[] getEnergyPortTransfer() {
        if (_energyPortTransfer == null) {
            _energyPortTransfer = parseIntArray(energyPortTransfer);
        }
        return _energyPortTransfer;
    }

    private static int[] parseIntArray(String str) {
        try {
            String[] split = str.split(",");
            int[] arr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                arr[i] = Integer.parseInt(split[i].trim());
            }
            return arr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static int getItemPortSlots(int tier) {
        int[] slots = getItemPortSlots();
        if (tier < 1 || tier > slots.length) return 1;
        return slots[tier - 1];
    }

    public static int getFluidPortCapacity(int tier) {
        int[] caps = getFluidPortCapacity();
        if (tier < 1 || tier > caps.length) return 1000;
        return caps[tier - 1];
    }

    public static int getGasPortCapacity(int tier) {
        int[] caps = getGasPortCapacity();
        if (tier < 1 || tier > caps.length) return 1000;
        return caps[tier - 1];
    }

    public static int getEnergyPortCapacity(int tier) {
        int[] caps = getEnergyPortCapacity();
        if (tier < 1 || tier > caps.length) return 2048;
        return caps[tier - 1];
    }

    public static int getEnergyPortTransfer(int tier) {
        int[] transfers = getEnergyPortTransfer();
        if (tier < 1 || tier > transfers.length) return 128;
        return transfers[tier - 1];
    }

    public static int[] getManaPortCapacity() {
        if (_manaPortCapacity == null) {
            _manaPortCapacity = parseIntArray(manaPortCapacity);
        }
        return _manaPortCapacity;
    }

    public static int getManaPortCapacity(int tier) {
        int[] caps = getManaPortCapacity();
        if (tier < 1 || tier > caps.length) return 10000;
        return caps[tier - 1];
    }

    public static int[] getManaPortTransfer() {
        if (_manaPortTransfer == null) {
            _manaPortTransfer = parseIntArray(manaPortTransfer);
        }
        return _manaPortTransfer;
    }

    public static int getManaPortTransfer(int tier) {
        int[] transfers = getManaPortTransfer();
        if (tier < 1 || tier > transfers.length) return 1000;
        return transfers[tier - 1];
    }

    public static int[] getEssentiaPortCapacity() {
        if (_essentiaPortCapacity == null) {
            _essentiaPortCapacity = parseIntArray(essentiaPortCapacity);
        }
        return _essentiaPortCapacity;
    }

    public static int getEssentiaPortCapacity(int tier) {
        int[] caps = getEssentiaPortCapacity();
        if (tier < 1 || tier > caps.length) return 64;
        return caps[tier - 1];
    }

    public static int[] getVisPortCapacity() {
        if (_visPortCapacity == null) {
            _visPortCapacity = parseIntArray(visPortCapacity);
        }
        return _visPortCapacity;
    }

    public static int getVisPortCapacity(int tier) {
        int[] caps = getVisPortCapacity();
        if (tier < 1 || tier > caps.length) return 100;
        return caps[tier - 1];
    }

    /**
     * Parse the configured tint color string to an ARGB integer.
     *
     * @return ARGB color value with full alpha, or white (0xFFFFFFFF) on parse
     *         error
     */
    public static int getDefaultTintColorInt() {
        try {
            String hex = defaultTintColor.replace("#", "");
            return (int) Long.parseLong(hex, 16) | 0xFF000000; // Add alpha channel
        } catch (Exception e) {
            return 0xFFFFFFFF; // White fallback
        }
    }
}
