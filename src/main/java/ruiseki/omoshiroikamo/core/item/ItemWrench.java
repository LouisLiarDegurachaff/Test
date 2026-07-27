package ruiseki.omoshiroikamo.core.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.item.IToolHammer;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.network.PacketToggleSide;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;

/**
 * Item Wrench - config ISidedIO.
 * TODO: Add Wrench texture
 * TODO: Add tooltip
 * TODO: Add announcement when change io
 */
public class ItemWrench extends ItemOK implements IToolHammer {

    public ItemWrench() {
        super(ModObject.WRENCH.name);
        setMaxStackSize(1);
        setTextureName("modular/wrench");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        // Right Click on Block: IO Toggle (Forward)
        if (world.getTileEntity(x, y, z) instanceof ISidedIO io) {
            if (world.isRemote) {
                ForgeDirection clicked = ForgeDirection.getOrientation(side);
                ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);
                OmoshiroiKamo.instance.getPacketHandler()
                    .sendToServer(new PacketToggleSide(io, target, false));
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(x, y, z);

        if (player.isSneaking()) {
            return false;
        }

        // Left Click on Block: IO Toggle (Backward)
        if (te instanceof ISidedIO io) {
            if (!world.isRemote) {
                MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    ForgeDirection clicked = ForgeDirection.getOrientation(mop.sideHit);
                    float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
                    float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
                    float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
                    ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);
                    io.toggleSide(target, true);
                }
            }
            return true; // Cancel breaking
        }
        return false;
    }

    public static ForgeDirection getClickedSide(ForgeDirection hitSide, float hitX, float hitY, float hitZ) {
        final float BORDER = 0.20f;

        // Determine horizontal/vertical ranges based on the face
        boolean hLeft = false, hRight = false, vTop = false, vBottom = false;

        switch (hitSide) {
            case UP:
            case DOWN:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitZ < BORDER;
                vBottom = hitZ > 1 - BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.NORTH;
                if (vBottom) return ForgeDirection.SOUTH;
                return hitSide;

            case NORTH:
            case SOUTH:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;

            case WEST:
            case EAST:
                hLeft = hitZ > 1 - BORDER;
                hRight = hitZ < BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.SOUTH;
                if (hRight) return ForgeDirection.NORTH;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;
            default:
                break;
        }

        return hitSide;
    }

    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {
        return true;
    }

    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

    }
}
