package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class PatchRustic {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("rustic.common.blocks.fluids.ModFluids$12", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_affectPlayer = ASMUtil.find(clazzNode, "affectPlayer");

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/player/EntityPlayer", "func_70651_bq", "getActivePotionEffects", "()Ljava/util/Collection;").find(), ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/util/CollectionUtil", "copy", "(Ljava/util/Collection;)Ljava/util/Collection;", false)
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.ICONST_2).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierMaximum", "I")
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.ICONST_1).ordinal(1).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.ICONST_1).ordinal(2).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")
			));
		});
		registry.add("rustic.common.blocks.fluids.ModFluids$13", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_affectPlayer = ASMUtil.find(clazzNode, "affectPlayer");

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/player/EntityPlayer", "func_70651_bq", "getActivePotionEffects", "()Ljava/util/Collection;").find(), ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/util/CollectionUtil", "copy", "(Ljava/util/Collection;)Ljava/util/Collection;", false)
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.SIPUSH).intInsn(12000).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).opcode(Opcodes.SIPUSH).intInsn(12000).ordinal(1).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).ldcInsn(2400.0F).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"),
					new InsnNode(Opcodes.I2F)
			));

			m_affectPlayer.instructions.insert(ASMUtil.first(m_affectPlayer).ldcInsn(2400.0D).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP2),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
					new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"),
					new InsnNode(Opcodes.I2D)
			));
		});
	}

}
