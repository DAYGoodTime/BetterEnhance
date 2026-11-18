package com.day.betterexperience.common.machine.singleblock;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.objects.overclockdescriber.EUOverclockDescriber;
import gregtech.api.objects.overclockdescriber.OverclockDescriber;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;

public class ElectricLatex extends MTEBasicMachineWithRecipe {

    public ElectricLatex(int id, String name, String regionalName, int tier) {
        super(
            id,
            name,
            regionalName,
            tier,
            new String[0],
            RecipeMaps.cableRecipes,
            6,
            1,
            true,
            false,
            getCapacityForTier(tier),
            SoundResource.GTCEU_LOOP_ASSEMBLER,
            SpecialEffects.NONE,
            "ASSEMBLER");
    }

    private ElectricLatex(String name, int tier, ITexture[][][] textures) {
        super(
            name,
            tier,
            new String[0],
            RecipeMaps.cableRecipes,
            6,
            1,
            true,
            false,
            getCapacityForTier(tier),
            RecipeMaps.cableRecipes.getAmperage(),
            textures,
            SoundResource.GTCEU_LOOP_ASSEMBLER,
            SpecialEffects.NONE);
        progressBarTexture = mRecipes.getFrontend()
            .getUIProperties().progressBarTexture;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new ElectricLatex(mName, mTier, mTextures);
    }

    @Override
    protected OverclockDescriber createOverclockDescriber() {
        return new EUOverclockDescriber(mTier, mAmperage) {

            @Override
            public OverclockCalculator createCalculator(OverclockCalculator template, GTRecipe recipe) {
                return super.createCalculator(template, recipe).setEUtDiscount(LatexMachineSupport.ENERGY_MULTIPLIER)
                    .setDurationModifier(LatexMachineSupport.DURATION_MULTIPLIER);
            }
        };
    }

    @Override
    public String[] getDescription() {
        return LatexMachineSupport.getDescription(false);
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return LatexMachineSupport.getElectricUIProperties(super.getUIProperties());
    }
}
