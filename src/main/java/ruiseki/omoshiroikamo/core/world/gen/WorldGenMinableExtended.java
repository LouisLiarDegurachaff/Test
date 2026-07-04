package ruiseki.omoshiroikamo.core.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenMinable;

/**
 * WorldGenerator for mineable blocks.
 *
 * @author rubensworks
 *
 */
public class WorldGenMinableExtended extends WorldGenMinable implements IRetroGen {

    protected int blocksPerVein;
    protected float veinsPerChunk;
    protected int startY;
    protected int endY;
    protected Block block;
    protected int meta;
    protected Block replaceTarget;

    /**
     * Make a new instance.
     *
     * @param block         Block to spawn.
     * @param meta          meta to spawn.
     * @param blocksPerVein Blocks per vein.
     * @param veinsPerChunk Veins per chunk (Accepts float for rare spawns, e.g., 0.1f).
     * @param startY        Start coordinate for Y
     * @param endY          End coordinate for Y.
     * @param replaceTarget The replace target blockState. Stone for overworld, netherrack for nether.
     */
    public WorldGenMinableExtended(Block block, int meta, int blocksPerVein, float veinsPerChunk, int startY, int endY,
        Block replaceTarget) {
        super(block, meta, blocksPerVein, replaceTarget);
        this.block = block;
        this.meta = meta;
        this.blocksPerVein = blocksPerVein;
        this.veinsPerChunk = veinsPerChunk;
        this.startY = startY;
        this.endY = endY;
        this.replaceTarget = replaceTarget;
    }

    /**
     * Helper method to determine how many veins to generate in a single chunk
     * based on the float value.
     */
    protected int getVeinCountToGenerate(Random rand) {
        int count = (int) this.veinsPerChunk;
        float remainder = this.veinsPerChunk - count;

        if (remainder > 0.0F && rand.nextFloat() < remainder) {
            count++;
        }
        return count;
    }

    /**
     * Generate the ores in a loop for the veins per chunk
     *
     * @param world  The world.
     * @param rand   Random object.
     * @param chunkX X chunk coordinate.
     * @param chunkZ Z chunk coordinate.
     */
    public void loopGenerate(World world, Random rand, int chunkX, int chunkZ) {
        int actualVeins = getVeinCountToGenerate(rand);

        for (int k = 0; k < actualVeins; k++) {
            int firstBlockXCoord = chunkX + rand.nextInt(16);
            int firstBlockYCoord = startY + rand.nextInt(endY - startY);
            int firstBlockZCoord = chunkZ + rand.nextInt(16);

            this.generate(world, rand, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        }
    }

    protected String getUniqueName() {
        return block.getUnlocalizedName();
    }

    /**
     * Generate for only one chunk (this will NOT cross chunk boundaries!)
     * Based on the generate method for World instead of Chunk.
     *
     * @param chunk The chunk to generate in.
     * @param rand  Random.
     * @param x     Chunk X.
     * @param y     Chunk Y.
     * @param z     Chunk Z.
     * @return If generation succeeded.
     */
    protected boolean generate(Chunk chunk, Random rand, int x, int y, int z) {

        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = (double) ((float) (x + 8) + MathHelper.sin(f) * (float) this.blocksPerVein / 8.0F);
        double d1 = (double) ((float) (x + 8) - MathHelper.sin(f) * (float) this.blocksPerVein / 8.0F);
        double d2 = (double) ((float) (z + 8) + MathHelper.cos(f) * (float) this.blocksPerVein / 8.0F);
        double d3 = (double) ((float) (z + 8) - MathHelper.cos(f) * (float) this.blocksPerVein / 8.0F);
        double d4 = (double) (y + rand.nextInt(3) - 2);
        double d5 = (double) (y + rand.nextInt(3) - 2);

        for (int l = 0; l <= this.blocksPerVein; ++l) {

            double d6 = d0 + (d1 - d0) * l / this.blocksPerVein;
            double d7 = d4 + (d5 - d4) * l / this.blocksPerVein;
            double d8 = d2 + (d3 - d2) * l / this.blocksPerVein;
            double d9 = rand.nextDouble() * this.blocksPerVein / 16.0D;
            double d10 = (MathHelper.sin((float) l * (float) Math.PI / this.blocksPerVein) + 1.0F) * d9 + 1.0D;
            double d11 = d10;

            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int cx = i1; cx <= l1; ++cx) {
                double dx = ((double) cx + 0.5D - d6) / (d10 / 2.0D);

                if (dx * dx < 1.0D) {
                    for (int cy = j1; cy <= i2; ++cy) {
                        double dy = ((double) cy + 0.5D - d7) / (d11 / 2.0D);

                        if (dx * dx + dy * dy < 1.0D) {
                            for (int cz = k1; cz <= j2; ++cz) {

                                if (cx >= 0 && cx < 16 && cz >= 0 && cz < 16 && cy >= 0 && cy < 256) {

                                    double dz = ((double) cz + 0.5D - d8) / (d10 / 2.0D);

                                    if (dx * dx + dy * dy + dz * dz < 1.0D) {

                                        Block oldBlock = chunk.getBlock(cx, cy, cz);
                                        int oldMeta = chunk.getBlockMetadata(cx, cy, cz);

                                        if (oldBlock != null && oldBlock.isReplaceableOreGen(
                                            chunk.worldObj,
                                            chunk.xPosition * 16 + cx,
                                            cy,
                                            chunk.zPosition * 16 + cz,
                                            replaceTarget) && !oldBlock.hasTileEntity(oldMeta)) {

                                            ExtendedBlockStorage storage = chunk.getBlockStorageArray()[cy >> 4];

                                            if (storage != null) {
                                                storage.func_150818_a(cx, cy & 15, cz, this.block);
                                                storage.setExtBlockMetadata(cx, cy & 15, cz, this.meta);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void retroGenerateChunk(NBTTagCompound tag, Chunk chunk, Random rand) {
        int actualVeins = getVeinCountToGenerate(rand);

        for (int k = 0; k < actualVeins; k++) {
            int x = rand.nextInt(16);
            int y = startY + rand.nextInt(endY - startY);
            int z = rand.nextInt(16);

            this.generate(chunk, rand, x, y, z);
        }
    }

    @Override
    public boolean shouldRetroGen(NBTTagCompound tag, int dimensionId) {
        return !tag.getBoolean(getUniqueName());
    }

    @Override
    public void afterRetroGen(NBTTagCompound tag) {
        tag.setBoolean(getUniqueName(), true);
    }
}
