package ruiseki.omoshiroikamo.core.helper;

import java.util.Arrays;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;

import com.mojang.realmsclient.gui.ChatFormatting;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.core.persist.nbt.INBTSerializable;

/**
 * A set of localization helpers.
 *
 * @author rubensworks
 */
public class LangHelpers {

    public static final int MAX_TOOLTIP_LINE_LENGTH = 25;
    private static final String KEY_ENABLED = "general." + Reference.MOD_ID + ".info.enabled";
    private static final String KEY_DISABLED = "general." + Reference.MOD_ID + ".info.disabled";

    /**
     * Localize a key that has values in language files.
     *
     * @param key    The key of the language file entry.
     * @param params The parameters of the formatting
     * @return The localized string.
     */
    public static String localize(String key, Object... params) {
        if (MinecraftHelpers.isModdedEnvironment()) {
            return StatCollector.translateToLocalFormatted(key, params);
        } else {
            return String.format("%s: %s", key, Arrays.toString(params));
        }
    }

    /**
     * Check if a key has an entry in the language files.
     * Useful for optional entries, where a missing key should not fall back to the raw key.
     *
     * @param key The key of the language file entry.
     * @return If the key can be localized.
     */
    public static boolean canLocalize(String key) {
        return MinecraftHelpers.isModdedEnvironment() && StatCollector.canTranslate(key);
    }

    /**
     * Show status info about the activation about an item to the info box.
     *
     * @param infoLines       The list to add info to.
     * @param isEnabled       If the item is enabled.
     * @param statusPrefixKey The prefix for the l10n key that will show if it is enabled,
     *                        this should be a formatted string with one parameter.
     */
    public static void addStatusInfo(List<String> infoLines, boolean isEnabled, String statusPrefixKey) {
        String autoSupply = localize(KEY_DISABLED);
        if (isEnabled) {
            autoSupply = localize(KEY_ENABLED);
        }
        infoLines.add(localize(statusPrefixKey, autoSupply));
    }

    /**
     * Localize a given entity id.
     *
     * @param entityId The unique entity name id.
     * @return The localized name.
     */
    public static String getLocalizedEntityName(String entityId) {
        return LangHelpers.localize("entity." + entityId + ".name");
    }

    /**
     * Add the optional info lines to the item tooltip.
     *
     * @param list   The list to add the lines to.
     * @param prefix The I18N key prefix, being the unlocalized name of blocks or items.
     */
    public static void addOptionalInfo(List<String> list, String prefix) {
        String key = prefix + ".info";
        if (StatCollector.canTranslate(key)) {
            if (MinecraftHelpers.isShifted()) {
                String localized = localize(key);
                list.addAll(
                    StringHelpers.splitLines(
                        localized,
                        MAX_TOOLTIP_LINE_LENGTH,
                        ChatFormatting.DARK_PURPLE.toString() + ChatFormatting.ITALIC.toString()));
            } else {
                list.add(
                    localize(
                        ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString()
                            + localize("general." + Reference.MOD_ID + ".tooltip.info")));
            }
        }
    }

    /**
     * Holder class that acts as a parameterized unlocalized string.
     * This can also take other unlocalized strings in the parameter list, and they will recursively
     * be localized when calling {@link UnlocalizedString#localize()}.
     */
    public static class UnlocalizedString implements INBTSerializable {

        private String parameterizedString;
        private Object[] parameters;

        public UnlocalizedString(String parameterizedString, Object... parameters) {
            this.parameterizedString = parameterizedString;
            this.parameters = parameters;
            for (int i = 0; i < parameters.length; i++) {
                if (!(parameters[i] instanceof UnlocalizedString || parameters[i] instanceof String)) {
                    parameters[i] = String.valueOf(parameters[i]);
                }
            }
        }

        public UnlocalizedString() {
            this.parameterizedString = null;
            this.parameters = null;
        }

        public String localize() {
            Object[] realParameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object param = parameters[i];
                if (param instanceof UnlocalizedString) {
                    realParameters[i] = ((UnlocalizedString) param).localize();
                } else {
                    realParameters[i] = param;
                }
            }
            return LangHelpers.localize(parameterizedString, realParameters);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("parameterizedString", parameterizedString);
            NBTTagList list = new NBTTagList();
            for (Object parameter : parameters) {
                if (parameter instanceof UnlocalizedString) {
                    NBTTagCompound objectTag = ((UnlocalizedString) parameter).serializeNBT();
                    objectTag.setString("type", "object");
                    list.appendTag(objectTag);
                } else {
                    NBTTagCompound stringTag = new NBTTagCompound();
                    stringTag.setTag("value", new NBTTagString((String) parameter));
                    stringTag.setString("type", "string");
                    list.appendTag(stringTag);
                }
            }
            tag.setTag("parameters", list);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound tag) {
            this.parameterizedString = tag.getString("parameterizedString");
            NBTTagList list = tag.getTagList("parameters", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
            this.parameters = new Object[list.tagCount()];
            for (int i = 0; i < this.parameters.length; i++) {
                NBTTagCompound elementTag = list.getCompoundTagAt(i);
                if ("object".equals(elementTag.getString("type"))) {
                    UnlocalizedString object = new UnlocalizedString();
                    object.deserializeNBT(elementTag);
                    this.parameters[i] = object;
                } else {
                    this.parameters[i] = elementTag.getString("value");
                }
            }
        }

    }
}
