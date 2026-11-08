package com.day.betterexperience.mixins.late;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.tileentities.machines.ISmartInputHatch;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEMultiBlockBaseMixin {

    @Shadow
    @Final
    private ArrayList<ISmartInputHatch> mSmartInputHatches;

    @Inject(method = "getOutputBusses", at = @At("RETURN"), cancellable = true)
    private void betterexperience$appendItemOutputs(CallbackInfoReturnable<List<IOutputBus>> cir) {
        List<IOutputBus> outputs = null;
        // Despite its name, GT .265 registers output watchers here too, including both distillation towers'
        // per-layer hatches. Reuse its native clear/rebuild lifecycle without flattening the fluid layer lists.
        for (var hatch : mSmartInputHatches) {
            if (!(hatch instanceof MEOutputAssembly assembly) || !assembly.isValid()) continue;
            if (outputs == null) outputs = new ArrayList<>(cir.getReturnValue());
            if (!outputs.contains(assembly.getItemOutput())) outputs.add(assembly.getItemOutput());
        }
        if (outputs != null) cir.setReturnValue(outputs);
    }

    @Inject(method = "addOutputBusToMachineList", at = @At("HEAD"), cancellable = true)
    private void betterexperience$acceptOutputAssembly(IGregTechTileEntity tile, int casingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        if (tile == null || !(tile.getMetaTileEntity() instanceof MEOutputAssembly assembly)) return;
        MTEMultiBlockBase machine = (MTEMultiBlockBase) (Object) this;
        // Keep one real hatch in the native list; no synthetic output bus or second MTE.
        if (machine.mOutputHatches.contains(assembly)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(machine.addOutputHatchToMachineList(tile, casingIndex));
        }
    }
}
