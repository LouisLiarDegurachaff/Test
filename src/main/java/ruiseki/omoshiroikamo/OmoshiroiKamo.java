package ruiseki.omoshiroikamo;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.config.ConfigException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.config.GeneralConfig;
import ruiseki.omoshiroikamo.core.CoreModule;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;
import ruiseki.omoshiroikamo.core.client.util.TextureLoader;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.command.CommandOK;
import ruiseki.omoshiroikamo.core.event.MemoryEventHandler;
import ruiseki.omoshiroikamo.core.event.UpdateNotificationHandler;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.integration.LibMods;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.integration.waila.WailaCompat;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.core.update.UpdateChecker;
import ruiseki.omoshiroikamo.integration.nei.NEIConfig;
import ruiseki.omoshiroikamo.module.chickens.ChickensModule;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.cows.CowsModule;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;
import ruiseki.omoshiroikamo.module.dml.DMLModule;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;
import ruiseki.omoshiroikamo.module.ids.IDsModule;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockModule;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    version = Reference.VERSION,
    dependencies = Reference.DEPENDENCIES,
    guiFactory = Reference.GUI_FACTORY)
public class OmoshiroiKamo extends ModBase {

    static {
        try {
            GeneralConfig.registerConfig();
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @SidedProxy(serverSide = Reference.PROXY_COMMON, clientSide = Reference.PROXY_CLIENT)
    public static ICommonProxy proxy;

    @Instance(Reference.MOD_ID)
    public static OmoshiroiKamo instance;

    public OmoshiroiKamo() {
        super(Reference.MOD_ID, Reference.MOD_NAME);
        putGenericReference(REFKEY_MOD_VERSION, Reference.VERSION);
    }

    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        CapabilityManager.INSTANCE.injectCapabilities(event.getASMHarvestedData());
        registerModule(new CoreModule());
        registerModule(new ChickensModule());
        registerModule(new CowsModule());
        registerModule(new DMLModule());
        registerModule(new IDsModule());
        registerModule(new MultiBlockModule());
    }

    @Override
    protected CommandMod constructBaseCommand() {
        Map<String, ICommand> commands = Maps.newHashMap();
        CommandMod command = new CommandOK(this, commands);
        command.addAlias("ok");
        return command;
    }

    @Override
    @EventHandler
    public final void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MemoryEventHandler.INSTANCE.register();
        if (MinecraftHelpers.isClientSide()) {
            ModelRegistry.registerModid(Reference.MOD_ID);
            if (LibMods.NotEnoughItems.isLoaded()) {
                NEIConfig config = new NEIConfig();
                MinecraftForge.EVENT_BUS.register(config);
                config.loadConfig();
            }
        }
        WailaCompat.init();
        if (MinecraftHelpers.isClientSide()) {
            FMLCommonHandler.instance()
                .bus()
                .register(new UpdateNotificationHandler());
        }
    }

    @Override
    @EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        StructureCompat.postInit();
        if (MinecraftHelpers.isClientSide()) {
            TextureLoader
                .loadFromConfig(Reference.MOD_ID, Reference.MOD_NAME + " Runtime Textures", OmoshiroiKamo.class);
            UpdateChecker.checkUpdates();
        }
    }

    @Override
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Override
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    @Override
    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return null;
    }

    @Override
    @EventHandler
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public static void okLog(String message) {
        OmoshiroiKamo.instance.log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void okLog(Level level, String message) {
        OmoshiroiKamo.instance.log(level, message);
    }

    private static final Map<String, Object> REMAPS = new HashMap<>();

    static {
        REMAPS.put("omoshiroikamo:roostCollector", ChickensBlocks.ROOST_COLLECTOR.getBlock());

        REMAPS.put("omoshiroikamo:chickenCatcher", ChickensItems.CHICKEN_CATCHER.getItem());
        REMAPS.put("omoshiroikamo:liquidEgg", ChickensItems.LIQUID_EGG.getItem());
        REMAPS.put("omoshiroikamo:emptyEgg", ChickensItems.EMPTY_EGG.getItem());
        REMAPS.put("omoshiroikamo:coloredEgg", ChickensItems.COLORED_EGG.getItem());
        REMAPS.put("omoshiroikamo:chickenFood", ChickensItems.CHICKEN_FOOD.getItem());
        REMAPS.put("omoshiroikamo:solidXp", ChickensItems.SOLID_XP.getItem());
        REMAPS.put("omoshiroikamo:chickenSpawnEgg", ChickensItems.CHICKEN_SPAWN_EGG.getItem());

        REMAPS.put("omoshiroikamo:cowHalter", CowsItems.COW_HALTER.getItem());
        REMAPS.put("omoshiroikamo:cowSpawnEgg", CowsItems.COW_SPAWN_EGG.getItem());

        REMAPS.put("omoshiroikamo:lootFabricator", DMLBlocks.LOOT_FABRICATOR.getBlock());
        REMAPS.put("omoshiroikamo:simulationChamber", DMLBlocks.SIMULATION_CHAMBER.getBlock());
        REMAPS.put("omoshiroikamo:machineCasing", DMLBlocks.MACHINE_CASING.getBlock());

        REMAPS.put("omoshiroikamo:creativeModelLearner", DMLItems.CREATIVE_MODEL_LEARNER.getItem());
        REMAPS.put("omoshiroikamo:deepLearner", DMLItems.DEEP_LEARNER.getItem());
        REMAPS.put("omoshiroikamo:dataModel", DMLItems.DATA_MODEL.getItem());
        REMAPS.put("omoshiroikamo:dataModelBlank", DMLItems.DATA_MODEL_BLANK.getItem());
        REMAPS.put("omoshiroikamo:pristineMatter", DMLItems.PRISTINE_MATTER.getItem());
        REMAPS.put("omoshiroikamo:livingMatter", DMLItems.LIVING_MATTER.getItem());
        REMAPS.put("omoshiroikamo:polymerClay", DMLItems.POLYMER_CLAY.getItem());
        REMAPS.put("omoshiroikamo:sootCoveredPlate", DMLItems.SOOT_COVERED_PLATE.getItem());
        REMAPS.put("omoshiroikamo:sootCoveredRedstone", DMLItems.SOOT_COVERED_REDSTONE.getItem());

        REMAPS.put("omoshiroikamo:menrilSapling", IDsBlocks.MENRIL_SAPLING.getBlock());
        REMAPS.put("omoshiroikamo:menrilLog", IDsBlocks.MENRIL_LOG.getBlock());
        REMAPS.put("omoshiroikamo:menrilLeaves", IDsBlocks.MENRIL_LEAVES.getBlock());
        REMAPS.put("omoshiroikamo:menrilDoor", IDsBlocks.MENRIL_DOOR.getBlock());
        REMAPS.put("omoshiroikamo:menrilPlanks", IDsBlocks.MENRIL_PLANKS.getBlock());

        REMAPS.put("omoshiroikamo:variableCard", IDsItems.LOGIC_CARD.getItem());
        REMAPS.put("omoshiroikamo:menrilBerries", IDsItems.MENRIL_BERRIES.getItem());
        REMAPS.put("omoshiroikamo:energyInterface", IDsItems.ENERGY_INTERFACE.getItem());
        REMAPS.put("omoshiroikamo:energyFilterInterface", IDsItems.ENERGY_FILTER_INTERFACE.getItem());
        REMAPS.put("omoshiroikamo:energyImporter", IDsItems.ENERGY_IMPORTER.getItem());
        REMAPS.put("omoshiroikamo:energyExporter", IDsItems.ENERGY_EXPORTER.getItem());
        REMAPS.put("omoshiroikamo:itemInterface", IDsItems.ITEM_INTERFACE.getItem());
        REMAPS.put("omoshiroikamo:itemFilterInterface", IDsItems.ITEM_FILTER_INTERFACE.getItem());
        REMAPS.put("omoshiroikamo:itemImporter", IDsItems.ITEM_IMPORTER.getItem());
        REMAPS.put("omoshiroikamo:itemExporter", IDsItems.ITEM_EXPORTER.getItem());
        REMAPS.put("omoshiroikamo:redstoneReader", IDsItems.REDSTONE_READER.getItem());
        REMAPS.put("omoshiroikamo:redstoneWriter", IDsItems.REDSTONE_WRITER.getItem());
        REMAPS.put("omoshiroikamo:blockReader", IDsItems.BLOCK_READER.getItem());
        REMAPS.put("omoshiroikamo:fluidReader", IDsItems.FLUID_READER.getItem());
        REMAPS.put("omoshiroikamo:inventoryReader", IDsItems.INVENTORY_READER.getItem());
        REMAPS.put("omoshiroikamo:storageTerminal", IDsItems.STORAGE_TERMINAL.getItem());

        REMAPS.put("omoshiroikamo:blockCrystal", MultiBlockBlocks.BLOCK_CRYSTAL.getBlock());
        REMAPS.put("omoshiroikamo:hardenedStone", MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock());
        REMAPS.put("omoshiroikamo:machineBase", MultiBlockBlocks.MACHINE_BASE.getBlock());
        REMAPS.put("omoshiroikamo:basaltStructure", MultiBlockBlocks.BASALT_STRUCTURE.getBlock());
        REMAPS.put("omoshiroikamo:hardenedStructure", MultiBlockBlocks.HARDENED_STRUCTURE.getBlock());
        REMAPS.put("omoshiroikamo:alabasterStructure", MultiBlockBlocks.ALABASTER_STRUCTURE.getBlock());
        REMAPS.put("omoshiroikamo:coloredLens", MultiBlockBlocks.COLORED_LENS.getBlock());
        REMAPS.put("omoshiroikamo:solarCell", MultiBlockBlocks.SOLAR_CELL.getBlock());
        REMAPS.put("omoshiroikamo:solarArray", MultiBlockBlocks.SOLAR_ARRAY.getBlock());
        REMAPS.put("omoshiroikamo:laserCore", MultiBlockBlocks.LASER_CORE.getBlock());
        REMAPS.put("omoshiroikamo:quantumOreExtractor", MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock());
        REMAPS.put("omoshiroikamo:quantumResExtractor", MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock());
        REMAPS.put("omoshiroikamo:quantumBeacon", MultiBlockBlocks.QUANTUM_BEACON.getBlock());
        REMAPS.put("omoshiroikamo:modifierNull", MultiBlockBlocks.MODIFIER_NULL.getBlock());
        REMAPS.put("omoshiroikamo:modifierAccuracy", MultiBlockBlocks.MODIFIER_ACCURACY.getBlock());
        REMAPS.put("omoshiroikamo:modifierPiezo", MultiBlockBlocks.MODIFIER_PIEZO.getBlock());
        REMAPS.put("omoshiroikamo:modifierSpeed", MultiBlockBlocks.MODIFIER_SPEED.getBlock());
        REMAPS.put("omoshiroikamo:modifierFlight", MultiBlockBlocks.MODIFIER_FLIGHT.getBlock());
        REMAPS.put("omoshiroikamo:modifierNightVision", MultiBlockBlocks.MODIFIER_NIGHT_VISION.getBlock());
        REMAPS.put("omoshiroikamo:modifierWaterBreathing", MultiBlockBlocks.MODIFIER_WATER_BREATHING.getBlock());
        REMAPS.put("omoshiroikamo:modifierStrength", MultiBlockBlocks.MODIFIER_STRENGTH.getBlock());
        REMAPS.put("omoshiroikamo:modifierHaste", MultiBlockBlocks.MODIFIER_HASTE.getBlock());
        REMAPS.put("omoshiroikamo:modifierRegeneration", MultiBlockBlocks.MODIFIER_REGENERATION.getBlock());
        REMAPS.put("omoshiroikamo:modifierSaturation", MultiBlockBlocks.MODIFIER_SATURATION.getBlock());
        REMAPS.put("omoshiroikamo:modifierResistance", MultiBlockBlocks.MODIFIER_RESISTANCE.getBlock());
        REMAPS.put("omoshiroikamo:modifierJumpBoost", MultiBlockBlocks.MODIFIER_JUMP_BOOST.getBlock());
        REMAPS.put("omoshiroikamo:modifierFireResistance", MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getBlock());
        REMAPS.put("omoshiroikamo:modifierLuck", MultiBlockBlocks.MODIFIER_LUCK.getBlock());

        REMAPS.put("omoshiroikamo:stabilizedEnderPear", MultiBlockItems.STABILIZED_ENDER_PEAR.getItem());
        REMAPS.put("omoshiroikamo:photovoltaicCell", MultiBlockItems.PHOTOVOLTAIC_CELL.getItem());

    }

    @Mod.EventHandler
    public void missingMappings(FMLMissingMappingsEvent event) {
        for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {

            okLog("Missing mapping detected: " + mapping.name + " type=" + mapping.type);

            Object target = REMAPS.get(mapping.name);
            if (target == null) {
                okLog("No remap found for: " + mapping.name);
                continue;
            }

            if (mapping.type == GameRegistry.Type.BLOCK && target instanceof Block) {
                okLog("Remapping BLOCK " + mapping.name + " -> " + target);
                mapping.remap((Block) target);
            }

            if (mapping.type == GameRegistry.Type.ITEM && target instanceof Block) {
                Item item = Item.getItemFromBlock((Block) target);
                okLog("Remapping ITEM " + mapping.name + " -> " + item);
                mapping.remap(item);
            }

            if (mapping.type == GameRegistry.Type.ITEM && target instanceof Item) {
                okLog("Remapping ITEM " + mapping.name + " -> " + target);
                mapping.remap((Item) target);
            }
        }
    }
}
