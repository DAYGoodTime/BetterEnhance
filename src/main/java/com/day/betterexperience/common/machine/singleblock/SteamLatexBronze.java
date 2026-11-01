package com.day.betterexperience.common.machine.singleblock;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.day.betterexperience.client.gui.SteamLatexGui;

import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;

public class SteamLatexBronze extends MTEBasicMachineBronze {

    public SteamLatexBronze(int id, String name, String regionalName) {
        super(id, name, regionalName, "", 6, 1, false);
    }

    public SteamLatexBronze(String name, String[] description, ITexture[][][] textures) {
        super(name, description, textures, 6, 1, false);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamLatexBronze(mName, mDescriptionArray, mTextures);
    }

    @Override
    public String[] getDescription() {
        return LatexMachineSupport.getDescription();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.cableRecipes;
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return LatexMachineSupport.getSteamUIProperties(super.getUIProperties());
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new SteamLatexGui(this, getUIProperties()).build(data, syncManager, uiSettings);
    }

    @Override
    public int getCapacity() {
        return 16000;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack fluid) {
        return fluid != null && !GTModHandler.isSteam(fluid) && getRecipeMap().containsInput(fluid);
    }

    @Override
    public int fill(FluidStack fluid, boolean doFill) {
        // Power steam belongs to GT's energy buffer, never the recipe fluid tank.
        if (fluid != null && GTModHandler.isSteam(fluid)) {
            return fluid.amount > 1 ? super.fill(ForgeDirection.UNKNOWN, fluid, doFill) : 0;
        }
        return super.fill(fluid, doFill);
    }

    @Override
    public boolean canFill(ForgeDirection side, Fluid fluid) {
        FluidStack stack = new FluidStack(fluid, 2);
        return GTModHandler.isSteam(stack) ? fill(side, stack, false) > 0 : super.canFill(side, fluid);
    }

    @Override
    public FluidStack getDrainableStack() {
        return getFillableStack();
    }

    @Override
    public FluidStack setDrainableStack(FluidStack fluid) {
        markDirty();
        return setFillableStack(fluid);
    }

    @Override
    public boolean isDrainableStackSeparate() {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return new FluidTankInfo[] { super.getTankInfo(side)[0],
            new FluidTankInfo(getFillableStack(), getCapacity()) };
    }

    @Override
    public int checkRecipe() {
        GTRecipe recipe = getRecipeMap().findRecipeQuery()
            .items(getAllInputs())
            .fluids(getFillableStack())
            .voltage(TierEU.LV)
            .find();
        if (recipe == null || !canOutput(recipe.mOutputs)
            || !recipe.isRecipeInputEqual(true, new FluidStack[] { getFillableStack() }, getAllInputs())) {
            return DID_NOT_FIND_RECIPE;
        }
        mOutputItems[0] = recipe.rollOutput(getBaseMetaTileEntity(), 0);
        calculateCustomOverclock(recipe);
        return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
    }

    @Override
    public ITexture[] getSideFacingActive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getSideFacingActive(color)[0], "SIDE", true);
    }

    @Override
    public ITexture[] getSideFacingInactive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getSideFacingInactive(color)[0], "SIDE", false);
    }

    @Override
    public ITexture[] getFrontFacingActive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getFrontFacingActive(color)[0], "FRONT", true);
    }

    @Override
    public ITexture[] getFrontFacingInactive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getFrontFacingInactive(color)[0], "FRONT", false);
    }

    @Override
    public ITexture[] getTopFacingActive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getTopFacingActive(color)[0], "TOP", true);
    }

    @Override
    public ITexture[] getTopFacingInactive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getTopFacingInactive(color)[0], "TOP", false);
    }

    @Override
    public ITexture[] getBottomFacingActive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getBottomFacingActive(color)[0], "BOTTOM", true);
    }

    @Override
    public ITexture[] getBottomFacingInactive(byte color) {
        return LatexMachineSupport.withAssemblerOverlay(super.getBottomFacingInactive(color)[0], "BOTTOM", false);
    }
}
