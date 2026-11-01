package com.day.betterexperience.client.gui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.day.betterexperience.common.machine.multiblock.base.BGTESteamMultiblockBase;
import com.day.betterexperience.common.machine.multiblock.base.SteamMachineTier;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.gui.modularui.widget.CircularGaugeDrawable;

/** MUI2 extension with server-authoritative material, parallel, speed and long-valued steam telemetry. */
public class BGTESteamMultiblockGui extends MTEMultiBlockBaseGui<BGTESteamMultiblockBase<?>> {

    private static final String STORED = "bgeSteamStored";
    private static final String CAPACITY = "bgeSteamCapacity";
    private static final String OC_COUNT = "bgeSteamRecipeOcCount";
    private static final String OC_PANEL = "bge_steam_recipe_oc";

    public BGTESteamMultiblockGui(BGTESteamMultiblockBase<?> machine) {
        super(machine);
    }

    @Override
    public ModularPanel build(PosGuiData data, PanelSyncManager manager, UISettings settings) {
        ModularPanel panel = super.build(data, manager, settings);
        LongSyncValue stored = manager.findSyncHandler(STORED, LongSyncValue.class);
        LongSyncValue capacity = manager.findSyncHandler(CAPACITY, LongSyncValue.class);
        panel.child(
            (multiblock.getMachineTier() == SteamMachineTier.STEEL ? GTGuiTextures.STEAM_GAUGE_BG_STEEL
                : GTGuiTextures.STEAM_GAUGE_BG).asWidget()
                    .size(48, 42)
                    .rightRel(1)
                    .top(8)
                    .tooltipDynamic(
                        tooltip -> tooltip.addLine(
                            StatCollector.translateToLocalFormatted(
                                "betterexperience.steam.ui.storage",
                                stored.getLongValue(),
                                capacity.getLongValue())))
                    .tooltipAutoUpdate(true));
        panel.child(
            new CircularGaugeDrawable(
                () -> capacity.getLongValue() <= 0 ? 0f
                    : (float) Math.min(1.0, (double) stored.getLongValue() / capacity.getLongValue())).asWidget()
                        .size(18, 4)
                        .rightRel(1)
                        .marginRight(9)
                        .top(29));
        return panel;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager manager) {
        super.registerSyncValues(manager);
        manager.syncValue(STORED, new LongSyncValue(multiblock::getSteamStoredLong));
        manager.syncValue(CAPACITY, new LongSyncValue(multiblock::getSteamCapacityLong));
        manager.syncValue(
            OC_COUNT,
            new IntSyncValue(() -> Math.min(4, multiblock.getRecipeOcCount()), multiblock::setRecipeOcCount)
                .allowC2S());
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager manager) {
        super.initPanelMap(parent, manager);
        panelMap.put(
            OC_PANEL,
            manager.syncedPanel(OC_PANEL, true, (panelManager, handler) -> createRecipeOcPanel(parent, manager)));
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager manager) {
        return super.createRightPanelGapRow(parent, manager).child(
            new ButtonWidget<>().name("bgeSteamRecipeOcButton")
                .size(18, 18)
                .marginLeft(4)
                .overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
                .onMousePressed(button -> {
                    var panel = panelMap.get(OC_PANEL);
                    if (panel == null) return false;
                    if (panel.isPanelOpen()) panel.closePanel();
                    else panel.openPanel();
                    return true;
                })
                .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("betterexperience.steam.ui.overclock_count"))));
    }

    private ModularPanel createRecipeOcPanel(ModularPanel parent, PanelSyncManager manager) {
        IntSyncValue count = manager.findSyncHandler(OC_COUNT, IntSyncValue.class);
        return new ModularPanel(OC_PANEL).relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(120, 54)
            .child(
                Flow.column()
                    .full()
                    .padding(3)
                    .child(
                        IKey.lang("betterexperience.steam.ui.overclock_count")
                            .asWidget()
                            .textAlign(Alignment.Center)
                            .size(114, 18)
                            .marginBottom(4))
                    .child(
                        new TextFieldWidget().name("bgeSteamRecipeOcField")
                            .value(count)
                            .setFormatAsInteger(true)
                            .numbersInt(0, 4)
                            .scrollValues(1, 4, 64, 256)
                            .setTextAlignment(Alignment.Center)
                            .size(114, 18)
                            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)));
    }
}
