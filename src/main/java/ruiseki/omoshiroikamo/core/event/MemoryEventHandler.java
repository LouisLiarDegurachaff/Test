package ruiseki.omoshiroikamo.core.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * Handles world-related and connection-related events to manage memory and
 * caches.
 * Ensures that static caches are cleared when worlds are unloaded or when the
 * client disconnects.
 */
public class MemoryEventHandler {

    public static final MemoryEventHandler INSTANCE = new MemoryEventHandler();

    private MemoryEventHandler() {}

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    /**
     * Fired when a world is unloaded.
     * Clears the StructureTintCache for the specific dimension being unloaded.
     */
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world != null) {
            int dimId = event.world.provider.dimensionId;
            Logger.info("World unload detected for dimension {}. Clearing structure tint cache.", dimId);
            StructureTintCache.clearDimension(event.world);
        }
    }

    /**
     * Fired when the client disconnects from a server.
     * Clears all structure tint caches on the client side.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Logger.info("Client disconnected from server. Clearing all structure tint caches.");
        StructureTintCache.clearAll();
    }
}
