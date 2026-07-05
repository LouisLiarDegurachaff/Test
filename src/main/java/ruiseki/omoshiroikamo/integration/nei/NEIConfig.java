package ruiseki.omoshiroikamo.integration.nei;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.event.NEIRegisterHandlerInfosEvent;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.integration.nei.IRecipeHandlerBase;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenDropsRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenLayingRecipeHandler;
import ruiseki.omoshiroikamo.module.chickens.integration.nei.ChickenThrowsRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowBreedingRecipeHandler;
import ruiseki.omoshiroikamo.module.cows.integration.nei.CowMilkingRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.integration.nei.LootFabricatorRecipeHandler;
import ruiseki.omoshiroikamo.module.dml.integration.nei.SimulationChamberRecipeHandler;
import ruiseki.omoshiroikamo.module.ids.client.gui.container.TerminalGuiContainer;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalOverlay;
import ruiseki.omoshiroikamo.module.ids.integration.nei.TerminalPositioner;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.NEIDimensionConfig;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.QuantumOreExtractorRecipeHandler;
import ruiseki.omoshiroikamo.module.multiblock.integration.nei.QuantumResExtractorRecipeHandler;

public class NEIConfig implements IConfigureNEI {

    /**
     * Register handler info for the NEI recipe tabs of each module.
     * This controls the appearance of the recipe tab in NEI.
     */
    @SubscribeEvent
    public void registerHandlerInfo(NEIRegisterHandlerInfosEvent event) {
        if (BackportConfigs.enableMultiBlock) {
            for (int i = 0; i < 6; i++) {
                String oreId = ModObject.QUANTUM_ORE_EXTRACTOR.getRegistryName() + ".tier" + i;
                event.registerHandlerInfo(
                    new HandlerInfo.Builder(oreId, Reference.MOD_NAME, Reference.MOD_ID)
                        .setDisplayStack(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i))
                        .setHeight(48)
                        .setWidth(166)
                        .build());

                String resId = ModObject.QUANTUM_RES_EXTRACTOR.getRegistryName() + ".tier" + i;
                event.registerHandlerInfo(
                    new HandlerInfo.Builder(resId, Reference.MOD_NAME, Reference.MOD_ID)
                        .setDisplayStack(MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i))
                        .setHeight(48)
                        .setWidth(166)
                        .build());
            }
        }

        if (BackportConfigs.enableChickens) {
            registerHandlerImage(event, ChickenLayingRecipeHandler.UID, "nei/chicken/laying_icon.png", 64, 6);
            registerHandlerImage(event, ChickenBreedingRecipeHandler.UID, "nei/chicken/breeding_icon.png", 64, 6);
            registerHandlerImage(event, ChickenDropsRecipeHandler.UID, "nei/chicken/drops_icon.png", 64, 6);
            registerHandlerImage(event, ChickenThrowsRecipeHandler.UID, "nei/chicken/throws_icon.png", 64, 6);
        }

        if (BackportConfigs.enableCows) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder(CowMilkingRecipeHandler.UID, Reference.MOD_NAME, Reference.MOD_ID)
                    .setDisplayStack(new ItemStack(Items.milk_bucket))
                    .setHeight(64)
                    .setWidth(166)
                    .build());
            event.registerHandlerInfo(
                new HandlerInfo.Builder(CowBreedingRecipeHandler.UID, Reference.MOD_NAME, Reference.MOD_ID)
                    .setDisplayStack(new ItemStack(Items.wheat))
                    .setHeight(64)
                    .setWidth(166)
                    .build());
        }

        if (BackportConfigs.enableDML) {
            event.registerHandlerInfo(
                new HandlerInfo.Builder(LootFabricatorRecipeHandler.UID, Reference.MOD_NAME, Reference.MOD_ID)
                    .setDisplayStack(DMLBlocks.LOOT_FABRICATOR.newItemStack())
                    .setHeight(48)
                    .setWidth(166)
                    .build());
            event.registerHandlerInfo(
                new HandlerInfo.Builder(SimulationChamberRecipeHandler.UID, Reference.MOD_NAME, Reference.MOD_ID)
                    .setDisplayStack(DMLBlocks.SIMULATION_CHAMBER.newItemStack())
                    .setHeight(48)
                    .setWidth(166)
                    .build());
        }
    }

    private void registerHandlerImage(NEIRegisterHandlerInfosEvent event, String handlerID, String iconPath, int height,
        int maxPerPage) {
        event.registerHandlerInfo(
            new HandlerInfo.Builder(handlerID, Reference.MOD_NAME, Reference.MOD_ID)
                .setDisplayImage(new ResourceLocation(Reference.PREFIX_GUI + iconPath), 0, 0, 16, 16)
                .setHeight(height)
                .setWidth(166)
                .build());
    }

    @Override
    public void loadConfig() {
        Logger.info("Loading NEIConfig: {}", getName());
        if (BackportConfigs.enableMultiBlock) {
            // TODO: Change Void Miner structure preview to Tier-based
            // buttons do not work now

            // Register Ore Extractors
            for (int i = 0; i < 6; i++) {
                QuantumOreExtractorRecipeHandler ore = new QuantumOreExtractorRecipeHandler(i);
                registerHandler(ore);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, i), ore.getRecipeID());
                registerDimensionCatalysts(ore.getRecipeID());
            }

            // Register Res Extractors
            for (int i = 0; i < 6; i++) {
                QuantumResExtractorRecipeHandler res = new QuantumResExtractorRecipeHandler(i);
                registerHandler(res);
                API.addRecipeCatalyst(MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, i), res.getRecipeID());
                registerDimensionCatalysts(res.getRecipeID());
            }
        }
        if (BackportConfigs.enableChickens) {
            registerHandler(new ChickenLayingRecipeHandler());
            API.addRecipeCatalyst(ChickensBlocks.ROOST.newItemStack(), ChickenLayingRecipeHandler.UID);

            registerHandler(new ChickenBreedingRecipeHandler());
            API.addRecipeCatalyst(ChickensBlocks.BREEDER.newItemStack(), ChickenBreedingRecipeHandler.UID);

            registerHandler(new ChickenDropsRecipeHandler());
            registerHandler(new ChickenThrowsRecipeHandler());
        }

        if (BackportConfigs.enableCows) {
            registerHandler(new CowBreedingRecipeHandler());
            registerHandler(new CowMilkingRecipeHandler());
        }

        if (BackportConfigs.enableIDs) {
            API.registerGuiOverlay(TerminalGuiContainer.class, "crafting", new TerminalPositioner());
            API.registerGuiOverlayHandler(TerminalGuiContainer.class, new TerminalOverlay(), "crafting");
        }

        if (BackportConfigs.enableDML) {
            registerHandler(new LootFabricatorRecipeHandler());
            registerHandler(new SimulationChamberRecipeHandler());
        }
    }

    protected static void registerHandler(IRecipeHandlerBase handler) {
        handler.prepare();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    private static void registerDimensionCatalysts(String recipeId) {
        for (NEIDimensionConfig.DimensionEntry dim : NEIDimensionConfig.getDimensions()) {
            ItemStack catalyst = NEIDimensionConfig.getCatalystStack(dim.id);
            if (catalyst != null) {
                API.addRecipeCatalyst(catalyst, recipeId);
            }
        }
    }

    @Override
    public String getName() {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return Reference.VERSION;
    }
}
