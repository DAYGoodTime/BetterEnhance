package com.day.betterexperience.client.gui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.day.betterexperience.common.machine.singleblock.CellDestroyerMixerFluids;
import com.day.betterexperience.common.machine.singleblock.SteamCellDestroyerMixer;

import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.singleblock.MTEBasicMachineBronzeGui;

public class SteamCellDestroyerMixerGui extends MTEBasicMachineBronzeGui {

    private final CellDestroyerMixerFluids fluids;

    public SteamCellDestroyerMixerGui(SteamCellDestroyerMixer machine, CellDestroyerMixerFluids fluids,
        BasicUIProperties properties) {
        super(machine, properties);
        this.fluids = fluids;
    }

    @Override
    protected int getBasePanelHeight() {
        return super.getBasePanelHeight() + 2 * SLOT_SIZE;
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createContentSection(panel, syncManager)
            .child(CellDestroyerMixerGui.createFluidArea(machine, fluids, slotOverlayFunction, SLOT_SIZE));
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager).child(
            createAutoOutputButton(
                true,
                syncManager,
                "fluidAutoOutput",
                GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID,
                BaseTileEntity.FLUID_TRANSFER_TOOLTIP,
                "GT5U.gui.button.forbidden.reason.fluid"));
    }

    @Override
    protected Flow createBottomRightCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.row()
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .coverChildren()
            .verticalCenter()
            .rightRel(0)
            .childIf(doesAddGregTechLogo(), this::makeLogoWidget)
            .childIf(doesAddCircuitSlot(), () -> createCircuitSlot(syncManager))
            .childIf(doesAddSpecialSlot(), this::createSpecialSlot);
    }

    @Override
    protected String createTooltipForProgressBar() {
        return net.minecraft.util.StatCollector.translateToLocalFormatted(
            "GT5U.machines.nei_transfer.voltage.tooltip",
            GTUtility.getColoredTierNameFromTier(machine.mTier));
    }
}
