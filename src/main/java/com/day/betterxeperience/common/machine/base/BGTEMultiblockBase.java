package com.day.betterxeperience.common.machine.base;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;

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
        return 1;
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
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public boolean supportsMaintenanceIssueHoverable() {
        return false;
    }

    @Override
    public boolean showRecipeTextInGUI() {
        return false;
    }
}
