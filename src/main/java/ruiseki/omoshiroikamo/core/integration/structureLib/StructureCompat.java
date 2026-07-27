package ruiseki.omoshiroikamo.core.integration.structureLib;

import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayStructure;

public class StructureCompat {

    public static void postInit() {
        if (BackportConfigs.enableMultiBlock) {
            SolarArrayStructure.registerStructureInfo();
            QuantumOreExtractorStructure.registerStructureInfo();
            QuantumResExtractorStructure.registerStructureInfo();
            QuantumBeaconStructure.registerStructureInfo();
        }
    }

    /**
     * Reload all structure definitions from StructureManager.
     * This re-registers the structures with StructureLib using updated shapes.
     */
    public static void reload() {
        if (BackportConfigs.enableMultiBlock) {
            SolarArrayStructure.registerStructureInfo();
            QuantumOreExtractorStructure.registerStructureInfo();
            QuantumResExtractorStructure.registerStructureInfo();
            QuantumBeaconStructure.registerStructureInfo();
        }

        Logger.info("StructureLib definitions reloaded");
    }
}
