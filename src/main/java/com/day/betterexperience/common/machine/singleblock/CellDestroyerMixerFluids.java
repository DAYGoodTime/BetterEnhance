package com.day.betterexperience.common.machine.singleblock;

import static gregtech.api.util.GTRecipeConstants.COMPRESSION_TIER;
import static gregtech.api.util.GTRecipeConstants.EXPLODE;
import static gregtech.api.util.GTRecipeConstants.ON_FIRE;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.gtnewhorizons.modularui.common.fluid.FluidStackTank;

import gregtech.GTMod;
import gregtech.api.interfaces.ICleanroom;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

/** Shared real tanks and recipe lifecycle for the electric and steam mixers. */
public final class CellDestroyerMixerFluids {

    // GT5U's checkRecipe result codes are protected; this helper is not a machine subclass.
    private static final int DID_NOT_FIND_RECIPE = 0;
    private static final int FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1;
    private static final int FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2;

    public static final int FLUID_SLOTS = 6;
    public static final int ITEM_SLOTS = 9;

    private final MTEBasicMachine machine;
    private final FluidStack[] inputs = new FluidStack[FLUID_SLOTS];
    private final FluidStack[] outputs = new FluidStack[FLUID_SLOTS];
    private final FluidStack[] pending = new FluidStack[FLUID_SLOTS];
    private final FluidStackTank[] inputTanks;
    private final FluidStackTank[] outputTanks;
    private GTRecipe lastRecipe;
    private int firstOutput;

    public CellDestroyerMixerFluids(MTEBasicMachine machine) {
        this.machine = machine;
        inputTanks = createTanks(inputs);
        outputTanks = createTanks(outputs);
    }

    private FluidStackTank[] createTanks(FluidStack[] fluids) {
        FluidStackTank[] tanks = new FluidStackTank[FLUID_SLOTS];
        for (int i = 0; i < FLUID_SLOTS; i++) {
            final int slot = i;
            tanks[i] = new FluidStackTank(() -> fluids[slot], fluid -> {
                fluids[slot] = fluid;
                changed();
            }, machine::getCapacity);
        }
        return tanks;
    }

    public FluidStackTank getInputTank(int slot) {
        return inputTanks[slot];
    }

    public FluidStackTank getOutputTank(int slot) {
        return outputTanks[slot];
    }

    public FluidStack getInput() {
        return inputs[0];
    }

    public FluidStack setInput(FluidStack fluid) {
        inputs[0] = fluid;
        changed();
        return fluid;
    }

    public int fill(FluidStack fluid, boolean doFill) {
        if (fluid == null || fluid.amount <= 0 || !machine.canTankBeFilled() || !machine.isFluidInputAllowed(fluid))
            return 0;
        int empty = -1;
        for (int i = 0; i < FLUID_SLOTS; i++) {
            if (inputs[i] == null || inputs[i].amount <= 0) {
                if (empty < 0) empty = i;
            } else if (inputs[i].isFluidEqual(fluid)) {
                return fillTank(i, fluid, doFill);
            }
        }
        return empty < 0 ? 0 : fillTank(empty, fluid, doFill);
    }

    private int fillTank(int slot, FluidStack fluid, boolean doFill) {
        int stored = inputs[slot] == null ? 0 : Math.max(0, inputs[slot].amount);
        int filled = Math.min(fluid.amount, Math.max(0, machine.getCapacity() - stored));
        if (doFill && filled > 0) {
            FluidStack result = fluid.copy();
            result.amount = stored + filled;
            inputs[slot] = result;
            changed();
        }
        return filled;
    }

    public boolean canOutput(GTRecipe recipe) {
        if (recipe == null || recipe.mOutputs.length > ITEM_SLOTS
            || recipe.mFluidInputs.length > FLUID_SLOTS
            || recipe.mFluidOutputs.length > FLUID_SLOTS) return false;
        for (int i = 0; i < FLUID_SLOTS; i++) {
            FluidStack stored = outputs[i];
            if (recipe.mNeedsEmptyOutput && stored != null && stored.amount > 0) return false;
            FluidStack output = recipe.getFluidOutput(i);
            if (output == null) continue;
            if (stored != null && stored.amount > 0 && !stored.isFluidEqual(output)) return false;
            if ((long) (stored == null ? 0 : stored.amount) + output.amount > machine.getCapacity()) return false;
        }
        return true;
    }

    public int checkRecipe(ItemStack[] items, ItemStack specialSlot, long voltage, Predicate<GTRecipe> canOutput,
        Consumer<GTRecipe> overclock, ICleanroom cleanroom, boolean skipOC) {
        GTRecipe recipe = RecipeMaps.mixerNonCellRecipes.findRecipeQuery()
            .items(items)
            .fluids(inputs)
            .specialSlot(specialSlot)
            .voltage(voltage)
            .cachedRecipe(lastRecipe)
            .find();
        if (recipe == null) return DID_NOT_FIND_RECIPE;
        if (recipe.getMetadataOrDefault(EXPLODE, false)) {
            machine.getBaseMetaTileEntity()
                .doExplosion(voltage * 4);
            return DID_NOT_FIND_RECIPE;
        }
        if (recipe.getMetadataOrDefault(ON_FIRE, false)) {
            machine.getBaseMetaTileEntity()
                .setOnFire();
            return DID_NOT_FIND_RECIPE;
        }
        if (recipe.getMetadataOrDefault(COMPRESSION_TIER, 0) > 0) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        if (GTMod.proxy.mLowGravProcessing && (recipe.mSpecialValue == -100 || recipe.mSpecialValue == -300)
            && !MTEBasicMachine.isValidForLowGravity(
                recipe,
                machine.getBaseMetaTileEntity()
                    .getWorld().provider.dimensionId)) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        boolean needsCleanroom = recipe.mSpecialValue == -200 || recipe.mSpecialValue == -300;
        if (needsCleanroom && (cleanroom == null || !cleanroom.isValidCleanroom() || cleanroom.getCleanness() == 0)) {
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        if (recipe.mCanBeBuffered) lastRecipe = recipe;
        if (!canOutput.test(recipe)) {
            machine.mOutputBlocked++;
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        // Reject an impossible overclock before consuming any input or rolling any product.
        if (!skipOC) {
            overclock.accept(recipe);
            if (machine.mMaxProgresstime == Integer.MAX_VALUE - 1 && machine.mEUt == Integer.MAX_VALUE - 1) {
                return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
            }
        }
        if (!recipe.isRecipeInputEqual(true, inputs, items)) return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        for (int i = 0; i < machine.mOutputItems.length; i++) {
            ItemStack output = recipe.rollOutput(machine.getBaseMetaTileEntity(), i);
            if (output != null && needsCleanroom
                && machine.getBaseMetaTileEntity()
                    .getRandomNumber(10000) > cleanroom.getCleanness())
                output = null;
            machine.mOutputItems[i] = output;
        }
        for (int i = 0; i < FLUID_SLOTS; i++) {
            pending[i] = machine.getBaseMetaTileEntity()
                .getRandomNumber(10000) < recipe.getFluidOutputChance(i) ? recipe.getFluidOutput(i) : null;
            if (inputs[i] != null && inputs[i].amount <= 0) inputs[i] = null;
        }
        // Only endProcess commits these products; the native one-fluid completion path stays empty.
        machine.mOutputFluid = null;
        changed();
        return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
    }

    public void endProcess() {
        for (int i = 0; i < FLUID_SLOTS; i++) {
            if (pending[i] == null) continue;
            if (outputs[i] == null || outputs[i].amount <= 0) outputs[i] = pending[i].copy();
            else outputs[i].amount += pending[i].amount;
        }
        abortProcess();
    }

    public void abortProcess() {
        Arrays.fill(pending, null);
        changed();
    }

    public void rotateOutput(long tick) {
        if (tick % 20 == 0) firstOutput = (firstOutput + 1) % FLUID_SLOTS;
    }

    private int getDrainableSlot() {
        for (int offset = 0; offset < FLUID_SLOTS; offset++) {
            int slot = (firstOutput + offset) % FLUID_SLOTS;
            if (outputs[slot] != null && outputs[slot].amount > 0) return slot;
        }
        return firstOutput;
    }

    public FluidStack getOutput() {
        return outputs[getDrainableSlot()];
    }

    public FluidStack setOutput(FluidStack fluid) {
        outputs[getDrainableSlot()] = fluid;
        changed();
        return fluid;
    }

    public FluidStack drain(int amount, boolean doDrain) {
        return drainTank(getDrainableSlot(), amount, doDrain);
    }

    public FluidStack drain(FluidStack fluid, int amount, boolean doDrain) {
        if (fluid == null) return null;
        for (int i = 0; i < FLUID_SLOTS; i++) {
            if (outputs[i] != null && outputs[i].amount > 0 && outputs[i].isFluidEqual(fluid)) {
                return drainTank(i, amount, doDrain);
            }
        }
        return null;
    }

    private FluidStack drainTank(int slot, int amount, boolean doDrain) {
        FluidStack stored = outputs[slot];
        if (stored == null || stored.amount <= 0 || amount <= 0 || !machine.canTankBeEmptied()) return null;
        FluidStack result = stored.copy();
        result.amount = Math.min(amount, stored.amount);
        if (doDrain) {
            stored.amount -= result.amount;
            if (stored.amount == 0) outputs[slot] = null;
            changed();
        }
        return result;
    }

    public FluidTankInfo[] getTankInfo(FluidTankInfo... extra) {
        FluidTankInfo[] info = Arrays.copyOf(extra, extra.length + FLUID_SLOTS * 2);
        for (int i = 0; i < FLUID_SLOTS; i++) {
            info[extra.length + i] = new FluidTankInfo(inputs[i], machine.getCapacity());
            info[extra.length + FLUID_SLOTS + i] = new FluidTankInfo(outputs[i], machine.getCapacity());
        }
        return info;
    }

    public void save(NBTTagCompound nbt) {
        for (int i = 0; i < FLUID_SLOTS; i++) {
            saveFluid(nbt, "mixerInput" + i, inputs[i]);
            saveFluid(nbt, "mixerOutput" + i, outputs[i]);
            saveFluid(nbt, "mixerPending" + i, pending[i]);
        }
    }

    private void saveFluid(NBTTagCompound nbt, String key, FluidStack fluid) {
        if (fluid == null) nbt.removeTag(key);
        else nbt.setTag(key, fluid.writeToNBT(new NBTTagCompound()));
    }

    public void load(NBTTagCompound nbt) {
        for (int i = 0; i < FLUID_SLOTS; i++) {
            inputs[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mixerInput" + i));
            outputs[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mixerOutput" + i));
            pending[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("mixerPending" + i));
        }
        machine.mOutputFluid = null;
    }

    private void changed() {
        machine.markDirty();
        if (machine.getBaseMetaTileEntity() != null) machine.getBaseMetaTileEntity()
            .markInventoryBeenModified();
    }
}
