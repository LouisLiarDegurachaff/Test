package ruiseki.omoshiroikamo.core.common.structure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Utility that scans a structure in the world and converts it to JSON.
 */
public class StructureScanner {

    // Characters used for symbols, assigned in order
    // Excludes reserved symbols: Q (Controller), _ (mandatory air)
    // Space is output for air blocks
    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Scan the specified region and save it as a JSON file.
     *
     * @param world     world instance
     * @param name      structure name
     * @param x1,       y1, z1 start coordinates
     * @param x2,       y2, z2 end coordinates
     * @param configDir configuration directory
     * @return true on success
     */
    public static ScanResult scan(World world, String name, int x1, int y1, int z1, int x2, int y2, int z2,
        File configDir) {

        // Normalize coordinates (smallest becomes the start point)
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        // Block -> symbol mapping
        Map<String, Character> blockToSymbol = new LinkedHashMap<>();
        Map<Character, String> symbolToBlock = new LinkedHashMap<>();
        int symbolIndex = 0;

        StructureEntryBuilder builder = new StructureEntryBuilder();
        builder.setName(name);

        int overflowCount = 0;

        // Scan from top to bottom to match the existing JSON format
        for (int y = maxY; y >= minY; y--) {
            List<String> rows = new ArrayList<>();

            for (int z = minZ; z <= maxZ; z++) {
                StringBuilder row = new StringBuilder();

                for (int x = minX; x <= maxX; x++) {
                    Block block = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);
                    TileEntity tile = world.getTileEntity(x, y, z);

                    // Output air blocks as spaces and skip further checks
                    String blockName = Block.blockRegistry.getNameForObject(block);
                    if (blockName == null || "minecraft:air".equals(blockName)) {
                        row.append(' ');
                        continue;
                    }

                    // Build the block ID
                    String blockId = getBlockId(block, meta);

                    // Assign a symbol
                    Character symbol = blockToSymbol.get(blockId);
                    if (symbol == null) {
                        // Skip 'Q' which is reserved
                        char assigned = SYMBOLS.charAt(symbolIndex);
                        if (assigned == 'Q') {
                            symbolIndex++;
                            if (symbolIndex >= SYMBOLS.length()) {
                                // Symbol overflow
                                row.append("{")
                                    .append(blockId)
                                    .append("}");
                                overflowCount++;
                                continue;
                            }
                            assigned = SYMBOLS.charAt(symbolIndex);
                        }

                        if (symbolIndex < SYMBOLS.length()) {
                            // Symbol can be assigned
                            symbol = SYMBOLS.charAt(symbolIndex++);
                            blockToSymbol.put(blockId, symbol);
                            symbolToBlock.put(symbol, blockId);
                            row.append(symbol);
                        } else {
                            // Symbol overflow: embed the block ID in braces
                            row.append("{")
                                .append(blockId)
                                .append("}");
                            overflowCount++;
                        }
                    } else {
                        row.append(symbol);
                    }
                }

                rows.add(row.toString());
            }

            builder.addLayer(new StructureLayer("y" + y, rows));
        }

        // Add mappings to builder
        for (Map.Entry<Character, String> entry : symbolToBlock.entrySet()) {
            builder.addMapping(entry.getKey(), new BlockMapping(entry.getKey(), entry.getValue()));
        }

        IStructureEntry entry = builder.build();
        JsonArray array = new JsonArray();
        array.add(entry.serialize());

        // Save to disk - in a 'custom' subdirectory
        File customDir = new File(configDir, "structures/custom");
        if (!customDir.exists()) {
            customDir.mkdirs();
        }

        File outputFile = new File(customDir, name + ".json");
        Gson gson = new GsonBuilder().setPrettyPrinting()
            .create();

        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(array, writer);
            Logger.info("Scanned structure saved to: " + outputFile.getName());

            String message = "Saved to " + outputFile.getName();
            if (overflowCount > 0) {
                message += " (WARNING: " + overflowCount + " blocks used direct IDs)";
                Logger.warn("Structure scan: " + overflowCount + " blocks exceeded symbol limit and used direct IDs");
            }
            return new ScanResult(true, message, outputFile);
        } catch (IOException e) {
            Logger.error("Failed to save scanned structure", e);
            return new ScanResult(false, "Failed to save: " + e.getMessage(), null);
        }
    }

    /**
     * Build the block ID string.
     * Note: air blocks are handled as spaces before reaching this method.
     */
    private static String getBlockId(Block block, int meta) {
        String blockName = Block.blockRegistry.getNameForObject(block);
        if (blockName == null) {
            return "unknown:block:0";
        }

        // Metadata 0 can be omitted
        if (meta == 0) {
            return blockName;
        }

        return blockName + ":" + meta;
    }

    /**
     * Scan result container.
     */
    public static class ScanResult {

        public final boolean success;
        public final String message;
        public final File outputFile;

        public ScanResult(boolean success, String message, File outputFile) {
            this.success = success;
            this.message = message;
            this.outputFile = outputFile;
        }
    }
}
