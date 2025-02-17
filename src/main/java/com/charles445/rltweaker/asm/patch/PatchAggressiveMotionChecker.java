package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchAggressiveMotionChecker {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.entity.monster.EntityMob", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			if (true) {
				MethodNode m_attackEntityAsMob = ASMUtil.findObf(clazzNode, "func_70652_k", "attackEntityAsMob");
				if (m_attackEntityAsMob == null)
					throw new RuntimeException("Couldn't find func_70652_k or attackEntityAsMob");

				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(hookMotionCheck());
				m_attackEntityAsMob.instructions.insert(m_attackEntityAsMob.instructions.getFirst(), inject);
			}
		});

		registry.add("net.minecraft.entity.Entity", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			if (true) {
				// func_70091_d,move
				MethodNode m_move = ASMUtil.findObf(clazzNode, "func_70091_d", "move");
				if (m_move == null)
					throw new RuntimeException("Couldn't find func_70091_d or move");

				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(hookMotionCheck());
				m_move.instructions.insert(m_move.instructions.getFirst(), inject);
			}
		});
	}

	private static MethodInsnNode hookMotionCheck() {
		return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "aggressiveMotionCheck", "(Lnet/minecraft/entity/Entity;)V", false);
	}

}
