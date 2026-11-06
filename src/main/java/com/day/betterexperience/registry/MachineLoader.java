package com.day.betterexperience.registry;

import net.minecraft.util.StatCollector;

import com.day.betterexperience.api.BGEMachineList;
import com.day.betterexperience.common.machine.multiblock.BetterTSTNetherInterface;
import com.day.betterexperience.common.machine.multiblock.BetterTSTPreciseHighEnergyPhotonicQuantumMaster;
import com.day.betterexperience.common.machine.multiblock.SteamIndustrialElectrolyzer;
import com.day.betterexperience.common.machine.singleblock.CellDestroyerElectrolyzer;
import com.day.betterexperience.common.machine.singleblock.ElectricLatex;
import com.day.betterexperience.common.machine.singleblock.SteamLatexBronze;
import com.day.betterexperience.common.machine.singleblock.SteamLatexSteel;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.GTValues;

public class MachineLoader {

    public static final int STARTER_ID = 32200;

    public static void register() {
        int id = STARTER_ID;
        if (Loader.isModLoaded("TwistSpaceTechnology")) {
            registerTSTMachines(id);
        }
        // Reserve the TST IDs even when it is absent to keep all other machine IDs stable.
        id += 2;
        BGEMachineList.SteamIndustrialElectrolyzer.set(
            new SteamIndustrialElectrolyzer(
                id++,
                "betterexperience.steam_industrial_electrolyzer",
                StatCollector.translateToLocal("betterexperience.machine.steam_industrial_electrolyzer.name"))
                    .getStackForm(1));
        BGEMachineList.LatexBronze.set(
            new SteamLatexBronze(
                id++,
                "betterexperience.latex.bronze",
                StatCollector.translateToLocal("betterexperience.machine.latex.bronze.name")).getStackForm(1));
        BGEMachineList.LatexSteel.set(
            new SteamLatexSteel(
                id++,
                "betterexperience.latex.steel",
                StatCollector.translateToLocal("betterexperience.machine.latex.steel.name")).getStackForm(1));
        BGEMachineList[] electricLatex = { BGEMachineList.LatexLV, BGEMachineList.LatexMV, BGEMachineList.LatexHV,
            BGEMachineList.LatexEV, BGEMachineList.LatexIV, BGEMachineList.LatexLuV, BGEMachineList.LatexZPM,
            BGEMachineList.LatexUV, BGEMachineList.LatexUHV, BGEMachineList.LatexUEV };
        for (int tier = 1; tier <= electricLatex.length; tier++) {
            String tierName = GTValues.VN[tier];
            electricLatex[tier - 1].set(
                new ElectricLatex(
                    id++,
                    "betterexperience.latex." + tierName,
                    StatCollector.translateToLocal("betterexperience.machine.latex.electric." + tier + ".name"),
                    tier).getStackForm(1));
        }
        BGEMachineList[] cellDestroyerElectrolyzers = { BGEMachineList.CellDestroyerElectrolyzerLV,
            BGEMachineList.CellDestroyerElectrolyzerMV, BGEMachineList.CellDestroyerElectrolyzerHV,
            BGEMachineList.CellDestroyerElectrolyzerEV };
        String[] cellDestroyerRomanTiers = { "I", "II", "III", "IV" };
        for (int tier = 1; tier <= cellDestroyerElectrolyzers.length; tier++) {
            String tierName = GTValues.VN[tier];
            cellDestroyerElectrolyzers[tier - 1].set(
                new CellDestroyerElectrolyzer(
                    id++,
                    "betterexperience.cell_destroyer_electrolyzer." + tierName,
                    StatCollector.translateToLocal("betterexperience.machine.cell_destroyer_electrolyzer.name") + " "
                        + cellDestroyerRomanTiers[tier - 1],
                    tier).getStackForm(1));
        }
    }

    @Optional.Method(modid = "TwistSpaceTechnology")
    private static void registerTSTMachines(int id) {
        BGEMachineList.BetterTSTNetherInterface.set(
            new BetterTSTNetherInterface(
                id++,
                "betterexperience.nether_interface",
                StatCollector.translateToLocal("betterexperience.machine.nether_interface.name")).getStackForm(1));
        BGEMachineList.BetterTSTPreciseHighEnergyPhotonicQuantumMaster.set(
            new BetterTSTPreciseHighEnergyPhotonicQuantumMaster(
                id,
                "betterexperience.photonic_quantum_master",
                StatCollector.translateToLocal("betterexperience.machine.photonic_quantum_master.name"))
                    .getStackForm(1));
    }
}
