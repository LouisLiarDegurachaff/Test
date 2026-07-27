package ruiseki.omoshiroikamo.core.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.item.ItemStructureWand;
import ruiseki.omoshiroikamo.core.item.ItemWrench;

public enum CoreItems {

    // spotless: off

    STRUCTURE_WAND(new ItemStructureWand()),
    WRENCH(new ItemWrench()),

    ;
    // spotless: on

    public static final CoreItems[] VALUES = values();

    public static void preInit() {
        for (CoreItems item : VALUES) {
            try {
                GameRegistry.registerItem(item.getItem(), item.getName());
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    @Getter
    private final Item item;

    CoreItems(Item item) {
        this.item = item;
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }
}
