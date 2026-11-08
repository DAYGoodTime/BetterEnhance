package com.day.betterexperience.client.gui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import appeng.core.localization.GuiText;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

public class MEOutputAssemblyGui extends MTEHatchBaseGui<MEOutputAssembly> {

    public MEOutputAssemblyGui(MEOutputAssembly machine) {
        super(machine);
    }

    @Override
    protected int getBasePanelHeight() {
        return super.getBasePanelHeight() + 64;
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return false;
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        Flow channels = Flow.column()
            .fullWidth()
            .coverChildrenHeight()
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .childPadding(2);
        createChannel(
            channels,
            syncManager,
            "items",
            machine.getItemOutput()
                .getProvider(),
            machine.getItemCellInventory());
        createChannel(channels, syncManager, "fluids", machine.getProvider(), machine.inventoryHandler);
        return super.createContentSection(panel, syncManager).child(channels);
    }

    private void createChannel(Flow channels, PanelSyncManager syncManager, String name,
        MTEHatchOutputMEBase<?> provider, ItemStackHandler inventory) {
        LongSyncValue amount = new LongSyncValue(provider::getCachedAmount);
        LongSyncValue capacity = new LongSyncValue(provider::getCacheCapacity);
        syncManager.syncValue(name + "Amount", 0, amount);
        syncManager.syncValue(name + "Capacity", 0, capacity);
        IntSyncValue priority = new IntSyncValue(provider::getPriority, provider::setPriority).allowC2S();
        BooleanSyncValue caching = new BooleanSyncValue(provider::getCacheMode, value -> {
            provider.setCacheMode(value);
            provider.cellToCacheTransfer();
            settingsChanged();
        }).allowC2S();
        BooleanSyncValue checking = new BooleanSyncValue(provider::getCheckMode, value -> {
            provider.setCheckMode(value);
            settingsChanged();
        }).allowC2S();
        IntSyncValue refresh = new IntSyncValue(
            () -> provider.getRefreshTime() / MTEHatchOutputMEBase.TICKS_PER_SECOND,
            seconds -> {
                provider.setRefreshTime(seconds * MTEHatchOutputMEBase.TICKS_PER_SECOND);
                settingsChanged();
            }).allowC2S();

        Flow row = Flow.row()
            .fullWidth()
            .height(18)
            .childPadding(2)
            // A positioned Y axis makes a row opt out of the enclosing column's layout.
            .crossAxisAlignment(Alignment.CrossAxis.CENTER);
        row.child(
            new ItemSlot().slot(new ModularSlot(inventory, 0).singletonSlotGroup())
                .addTooltipLine(StatCollector.translateToLocal("betterexperience.machine.me_output_assembly." + name)));
        row.child(
            new ToggleButton().value(caching)
                .overlay(GuiTextures.FOLDER)
                .addTooltipLine(StatCollector.translateToLocal("GT5U.hatch.outputme.toggle_caching")));
        row.child(
            new TextFieldWidget().size(50, 14)
                .formatAsInteger(true)
                .value(priority)
                .numbersInt(Integer.MIN_VALUE, Integer.MAX_VALUE)
                .setMaxLength(11)
                .tooltip(t -> t.addLine(GuiText.Priority.getLocal()))
                .setEnabledIf(widget -> caching.getBoolValue())
                .marginLeft(3));
        row.child(
            new ToggleButton().value(checking)
                .overlay(GuiTextures.SEARCH)
                .addTooltipLine(StatCollector.translateToLocal("GT5U.hatch.outputme.toggle_checking")));
        row.child(
            new TextFieldWidget().size(40, 14)
                .formatAsInteger(true)
                .value(refresh)
                .numbersInt(
                    MTEHatchOutputMEBase.MIN_REFRESH_TIME / MTEHatchOutputMEBase.TICKS_PER_SECOND,
                    Integer.MAX_VALUE / MTEHatchOutputMEBase.TICKS_PER_SECOND)
                .setMaxLength(10)
                .marginLeft(3)
                .tooltip(t -> t.addLine(StatCollector.translateToLocal("GT5U.hatch.outputme.refresh_time"))));

        channels.child(
            IKey.lang("betterexperience.machine.me_output_assembly." + name + "_cache_title")
                .asWidget()
                .fullWidth()
                .height(12));
        channels.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted(
                    "betterexperience.machine.me_output_assembly."
                        + (name.equals("items") ? "item_cache" : "fluid_cache"),
                    NumberFormatUtil.formatNumber(amount.getLongValue()),
                    NumberFormatUtil.formatNumber(capacity.getLongValue())))
                .asWidget()
                .fullWidth()
                .height(20)
                .textAlign(Alignment.TopLeft));
        channels.child(row);
    }

    private void settingsChanged() {
        machine.notifyOutputSpaceChanged();
        machine.markDirty();
    }
}
