package com.day.betterexperience.registry;

import net.minecraft.util.StatCollector;

import com.day.betterexperience.api.BGEMachineList;
import com.day.betterexperience.common.machine.multiblock.BetterTSTNetherInterface;
import com.day.betterexperience.common.machine.multiblock.BetterTSTPreciseHighEnergyPhotonicQuantumMaster;
import com.day.betterexperience.common.machine.multiblock.SteamIndustrialElectrolyzer;

import gregtech.api.GregTechAPI;

public class MachineLoader {

    public static final int STARTER_ID = 32200;
    public static final int MACHINE_COUNT = 3;

    public static void register() {
        for (int id = STARTER_ID; id < STARTER_ID + MACHINE_COUNT; id++) {
            if (id >= GregTechAPI.METATILEENTITIES.length) {
                throw new IllegalArgumentException("BetterGTExperience machine ID outside GT5U registry: " + id);
            }
            if (GregTechAPI.METATILEENTITIES[id] != null) {
                throw new IllegalStateException("BetterGTExperience approved machine ID is occupied: " + id);
            }
        }
        int id = STARTER_ID;
        BGEMachineList.BetterTSTNetherInterface.set(
            new BetterTSTNetherInterface(
                id++,
                "betterexperience.nether_interface",
                StatCollector.translateToLocal("betterexperience.machine.nether_interface.name")).getStackForm(1));
        BGEMachineList.BetterTSTPreciseHighEnergyPhotonicQuantumMaster.set(
            new BetterTSTPreciseHighEnergyPhotonicQuantumMaster(
                id++,
                "betterexperience.photonic_quantum_master",
                StatCollector.translateToLocal("betterexperience.machine.photonic_quantum_master.name"))
                    .getStackForm(1));
        BGEMachineList.SteamIndustrialElectrolyzer.set(
            new SteamIndustrialElectrolyzer(
                id++,
                "betterexperience.steam_industrial_electrolyzer",
                StatCollector.translateToLocal("betterexperience.machine.steam_industrial_electrolyzer.name"))
                    .getStackForm(1));
        if (id != STARTER_ID + MACHINE_COUNT) {
            throw new IllegalStateException("BetterGTExperience machine count does not match registrations");
        }
    }
}
