package com.day.betterexperience.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.day.betterexperience.common.machine.singleblock.CellDestroyerMixer;
import com.day.betterexperience.common.machine.singleblock.CellDestroyerMixerFluids;

import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;

public class CellDestroyerMixerGui extends MTEBasicMachineBaseGui<CellDestroyerMixer> {

    private final CellDestroyerMixerFluids fluids;

    public CellDestroyerMixerGui(CellDestroyerMixer machine, CellDestroyerMixerFluids fluids,
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
            .child(createFluidArea(machine, fluids, slotOverlayFunction, SLOT_SIZE));
    }

    static ParentWidget<?> createFluidArea(MTEBasicMachine machine, CellDestroyerMixerFluids fluids,
        BasicUIProperties.SlotOverlayGetter<IDrawable> overlay, int slotSize) {
        List<IWidget> inputs = new ArrayList<>();
        List<IWidget> outputs = new ArrayList<>();
        for (int i = 0; i < CellDestroyerMixerFluids.FLUID_SLOTS; i++) {
            inputs.add(createFluidSlot(machine, fluids, overlay, i, false));
            outputs.add(createFluidSlot(machine, fluids, overlay, i, true));
        }
        return new ParentWidget<>().size(8 * slotSize, 2 * slotSize)
            .horizontalCenter()
            .bottomRel(0)
            .child(
                new Grid().coverChildren()
                    .gridOf(3, inputs)
                    .leftRel(0))
            .child(
                new Grid().coverChildren()
                    .gridOf(3, outputs)
                    .rightRel(0));
    }

    private static FluidSlot createFluidSlot(MTEBasicMachine machine, CellDestroyerMixerFluids fluids,
        BasicUIProperties.SlotOverlayGetter<IDrawable> overlay, int slot, boolean output) {
        FluidSlotSyncHandler handler = new FluidSlotSyncHandler(
            output ? fluids.getOutputTank(slot) : fluids.getInputTank(slot)) {

            @Override
            protected void onValueChanged() {
                super.onValueChanged();
                if (getSyncManager().isClient()) return;
                machine.markDirty();
                machine.getBaseMetaTileEntity()
                    .markInventoryBeenModified();
            }
        };
        if (output) handler.canFillSlot(false);
        else handler.filter(machine::isFluidInputAllowed);
        return new FluidSlot().backgroundOverlay(overlay.apply(slot, true, output, false))
            .syncHandler(handler);
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        // The six input tanks in the content area replace the native single input slot.
        return Flow.row()
            .coverChildren()
            .verticalCenter()
            .leftRel(0)
            .child(
                createAutoOutputButton(
                    true,
                    syncManager,
                    "fluidAutoOutput",
                    GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID,
                    BaseTileEntity.FLUID_TRANSFER_TOOLTIP,
                    "GT5U.gui.button.forbidden.reason.fluid"))
            .child(
                createAutoOutputButton(
                    true,
                    syncManager,
                    "itemAutoOutput",
                    GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM,
                    BaseTileEntity.ITEM_TRANSFER_TOOLTIP,
                    "GT5U.gui.button.forbidden.reason.item"));
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
}
