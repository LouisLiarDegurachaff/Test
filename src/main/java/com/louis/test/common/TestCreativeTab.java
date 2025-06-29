package com.louis.test.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.louis.test.common.block.ModBlocks;
import com.louis.test.common.item.ModItems;
import com.louis.test.lib.LibMisc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TestCreativeTab extends CreativeTabs {

    public static final CreativeTabs INSTANCE = new TestCreativeTab();
    List list;

    public TestCreativeTab() {
        super(LibMisc.MOD_ID);
        setNoTitle();
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ModItems.itemOperationOrb);
    }

    @Override
    public Item getTabIconItem() {
        return getIconItemStack().getItem();
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Override
    public void displayAllReleventItems(List list) {
        this.list = list;

        addItem(ModItems.itemOperationOrb);
        addBlock(ModBlocks.blockTest);
        addBlock(ModBlocks.blockSolar);
    }

    private void addItem(Item item) {
        item.getSubItems(item, this, list);
    }

    private void addBlock(Block block) {
        ItemStack stack = new ItemStack(block);
        block.getSubBlocks(stack.getItem(), this, list);
    }

    private void addStack(ItemStack stack) {
        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTabLabel() {
        return LibMisc.MOD_ID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return LibMisc.MOD_ID;
    }
}
