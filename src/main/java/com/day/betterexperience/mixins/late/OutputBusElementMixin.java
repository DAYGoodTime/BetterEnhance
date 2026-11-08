package com.day.betterexperience.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

// GT5U 5.09.54.265: OutputBus is HatchElement's sixth anonymous constant subclass.
@Mixin(targets = "gregtech.api.enums.HatchElement$6", remap = false)
public abstract class OutputBusElementMixin {

    @Inject(
        method = "count(Lgregtech/api/metatileentity/implementations/MTEMultiBlockBase;)J",
        at = @At("RETURN"),
        cancellable = true)
    private void betterexperience$countOutputAssemblies(MTEMultiBlockBase machine, CallbackInfoReturnable<Long> cir) {
        long count = MEOutputAssembly.countIn(machine);
        if (count != 0) cir.setReturnValue(cir.getReturnValue() + count);
    }
}
