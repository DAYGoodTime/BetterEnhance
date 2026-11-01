package com.day.betterexperience.common.machine.singleblock;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMaps;

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
        progressBarTexture = mRecipes.getFrontend().getUIProperties().progressBarTexture;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new ElectricLatex(mName, mTier, mTextures);
    }

    @Override
    public String[] getDescription() {
        return LatexMachineSupport.getDescription();
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return LatexMachineSupport.getElectricUIProperties(super.getUIProperties());
    }
}
