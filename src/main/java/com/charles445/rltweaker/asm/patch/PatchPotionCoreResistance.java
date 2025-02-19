package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchPotionCoreResistance {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("com.tmtravlr.potioncore.PotionCoreAttributes", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_clinit = ASMUtil.find(clazzNode, "<clinit>", "()V");

			AbstractInsnNode target = ASMUtil.first(m_clinit).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/ai/attributes/RangedAttribute", "func_111112_a", "setShouldWatch", "(Z)Lnet/minecraft/entity/ai/attributes/BaseAttribute;").ordinal(3).find();
			target = target.getPrevious();

			m_clinit.instructions.insertBefore(target, ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "resistance_createAttribute", "(Lnet/minecraft/entity/ai/attributes/RangedAttribute;)Lnet/minecraft/entity/ai/attributes/RangedAttribute;", false)
			));
		});

		registry.add("com.tmtravlr.potioncore.PotionCoreEffects", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_loadPotionEffects = ASMUtil.find(clazzNode, "loadPotionEffects", "(Lnet/minecraftforge/registries/IForgeRegistry;)V");

			MethodInsnNode i_registerPotionAttributeModifier = ASMUtil.first(m_loadPotionEffects).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/potion/Potion", "func_111184_a", "registerPotionAttributeModifier", "(Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;").find();
			MethodInsnNode redirect = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "resistance_registerPotionAttributeModifier", "(Lnet/minecraft/potion/Potion;Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", false);
			m_loadPotionEffects.instructions.set(i_registerPotionAttributeModifier, redirect);
		});

		registry.add("com.tmtravlr.potioncore.potion.PotionVulnerable", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_registerPotionAttributeModifiers = ASMUtil.find(clazzNode, "registerPotionAttributeModifiers", "()V");

			MethodInsnNode i_registerPotionAttributeModifier = ASMUtil.first(m_registerPotionAttributeModifiers).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("com/tmtravlr/potioncore/potion/PotionVulnerable", "func_111184_a", "registerPotionAttributeModifier", "(Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;").find();
			MethodInsnNode redirect = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "vulnerable_registerPotionAttributeModifier", "(Lnet/minecraft/potion/Potion;Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", false);
			m_registerPotionAttributeModifiers.instructions.set(i_registerPotionAttributeModifier, redirect);
		});

		registry.add("com.tmtravlr.potioncore.PotionCoreEventHandler", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_onLivingHurt = ASMUtil.find(clazzNode, "onLivingHurt", "(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V");

			MethodInsnNode i_getAdjustedDamageResistanceAttribute = ASMUtil.first(m_onLivingHurt).opcode(Opcodes.INVOKESTATIC).methodInsn("com/tmtravlr/potioncore/PotionCoreAttributes", "getAdjustedDamageResistanceAttribute", "(Lnet/minecraft/entity/EntityLivingBase;)D").find();
			MethodInsnNode redirect = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "getAdjustedDamageResistanceAttribute", "(Lnet/minecraft/entity/EntityLivingBase;)D", false);
			m_onLivingHurt.instructions.set(i_getAdjustedDamageResistanceAttribute, redirect);
		});

		registry.add("net.minecraft.entity.EntityLivingBase", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_applyPotionDamageCalculations = ASMUtil.findObf(clazzNode, "func_70672_c", "applyPotionDamageCalculations", "(Lnet/minecraft/util/DamageSource;F)F");

			AbstractInsnNode target = ASMUtil.first(m_applyPotionDamageCalculations).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70644_a", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z").find();
			target = ASMUtil.prev(m_applyPotionDamageCalculations, target).type(LabelNode.class).find();
			AbstractInsnNode target1 = ASMUtil.first(m_applyPotionDamageCalculations).opcode(Opcodes.IFGT).find();
			target1 = ASMUtil.prev(m_applyPotionDamageCalculations, target1).type(LabelNode.class).find();

			m_applyPotionDamageCalculations.instructions.insert(target, ASMUtil.listOf(
					new VarInsnNode(Opcodes.FLOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)
			));
			m_applyPotionDamageCalculations.instructions.insert(target1, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.FLOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false),
					new VarInsnNode(Opcodes.FSTORE, 2)
			));
		});

		registry.add("ichttt.mods.firstaid.common.util.ArmorUtils", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_applyPotionDamageCalculations = ASMUtil.find(clazzNode, "applyGlobalPotionModifiers", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/DamageSource;F)F");

			AbstractInsnNode target = ASMUtil.first(m_applyPotionDamageCalculations).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/player/EntityPlayer", "func_70644_a", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z").find();
			target = ASMUtil.prev(m_applyPotionDamageCalculations, target).type(LabelNode.class).find();
			AbstractInsnNode target1 = ASMUtil.first(m_applyPotionDamageCalculations).opcode(Opcodes.IFGT).find();
			target1 = ASMUtil.prev(m_applyPotionDamageCalculations, target1).type(LabelNode.class).find();

			m_applyPotionDamageCalculations.instructions.insert(target, ASMUtil.listOf(
					new VarInsnNode(Opcodes.FLOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)
			));
			m_applyPotionDamageCalculations.instructions.insert(target1, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.FLOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false),
					new VarInsnNode(Opcodes.FSTORE, 2)
			));
		});

		registry.add("com.tmtravlr.potioncore.PotionCoreEventHandlerClient", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_renderOverlaysPre = ASMUtil.find(clazzNode, "renderOverlaysPre", "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Pre;)V");
			MethodNode m_renderOverlaysPost = ASMUtil.find(clazzNode, "renderOverlaysPost", "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Post;)V");

			AbstractInsnNode target = ASMUtil.first(m_renderOverlaysPre).opcode(Opcodes.INVOKEINTERFACE).methodInsnObf("net/minecraft/entity/ai/attributes/IAttributeInstance", "func_111126_e", "getAttributeValue", "()D").find();
			target = target.getNext();
			target = target.getNext();
			AbstractInsnNode target1 = ASMUtil.first(m_renderOverlaysPost).opcode(Opcodes.INVOKEINTERFACE).methodInsnObf("net/minecraft/entity/ai/attributes/IAttributeInstance", "func_111126_e", "getAttributeValue", "()D").ordinal(1).find();
			target1 = target1.getNext();
			target1 = target1.getNext();

			m_renderOverlaysPre.instructions.insert(target, ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "getActualResistance", "(D)D", false)
			));
			m_renderOverlaysPost.instructions.insert(target1, ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "getActualResistance", "(D)D", false)
			));
		});
	}

}
