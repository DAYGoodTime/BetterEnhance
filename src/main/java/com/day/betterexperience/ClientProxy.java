package com.day.betterexperience;

import net.minecraft.util.EnumChatFormatting;

import com.day.betterexperience.api.BGEMachineList;
import com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        for (BGEMachineList machine : BGEMachineList.values()) {
            if (!machine.isRegistered()) continue;
            if (machine == BGEMachineList.BetterTSTNetherInterface
                || machine == BGEMachineList.BetterTSTPreciseHighEnergyPhotonicQuantumMaster) {
                AnimatedTooltipHandler.addItemTooltip(
                    machine.get(1),
                    AnimatedTooltipHandler.translatedText("betterexperience.tooltip.tst_original_author"));
            }
            AnimatedTooltipHandler.addItemTooltip(
                machine.get(1),
                AnimatedTooltipHandler.translatedText(
                    "betterexperience.tooltip.added_by",
                    EnumChatFormatting.GREEN + "BetterGTExperience" + EnumChatFormatting.GRAY));
        }
    }
}
