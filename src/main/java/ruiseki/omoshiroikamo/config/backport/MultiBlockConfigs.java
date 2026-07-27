package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;

/**
 * Registration helper for multiblock config classes.
 */
public class MultiBlockConfigs {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(QuantumBeaconConfig.class);
        ConfigurationManager.registerConfig(QuantumExtractorConfig.class);
        ConfigurationManager.registerConfig(SolarArrayConfig.class);
    }
}
