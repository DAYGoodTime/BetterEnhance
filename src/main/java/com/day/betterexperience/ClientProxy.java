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
            if (machine.isRegistered()) {
                AnimatedTooltipHandler.addItemTooltip(
                    machine.get(1),
                    AnimatedTooltipHandler
                        .text(EnumChatFormatting.AQUA + "BetterGTExperience" + EnumChatFormatting.RESET));
            }
        }
    }
}
