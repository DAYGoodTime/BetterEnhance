package com.day.betterexperience.common.machine.base;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.util.GTUtility;

public abstract class BGTEMultiblockBase<T extends BGTEMultiblockBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements ISurvivalConstructable {

    protected BGTEMultiblockBase(int id, String name, String regional) {
        super(id, name, regional);
    }

    protected BGTEMultiblockBase(String name) {
        super(name);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setMaxParallelSupplier(this::getTrueParallel)
            .setEuModifierSupplier(this::getEUtDiscount)
            .setSpeedBonusSupplier(this::getDurationModifier);
    }

    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setOverclock(getPerfectOC() ? 4.0 : 2.0, 4.0);
    }

    public boolean getPerfectOC() {
        return false;
    }

    public double getEUtDiscount() {
        return 1.0;
    }

    public double getDurationModifier() {
        return 1.0;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    /**
     * Consumes an exact item count across the ordinary recipe input stacks. Call during recipe processing so ME
     * snapshots are committed by GT's existing lifecycle. Matching includes NBT; no ore-dictionary substitution or
     * filled-container conversion is performed. A single ME item snapshot remains int-limited.
     *
     * @return false without consuming anything when the visible inputs are insufficient
     */
    public boolean depleteInput(ItemStack template, long amount, boolean simulate) {
        if (amount < 0) throw new IllegalArgumentException("Input amount must not be negative");
        if (template == null || template.getItem() == null) return false;
        if (amount == 0) return true;

        List<ItemStack> inputs = getStoredInputs();
        long remaining = amount;
        for (ItemStack input : inputs) {
            if (input == null || input.stackSize <= 0 || !GTUtility.areStacksEqual(template, input)) continue;
            remaining -= Math.min(remaining, (long) input.stackSize);
            if (remaining == 0) break;
        }
        if (remaining != 0) return false;
        if (simulate) return true;

        remaining = amount;
        for (ItemStack input : inputs) {
            if (input == null || input.stackSize <= 0 || !GTUtility.areStacksEqual(template, input)) continue;
            int consumed = (int) Math.min(remaining, (long) input.stackSize);
            input.stackSize -= consumed;
            remaining -= consumed;
            if (remaining == 0) break;
        }
        updateSlots();
        return true;
    }

    public boolean depleteInput(ItemStack template, long amount) {
        return depleteInput(template, amount, false);
    }

    /**
     * Support long amount input
     *
     * @return false without consuming anything when the visible inputs are insufficient
     */
    public boolean depleteInput(FluidStack template, long amount, boolean simulate) {
        if (amount < 0) throw new IllegalArgumentException("Input amount must not be negative");
        if (template == null || template.getFluid() == null) return false;
        if (amount == 0) return true;

        List<FluidStack> inputs = getStoredFluids();
        long remaining = amount;
        for (FluidStack input : inputs) {
            if (input == null || input.amount <= 0 || !template.isFluidEqual(input)) continue;
            remaining -= Math.min(remaining, (long) input.amount);
            if (remaining == 0) break;
        }
        if (remaining != 0) return false;
        if (simulate) return true;

        remaining = amount;
        for (FluidStack input : inputs) {
            if (input == null || input.amount <= 0 || !template.isFluidEqual(input)) continue;
            int consumed = (int) Math.min(remaining, (long) input.amount);
            input.amount -= consumed;
            remaining -= consumed;
            if (remaining == 0) break;
        }
        updateSlots();
        return true;
    }

    public boolean depleteInput(FluidStack template, long amount) {
        return depleteInput(template, amount, false);
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity tile, int casingIndex) {
        return super.addToMachineList(tile, casingIndex) || addExoticEnergyInputToMachineList(tile, casingIndex);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack stack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    private UUID wirelessOwner() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        return tile == null ? null : tile.getOwnerUuid();
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity tile, EntityPlayer player) {
        // TODO wireless support
        return super.onRightclick(tile, player);
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }
}
