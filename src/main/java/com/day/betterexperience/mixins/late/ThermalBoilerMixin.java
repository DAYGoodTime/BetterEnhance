package com.day.betterexperience.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import gregtech.common.tileentities.machines.multi.MTEThermalBoiler;

@Mixin(value = MTEThermalBoiler.class, remap = false)
public abstract class ThermalBoilerMixin {

    // These two lambdas bypass HatchElement.OutputBus.count in GT5U .265.
    @Inject(
        method = { "lambda$getStructureDefinition$1", "lambda$getStructureDefinition$4" },
        at = @At("RETURN"),
        cancellable = true,
        require = 2)
    private static void betterexperience$countOutputAssemblies(MTEThermalBoiler machine,
        CallbackInfoReturnable<Long> cir) {
        long count = MEOutputAssembly.countIn(machine);
        if (count != 0) cir.setReturnValue(cir.getReturnValue() + count);
    }
}
