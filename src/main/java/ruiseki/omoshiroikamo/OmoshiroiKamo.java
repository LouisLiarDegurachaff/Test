package ruiseki.omoshiroikamo;

import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;
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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
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
import ruiseki.omoshiroikamo.core.integration.nei.NEIConfig;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.integration.waila.WailaCompat;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.core.update.UpdateChecker;
import ruiseki.omoshiroikamo.module.chickens.ChickensModule;
import ruiseki.omoshiroikamo.module.cows.CowsModule;
import ruiseki.omoshiroikamo.module.dml.DMLModule;
import ruiseki.omoshiroikamo.module.machinery.MachineryModule;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockModule;

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
        registerModule(new MachineryModule());
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
}
