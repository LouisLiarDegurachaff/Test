package ruiseki.omoshiroikamo.module.multiblock.common.init;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.backport.WorldGenConfig;

public class MultiblockWorldGenerator implements IWorldGenerator {

    public static final MultiblockWorldGenerator INSTANCE = new MultiblockWorldGenerator();
    private final WorldGenMinable hardened_stone = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock(),
        WorldGenConfig.hardenedStone.nodeSize);
    private final WorldGenMinable alabaster = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_ALABASTER.getBlock(),
        WorldGenConfig.alabaster.nodeSize);
    private final WorldGenMinable basalt = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_BASALT.getBlock(),
        WorldGenConfig.basalt.nodeSize);

    public static void preInit() {
        GameRegistry.registerWorldGenerator(INSTANCE, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (world.provider instanceof WorldProviderSurface) {

            if (BackportConfigs.enableMultiBlock) {

                if (WorldGenConfig.hardenedStone.enable) {
                    this.runGeneration(
                        this.hardened_stone,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        WorldGenConfig.hardenedStone.nodes,
                        WorldGenConfig.hardenedStone.minHeight,
                        WorldGenConfig.hardenedStone.maxHeight);
                }

                if (WorldGenConfig.alabaster.enable) {
                    this.runGeneration(
                        this.alabaster,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        WorldGenConfig.alabaster.nodes,
                        WorldGenConfig.alabaster.minHeight,
                        WorldGenConfig.alabaster.maxHeight);
                }

                if (WorldGenConfig.basalt.enable) {
                    this.runGeneration(
                        this.basalt,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        WorldGenConfig.basalt.nodes,
                        WorldGenConfig.basalt.minHeight,
                        WorldGenConfig.basalt.maxHeight);
                }
            }

        }
    }

    public void runGeneration(WorldGenerator gen, World world, Random rand, int chunkX, int chunkZ, float chance,
        int minY, int maxY) {
        if (minY < 0 || maxY > 255 || minY >= maxY || chance <= 0) {
            return;
        }

        int heightDiff = maxY - minY;
        int attempts = (chance < 1 ? 1 : (int) chance);

        for (int i = 0; i < attempts; i++) {
            if (chance >= 1 || rand.nextFloat() < chance) {
                int x = (chunkX << 4) + rand.nextInt(16);
                int y = minY + rand.nextInt(heightDiff);
                int z = (chunkZ << 4) + rand.nextInt(16);
                gen.generate(world, rand, x, y, z);
            }
        }
    }

}
