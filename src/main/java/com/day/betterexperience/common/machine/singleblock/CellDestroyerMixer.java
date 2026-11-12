package com.day.betterexperience.common.machine.singleblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.day.betterexperience.client.gui.CellDestroyerMixerGui;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

public class CellDestroyerMixer extends MTEBasicMachineWithRecipe {

    private final CellDestroyerMixerFluids fluids = new CellDestroyerMixerFluids(this);

    public CellDestroyerMixer(int id, String name, String regionalName, int tier) {
        super(
            id,
            name,
            regionalName,
            tier,
            new String[0],
            RecipeMaps.mixerNonCellRecipes,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            true,
            true,
            getCapacityForTier(tier),
            SoundResource.GTCEU_LOOP_MIXER,
            SpecialEffects.NONE,
            "MIXER");
    }

    private CellDestroyerMixer(String name, int tier, ITexture[][][] textures) {
        super(
            name,
            tier,
            new String[0],
            RecipeMaps.mixerNonCellRecipes,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            true,
            true,
            getCapacityForTier(tier),
            RecipeMaps.mixerNonCellRecipes.getAmperage(),
            textures,
            SoundResource.GTCEU_LOOP_MIXER,
            SpecialEffects.NONE);
        progressBarTexture = mRecipes.getFrontend()
            .getUIProperties().progressBarTexture;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new CellDestroyerMixer(mName, mTier, mTextures);
    }

    @Override
    public String[] getDescription() {
        return CellDestroyerMixerSupport.getDescription();
    }

    @Override
    protected boolean canOutput(GTRecipe recipe) {
        return fluids.canOutput(recipe) && canOutput(recipe.mOutputs) && (!recipe.mNeedsEmptyOutput || isOutputEmpty());
    }

    @Override
    public int checkRecipe(boolean skipOC) {
        return fluids.checkRecipe(
            getAllInputs(),
            getSpecialSlot(),
            GTValues.V[mTier],
            this::canOutput,
            this::calculateCustomOverclock,
            cleanroomReference.getCleanroom(),
            skipOC);
    }

    @Override
    public void endProcess() {
        fluids.endProcess();
        super.endProcess();
    }

    @Override
    public void abortProcess() {
        fluids.abortProcess();
        super.abortProcess();
    }

    @Override
    public void onPostTick(IGregTechTileEntity tileEntity, long tick) {
        if (tileEntity.isServerSide()) fluids.rotateOutput(tick);
        super.onPostTick(tileEntity, tick);
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack fluid) {
        return fluid != null && getRecipeMap().containsInput(fluid);
    }

    @Override
    public int fill(FluidStack fluid, boolean doFill) {
        return fluids.fill(fluid, doFill);
    }

    @Override
    public FluidStack getFillableStack() {
        return fluids.getInput();
    }

    @Override
    public FluidStack setFillableStack(FluidStack fluid) {
        return fluids.setInput(fluid);
    }

    @Override
    public FluidStackTank getFluidTank() {
        return fluids.getInputTank(0);
    }

    @Override
    public FluidStackTank getFluidOutputTank() {
        return fluids.getOutputTank(0);
    }

    @Override
    public FluidStack getDrainableStack() {
        return fluids.getOutput();
    }

    @Override
    public FluidStack setDrainableStack(FluidStack fluid) {
        return fluids.setOutput(fluid);
    }

    @Override
    public FluidStack drain(int amount, boolean doDrain) {
        return fluids.drain(amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack fluid, int amount, boolean doDrain) {
        return fluids.drain(fluid, amount, doDrain);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return fluids.getTankInfo();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        fluids.save(nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        fluids.load(nbt);
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return CellDestroyerMixerSupport.getUIProperties(super.getUIProperties());
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new CellDestroyerMixerGui(this, fluids, getUIProperties()).build(data, syncManager, uiSettings);
    }
}
