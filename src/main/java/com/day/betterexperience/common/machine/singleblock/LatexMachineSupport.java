package com.day.betterexperience.common.machine.singleblock;

import java.util.Collections;

import net.minecraft.util.ResourceLocation;

import com.day.betterexperience.BetterGTExperience;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.tooltip.MarkdownTooltipLoader;

public final class LatexMachineSupport {

    private static final ResourceLocation TOOLTIP = new ResourceLocation(BetterGTExperience.MODID, "latex");

    private LatexMachineSupport() {}

    public static String[] getDescription() {
        return MarkdownTooltipLoader.STANDARD.loadStandardPath(TOOLTIP, Collections.emptyMap())
            .toArray(new String[0]);
    }

    public static BasicUIProperties getElectricUIProperties(BasicUIProperties properties) {
        return properties.toBuilder()
            .slotOverlaysMUI2((index, isFluid, isOutput, isSpecial) -> {
                if (isFluid) return GTGuiTextures.OVERLAY_SLOT_CANISTER;
                return isOutput ? GTGuiTextures.OVERLAY_SLOT_OUT_STANDARD : GTGuiTextures.OVERLAY_SLOT_IN_STANDARD;
            })
            .build();
    }

    public static BasicUIProperties getSteamUIProperties(BasicUIProperties properties) {
        return properties.toBuilder()
            .maxFluidInputs(1)
            .progressBarTextureSteamMUI2(GTGuiTextures.PROGRESSBAR_ARROW_STEAM)
            .slotOverlaysSteamMUI2((index, isFluid, isOutput, isSpecial) -> {
                if (isFluid) return GTGuiTextures.OVERLAY_SLOT_CANISTER_STEAM;
                return isOutput ? GTGuiTextures.OVERLAY_SLOT_OUT_STEAM : GTGuiTextures.OVERLAY_SLOT_IN_STEAM;
            })
            .build();
    }

    public static ITexture[] withAssemblerOverlay(ITexture casing, String face, boolean active) {
        String path = "basicmachines/assembler/OVERLAY_" + face + (active ? "_ACTIVE" : "");
        return new ITexture[] { casing,
            TextureFactory.of(Textures.BlockIcons.customOptional("gregtech", path)),
            TextureFactory.builder()
                .addIcon(Textures.BlockIcons.customOptional("gregtech", path + "_GLOW"))
                .glow()
                .build() };
    }
}
