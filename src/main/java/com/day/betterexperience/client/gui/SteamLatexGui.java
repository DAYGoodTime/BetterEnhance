package com.day.betterexperience.client.gui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.MTEBasicMachineBronzeGui;

public class SteamLatexGui extends MTEBasicMachineBronzeGui {

    public SteamLatexGui(MTEBasicMachineBronze machine, BasicUIProperties properties) {
        super(machine, properties);
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        // Layout reference: GTNLSteamBasicMachineGui; only native GT widgets are used here.
        return super.createBottomLeftCornerFlow(panel, syncManager)
            .child(createFluidInputSlot().marginLeft(SLOT_SIZE / 2));
    }

    @Override
    protected FluidSlot createFluidInputSlot() {
        FluidSlotSyncHandler recipeFluid = new FluidSlotSyncHandler(machine.getFluidTank()) {

            @Override
            protected void onValueChanged() {
                super.onValueChanged();
                if (!getSyncManager().isClient()) {
                    baseMetaTileEntity.markInventoryBeenModified();
                }
            }
        }.filter(machine::isFluidInputAllowed);
        return new FluidSlot().backgroundOverlay(slotOverlayFunction.apply(0, true, false, false))
            .syncHandler(recipeFluid);
    }
}
