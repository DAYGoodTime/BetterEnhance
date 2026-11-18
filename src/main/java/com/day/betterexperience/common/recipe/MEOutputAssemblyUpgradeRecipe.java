package com.day.betterexperience.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.day.betterexperience.BetterGTExperience;
import com.day.betterexperience.api.BGEMachineList;
import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

/** Native ME cache component tiers, applied to one side without resetting the other side or pickup NBT. */
final class MEOutputAssemblyUpgradeRecipe extends ShapelessOreRecipe {

    private final boolean items;
    private final long capacity;

    private MEOutputAssemblyUpgradeRecipe(ItemStack component, boolean items, long capacity) {
        super(
            withCapacity(BGEMachineList.MEOutputAssembly.get(1), items, capacity),
            BGEMachineList.MEOutputAssembly.get(1),
            component);
        this.items = items;
        this.capacity = capacity;
    }

    static void registerRecipes() {
        int[] itemMetadata = { 35, 36, 37, 38, 57, 58, 59, 60 };
        long capacity = 8192;
        for (int i = 0; i < itemMetadata.length; i++) {
            register(
                GTModHandler.getModItem(Mods.AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, itemMetadata[i]),
                true,
                capacity);
            register(GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_part", 1, i), false, capacity * 256);
            capacity *= 4;
        }
        register(
            GTModHandler.getModItem(Mods.AppliedEnergistics2.ID, "item.ItemExtremeStorageCell.Singularity", 1),
            true,
            Long.MAX_VALUE);
        register(
            GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_storage.singularity", 1, 0),
            false,
            Long.MAX_VALUE);
    }

    private static void register(ItemStack component, boolean items, long capacity) {
        if (component == null || component.getItem() == null) {
            BetterGTExperience.LOG
                .error("Missing ME output assembly upgrade component: items={}, capacity={}", items, capacity);
            return;
        }
        // GT's shapeless NBT overwrite replaces whole top-level tags, including the item-side compound.
        GameRegistry.addRecipe(new MEOutputAssemblyUpgradeRecipe(component, items, capacity));
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        for (int i = 0; i < grid.getSizeInventory(); i++) {
            ItemStack input = grid.getStackInSlot(i);
            if (GTUtility.areStacksEqual(input, BGEMachineList.MEOutputAssembly.get(1), true)) {
                ItemStack result = input.copy();
                result.stackSize = 1;
                return withCapacity(result, items, capacity);
            }
        }
        return null;
    }

    private static ItemStack withCapacity(ItemStack stack, boolean items, long capacity) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if (items) {
            NBTTagCompound itemTag = tag.getCompoundTag(MEOutputAssembly.ITEM_OUTPUT_TAG);
            itemTag.setLong("baseCapacity", capacity);
            tag.setTag(MEOutputAssembly.ITEM_OUTPUT_TAG, itemTag);
        } else {
            tag.setLong("baseCapacity", capacity);
        }
        return stack;
    }
}
