package com.day.betterexperience.registry;

import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.day.betterexperience.api.BGEMachineList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public final class MachineRecipes {

    private MachineRecipes() {}

    public static void register() {
        // Enhanced Nether Interface
        GTValues.RA.stdBuilder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, GTCMItemList.NetherInterface.get(1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(10 * SECONDS, RECIPE_ZPM))
            .itemInputs(
                GTCMItemList.NetherInterface.get(1),
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Netherite, 64),
                ItemList.Netherite_Nanoparticles.get(64),
                ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(64),
                ItemList.Brittle_Netherite_Scrap.get(64))
            .fluidInputs(
                Materials.RichNetherWaste.getFluid(64_000),
                Materials.PoorNetherWaste.getFluid(64_000),
                Materials.HellishMetal.getMolten(9_216))
            .itemOutputs(BGEMachineList.BetterTSTNetherInterface.get(1))
            .duration(100 * SECONDS)
            .eut(RECIPE_ZPM)
            .addTo(GTRecipeConstants.AssemblyLine);

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

        // Steam Industrial Electrolyzer
        GTModHandler.addCraftingRecipe(
            BGEMachineList.SteamIndustrialElectrolyzer.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "BGB", "WTW", "BWB", 'B', ItemList.Casing_BronzePlatedBricks, 'G',
                new ItemStack(Blocks.glass), 'T', ItemList.Generator_Steam_Turbine_LV, 'W',
                GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.Gold, 1) });
    }
}
