package com.day.betterexperience.mixins.late;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;

import com.day.betterexperience.common.recipe.CraftingRecipe;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import gregtech.api.recipe.maps.AssemblerBackend;
import gregtech.api.util.GTRecipe;

@Mixin(value = AssemblerBackend.class, remap = false)
public class AssemblerBackendMixin {

    @WrapMethod(
        method = "modifyFoundRecipe(Lgregtech/api/util/GTRecipe;[Lnet/minecraft/item/ItemStack;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;)Lgregtech/api/util/GTRecipe;",
        require = 1,
        allow = 1)
    private GTRecipe betterexperience$inheritOutputCapacities(GTRecipe recipe, ItemStack[] items, FluidStack[] fluids,
        ItemStack specialSlot, Operation<GTRecipe> original) {
        // Keep the entry recipe: vanilla can reassign its recipe argument to a metadata-free copy.
        GTRecipe found = original.call(recipe, items, fluids, specialSlot);
        return CraftingRecipe.inheritOutputCapacities(recipe, found, items);
    }
}
