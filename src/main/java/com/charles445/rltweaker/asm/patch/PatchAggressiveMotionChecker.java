package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchAggressiveMotionChecker extends PatchManager {
	public PatchAggressiveMotionChecker() {
		super("Aggressive Motion Checker");

		add(new Patch(this, "net.minecraft.entity.monster.EntityMob", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (true) {
					MethodNode m_attackEntityAsMob = findMethod(clazzNode, "func_70652_k", "attackEntityAsMob");
					if (m_attackEntityAsMob == null)
						throw new RuntimeException("Couldn't find func_70652_k or attackEntityAsMob");

					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(hookMotionCheck());
					insert(m_attackEntityAsMob, first(m_attackEntityAsMob), inject);
				}
			}
		});

		add(new Patch(this, "net.minecraft.entity.Entity", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (true) {
					// func_70091_d,move
					MethodNode m_move = findMethod(clazzNode, "func_70091_d", "move");
					if (m_move == null)
						throw new RuntimeException("Couldn't find func_70091_d or move");

					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(hookMotionCheck());
					insert(m_move, first(m_move), inject);
				}
			}
		});
	}

	private MethodInsnNode hookMotionCheck() {
		return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "aggressiveMotionCheck", "(Lnet/minecraft/entity/Entity;)V", false);
	}

}
