package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchPotionCoreResistance extends PatchManager {

	public PatchPotionCoreResistance() {
		super("Patch Potion Core Resistance");

		add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreEffects",
				ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_loadPotionEffects = this.findMethodWithDesc(clazzNode,
						"(Lnet/minecraftforge/registries/IForgeRegistry;)V", "loadPotionEffects");

				MethodInsnNode i_registerPotionAttributeModifier = ASMUtil.findMethodInsn(m_loadPotionEffects,
						Opcodes.INVOKEVIRTUAL, "net/minecraft/potion/Potion", "func_111184_a",
						"registerPotionAttributeModifier",
						"(Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;",
						0);
				MethodInsnNode redirect = new MethodInsnNode(Opcodes.INVOKESTATIC,
						"com/charles445/rltweaker/hook/HookPotionCore", "resistance_registerPotionAttributeModifier",
						"(Lnet/minecraft/potion/Potion;Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;",
						false);
				m_loadPotionEffects.instructions.set(i_registerPotionAttributeModifier, redirect);
			}
		});

		add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreEventHandler",
				ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onLivingHurt = this.findMethodWithDesc(clazzNode,
						"(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", "onLivingHurt");

				MethodInsnNode i_getAdjustedDamageResistanceAttribute = ASMUtil.findMethodInsn(m_onLivingHurt,
						Opcodes.INVOKESTATIC, "com/tmtravlr/potioncore/PotionCoreAttributes",
						"getAdjustedDamageResistanceAttribute", "(Lnet/minecraft/entity/EntityLivingBase;)D", 0);
				MethodInsnNode redirect = new MethodInsnNode(Opcodes.INVOKESTATIC,
						"com/charles445/rltweaker/hook/HookPotionCore", "getAdjustedDamageResistanceAttribute",
						"(Lnet/minecraft/entity/EntityLivingBase;)D", false);
				m_onLivingHurt.instructions.set(i_getAdjustedDamageResistanceAttribute, redirect);
			}
		});

		add(new Patch(this, "net.minecraft.entity.EntityLivingBase",
				ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_applyPotionDamageCalculations = this.findMethodWithDesc(clazzNode,
						"(Lnet/minecraft/util/DamageSource;F)F", "func_70672_c", "applyPotionDamageCalculations");

				AbstractInsnNode target = ASMUtil.findMethodInsn(m_applyPotionDamageCalculations, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", "func_70644_a", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z", 0);
				target = TransformUtil.findPreviousInsnWithType(target, LabelNode.class);
				AbstractInsnNode target1 = ASMUtil.findInsnWithOpcode(m_applyPotionDamageCalculations, Opcodes.IFGT, 0);
				target1 = TransformUtil.findPreviousInsnWithType(target1, LabelNode.class);

				insert(m_applyPotionDamageCalculations, target, ASMUtil.listOf(
						new VarInsnNode(Opcodes.FLOAD, 2),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)));
				insert(m_applyPotionDamageCalculations, target1, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.FLOAD, 2),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false),
						new VarInsnNode(Opcodes.FSTORE, 2)));
			}
		});

		add(new Patch(this, "ichttt.mods.firstaid.common.util.ArmorUtils",
				ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_applyPotionDamageCalculations = this.findMethodWithDesc(clazzNode,
						"(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/DamageSource;F)F",
						"applyGlobalPotionModifiers");

				AbstractInsnNode target = ASMUtil.findMethodInsn(m_applyPotionDamageCalculations, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "func_70644_a", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z", 0);
				target = TransformUtil.findPreviousInsnWithType(target, LabelNode.class);
				AbstractInsnNode target1 = ASMUtil.findInsnWithOpcode(m_applyPotionDamageCalculations, Opcodes.IFGT, 0);
				target1 = TransformUtil.findPreviousInsnWithType(target1, LabelNode.class);

				insert(m_applyPotionDamageCalculations, target, ASMUtil.listOf(
						new VarInsnNode(Opcodes.FLOAD, 2),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)));
				insert(m_applyPotionDamageCalculations, target1, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.FLOAD, 2),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false),
						new VarInsnNode(Opcodes.FSTORE, 2)));
			}
		});
	}

}
