package com.day.betterexperience.common.machine.singleblock;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.day.betterexperience.BetterGTExperience;

import gregtech.api.enums.Textures;
import gregtech.api.enums.TieredVariant;
import gregtech.api.interfaces.ITexture;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.objects.overclockdescriber.OverclockDescriber;
import gregtech.api.objects.overclockdescriber.SteamOverclockDescriber;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.tooltip.MarkdownTooltipLoader;
import gregtech.common.modularui2.util.SteamTexture;

public final class LatexMachineSupport {

    private static final ResourceLocation TOOLTIP = new ResourceLocation(BetterGTExperience.MODID, "latex");
    static final double ENERGY_MULTIPLIER = 0.75;
    static final double DURATION_MULTIPLIER = 0.5;

    private LatexMachineSupport() {}

    public static String[] getDescription(boolean steam) {
        String keyPrefix = "betterexperience.machine.latex.tooltip." + (steam ? "steam." : "electric.");
        return MarkdownTooltipLoader.STANDARD
            .loadStandardPath(
                TOOLTIP,
                Map.of(
                    "speedKey",
                    keyPrefix + "speed",
                    "usageKey",
                    keyPrefix + "usage",
                    "speedBonus",
                    Math.round((1 / DURATION_MULTIPLIER - 1) * 100),
                    "usage",
                    Math.round(ENERGY_MULTIPLIER * 100)))
            .toArray(new String[0]);
    }

    static OverclockDescriber createSteamOverclockDescriber(TieredVariant variant, int baseEnergy, int baseDuration) {
        return new SteamOverclockDescriber(variant, baseEnergy, baseDuration) {

            @Override
            public OverclockCalculator createCalculator(OverclockCalculator template, GTRecipe recipe) {
                return super.createCalculator(template, recipe).setEUtDiscount(baseEnergy * ENERGY_MULTIPLIER)
                    .setDurationModifier(baseDuration * DURATION_MULTIPLIER);
            }
        };
    }

    public static BasicUIProperties getElectricUIProperties(BasicUIProperties properties) {
        return properties.toBuilder()
            .slotOverlaysMUI2((index, isFluid, isOutput, isSpecial) -> IDrawable.EMPTY)
            .build();
    }

    public static BasicUIProperties getSteamUIProperties(BasicUIProperties properties) {
        return properties.toBuilder()
            .maxFluidInputs(1)
            .progressBarTextureSteamMUI2(GTGuiTextures.PROGRESSBAR_ARROW_STEAM)
            .slotOverlaysSteamMUI2((index, isFluid, isOutput, isSpecial) -> SteamTexture.NONE)
            .build();
    }

    public static ITexture[] withAssemblerOverlay(ITexture casing, String face, boolean active) {
        String path = "basicmachines/assembler/OVERLAY_" + face + (active ? "_ACTIVE" : "");
        return new ITexture[] { casing, TextureFactory.of(Textures.BlockIcons.customOptional("gregtech", path)),
            TextureFactory.builder()
                .addIcon(Textures.BlockIcons.customOptional("gregtech", path + "_GLOW"))
                .glow()
                .build() };
    }
}
