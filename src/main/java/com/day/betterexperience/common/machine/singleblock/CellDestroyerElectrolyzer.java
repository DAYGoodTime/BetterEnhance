package com.day.betterexperience.common.machine.singleblock;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.day.betterexperience.client.gui.CellDestroyerElectrolyzerGui;
import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

public class CellDestroyerElectrolyzer extends MTEBasicMachineWithRecipe {

    public static final int FLUID_OUTPUT_SLOTS = 6;

    private final FluidStack[] outputFluids = new FluidStack[FLUID_OUTPUT_SLOTS];
    private final FluidStack[] pendingFluids = new FluidStack[FLUID_OUTPUT_SLOTS];
    private final FluidStackTank[] outputTanks = createOutputTanks();
    private GTRecipe checkedRecipe;
    private int firstOutputTank;

    public CellDestroyerElectrolyzer(int id, String name, String regionalName, int tier) {
        super(
            id,
            name,
            regionalName,
            tier,
            new String[0],
            RecipeMaps.electrolyzerNonCellRecipes,
            6,
            6,
            true,
            true,
            getCapacityForTier(tier),
            SoundResource.GTCEU_LOOP_ELECTROLYZER,
            SpecialEffects.NONE,
            "ELECTROLYZER");
        progressBarTexture = RecipeMaps.electrolyzerRecipes.getFrontend()
            .getUIProperties().progressBarTexture;
    }

    private CellDestroyerElectrolyzer(String name, int tier, ITexture[][][] textures) {
        super(
            name,
            tier,
            new String[0],
            RecipeMaps.electrolyzerNonCellRecipes,
            6,
            6,
            true,
            true,
            getCapacityForTier(tier),
            RecipeMaps.electrolyzerNonCellRecipes.getAmperage(),
            textures,
            SoundResource.GTCEU_LOOP_ELECTROLYZER,
            SpecialEffects.NONE);
        progressBarTexture = RecipeMaps.electrolyzerRecipes.getFrontend()
            .getUIProperties().progressBarTexture;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new CellDestroyerElectrolyzer(mName, mTier, mTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[] {
            StatCollector.translateToLocal("betterexperience.machine.cell_destroyer_electrolyzer.recipes"), };
    }

    @Override
    protected boolean canOutput(GTRecipe recipe) {
        checkedRecipe = recipe;
        if (recipe == null || recipe.mFluidOutputs.length > FLUID_OUTPUT_SLOTS) return false;
        if (!canOutput(recipe.mOutputs)) return false;
        if (recipe.mNeedsEmptyOutput && !isOutputEmpty()) return false;
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            FluidStack stored = outputFluids[i];
            if (recipe.mNeedsEmptyOutput && stored != null) return false;
            FluidStack output = recipe.getFluidOutput(i);
            if (output == null) continue;
            if (stored != null && !stored.isFluidEqual(output)) return false;
            if ((long) (stored == null ? 0 : stored.amount) + output.amount > getCapacity()) return false;
        }
        return true;
    }

    @Override
    public int checkRecipe(boolean skipOC) {
        checkedRecipe = null;
        Arrays.fill(pendingFluids, null);
        mOutputFluid = null;
        int result = super.checkRecipe(skipOC);
        if (result == FOUND_AND_SUCCESSFULLY_USED_RECIPE) {
            // The native checker rolls output zero; keep its roll, then roll the remaining outputs once.
            pendingFluids[0] = mOutputFluid;
            for (int i = 1; i < FLUID_OUTPUT_SLOTS; i++) {
                if (getBaseMetaTileEntity().getRandomNumber(10000) < checkedRecipe.getFluidOutputChance(i)) {
                    pendingFluids[i] = checkedRecipe.getFluidOutput(i);
                }
            }
        }
        // The native tick loop must not also commit the first fluid to its single output tank.
        mOutputFluid = null;
        checkedRecipe = null;
        return result;
    }

    @Override
    public void endProcess() {
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            FluidStack output = pendingFluids[i];
            if (output == null) continue;
            if (outputFluids[i] == null) outputFluids[i] = output.copy();
            else outputFluids[i].amount += output.amount;
        }
        Arrays.fill(pendingFluids, null);
        markDirty();
        super.endProcess();
    }

    @Override
    public void abortProcess() {
        Arrays.fill(pendingFluids, null);
        super.abortProcess();
    }

    @Override
    public void onPostTick(IGregTechTileEntity tileEntity, long tick) {
        // Keep native auto-output timing and transfer limits, without starving fluids behind a blocked output.
        if (tileEntity.isServerSide() && tick % 20 == 0) {
            firstOutputTank = (firstOutputTank + 1) % FLUID_OUTPUT_SLOTS;
        }
        super.onPostTick(tileEntity, tick);
    }

    private FluidStackTank[] createOutputTanks() {
        FluidStackTank[] tanks = new FluidStackTank[FLUID_OUTPUT_SLOTS];
        for (int i = 0; i < tanks.length; i++) {
            final int slot = i;
            tanks[i] = new FluidStackTank(
                () -> outputFluids[slot],
                fluid -> outputFluids[slot] = fluid,
                this::getCapacity);
        }
        return tanks;
    }

    public FluidStackTank getFluidOutputTank(int slot) {
        return outputTanks[slot];
    }

    @Override
    public FluidStackTank getFluidOutputTank() {
        return getFluidOutputTank(0);
    }

    private int getDrainableTankIndex() {
        for (int offset = 0; offset < FLUID_OUTPUT_SLOTS; offset++) {
            int slot = (firstOutputTank + offset) % FLUID_OUTPUT_SLOTS;
            if (outputFluids[slot] != null && outputFluids[slot].amount > 0) return slot;
        }
        return firstOutputTank;
    }

    @Override
    public FluidStack getDrainableStack() {
        return outputFluids[getDrainableTankIndex()];
    }

    @Override
    public FluidStack setDrainableStack(FluidStack fluid) {
        outputFluids[getDrainableTankIndex()] = fluid;
        markDirty();
        return fluid;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return drainOutputTank(getDrainableTankIndex(), maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection side, FluidStack fluid, int amount, boolean doDrain) {
        if (fluid == null) return null;
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            if (fluid.isFluidEqual(outputFluids[i])) return drainOutputTank(i, amount, doDrain);
        }
        return null;
    }

    private FluidStack drainOutputTank(int slot, int maxDrain, boolean doDrain) {
        FluidStack stored = outputFluids[slot];
        if (stored == null || stored.amount <= 0 || maxDrain <= 0 || !canTankBeEmptied()) return null;
        FluidStack drained = stored.copy();
        drained.amount = Math.min(maxDrain, stored.amount);
        if (doDrain) {
            stored.amount -= drained.amount;
            if (stored.amount == 0) outputFluids[slot] = null;
            markDirty();
            getBaseMetaTileEntity().markInventoryBeenModified();
        }
        return drained;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        FluidTankInfo[] info = new FluidTankInfo[1 + FLUID_OUTPUT_SLOTS];
        info[0] = new FluidTankInfo(getFillableStack(), getCapacity());
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            info[i + 1] = new FluidTankInfo(outputFluids[i], getCapacity());
        }
        return info;
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            if (outputFluids[i] != null) {
                nbt.setTag("cellDestroyerOutput" + i, outputFluids[i].writeToNBT(new NBTTagCompound()));
            }
            if (pendingFluids[i] != null) {
                nbt.setTag("cellDestroyerPending" + i, pendingFluids[i].writeToNBT(new NBTTagCompound()));
            }
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        for (int i = 0; i < FLUID_OUTPUT_SLOTS; i++) {
            outputFluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("cellDestroyerOutput" + i));
            pendingFluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("cellDestroyerPending" + i));
        }
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        BasicUIProperties electrolyzer = RecipeMaps.electrolyzerRecipes.getFrontend()
            .getUIProperties();
        return super.getUIProperties().toBuilder()
            .maxFluidInputs(1)
            .maxFluidOutputs(FLUID_OUTPUT_SLOTS)
            .slotOverlays(electrolyzer::getOverlayForSlot)
            .slotOverlaysMUI2(electrolyzer.slotOverlaysMUI2)
            .progressBarTexture(electrolyzer.progressBarTexture)
            .progressBarTextureMUI2(electrolyzer.progressBarMUI2)
            .build();
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new CellDestroyerElectrolyzerGui(this, getUIProperties()).build(data, syncManager, uiSettings);
    }
}
