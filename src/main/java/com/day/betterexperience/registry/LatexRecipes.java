package com.day.betterexperience.registry;

import com.day.betterexperience.api.BGEMachineList;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;

public final class LatexRecipes {

    private LatexRecipes() {}

    public static void register() {
        addRecipe(
            BGEMachineList.LatexBronze,
            OrePrefixes.plate.get(Materials.Rubber),
            OrePrefixes.pipeMedium.get(Materials.Bronze),
            ItemList.Circuit_Parts_Vacuum_Tube,
            ItemList.Hull_Bronze_Bricks);
        addRecipe(
            BGEMachineList.LatexSteel,
            OrePrefixes.plate.get(Materials.Rubber),
            OrePrefixes.pipeMedium.get(Materials.Steel),
            ItemList.Circuit_Parts_Vacuum_Tube,
            ItemList.Hull_HP_Bricks);
        addRecipe(
            BGEMachineList.LatexLV,
            OrePrefixes.plate.get(Materials.Rubber),
            ItemList.Electric_Pump_LV,
            OrePrefixes.circuit.get(Materials.LV),
            ItemList.Casing_LV);
        addRecipe(
            BGEMachineList.LatexMV,
            OrePrefixes.plateDense.get(Materials.Rubber),
            ItemList.Electric_Pump_MV,
            OrePrefixes.circuit.get(Materials.MV),
            ItemList.Casing_MV);
        addSyntheticRubberRecipes(
            BGEMachineList.LatexHV,
            ItemList.Electric_Pump_HV,
            Materials.HV,
            ItemList.Casing_HV);
        addSyntheticRubberRecipes(
            BGEMachineList.LatexEV,
            ItemList.Electric_Pump_EV,
            Materials.EV,
            ItemList.Casing_EV);
        addSyntheticRubberRecipes(
            BGEMachineList.LatexIV,
            ItemList.Electric_Pump_IV,
            Materials.IV,
            ItemList.Casing_IV);
        addRecipe(
            BGEMachineList.LatexLuV,
            OrePrefixes.plateDense.get(Materials.Polybenzimidazole),
            ItemList.Electric_Pump_LuV,
            OrePrefixes.circuit.get(Materials.LuV),
            ItemList.Casing_LuV);
        addRecipe(
            BGEMachineList.LatexZPM,
            OrePrefixes.plateDense.get(Materials.Polybenzimidazole),
            ItemList.Electric_Pump_ZPM,
            OrePrefixes.circuit.get(Materials.ZPM),
            ItemList.Casing_ZPM);
        addRecipe(
            BGEMachineList.LatexUV,
            OrePrefixes.plateDense.get(Materials.Polybenzimidazole),
            ItemList.Electric_Pump_UV,
            OrePrefixes.circuit.get(Materials.UV),
            ItemList.Casing_UV);
        addRecipe(
            BGEMachineList.LatexUHV,
            OrePrefixes.plateDense.get(Materials.Polybenzimidazole),
            ItemList.Electric_Pump_UHV,
            OrePrefixes.circuit.get(Materials.UHV),
            ItemList.Casing_MAX);
        addRecipe(
            BGEMachineList.LatexUEV,
            OrePrefixes.plateDense.get(Materials.Polybenzimidazole),
            ItemList.Electric_Pump_UEV,
            OrePrefixes.circuit.get(Materials.UEV),
            ItemList.Casing_UEV);
    }

    private static void addSyntheticRubberRecipes(BGEMachineList machine, ItemList pump, Materials circuitTier,
        ItemList casing) {
        addRecipe(
            machine,
            OrePrefixes.plateDense.get(Materials.RubberSilicone),
            pump,
            OrePrefixes.circuit.get(circuitTier),
            casing);
        addRecipe(
            machine,
            OrePrefixes.plateDense.get(Materials.StyreneButadieneRubber),
            pump,
            OrePrefixes.circuit.get(circuitTier),
            casing);
    }

    private static void addRecipe(BGEMachineList machine, Object plate, Object pump, Object circuit, ItemList casing) {
        GTModHandler.addCraftingRecipe(
            machine.get(1),
            GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS,
            new Object[] { "SPS", "CMC", "SPS", 'S', plate, 'P', pump, 'C', circuit, 'M', casing });
    }
}
