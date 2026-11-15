package com.day.betterexperience.api;

import net.minecraft.item.ItemStack;

public enum BGEMachineList {

    BetterTSTNetherInterface,
    BetterTSTPreciseHighEnergyPhotonicQuantumMaster,
    SteamIndustrialElectrolyzer,
    LatexBronze,
    LatexSteel,
    LatexLV,
    LatexMV,
    LatexHV,
    LatexEV,
    LatexIV,
    LatexLuV,
    LatexZPM,
    LatexUV,
    LatexUHV,
    LatexUEV,
    CellDestroyerElectrolyzerLV,
    CellDestroyerElectrolyzerMV,
    CellDestroyerElectrolyzerHV,
    CellDestroyerElectrolyzerEV,
    MEOutputAssembly,
    CellDestroyerMixerBronze,
    CellDestroyerMixerSteel,
    CellDestroyerMixerLV,
    CellDestroyerMixerMV,
    CellDestroyerMixerHV,
    CellDestroyerMixerEV,
    LargeSteamHatch;

    private ItemStack stack;

    public boolean isRegistered() {
        return stack != null;
    }

    public void set(ItemStack value) {
        if (stack != null) throw new IllegalStateException("Machine already registered: " + name());
        if (value == null || value.getItem() == null || value.stackSize <= 0) {
            throw new IllegalArgumentException("Invalid machine stack: " + name());
        }
        stack = value.copy();
        stack.stackSize = 1;
    }

    public ItemStack get(int amount) {
        if (stack == null) throw new IllegalStateException("Machine not registered: " + name());
        if (amount <= 0) throw new IllegalArgumentException("Machine amount must be positive");
        ItemStack result = stack.copy();
        result.stackSize = amount;
        return result;
    }
}
