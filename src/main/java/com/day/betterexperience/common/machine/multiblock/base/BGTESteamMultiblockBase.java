package com.day.betterexperience.common.machine.multiblock.base;

import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.day.betterexperience.client.gui.BGTESteamMultiblockGui;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gregtech.api.casing.Casings;
import gregtech.api.enums.Materials;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/** Project steam layer: shared material tier, dedicated steam supply and a synchronized MUI2 interface. */
public abstract class BGTESteamMultiblockBase<T extends BGTESteamMultiblockBase<T>> extends MTESteamMultiBlockBase<T>
    implements ISurvivalConstructable {

    protected SteamMachineTier checkedTier = SteamMachineTier.UNFORMED;
    private SteamMachineTier machineTier = SteamMachineTier.UNFORMED;
    private int recipeOcCount;

    public int getRecipeOcCount() {
        return recipeOcCount;
    }

    public void setRecipeOcCount(int count) {
        recipeOcCount = Math.min(4, count);
    }

    protected BGTESteamMultiblockBase(int id, String name, String regional) {
        super(id, name, regional);
    }

    protected BGTESteamMultiblockBase(String name) {
        super(name);
    }

    protected abstract int getBaseParallel();

    public SteamMachineTier getMachineTier() {
        return machineTier;
    }

    @Override
    public int getMaxParallelRecipes() {
        return machineTier.getParallel(getBaseParallel());
    }

    @Override
    protected boolean isHighPressure() {
        return machineTier == SteamMachineTier.STEEL;
    }

    @Override
    public int getTierRecipes() {
        return VoltageIndex.MV;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean getDefaultInputSeparationMode() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    protected @NotNull CheckRecipeResult checkRecipeForCustomHatches(CheckRecipeResult lastResult) {
        CheckRecipeResult result = super.checkRecipeForCustomHatches(lastResult);
        if (result.wasSuccessful() || !isInputSeparationEnabled() || mSteamInputs.isEmpty()) return result;

        // GT checks ordinary buses itself, but does not visit steam buses in its separated-input path.
        // Keep dual-input inventories, ordinary buses and the recipe-processing lifecycle in the native path.
        short hatchColors = 0;
        for (var bus : validMTEList(mInputBusses)) hatchColors |= (short) (1 << bus.getColor());
        for (var bus : validMTEList(mSteamInputs)) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : validMTEList(mInputHatches)) hatchColors |= (short) (1 << hatch.getColor());
        for (var hatch : validMTEList(mSteamInputFluids)) hatchColors |= (short) (1 << hatch.getColor());
        if (hatchColors == 0) hatchColors = 1;

        for (byte color = 0; color < 16; color++) {
            if ((hatchColors & (1 << color)) == 0) continue;
            processingLogic.setInputFluids(getStoredFluidsForColor(Optional.of(color)));
            for (var bus : validMTEList(mSteamInputs)) {
                if (bus.getColor() != -1 && bus.getColor() != color) continue;
                List<ItemStack> inputs = new ArrayList<>();
                for (int slot = bus.getSizeInventory() - 1; slot >= 0; slot--) {
                    ItemStack stack = bus.getStackInSlot(slot);
                    if (stack != null) inputs.add(stack);
                }
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputs.add(getControllerSlot());
                }
                processingLogic.setInputItems(inputs);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            protected @NotNull OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                // Like GTNL, apply manual steam OC after parallel selection; supply is checked when draining steam.
                return super.createOverclockCalculator(recipe).setMaxOverclocks(0)
                    .setEUtDiscount(1 << (2 * Math.min(4, recipeOcCount)));
            }
        }.setMaxParallelSupplier(this::getTrueParallel)
            .setSpeedBonusSupplier(() -> machineTier.getDurationModifier() / (1 << Math.min(4, recipeOcCount)));
    }

    protected IStructureElement<T> tieredCasing(Casings bronze, Casings steel) {
        return StructureUtility.ofBlocksTiered((block, meta) -> {
            if (block == bronze.getBlock() && meta == bronze.meta) return SteamMachineTier.BRONZE;
            if (block == steel.getBlock() && meta == steel.meta) return SteamMachineTier.STEEL;
            return null;
        },
            ImmutableList.of(Pair.of(bronze.getBlock(), bronze.meta), Pair.of(steel.getBlock(), steel.meta)),
            SteamMachineTier.UNFORMED,
            (machine, tier) -> machine.checkedTier = tier,
            machine -> machine.checkedTier);
    }

    protected void finishSteamStructureCheck(List<StructureError> errors) {
        checkHasSteamInput(errors);
        if (mSteamInputs.isEmpty() && mInputBusses.isEmpty()) {
            errors.add(StructureErrors.of("betterexperience.steam.structure.missing_input_bus"));
        }
        if (mSteamOutputs.isEmpty() && mOutputBusses.isEmpty()) {
            errors.add(StructureErrors.of("betterexperience.steam.structure.missing_output_bus"));
        }
        if (checkedTier == SteamMachineTier.UNFORMED) {
            errors.add(StructureErrors.of("betterexperience.steam.structure.invalid_tier"));
        }
        machineTier = errors.isEmpty() ? checkedTier : SteamMachineTier.UNFORMED;
        updateHatchTexture();
    }

    protected void beginSteamStructureCheck() {
        checkedTier = SteamMachineTier.UNFORMED;
        machineTier = SteamMachineTier.UNFORMED;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        checkedTier = SteamMachineTier.UNFORMED;
    }

    @Override
    public boolean addSteamInputFluidHatch(IGregTechTileEntity tile, int texture) {
        if (tile == null || !(tile.getMetaTileEntity() instanceof MTEHatchCustomFluidBase hatch)) return false;
        if (!Materials.Steam.mGas.equals(hatch.mLockedFluid)) return false;
        return addToMachineListInternal(mSteamInputFluids, hatch, texture);
    }

    protected static <M extends BGTESteamMultiblockBase<M>> HatchElementBuilder<M> buildSteamSupply(Class<M> type) {
        return GTStructureUtility.buildHatchAdder(type)
            .adder(BGTESteamMultiblockBase::addSteamInputFluidHatch);
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> inputs = super.getStoredInputsForColor(color);
        Map<GTUtility.ItemId, ItemStack> meInputs = new HashMap<>();
        for (MTEHatchInputBus bus : validMTEList(mInputBusses)) {
            if (bus instanceof MTEHatchCraftingInputME) continue;
            if (color.isPresent() && bus.getColor() != -1 && bus.getColor() != color.get()) continue;
            bus.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tile = bus.getBaseMetaTileEntity();
            for (int slot = tile.getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = tile.getStackInSlot(slot);
                if (stack == null) continue;
                if (bus instanceof MTEHatchInputBusME) {
                    meInputs.put(GTUtility.ItemId.createNoCopy(stack), stack);
                } else {
                    inputs.add(stack);
                }
            }
        }
        inputs.addAll(meInputs.values());
        return inputs;
    }

    @Override
    public List<IOutputBus> getOutputBusses() {
        List<IOutputBus> outputs = super.getOutputBusses();
        for (var bus : validMTEList(mOutputBusses)) outputs.add(bus);
        return outputs;
    }

    @Override
    public void updateHatchTexture() {
        super.updateHatchTexture();
        int texture = getCasingTextureId();
        for (var bus : validMTEList(mInputBusses)) bus.updateTexture(texture);
        for (var bus : validMTEList(mOutputBusses)) bus.updateTexture(texture);
    }

    @Override
    public void getExtraWailaBody(ItemStack stack, List<String> lines, NBTTagCompound tag, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getExtraWailaBody(stack, lines, tag, accessor, config);
        int maximum = tag.getInteger("maxParallelRecipes");
        if (maximum <= 1) return;
        lines.add(
            tag.hasKey("powerPanelMaxParallel")
                ? StatCollector.translateToLocalFormatted(
                    "GT5U.multiblock.parallelism_override",
                    tag.getInteger("powerPanelMaxParallel"),
                    maximum)
                : StatCollector.translateToLocal("GT5U.multiblock.parallelism") + ": "
                    + EnumChatFormatting.BLUE
                    + maximum
                    + EnumChatFormatting.RESET);
    }

    @Override
    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> steam = new ArrayList<>();
        FluidStack template = Materials.Steam.getGas(1);
        for (MTEHatchCustomFluidBase hatch : validMTEList(mSteamInputFluids)) {
            FluidStack fluid = hatch.getFillableStack();
            if (fluid != null && fluid.isFluidEqual(template)) steam.add(fluid);
        }
        return steam;
    }

    public long getSteamStoredLong() {
        long stored = 0;
        for (FluidStack fluid : getAllSteamStacks()) stored += fluid.amount;
        return stored;
    }

    public long getSteamCapacityLong() {
        long capacity = 0;
        for (MTEHatchCustomFluidBase hatch : validMTEList(mSteamInputFluids)) capacity += hatch.getRealCapacity();
        return capacity;
    }

    @Override
    public int getTotalSteamStored() {
        return (int) Math.min(Integer.MAX_VALUE, getSteamStoredLong());
    }

    @Override
    public int getTotalSteamCapacity() {
        return (int) Math.min(Integer.MAX_VALUE, getSteamCapacityLong());
    }

    @Override
    public boolean tryConsumeSteam(int amount) {
        return consumeSteam(amount);
    }

    private boolean consumeSteam(long amount) {
        if (amount < 0 || getSteamStoredLong() < amount) return false;
        long remaining = amount;
        FluidStack template = Materials.Steam.getGas(1);
        for (MTEHatchCustomFluidBase hatch : validMTEList(mSteamInputFluids)) {
            if (remaining == 0) break;
            FluidStack fluid = hatch.getFillableStack();
            if (fluid == null || !fluid.isFluidEqual(template)) continue;
            FluidStack drained = hatch.drain((int) Math.min(remaining, Integer.MAX_VALUE), true);
            if (drained != null) remaining -= drained.amount;
        }
        return remaining == 0;
    }

    public long getSteamUsagePerTick() {
        return lEUt < 0 ? getActualEnergyUsage() : 0;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (consumeSteam(getSteamUsagePerTick())) return true;
        stopMachine(ShutDownReasonRegistry.POWER_LOSS);
        return false;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        // Steam hatches are energy storage, never recipe ingredients. Preserve live recipe stacks for GT's commit path.
        ArrayList<FluidStack> fluids = new ArrayList<>();
        Map<Fluid, FluidStack> meFluids = new HashMap<>();
        for (MTEHatchInput hatch : validMTEList(mInputHatches)) {
            if (color.isPresent() && hatch.getColor() != -1 && hatch.getColor() != color.get()) continue;
            hatch.mRecipeMap = getRecipeMap();
            if (hatch instanceof MTEHatchInputME me) {
                for (FluidStack fluid : me.getStoredFluids()) {
                    if (fluid != null) meFluids.put(fluid.getFluid(), fluid);
                }
            } else if (hatch instanceof MTEHatchMultiInput multi) {
                for (FluidStack fluid : multi.getStoredFluid()) {
                    if (fluid != null) fluids.add(fluid);
                }
            } else if (hatch.getFillableStack() != null) {
                fluids.add(hatch.getFillableStack());
            }
        }
        fluids.addAll(meFluids.values());
        return fluids;
    }

    @Override
    public void updateSlots() {
        super.updateSlots();
        for (MTEHatchInput hatch : validMTEList(mInputHatches)) hatch.updateSlots();
        for (MTEHatchInputBus bus : validMTEList(mInputBusses)) bus.updateSlots();
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setInteger("bgeSteamTier", machineTier.getId());
        tag.setInteger("bgeSteamRecipeOcCount", recipeOcCount);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        machineTier = SteamMachineTier.fromId(tag.getInteger("bgeSteamTier"));
        recipeOcCount = tag.getInteger("bgeSteamRecipeOcCount");
    }

    @Override
    public byte getUpdateData() {
        return (byte) machineTier.getId();
    }

    @Override
    public void onValueUpdate(byte value) {
        machineTier = SteamMachineTier.fromId(value);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new BGTESteamMultiblockGui(this);
    }
}
