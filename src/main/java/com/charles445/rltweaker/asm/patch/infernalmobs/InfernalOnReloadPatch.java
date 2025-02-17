package com.charles445.rltweaker.asm.patch.infernalmobs;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class InfernalOnReloadPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("atomicstryker.infernalmobs.common.InfernalMobsCore", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_processEntitySpawn = ASMUtil.find(clazzNode, "processEntitySpawn");
			JumpInsnNode target = ASMUtil.first(m_processEntitySpawn).type(JumpInsnNode.class).find();
			LabelNode label = new LabelNode();
			m_processEntitySpawn.instructions.insert(target, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;", false),
					new LdcInsnNode("InfernalMobsInitialized"),
					TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74767_n", "(Ljava/lang/String;)Z", false), // getBoolean
					new JumpInsnNode(Opcodes.IFEQ, label),
					new InsnNode(Opcodes.RETURN),
					label,
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;", false),
					new LdcInsnNode("InfernalMobsInitialized"),
					new InsnNode(Opcodes.ICONST_1),
					TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74757_a", "(Ljava/lang/String;Z)V", false) // setBoolean
			));
		});
	}

}
