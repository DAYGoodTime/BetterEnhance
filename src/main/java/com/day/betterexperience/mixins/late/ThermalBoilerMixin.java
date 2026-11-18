package com.day.betterexperience.mixins.late;

import java.util.function.ToLongFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.common.tileentities.machines.multi.MTEThermalBoiler;

@Mixin(value = MTEThermalBoiler.class, remap = false)
public abstract class ThermalBoilerMixin {

    // GT5U .265 overrides both sides' bus counts. Wrap only this machine's OutputBus counters,
    // not compiler-generated lambda names or the global structure builder.
    @WrapOperation(
        method = "getStructureDefinition",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/enums/HatchElement;withCount(Ljava/util/function/ToLongFunction;)Lgregtech/api/interfaces/IHatchElement;"),
        require = 4,
        allow = 4)
    private IHatchElement<MTEThermalBoiler> betterexperience$countOutputAssemblies(HatchElement element,
        ToLongFunction<MTEThermalBoiler> count, Operation<IHatchElement<MTEThermalBoiler>> original) {
        if (element != HatchElement.OutputBus) return original.call(element, count);
        ToLongFunction<MTEThermalBoiler> assemblyCount = machine -> count.applyAsLong(machine)
            + MEOutputAssembly.countIn(machine);
        return original.call(element, assemblyCount);
    }
}
