package com.charles445.rltweaker.asm.patch.minecraft;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class HurtByTargetInvalidTargetPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.addObf("net.minecraft.entity.ai.EntityAIHurtByTarget", "func_75249_e", "startExecuting", 0, method -> {
			MethodInsnNode setAttackTarget = ASMUtil.first(method).methodInsnObf("setAttackTarget", "func_70624_b").find();
			setAttackTarget.setOpcode(Opcodes.INVOKESTATIC);
			setAttackTarget.owner = "com/charles445/rltweaker/asm/patch/minecraft/InvestigateAIPatch$Hook";
			setAttackTarget.name = "setAttackTarget";
			setAttackTarget.desc = "(Lnet/minecraft/entity/EntityCreature;Lnet/minecraft/entity/EntityLivingBase;)V";
		});
		registry.addObf("net.minecraft.entity.ai.EntityAIHurtByTarget", "func_179446_a", "setEntityAttackTarget", 0, method -> {
			MethodInsnNode setAttackTarget = ASMUtil.first(method).methodInsnObf("setAttackTarget", "func_70624_b").find();
			setAttackTarget.setOpcode(Opcodes.INVOKESTATIC);
			setAttackTarget.owner = "com/charles445/rltweaker/asm/patch/minecraft/InvestigateAIPatch$Hook";
			setAttackTarget.name = "setAttackTarget";
			setAttackTarget.desc = "(Lnet/minecraft/entity/EntityCreature;Lnet/minecraft/entity/EntityLivingBase;)V";
		});
	}

	public static class Hook {

		public static void setAttackTarget(EntityCreature entity, EntityLivingBase target) {
			entity.setAttackTarget(target);
			if (entity.targetTasks.taskEntries.stream()
					.map(entry -> entry.action)
					.filter(EntityAINearestAttackableTarget.class::isInstance)
					.map(EntityAINearestAttackableTarget.class::cast)
					.filter(ai -> ai.targetClass.isInstance(target))
					.anyMatch(ai -> !ai.shouldContinueExecuting())) {
				entity.setAttackTarget(null);
			}
		}

	}

}
