package com.day.betterexperience.common.machine.singleblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.day.betterexperience.client.gui.SteamCellDestroyerMixerGui;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TieredVariant;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.objects.overclockdescriber.OverclockDescriber;
import gregtech.api.objects.overclockdescriber.SteamOverclockDescriber;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class SteamCellDestroyerMixer extends MTEBasicMachineBronze {

    private final CellDestroyerMixerFluids fluids = new CellDestroyerMixerFluids(this);

    public SteamCellDestroyerMixer(int id, String name, String regionalName, boolean highPressure) {
        super(
            id,
            name,
            regionalName,
            "",
            CellDestroyerMixerFluids.ITEM_SLOTS,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            highPressure);
    }

    private SteamCellDestroyerMixer(String name, ITexture[][][] textures, boolean highPressure) {
        super(
            name,
            new String[0],
            textures,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            CellDestroyerMixerFluids.ITEM_SLOTS,
            highPressure);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamCellDestroyerMixer(mName, mTextures, mTier == 2);
    }

    @Override
    public TieredVariant getTieredVariant() {
        return mTier == 2 ? TieredVariant.STEEL : TieredVariant.BRONZE;
    }

    @Override
    public OverclockDescriber createOverclockDescriber() {
        return mTier == 2 ? new SteamOverclockDescriber(TieredVariant.STEEL, 2, 1)
            : new SteamOverclockDescriber(TieredVariant.BRONZE, 1, 2);
    }

    @Override
    public String[] getDescription() {
        return CellDestroyerMixerSupport.getDescription();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.mixerNonCellRecipes;
    }

    @Override
    public int getCapacity() {
        return 16000;
    }

    @Override
    protected boolean canOutput(GTRecipe recipe) {
        return fluids.canOutput(recipe) && canOutput(recipe.mOutputs) && (!recipe.mNeedsEmptyOutput || isOutputEmpty());
    }

    @Override
    public int checkRecipe() {
        return checkRecipe(false);
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
        return fluid != null && !GTModHandler.isSteam(fluid) && getRecipeMap().containsInput(fluid);
    }

    @Override
    public int fill(FluidStack fluid, boolean doFill) {
        // Route power steam to the native energy buffer, not one of the recipe input tanks.
        if (fluid != null && GTModHandler.isSteam(fluid)) {
            return fluid.amount > 1 ? super.fill(ForgeDirection.UNKNOWN, fluid, doFill) : 0;
        }
        return fluids.fill(fluid, doFill);
    }

    @Override
    public boolean canFill(ForgeDirection side, Fluid fluid) {
        if (fluid == null) return false;
        FluidStack stack = new FluidStack(fluid, 2);
        return GTModHandler.isSteam(stack) ? fill(side, stack, false) > 0 : super.canFill(side, fluid);
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
        return fluids.getTankInfo(super.getTankInfo(side));
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
    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public void startSoundLoop(byte index, double x, double y, double z) {
        super.startSoundLoop(index, x, y, z);
        if (index == 1) GTUtility.doSoundAtClient(SoundResource.GTCEU_LOOP_MIXER, 10, 1.0F, x, y, z);
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return CellDestroyerMixerSupport.getUIProperties(super.getUIProperties());
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new SteamCellDestroyerMixerGui(this, fluids, getUIProperties()).build(data, syncManager, uiSettings);
    }

    @Override
    public ITexture[] getSideFacingActive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "SIDE", true);
    }

    @Override
    public ITexture[] getSideFacingInactive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "SIDE", false);
    }

    @Override
    public ITexture[] getFrontFacingActive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "FRONT", true);
    }

    @Override
    public ITexture[] getFrontFacingInactive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "FRONT", false);
    }

    @Override
    public ITexture[] getTopFacingActive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "TOP", true);
    }

    @Override
    public ITexture[] getTopFacingInactive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "TOP", false);
    }

    @Override
    public ITexture[] getBottomFacingActive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "BOTTOM", true);
    }

    @Override
    public ITexture[] getBottomFacingInactive(byte color) {
        return CellDestroyerMixerSupport.getTexture(mTier, color, "BOTTOM", false);
    }

    @Override
    public ITexture[] getSideFacingPipeActive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "SIDE");
    }

    @Override
    public ITexture[] getSideFacingPipeInactive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "SIDE");
    }

    @Override
    public ITexture[] getTopFacingPipeActive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "TOP");
    }

    @Override
    public ITexture[] getTopFacingPipeInactive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "TOP");
    }

    @Override
    public ITexture[] getBottomFacingPipeActive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "BOTTOM");
    }

    @Override
    public ITexture[] getBottomFacingPipeInactive(byte color) {
        return CellDestroyerMixerSupport.getPipeTexture(mTier, color, "BOTTOM");
    }
}
