package com.day.betterexperience.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.day.betterexperience.common.machine.singleblock.CellDestroyerElectrolyzer;

import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.base.MTEBasicMachineBaseGui;

public class CellDestroyerElectrolyzerGui extends MTEBasicMachineBaseGui<CellDestroyerElectrolyzer> {

    public CellDestroyerElectrolyzerGui(CellDestroyerElectrolyzer machine, BasicUIProperties properties) {
        super(machine, properties);
    }

    @Override
    protected int getBasePanelHeight() {
        return super.getBasePanelHeight() + 2 * SLOT_SIZE;
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        List<IWidget> slots = new ArrayList<>();
        for (int i = 0; i < CellDestroyerElectrolyzer.FLUID_OUTPUT_SLOTS; i++) {
            slots.add(createFluidOutputSlot(i));
        }
        return super.createContentSection(panel, syncManager).child(
            new ParentWidget<>().size(8 * SLOT_SIZE, 2 * SLOT_SIZE)
                .horizontalCenter()
                .bottomRel(0)
                .child(
                    new Grid().coverChildren()
                        .gridOf(3, slots)
                        .rightRel(0)));
    }

    private FluidSlot createFluidOutputSlot(int slot) {
        return new FluidSlot().backgroundOverlay(slotOverlayFunction.apply(slot, true, true, false))
            .syncHandler(new FluidSlotSyncHandler(machine.getFluidOutputTank(slot)) {

                @Override
                protected void onValueChanged() {
                    super.onValueChanged();
                    if (getSyncManager().isClient()) return;
                    baseMetaTileEntity.markInventoryBeenModified();
                }
            }.canFillSlot(false));
    }

    @Override
    protected Flow createBottomRightCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        // The six real output tanks above replace the native single fluid slot in this corner.
        return Flow.row()
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .coverChildren()
            .verticalCenter()
            .rightRel(0)
            .childIf(doesAddCircuitSlot(), () -> createCircuitSlot(syncManager))
            .childIf(doesAddSpecialSlot(), this::createSpecialSlot);
    }
}
