package com.charles445.rltweaker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.item.ItemStack;

@Pseudo
@Mixin(targets = "pw.prok.realbench.asm.ASMHooks", remap = false)
public class MixinASMHooks {

	@ModifyArg(method = "dropBlockAsItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"), index = 4, remap = false, require = 0)
	private static ItemStack split(ItemStack stack) {
		return stack.splitStack(Integer.MAX_VALUE);
	}

}
