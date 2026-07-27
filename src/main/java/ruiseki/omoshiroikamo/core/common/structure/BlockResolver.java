package ruiseki.omoshiroikamo.core.common.structure;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementChain;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Utility for resolving Block objects from block ID strings
 * and creating StructureLib elements dynamically.
 */
public class BlockResolver {

    /**
     * Resolve a block and metadata from a "mod:block:meta" string.
     *
     * @param blockId identifier such as "minecraft:iron_block:0"
     * @return resolved result, or null on failure
     */
    public static ResolvedBlock resolve(String blockId) {
        if (blockId == null || blockId.isEmpty()) {
            return null;
        }

        // Special handling for "air"
        if ("air".equalsIgnoreCase(blockId)) {
            return new ResolvedBlock(null, 0, false, true, false);
        }

        // Special handling for "any" placeholder
        if ("any".equalsIgnoreCase(blockId)) {
            return new ResolvedBlock(null, 0, false, false, true);
        }

        String remapped = BlockCompat.remapRemovedBlocks(blockId);
        if (remapped != null) {
            blockId = remapped;
        }

        String[] parts = blockId.split(":");
        if (parts.length < 2) {
            return null;
        }

        String modId = parts[0];
        String blockName = parts[1];
        int meta = 0;
        boolean anyMeta = false;

        if (parts.length >= 3) {
            if ("*".equals(parts[2])) {
                anyMeta = true;
            } else {
                try {
                    meta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        Block block = GameRegistry.findBlock(modId, blockName);
        if (block == null) {
            Logger.warn("BlockResolver: Block not found - " + modId + ":" + blockName);
            return null;
        }

        return new ResolvedBlock(block, meta, anyMeta, false, false);
    }

    /**
     * Create a StructureLib element for a single block string.
     *
     * @param blockString Format: "mod:blockId:meta" or "mod:blockId:*"
     * @return IStructureElement or null if invalid
     */
    public static <T> IStructureElement<T> createElement(String blockString) {
        ResolvedBlock result = resolve(blockString);
        if (result == null) {
            return null;
        }

        if (result.isAir) {
            IStructureElement<T> airElement = isAir();
            return withTracking(new HybridStructureElement<>(airElement, airElement));
        }

        if (result.isAny) {
            IStructureElement<T> anyElement = new IStructureElement<T>() {

                @Override
                public boolean check(T t, World world, int x, int y, int z) {
                    return true;
                }

                @Override
                public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
                    return true;
                }

                @Override
                public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
                    return true;
                }
            };
            return withTracking(new HybridStructureElement<>(anyElement, anyElement));
        }

        if (result.anyMeta) {
            // For wildcards:
            // - Logic: check any meta
            // - Visual: display as meta 0
            IStructureElement<T> logic = ofBlockAnyMeta(result.block, 0);
            IStructureElement<T> visual = ofBlock(result.block, 0);
            return withTracking(new HybridStructureElement<>(logic, visual));
        } else {
            // For specific meta:
            // Use HybridStructureElement to ensure NEI's StructureHacks processes via
            // fallbacks() path, which correctly applies tinting.
            IStructureElement<T> element = ofBlock(result.block, result.meta);
            return withTracking(new HybridStructureElement<>(element, element));
        }
    }

    /**
     * Create a chain element from multiple block strings.
     * This allows any of the specified blocks to be valid at this position.
     *
     * @param blockStrings List of block strings
     * @return IStructureElement using ofChain, or null if all invalid
     */
    @SuppressWarnings("unchecked")
    public static <T> IStructureElement<T> createChainElement(List<String> blockStrings) {
        if (blockStrings == null || blockStrings.isEmpty()) {
            return null;
        }

        List<IStructureElement<T>> elements = new ArrayList<>();
        for (String blockString : blockStrings) {
            IStructureElement<T> element = createElement(blockString);
            if (element != null) {
                elements.add(element);
            }
        }

        if (elements.isEmpty()) {
            return null;
        }

        if (elements.size() == 1) {
            return elements.get(0);
        }

        return ofChain(elements.toArray(new IStructureElement[0]));
    }

    /**
     * Wrap an element to track its position in the controller on success.
     */
    private static <T> IStructureElement<T> withTracking(IStructureElement<T> element) {
        return new TrackingStructureElement<>(element);
    }

    /**
     * A hybrid element that separates logic from visuals.
     * Used to fix NEI rendering issues where wildcards don't get tinted correctly.
     */
    private static class HybridStructureElement<T> implements IStructureElementChain<T> {

        private final IStructureElement<T> logicElement;
        private final IStructureElement<T> visualElement;

        public HybridStructureElement(IStructureElement<T> logic, IStructureElement<T> visual) {
            this.logicElement = logic;
            this.visualElement = visual;
        }

        @Override
        public boolean check(T t, World world, int x, int y, int z) {
            return logicElement.check(t, world, x, y, z);
        }

        @Override
        public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
            return visualElement.spawnHint(t, world, x, y, z, trigger);
        }

        @Override
        public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
            return visualElement.placeBlock(t, world, x, y, z, trigger);
        }

        @Override
        public BlocksToPlace getBlocksToPlace(T t, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            return visualElement.getBlocksToPlace(t, world, x, y, z, trigger, env);
        }

        @Override
        @SuppressWarnings("unchecked")
        public IStructureElement<T>[] fallbacks() {
            // Expose visual element to NEI
            return new IStructureElement[] { visualElement };
        }
    }

    private static class TrackingStructureElement<T> implements IStructureElementChain<T> {

        private final IStructureElement<T> wrappedElement;

        public TrackingStructureElement(IStructureElement<T> wrapped) {
            this.wrappedElement = wrapped;
        }

        @Override
        public boolean check(T t, World world, int x, int y, int z) {
            return wrappedElement.check(t, world, x, y, z);
        }

        @Override
        public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
            return wrappedElement.spawnHint(t, world, x, y, z, trigger);
        }

        @Override
        public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
            return wrappedElement.placeBlock(t, world, x, y, z, trigger);
        }

        @Override
        public BlocksToPlace getBlocksToPlace(T t, World world, int x, int y, int z, ItemStack trigger,
            AutoPlaceEnvironment env) {
            return wrappedElement.getBlocksToPlace(t, world, x, y, z, trigger, env);
        }

        @Override
        @SuppressWarnings("unchecked")
        public IStructureElement<T>[] fallbacks() {
            // Expose the wrapped element to NEI's StructureHacks via the chain interface
            return new IStructureElement[] { wrappedElement };
        }
    }

    /**
     * Resolved block information.
     */
    public static class ResolvedBlock {

        public final Block block;
        public final int meta;
        public final boolean anyMeta;
        public final boolean isAir;
        public final boolean isAny;

        public ResolvedBlock(Block block, int meta, boolean anyMeta, boolean isAir, boolean isAny) {
            this.block = block;
            this.meta = meta;
            this.anyMeta = anyMeta;
            this.isAir = isAir;
            this.isAny = isAny;
        }
    }
}
