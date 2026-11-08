package com.day.betterexperience.mixins.late;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.day.betterexperience.common.machine.hatch.MEOutputAssembly;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.tileentities.machines.multi.MTEAssemblyLine;

@Mixin(value = MTEAssemblyLine.class, remap = false)
public abstract class AssemblyLineMixin {

    @Redirect(
        method = "checkMachine(ZLjava/util/List;)I",
        at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z"),
        require = 1)
    private boolean betterexperience$recognizeOutputLayer(ArrayList<?> busses) {
        return busses.isEmpty() && MEOutputAssembly.countIn((MTEMultiBlockBase) (Object) this) == 0;
    }
}
