package com.day.betterxeperience.registry;

import net.minecraft.util.StatCollector;

import com.day.betterxeperience.api.BGEMachineList;
import com.day.betterxeperience.common.machine.BetterTSTNetherInterface;

import gregtech.api.GregTechAPI;

public class MachineLoader {

    public static final int STARTER_ID = 32200;
    public static final int MACHINE_COUNT = 1;

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
                "betterxeperience.nether_interface",
                StatCollector.translateToLocal("betterxeperience.machine.nether_interface.name")).getStackForm(1));
        if (id != STARTER_ID + MACHINE_COUNT) {
            throw new IllegalStateException("BetterGTExperience machine count does not match registrations");
        }
    }
}
