package ruiseki.omoshiroikamo.module.dml.common.command;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.module.dml.common.writer.MobDataWriter;

public class CommandMobDump extends CommandMod {

    public static final String NAME = "mobs";

    public CommandMobDump(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new CommandException(LangHelpers.localize("command.ok.dml_mobdump_usage"));
        }

        String target = args[0].toLowerCase();
        List<Class<? extends EntityLivingBase>> targetClasses = new ArrayList<>();

        if (target.equalsIgnoreCase("all")) {
            for (Object obj : EntityList.stringToClassMapping.values()) {
                if (obj instanceof Class && EntityLivingBase.class.isAssignableFrom((Class<?>) obj)) {
                    targetClasses.add((Class<? extends EntityLivingBase>) obj);
                }
            }
        } else {
            Class<?> clazz = (Class<?>) EntityList.stringToClassMapping.get(args[0]);
            if (clazz == null) {
                try {
                    int id = Integer.parseInt(args[0]);
                    clazz = (Class<?>) EntityList.IDtoClassMapping.get(id);
                } catch (NumberFormatException ignored) {}
            }

            if (clazz == null || !EntityLivingBase.class.isAssignableFrom(clazz)) {
                // Secondary check: case insensitive search in mapping
                for (Object key : EntityList.stringToClassMapping.keySet()) {
                    if (key.toString()
                        .equalsIgnoreCase(args[0])) {
                        clazz = (Class<?>) EntityList.stringToClassMapping.get(key);
                        break;
                    }
                }
            }

            if (clazz == null || !EntityLivingBase.class.isAssignableFrom(clazz)) {
                throw new CommandException(LangHelpers.localize("command.ok.dml_mobdump_not_found", args[0]));
            }
            targetClasses.add((Class<? extends EntityLivingBase>) clazz);
        }

        File baseDir = new File("dumps/OmoshiroiKamo/Mobs");
        MobDataWriter writer = new MobDataWriter(baseDir);

        int count = 0;
        World world = sender.getEntityWorld();
        for (Class<? extends EntityLivingBase> clazz : targetClasses) {
            try {
                String entityName = (String) EntityList.classToStringMapping.get(clazz);
                if (entityName == null) entityName = clazz.getSimpleName();

                // Determine ModID
                String modId = "minecraft";
                int dotIndex = entityName.indexOf('.');
                if (dotIndex != -1) {
                    modId = entityName.substring(0, dotIndex);
                }

                // Create individual directory for this mob: [ModID]/[MobName]
                String safeMobName = entityName.replaceAll("[:\\\\/*?\"<>|]", "_");
                File mobDir = new File(baseDir, modId + "/" + safeMobName);
                if (!mobDir.exists()) mobDir.mkdirs();

                MobDataWriter mobWriter = new MobDataWriter(mobDir);
                dumpMob(clazz, mobWriter, world, entityName);
                count++;
            } catch (Exception e) {
                sendColoredMessage(
                    sender,
                    EnumChatFormatting.RED,
                    "command.ok.dml_mobdump_failed",
                    clazz.getSimpleName(),
                    e.getMessage());
                Logger.error("Failed to dump " + clazz.getSimpleName(), e);
            }
        }

        sendColoredMessage(
            sender,
            EnumChatFormatting.GREEN,
            "command.ok.dml_mobdump_success",
            count,
            baseDir.getPath());
    }

    private void dumpMob(Class<? extends EntityLivingBase> clazz, MobDataWriter writer, World world, String name)
        throws Exception {
        EntityLivingBase dummy = (EntityLivingBase) clazz.getConstructor(World.class)
            .newInstance(world);

        double maxHealth = dummy.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .getBaseValue();

        String texturePath = OmoshiroiKamo.proxy.getEntityTexturePath(clazz, dummy);
        if (texturePath != null) {
            // Internal texture dumper logic might need adjustment, but we pass the specific
            // mobDir here
            OmoshiroiKamo.proxy.dumpTexture(writer.getBaseDir(), texturePath);
        }

        List<MobDataWriter.DropData> drops = new ArrayList<>();

        // Fallback or augment with simulation
        if (dummy instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) dummy;
            Set<String> seenDrops = new HashSet<>();

            // 100 times simulations to catch rare drops
            for (int i = 0; i < 100; i++) {
                try {
                    List<EntityItem> capturedDrops = new ArrayList<>();

                    Field captureField = Entity.class.getDeclaredField("capturedDrops");
                    captureField.setAccessible(true);
                    captureField.set(living, capturedDrops);

                    Field capturingField = Entity.class.getDeclaredField("captureDrops");
                    capturingField.setAccessible(true);
                    capturingField.set(living, true);

                    // dropFewItems(boolean, int)
                    // 1st arg: playerKill (true for rare drops), 2nd arg: lootingLevel (3 for max)
                    Method dropFewItems = findMethod(
                        living.getClass(),
                        new String[] { "dropFewItems", "func_70628_a" },
                        boolean.class,
                        int.class);
                    if (dropFewItems != null) {
                        dropFewItems.setAccessible(true);
                        dropFewItems.invoke(living, true, 3);
                    }

                    // dropRareDrop(int) - Catch vanilla rare drops (iron ingots, etc.)
                    // Usually called with looting level
                    Method dropRareDrop = findMethod(
                        living.getClass(),
                        new String[] { "dropRareDrop", "func_70600_l" },
                        int.class);
                    if (dropRareDrop != null) {
                        dropRareDrop.setAccessible(true);
                        dropRareDrop.invoke(living, 3);
                    }

                    // getDropItem() is basic drop, so only once is enough (but it doesn't hurt to
                    // do it in a loop)
                    if (i == 0) {
                        Method getDropItem = findMethod(
                            living.getClass(),
                            new String[] { "getDropItem", "func_146068_u" });
                        if (getDropItem != null) {
                            getDropItem.setAccessible(true);
                            Object itemObj = getDropItem.invoke(living);
                            if (itemObj instanceof Item) {
                                Item item = (Item) itemObj;
                                String itemId = Item.itemRegistry.getNameForObject(item);
                                if (seenDrops.add(itemId + ":0")) {
                                    drops.add(new MobDataWriter.DropData(itemId, 0, 1));
                                }
                            }
                        }
                    }

                    for (EntityItem entityItem : capturedDrops) {
                        ItemStack stack = entityItem.getEntityItem();
                        if (stack != null && stack.getItem() != null) {
                            String itemId = Item.itemRegistry.getNameForObject(stack.getItem());
                            int meta = stack.getItemDamage();
                            if (seenDrops.add(itemId + ":" + meta)) {
                                drops.add(new MobDataWriter.DropData(itemId, meta, 1));
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.error("Failed to capture drops for " + name + " (attempt " + i + ")", e);
                    break;
                }
            }
        }

        MobDataWriter.MobData data = new MobDataWriter.MobData(name, name, maxHealth, texturePath, drops);
        writer.dump(data);
    }

    private Method findMethod(Class<?> clazz, String[] names, Class<?>... params) {
        for (String name : names) {
            try {
                return clazz.getDeclaredMethod(name, params);
            } catch (NoSuchMethodException ignored) {
                Class<?> superClass = clazz.getSuperclass();
                while (superClass != null && superClass != Object.class) {
                    try {
                        return superClass.getDeclaredMethod(name, params);
                    } catch (NoSuchMethodException ignored2) {
                        superClass = superClass.getSuperclass();
                    }
                }
            }
        }
        return null;
    }
}
