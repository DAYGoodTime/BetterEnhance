package com.day.betterexperience.registry;

import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.day.betterexperience.BetterGTExperience;
import com.day.betterexperience.api.BGEMachineList;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
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

        registerCellDestroyerElectrolyzerRecipes();
        registerCellDestroyerMixerRecipes();
        registerMEOutputAssemblyRecipe();
    }

    private static void registerMEOutputAssemblyRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Hatch_Output_Bus_ME.get(1), ItemList.Hatch_Output_ME.get(1))
            .fluidInputs(Materials.GlueAdvanced.getFluid(5_000))
            .itemOutputs(BGEMachineList.MEOutputAssembly.get(1))
            .duration(20 * SECONDS)
            .eut(RECIPE_HV)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    private static void registerCellDestroyerElectrolyzerRecipes() {
        BGEMachineList[] outputs = { BGEMachineList.CellDestroyerElectrolyzerLV,
            BGEMachineList.CellDestroyerElectrolyzerMV, BGEMachineList.CellDestroyerElectrolyzerHV,
            BGEMachineList.CellDestroyerElectrolyzerEV };
        ItemStack[] tanks = { GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "copperTank", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "ironTank", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "goldTank", 1) };
        ItemList[] electrolyzers = { ItemList.Machine_LV_Electrolyzer, ItemList.Machine_MV_Electrolyzer,
            ItemList.Machine_HV_Electrolyzer, ItemList.Machine_EV_Electrolyzer };
        Materials[] circuits = { Materials.LV, Materials.MV, Materials.HV, Materials.EV };
        Materials[] cables = { Materials.Tin, Materials.AnyCopper, Materials.Gold, Materials.Aluminium };
        ItemStack glass = ItemList.ReinforcedGlass.get(1);

        for (int i = 0; i < outputs.length; i++) {
            String tierName = GTValues.VN[i + 1];
            ItemStack electrolyzer = electrolyzers[i].get(1);
            if (tanks[i] == null || tanks[i].getItem() == null
                || glass == null
                || glass.getItem() == null
                || electrolyzer == null
                || electrolyzer.getItem() == null) {
                BetterGTExperience.LOG.error(
                    "Cannot register Cell Destroyer Electrolyzer {} crafting recipe: missing tank={}, reinforcedGlass={}, electrolyzer={}. Check BuildCraft Factory, Iron Tanks and GregTech.",
                    tierName,
                    tanks[i],
                    glass,
                    electrolyzer);
                continue;
            }
            boolean registered = GTModHandler.addCraftingRecipe(
                outputs[i].get(1),
                GTModHandler.RecipeBits.BITS,
                new Object[] { "TGT", "TMT", "CWC", 'T', tanks[i], 'G', glass, 'M', electrolyzer, 'C',
                    OrePrefixes.circuit.get(circuits[i]), 'W', OrePrefixes.cableGt16.get(cables[i]) });
            if (registered) {
                BetterGTExperience.LOG.info("Registered Cell Destroyer Electrolyzer {} crafting recipe", tierName);
            } else {
                BetterGTExperience.LOG.error("GT5U rejected Cell Destroyer Electrolyzer {} crafting recipe", tierName);
            }
        }
    }

    private static void registerCellDestroyerMixerRecipes() {
        BGEMachineList[] outputs = { BGEMachineList.CellDestroyerMixerLV, BGEMachineList.CellDestroyerMixerMV,
            BGEMachineList.CellDestroyerMixerHV, BGEMachineList.CellDestroyerMixerEV };
        ItemStack[] tanks = { GTModHandler.getModItem(Mods.BuildCraftFactory.ID, "tankBlock", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "copperTank", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "ironTank", 1),
            GTModHandler.getModItem(Mods.IronTanks.ID, "goldTank", 1) };
        ItemList[] mixers = { ItemList.Machine_LV_Mixer, ItemList.Machine_MV_Mixer, ItemList.Machine_HV_Mixer,
            ItemList.Machine_EV_Mixer };
        Materials[] circuits = { Materials.LV, Materials.MV, Materials.HV, Materials.EV };
        Materials[] rotors = { Materials.Tin, Materials.Bronze, Materials.Steel, Materials.StainlessSteel };

        for (int i = 0; i < outputs.length; i++) {
            addCellDestroyerMixerRecipe(
                outputs[i],
                GTValues.VN[i + 1],
                tanks[i],
                rotors[i],
                mixers[i].get(1),
                OrePrefixes.circuit.get(circuits[i]));
        }

        addCellDestroyerMixerRecipe(
            BGEMachineList.CellDestroyerMixerBronze,
            "Bronze Steam",
            tanks[0],
            Materials.Bronze,
            ItemList.Hull_Bronze_Bricks.get(1),
            new ItemStack(Blocks.chest));
        addCellDestroyerMixerRecipe(
            BGEMachineList.CellDestroyerMixerSteel,
            "Steel Steam",
            tanks[0],
            Materials.Steel,
            ItemList.Hull_HP_Bricks.get(1),
            new ItemStack(Blocks.chest));
    }

    private static void addCellDestroyerMixerRecipe(BGEMachineList output, String tierName, ItemStack tank,
        Materials rotorMaterial, ItemStack machine, Object circuitOrChest) {
        ItemStack rotor = GTOreDictUnificator.get(OrePrefixes.rotor, rotorMaterial, 1);
        if (tank == null || tank.getItem() == null
            || rotor == null
            || rotor.getItem() == null
            || machine == null
            || machine.getItem() == null) {
            BetterGTExperience.LOG.error(
                "Cannot register Cell Destroyer Mixer {} crafting recipe: missing tank={}, rotor={}, machineOrHull={}. Check BuildCraft Factory, Iron Tanks and GregTech.",
                tierName,
                tank,
                rotor,
                machine);
            return;
        }
        boolean registered = GTModHandler.addCraftingRecipe(
            output.get(1),
            GTModHandler.RecipeBits.BITS,
            new Object[] { "TRT", "TRT", "CMC", 'T', tank, 'R', rotor, 'M', machine, 'C', circuitOrChest });
        if (registered) {
            BetterGTExperience.LOG.info("Registered Cell Destroyer Mixer {} crafting recipe", tierName);
        } else {
            BetterGTExperience.LOG.error("GT5U rejected Cell Destroyer Mixer {} crafting recipe", tierName);
        }
    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    private static void registerTSTRecipes() {
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

    }
}
