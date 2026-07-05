package ruiseki.omoshiroikamo.core.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.omoshiroikamo.core.client.icon.IconProvider;
import ruiseki.omoshiroikamo.core.client.key.IKeyRegistry;
import ruiseki.omoshiroikamo.core.integration.LibMods;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.integration.nei.NEIReloadHandlers;

/**
 * Base proxy for the client side.
 *
 * @author rubensworks
 *
 */
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class ClientProxyComponent extends CommonProxyComponent implements IClientProxy {

    protected static final String SOUND_NONE = "none";

    private final CommonProxyComponent commonProxyComponent;
    private final IconProvider iconProvider;
    protected final Map<Class<? extends Entity>, Render> entityRenderers = Maps.newHashMap();
    protected final Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> tileEntityRenderers = Maps.newHashMap();

    public ClientProxyComponent(CommonProxyComponent commonProxyComponent) {
        this.commonProxyComponent = commonProxyComponent;
        this.iconProvider = constructIconProvider();
    }

    protected IconProvider constructIconProvider() {
        return new IconProvider(this);
    }

    @Override
    public void registerRenderer(Class<? extends Entity> clazz, Render renderer) {
        entityRenderers.put(clazz, renderer);
    }

    @Override
    public void registerRenderer(Class<? extends TileEntity> clazz, TileEntitySpecialRenderer renderer) {
        tileEntityRenderers.put(clazz, renderer);
    }

    @Override
    public void registerRenderers() {
        // Entity renderers
        for (Map.Entry<Class<? extends Entity>, Render> entry : entityRenderers.entrySet()) {
            RenderingRegistry.registerEntityRenderingHandler(entry.getKey(), entry.getValue());
            getMod().getLoggerHelper()
                .log(Level.TRACE, String.format("Registered %s renderer %s", entry.getKey(), entry.getValue()));
        }

        // Special TileEntity renderers
        for (Map.Entry<Class<? extends TileEntity>, TileEntitySpecialRenderer> entry : tileEntityRenderers.entrySet()) {
            ClientRegistry.bindTileEntitySpecialRenderer(entry.getKey(), entry.getValue());
            getMod().getLoggerHelper()
                .log(Level.TRACE, String.format("Registered %s special renderer %s", entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {
        getMod().getLoggerHelper()
            .log(Level.TRACE, "Registered key bindings");
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        commonProxyComponent.registerPacketHandlers(packetHandler);
        getMod().getLoggerHelper()
            .log(Level.TRACE, "Registered packet handlers");
    }

    @Override
    public void registerTickHandlers() {
        commonProxyComponent.registerTickHandlers();
        getMod().getLoggerHelper()
            .log(Level.TRACE, "Registered tick handlers");
    }

    @Override
    public void registerEventHooks() {
        commonProxyComponent.registerEventHooks();
        getMod().getLoggerHelper()
            .log(Level.TRACE, "Registered event hooks");
        FMLCommonHandler.instance()
            .bus()
            .register(getMod().getKeyRegistry());
    }

    @Override
    public void playSound(double x, double y, double z, String sound, float volume, float frequency, String mod) {
        if (!SOUND_NONE.equals(sound)) {
            ResourceLocation soundLocation = new ResourceLocation(mod, sound);
            PositionedSoundRecord record = new PositionedSoundRecord(
                soundLocation,
                volume,
                frequency,
                (float) x,
                (float) y,
                (float) z);

            // If we notice this sound is no mod sound, relay it to the default MC sound system.
            if (!mod.equals(DEFAULT_RESOURCELOCATION_MOD) && FMLClientHandler.instance()
                .getClient()
                .getSoundHandler()
                .getSound(record.getPositionedSoundLocation()) == null) {
                playSoundMinecraft(x, y, z, sound, volume, frequency);
            } else {
                FMLClientHandler.instance()
                    .getClient()
                    .getSoundHandler()
                    .playSound(record);
            }
        }
    }

    @Override
    public String getEntityTexturePath(Class<? extends Entity> clazz, Entity entity) {
        try {
            Render renderer = (Render) RenderManager.instance.getEntityClassRenderObject(clazz);
            if (renderer != null) {
                // Use reflection as a fallback, although AT should make func_110775_a public.
                // In some IDE environments, it might still show as an error until re-import.
                try {
                    Method m = Render.class.getDeclaredMethod("func_110775_a", Entity.class);
                    m.setAccessible(true);
                    ResourceLocation res = (ResourceLocation) m.invoke(renderer, entity);
                    return res != null ? res.toString() : null;
                } catch (NoSuchMethodException e) {
                    // Fallback to deobfuscated name if available
                    try {
                        Method m = Render.class.getDeclaredMethod("getEntityTexture", Entity.class);
                        m.setAccessible(true);
                        ResourceLocation res = (ResourceLocation) m.invoke(renderer, entity);
                        return res != null ? res.toString() : null;
                    } catch (NoSuchMethodException e2) {
                        // Ignore
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public void dumpTexture(File baseDir, String pathStr) {
        if (pathStr == null || pathStr.isEmpty()) return;
        try {
            ResourceLocation res = new ResourceLocation(pathStr);
            InputStream in = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(res)
                .getInputStream();

            String resourcePath = res.getResourcePath();
            // Get just the filename from the path
            String fileName = new File(resourcePath).getName();
            File target = new File(baseDir, fileName);

            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileOutputStream out = new FileOutputStream(target)) {
                IOUtils.copy(in, out);
            } finally {
                IOUtils.closeQuietly(in);
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    @Override
    public void reloadNEI() {
        if (LibMods.NotEnoughItems.isLoaded()) {
            NEIReloadHandlers.runAll();
        }
    }
}
