package ruiseki.omoshiroikamo.core.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.network.ExtendedBuffer;
import ruiseki.omoshiroikamo.core.network.PacketCodec;
import ruiseki.omoshiroikamo.integration.nei.NEIReloadHandlers;

/**
 * Packet to trigger NEI recipe reload on the client side.
 */
public class PacketReloadNEI extends PacketCodec {

    public PacketReloadNEI() {}

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void encode(ExtendedBuffer output) {}

    @Override
    public void decode(ExtendedBuffer input) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        NEIReloadHandlers.runAll();
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}
}
