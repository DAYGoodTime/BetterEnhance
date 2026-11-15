package com.day.betterexperience.common.machine.hatch;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;

@IMetaTileEntity.SkipGenerateDescription
public class LargeSteamHatch extends MTEHatchCustomFluidBase {

    public static final int CAPACITY = 4_096_000;

    public LargeSteamHatch(int id, String name, String regional) {
        super(Materials.Steam.mGas, CAPACITY, id, name, regional, 0);
    }

    private LargeSteamHatch(String name, int tier, String[] description, ITexture[][][] textures) {
        super(Materials.Steam.mGas, CAPACITY, name, tier, description, textures);
    }

    @Override
    public LargeSteamHatch newMetaEntity(IGregTechTileEntity tile) {
        return new LargeSteamHatch(mName, mTier, mDescriptionArray, mTextures);
    }
}
