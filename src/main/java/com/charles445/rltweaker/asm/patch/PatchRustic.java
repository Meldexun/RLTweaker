package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.ASMUtil;

public class PatchRustic extends PatchManager {

	public PatchRustic() {
		this.add(new Patch(this, "rustic.common.blocks.fluids.ModFluids$12", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {

			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_affectPlayer = this.findMethod(clazzNode, "affectPlayer");

				this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, Opcodes.ICONST_2, 0), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierMaximum", "I")));

				this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, Opcodes.ICONST_1, 1), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")));

				this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, Opcodes.ICONST_1, 2), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")));
			}

		});
		this.add(new Patch(this, "rustic.common.blocks.fluids.ModFluids$13", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {

			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_affectPlayer = this.findMethod(clazzNode, "affectPlayer");

				this.insert(m_affectPlayer, ASMUtil.findIntInsn(m_affectPlayer, Opcodes.SIPUSH, 12000, 0), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")));

				this.insert(m_affectPlayer, ASMUtil.findIntInsn(m_affectPlayer, Opcodes.SIPUSH, 12000, 1), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")));

				this.insert(m_affectPlayer, ASMUtil.findLdcInsn(m_affectPlayer, 2400.0F, 0), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"),
						new InsnNode(Opcodes.I2F)));

				this.insert(m_affectPlayer, ASMUtil.findLdcInsn(m_affectPlayer, 2400.0D, 0), ASMUtil.listOf(
						new InsnNode(Opcodes.POP2),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"),
						new InsnNode(Opcodes.I2D)));
			}

		});
	}

}
