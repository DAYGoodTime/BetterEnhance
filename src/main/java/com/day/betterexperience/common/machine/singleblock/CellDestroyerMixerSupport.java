package com.day.betterexperience.common.machine.singleblock;

import java.util.Collections;

import net.minecraft.util.ResourceLocation;

import com.day.betterexperience.BetterGTExperience;

import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.tooltip.MarkdownTooltipLoader;
import gregtech.common.modularui2.util.SteamTexture;

public final class CellDestroyerMixerSupport {

    private static final ResourceLocation TOOLTIP = new ResourceLocation(
        BetterGTExperience.MODID,
        "cell-destroyer-mixer");

    private CellDestroyerMixerSupport() {}

    public static String[] getDescription() {
        return MarkdownTooltipLoader.STANDARD.loadStandardPath(TOOLTIP, Collections.emptyMap())
            .toArray(new String[0]);
    }

    public static BasicUIProperties getUIProperties(BasicUIProperties properties) {
        return properties.toBuilder()
            .maxItemInputs(CellDestroyerMixerFluids.ITEM_SLOTS)
            .maxItemOutputs(CellDestroyerMixerFluids.ITEM_SLOTS)
            .maxFluidInputs(CellDestroyerMixerFluids.FLUID_SLOTS)
            .maxFluidOutputs(CellDestroyerMixerFluids.FLUID_SLOTS)
            .slotOverlaysMUI2(
                (index, isFluid, isOutput, isSpecial) -> { return isFluid ? null : GTGuiTextures.OVERLAY_SLOT_DUST; })
            .slotOverlaysSteamMUI2(
                (index, isFluid, isOutput, isSpecial) -> {
                    return isFluid ? SteamTexture.NONE : GTGuiTextures.OVERLAY_SLOT_DUST_STEAM;
                })
            .progressBarTextureSteamMUI2(GTGuiTextures.PROGRESSBAR_ARROW_STEAM)
            .build();
    }

    public static ITexture[] getTexture(int tier, byte color, String face, boolean active) {
        ITexture casing = getSteamCasing(tier, color, face);
        if ("BOTTOM".equals(face)) return new ITexture[] { casing };
        String path = "basicmachines/mixer/OVERLAY_" + face + (active ? "_ACTIVE" : "");
        return new ITexture[] { casing, TextureFactory.of(Textures.BlockIcons.customOptional("gregtech", path)) };
    }

    public static ITexture[] getPipeTexture(int tier, byte color, String face) {
        return new ITexture[] { getSteamCasing(tier, color, face),
            TextureFactory.of(Textures.BlockIcons.OVERLAY_PIPE_OUT) };
    }

    private static ITexture getSteamCasing(int tier, byte color, String face) {
        IIconContainer casing = switch (face) {
            case "TOP" -> tier == 2 ? Textures.BlockIcons.MACHINE_STEEL_TOP : Textures.BlockIcons.MACHINE_BRONZE_TOP;
            case "BOTTOM" -> tier == 2 ? Textures.BlockIcons.MACHINE_STEEL_BOTTOM
                : Textures.BlockIcons.MACHINE_BRONZE_BOTTOM;
            default -> tier == 2 ? Textures.BlockIcons.MACHINE_STEEL_SIDE : Textures.BlockIcons.MACHINE_BRONZE_SIDE;
        };
        return TextureFactory.of(casing, Dyes.getModulation(color));
    }
}
