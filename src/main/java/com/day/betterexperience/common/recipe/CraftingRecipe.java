package com.day.betterexperience.common.recipe;

import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.day.betterexperience.api.BGEMachineList;
import com.day.betterexperience.api.ModRecipeLoader;
import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputBusME;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputME;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public final class CraftingRecipe implements ModRecipeLoader {

    private static final RecipeMetadataKey<Boolean> INHERIT_OUTPUT_CAPACITIES = SimpleRecipeMetadataKey
        .create(Boolean.class, "betterexperience.me_output_assembly.inherit_output_capacities");

    @Override
    public void loadRecipes() {
        // TST machines
        if (Loader.isModLoaded("TwistSpaceTechnology")) {
            registerTSTRecipes();
        }
        // Steam Industrial Electrolyzer
        GTModHandler.addCraftingRecipe(
            BGEMachineList.SteamIndustrialElectrolyzer.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "BGB", "WTW", "BWB", 'B', ItemList.Casing_BronzePlatedBricks, 'G',
                new ItemStack(Blocks.glass), 'T', ItemList.Generator_Steam_Turbine_LV, 'W',
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.Gold, 1) });
        // Large Steam hatch
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LargeSteamHatch.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "BBB", "HPH", "BBB", 'B', OrePrefixes.plate.get(Materials.Bronze), 'H',
                GregtechItemList.Hatch_Input_Steam.get(1), 'P', OrePrefixes.pipeHuge.get(Materials.Bronze) });
        ItemStack buildCraftTank = GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1);
        ItemStack copperTank = GTModHandler.getModItem(Mods.IronTanks.ID, "copperTank", 1);
        ItemStack ironTank = GTModHandler.getModItem(Mods.IronTanks.ID, "ironTank", 1);
        ItemStack goldTank = GTModHandler.getModItem(Mods.IronTanks.ID, "goldTank", 1);

        // Cell Destroyer Mixers
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerLV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', buildCraftTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Tin, 1), 'M', ItemList.Machine_LV_Mixer, 'C',
                OrePrefixes.circuit.get(Materials.LV) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerMV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', copperTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1), 'M', ItemList.Machine_MV_Mixer, 'C',
                OrePrefixes.circuit.get(Materials.MV) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerHV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', ironTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1), 'M', ItemList.Machine_HV_Mixer, 'C',
                OrePrefixes.circuit.get(Materials.HV) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerEV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', goldTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1), 'M', ItemList.Machine_EV_Mixer,
                'C', OrePrefixes.circuit.get(Materials.EV) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerBronze.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', buildCraftTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1), 'M', ItemList.Hull_Bronze_Bricks, 'C',
                new ItemStack(Blocks.chest) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerMixerSteel.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', buildCraftTank, 'R',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1), 'M', ItemList.Hull_HP_Bricks, 'C',
                new ItemStack(Blocks.chest) });

        // Cell Destroyer Electrolyzers
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerElectrolyzerLV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TGT", "TMT", "CWC", 'T', buildCraftTank, 'G', ItemList.ReinforcedGlass, 'M',
                ItemList.Machine_LV_Electrolyzer, 'C', OrePrefixes.circuit.get(Materials.LV), 'W',
                OrePrefixes.cableGt16.get(Materials.Tin) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerElectrolyzerMV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TGT", "TMT", "CWC", 'T', copperTank, 'G', ItemList.ReinforcedGlass, 'M',
                ItemList.Machine_MV_Electrolyzer, 'C', OrePrefixes.circuit.get(Materials.MV), 'W',
                OrePrefixes.cableGt16.get(Materials.AnyCopper) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerElectrolyzerHV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TGT", "TMT", "CWC", 'T', ironTank, 'G', ItemList.ReinforcedGlass, 'M',
                ItemList.Machine_HV_Electrolyzer, 'C', OrePrefixes.circuit.get(Materials.HV), 'W',
                OrePrefixes.cableGt16.get(Materials.Gold) });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.CellDestroyerElectrolyzerEV.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TGT", "TMT", "CWC", 'T', goldTank, 'G', ItemList.ReinforcedGlass, 'M',
                ItemList.Machine_EV_Electrolyzer, 'C', OrePrefixes.circuit.get(Materials.EV), 'W',
                OrePrefixes.cableGt16.get(Materials.Aluminium) });
        registerLatexRecipes();
        registerMEOutputAssemblyRecipe();
        registerFixedDistillationTowerRecipe();
    }

    @Override
    public String name() {
        return "Crafting Recipes";
    }

    private static void registerLatexRecipes() {
        long recipeBits = GTModHandler.RecipeBits.BITS | GTModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS;

        // Steam latex machines
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexBronze.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1),
                'P', GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Bronze, 1), 'C',
                ItemList.Circuit_Parts_Vacuum_Tube, 'M', ItemList.Hull_Bronze_Bricks });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexSteel.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1),
                'P', GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Steel, 1), 'C',
                ItemList.Circuit_Parts_Vacuum_Tube, 'M', ItemList.Hull_HP_Bricks });

        // Electric latex machines
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexLV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1),
                'P', ItemList.Electric_Pump_LV, 'C', OrePrefixes.circuit.get(Materials.LV), 'M', ItemList.Casing_LV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexMV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Rubber, 1), 'P', ItemList.Electric_Pump_MV,
                'C', OrePrefixes.circuit.get(Materials.MV), 'M', ItemList.Casing_MV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexHV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.RubberSilicone, 1), 'P',
                ItemList.Electric_Pump_HV, 'C', OrePrefixes.circuit.get(Materials.HV), 'M', ItemList.Casing_HV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexHV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.StyreneButadieneRubber, 1), 'P',
                ItemList.Electric_Pump_HV, 'C', OrePrefixes.circuit.get(Materials.HV), 'M', ItemList.Casing_HV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexEV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.RubberSilicone, 1), 'P',
                ItemList.Electric_Pump_EV, 'C', OrePrefixes.circuit.get(Materials.EV), 'M', ItemList.Casing_EV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexEV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.StyreneButadieneRubber, 1), 'P',
                ItemList.Electric_Pump_EV, 'C', OrePrefixes.circuit.get(Materials.EV), 'M', ItemList.Casing_EV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexIV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.RubberSilicone, 1), 'P',
                ItemList.Electric_Pump_IV, 'C', OrePrefixes.circuit.get(Materials.IV), 'M', ItemList.Casing_IV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexIV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.StyreneButadieneRubber, 1), 'P',
                ItemList.Electric_Pump_IV, 'C', OrePrefixes.circuit.get(Materials.IV), 'M', ItemList.Casing_IV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexLuV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1), 'P',
                ItemList.Electric_Pump_LuV, 'C', OrePrefixes.circuit.get(Materials.LuV), 'M', ItemList.Casing_LuV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexZPM.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1), 'P',
                ItemList.Electric_Pump_ZPM, 'C', OrePrefixes.circuit.get(Materials.ZPM), 'M', ItemList.Casing_ZPM });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexUV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1), 'P',
                ItemList.Electric_Pump_UV, 'C', OrePrefixes.circuit.get(Materials.UV), 'M', ItemList.Casing_UV });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexUHV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1), 'P',
                ItemList.Electric_Pump_UHV, 'C', OrePrefixes.circuit.get(Materials.UHV), 'M', ItemList.Casing_MAX });
        GTModHandler.addCraftingRecipe(
            BGEMachineList.LatexUEV.get(1),
            recipeBits,
            new Object[] { "SPS", "CMC", "SPS", 'S',
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Polybenzimidazole, 1), 'P',
                ItemList.Electric_Pump_UEV, 'C', OrePrefixes.circuit.get(Materials.UEV), 'M', ItemList.Casing_UEV });
    }

    private static void registerFixedDistillationTowerRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Distillation_Tower.get(1),
                ItemList.Electric_Pump_LuV.get(8),
                GTOreDictUnificator.get(OrePrefixes.circuit.get(Materials.ZPM), 2))
            .itemOutputs(BGEMachineList.FixedDistillationTower.get(1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .duration(30 * SECONDS)
            .eut(RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void registerMEOutputAssemblyRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Hatch_Output_Bus_ME.get(1), ItemList.Hatch_Output_ME.get(1))
            .fluidInputs(Materials.GlueAdvanced.getFluid(5_000))
            .itemOutputs(BGEMachineList.MEOutputAssembly.get(1))
            .duration(20 * SECONDS)
            .eut(RECIPE_HV)
            .metadata(INHERIT_OUTPUT_CAPACITIES, true)
            .noBuffer()
            .addTo(RecipeMaps.assemblerRecipes);
        MEOutputAssemblyUpgradeRecipe.registerRecipes();
    }

    /** Copies the input bus and hatch capacities to the ME output assembly. */
    public static GTRecipe inheritOutputCapacities(GTRecipe registeredRecipe, GTRecipe found, ItemStack[] items) {
        if (found == null || !registeredRecipe.getMetadataOrDefault(INHERIT_OUTPUT_CAPACITIES, false)) return found;

        ItemStack bus = null;
        ItemStack hatch = null;
        for (ItemStack input : items) {
            if (input == null || input.stackSize <= 0) continue;
            if (bus == null && ItemList.Hatch_Output_Bus_ME.isStackEqual(input, false, true)) {
                bus = input;
            } else if (hatch == null && ItemList.Hatch_Output_ME.isStackEqual(input, false, true)) {
                hatch = input;
            }
            if (bus != null && hatch != null) break;
        }
        if (bus == null || hatch == null) return found;

        GTRecipe result = found.copy();
        result.mCanBeBuffered = false;
        result.isNBTSensitive = true;
        for (int i = 0; i < result.mInputs.length; i++) {
            ItemStack input = result.mInputs[i];
            ItemStack source = ItemList.Hatch_Output_Bus_ME.isStackEqual(input, false, true) ? bus
                : ItemList.Hatch_Output_ME.isStackEqual(input, false, true) ? hatch : null;
            if (source == null) continue;
            result.mInputs[i] = source.copy();
            result.mInputs[i].stackSize = input.stackSize;
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("baseCapacity", capacity(hatch, MTEHatchOutputME.DEFAULT_CAPACITY));
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setLong("baseCapacity", capacity(bus, MTEHatchOutputBusME.DEFAULT_CAPACITY));
        tag.setTag(MEOutputAssembly.ITEM_OUTPUT_TAG, itemTag);
        result.mOutputs[0].setTagCompound(tag);
        return result;
    }

    private static long capacity(ItemStack stack, long fallback) {
        long capacity = stack.hasTagCompound() ? stack.getTagCompound()
            .getLong("baseCapacity") : 0;
        return capacity > 0 ? capacity : fallback;
    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    private static void registerTSTRecipes() {
        // Enhanced Nether Interface: disabled controller recipe from TST 0.8.2 (GTCMMachineRecipes).
        ItemStack teleporter = GTModHandler.getModItem(Mods.EnderIO.ID, "blockTransceiver", 4, 0);
        if (teleporter == null) {
            teleporter = GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockMachine2", 4, 0);
        }
        if (teleporter == null) {
            teleporter = new ItemStack(Blocks.obsidian, 64);
        }
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Obsidian, 16),
                teleporter,
                ItemList.Field_Generator_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 16 },
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Netherite, 16))
            .fluidInputs(Materials.HellishMetal.getMolten(144 * 64))
            .itemOutputs(BGEMachineList.BetterTSTNetherInterface.get(1))
            .duration(360 * SECONDS)
            .eut(RECIPE_LuV)
            .addTo(RecipeMaps.assemblerRecipes);

        // Previous Enhanced Nether Interface assembly line recipe (disabled).
        // GTValues.RA.stdBuilder()
        // .metadata(GTRecipeConstants.RESEARCH_ITEM, GTCMItemList.NetherInterface.get(1))
        // .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * SECONDS, RECIPE_ZPM))
        // .itemInputs(
        // GTCMItemList.NetherInterface.get(1),
        // GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Netherite, 64),
        // ItemList.Netherite_Nanoparticles.get(64),
        // ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(64),
        // ItemList.Brittle_Netherite_Scrap.get(64))
        // .fluidInputs(
        // Materials.RichNetherWaste.getFluid(64_000),
        // Materials.PoorNetherWaste.getFluid(64_000),
        // Materials.HellishMetal.getMolten(9_216))
        // .itemOutputs(BGEMachineList.BetterTSTNetherInterface.get(1))
        // .duration(100 * SECONDS)
        // .eut(RECIPE_ZPM)
        // .addTo(GTRecipeConstants.AssemblyLine);

        // Enhanced Photonic Quantum Master
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTCMItemList.PreciseHighEnergyPhotonicQuantumMaster.get(1),
                GTCMItemList.PhotonControllerUpgradeUHV.get(25))
            .fluidInputs(WerkstoffMaterialPool.CeriumDopedLutetiumAluminiumOxygenBlend.getMolten(9_216))
            .itemOutputs(BGEMachineList.BetterTSTPreciseHighEnergyPhotonicQuantumMaster.get(1))
            .duration(120 * SECONDS)
            .eut(RECIPE_UHV)
            .addTo(RecipeMaps.assemblerRecipes);

    }
}
