package com.louis.test.common.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RecipeBuilder {

    private final List<ItemStack> itemInputs = new ArrayList<>();
    private final List<ItemStack> itemOutputs = new ArrayList<>();
    private final List<FluidStack> fluidInputs = new ArrayList<>();
    private final List<FluidStack> fluidOutputs = new ArrayList<>();

    private int requiredTemperature = 0;
    private int requiredPressure = 0;
    private int energyCost = 0;
    private String uid = "";

    public RecipeBuilder addItemInput(Item item, int count) {
        itemInputs.add(new ItemStack(item, count));
        return this;
    }

    public RecipeBuilder addItemInput(Block block, int count) {
        itemInputs.add(new ItemStack(block, count));
        return this;
    }

    public List<ItemStack> getItemInputs() {
        return itemInputs;
    }

    public RecipeBuilder addFluidInput(Fluid fluid, int amount) {
        fluidInputs.add(new FluidStack(fluid, amount));
        return this;
    }

    public List<FluidStack> getFluidInputs() {
        return fluidInputs;
    }

    public RecipeBuilder addItemOutput(Item item, int count) {
        itemOutputs.add(new ItemStack(item, count));
        return this;
    }

    public RecipeBuilder addItemOutput(Block block, int count) {
        itemOutputs.add(new ItemStack(block, count));
        return this;
    }

    public List<ItemStack> getItemOutputs() {
        return itemOutputs;
    }

    public RecipeBuilder addFluidOutput(Fluid fluid, int amount) {
        fluidOutputs.add(new FluidStack(fluid, amount));
        return this;
    }

    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    public RecipeBuilder setTemperature(int kelvin) {
        this.requiredTemperature = kelvin;
        return this;
    }

    public int getTemperature() {
        return requiredTemperature;
    }

    public RecipeBuilder setPressure(int kPa) {
        this.requiredPressure = kPa;
        return this;
    }

    public int getPressure() {
        return requiredPressure;
    }

    public RecipeBuilder setEnergyCost(int energy) {
        this.energyCost = energy;
        return this;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public RecipeBuilder setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public MachineRecipe build() {
        return new MachineRecipe(
            itemInputs,
            fluidInputs,
            itemOutputs,
            fluidOutputs,
            requiredTemperature,
            requiredPressure,
            energyCost,
            uid);
    }
}
