package ruiseki.omoshiroikamo.api.enums;

import ruiseki.omoshiroikamo.Reference;

public enum ModObject {

    // spotless: off

    // MultiBlock
    BLOCK_CRYSTAL("block_crystal"),
    BASALT("basalt"),
    ALABASTER("alabaster"),
    HARDENED_STONE("hardened_stone"),
    MICA("mica"),
    MACHINE_BASE("machine_base"),
    BASALT_STRUCTURE("basalt_structure"),
    HARDENED_STRUCTURE("hardened_structure"),
    ALABASTER_STRUCTURE("alabaster_structure"),
    LENS("lens"),
    LASER_CORE("laser_core"),
    COLORED_LENS("colored_lens"),
    SOLAR_ARRAY("solar_array"),
    SOLAR_CELL("solar_cell"),
    QUANTUM_ORE_EXTRACTOR("quantum_ore_extractor"),
    QUANTUM_RES_EXTRACTOR("quantum_res_extractor"),
    QUANTUM_BEACON("quantum_beacon"),
    MODIFIER_NULL("modifier_null"),
    MODIFIER_ACCURACY("modifier_accuracy"),
    MODIFIER_PIEZO("modifier_piezo"),
    MODIFIER_SPEED("modifier_speed"),
    MODIFIER_FLIGHT("modifier_flight"),
    MODIFIER_NIGHT_VISION("modifier_night_vision"),
    MODIFIER_HASTE("modifier_haste"),
    MODIFIER_STRENGTH("modifier_strength"),
    MODIFIER_WATER_BREATHING("modifier_water_breathing"),
    MODIFIER_REGENERATION("modifier_regeneration"),
    MODIFIER_SATURATION("modifier_saturation"),
    MODIFIER_RESISTANCE("modifier_resistance"),
    MODIFIER_JUMP_BOOST("modifier_jump_boost"),
    MODIFIER_LUCK("modifier_luck"),
    MODIFIER_FIRE_RESISTANCE("modifier_fire_resistance"),

    CRYSTAL("crystal"),
    STABILIZED_ENDER_PEAR("stabilized_ender_pear"),
    PHOTOVOLTAIC_CELL("photovoltaic_cell"),
    ASSEMBLER("assembler"),

    STRUCTURE_WAND("structure_wand"),
    WRENCH("wrench"),

    // IDs
    CABLE("cable"),
    PROGRAMMER("programmer"),
    MENRIL_SAPLING("menril_sapling"),
    MENRIL_LOG("menril_log"),
    MENRIL_LEAVES("menril_leaves"),
    MENRIL_DOOR("menril_door"),
    MENRIL_PLANKS("menril_planks"),

    VARIABLE_CARD("variable_card"),
    MENRIL_BERRIES("menril_berries"),
    ENERGY_INTERFACE("energy_interface"),
    ENERGY_FILTER_INTERFACE("energy_filter_interface"),
    ENERGY_IMPORTER("energy_importer"),
    ENERGY_EXPORTER("energy_exporter"),
    ITEM_INTERFACE("item_interface"),
    ITEM_FILTER_INTERFACE("item_filter_interface"),
    ITEM_IMPORTER("item_importer"),
    ITEM_EXPORTER("item_exporter"),
    REDSTONE_READER("redstone_reader"),
    REDSTONE_WRITER("redstone_writer"),
    BLOCK_READER("block_reader"),
    FLUID_READER("fluid_reader"),
    INVENTORY_READER("inventory_reader"),
    STORAGE_TERMINAL("storage_terminal"),

    // Chickens
    ROOST("roost"),
    BREEDER("breeder"),
    ROOST_COLLECTOR("roost_collector"),

    CHICKEN_CATCHER("chicken_catcher"),
    CHICKEN_SPAWN_EGG("chicken_spawn_egg"),
    CHICKEN("chicken"),
    COLORED_EGG("colored_egg"),
    LIQUID_EGG("liquid_egg"),
    EMPTY_EGG("empty_egg"),
    ANALYZER("analyzer"),
    SOLID_XP("solid_xp"),
    CHICKEN_FOOD("chicken_food"),

    // Cows
    STALL("stall"),

    COW_HALTER("cow_halter"),
    COW_SPAWN_EGG("cow_spawn_egg"),

    // DML
    LOOT_FABRICATOR("loot_fabricator"),
    SIMULATION_CHAMBER("simulation_chamber"),
    MACHINE_CASING("machine_casing"),

    CREATIVE_MODEL_LEARNER("creative_model_learner"),
    DEEP_LEARNER("deep_learner"),
    DATA_MODEL("data_model"),
    DATA_MODEL_BLANK("data_model_blank"),
    PRISTINE_MATTER("pristine_matter"),
    LIVING_MATTER("living_matter"),
    POLYMER_CLAY("polymer_clay"),
    SOOT_COVERED_PLATE("soot_covered_plate"),
    SOOT_COVERED_REDSTONE("soot_covered_redstone"),;
    // spotless: on

    public final String name;

    ModObject() {
        this(null);
    }

    ModObject(String customName) {
        if (customName != null) {
            this.name = customName;
            return;
        }

        String raw = name();

        if (raw.startsWith("block")) {
            raw = raw.substring(5);
        } else if (raw.startsWith("item")) {
            raw = raw.substring(4);
        }

        this.name = Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }

    public String getRegistryName() {
        return Reference.MOD_ID + ":" + name;
    }
}
