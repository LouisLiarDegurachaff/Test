package ruiseki.omoshiroikamo.module.machinery.common.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import ruiseki.omoshiroikamo.core.world.gen.WorldGenMinableExtended;

/**
 * WorldGenerator specialized for gas pockets.
 * Inherits from WorldGenMinableExtended for blob-like generation.
 */
public class WorldGenGasPocket extends WorldGenMinableExtended {

    /**
     * Create a new gas pocket generator.
     *
     * @param block         The gas block to generate.
     * @param blocksPerVein How many blocks in one pocket.
     * @param veinsPerChunk How many pockets per chunk.
     * @param startY        Minimum height.
     * @param endY          Maximum height.
     */
    public WorldGenGasPocket(Block block, int blocksPerVein, float veinsPerChunk, int startY, int endY) {
        super(block, 0, blocksPerVein, veinsPerChunk, startY, endY, Blocks.stone);
    }
}
