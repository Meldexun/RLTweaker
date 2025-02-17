package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchBetterCombatCriticalsFix {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("bettercombat.mod.util.Helpers", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_attackItem = ASMUtil.find(clazzNode, "attackTargetEntityItem");
			if (m_attackItem == null)
				throw new RuntimeException("Couldn't find attackTargetEntityItem in Helpers");

			LocalVariableNode lvn_isCrit = ASMUtil.findLocalVariable(m_attackItem, "isCrit");
			if (lvn_isCrit == null)
				throw new RuntimeException("Couldn't find local variable isCrit in attackTargetEntityItem");

			int isCrit_index = lvn_isCrit.index;

			AbstractInsnNode anchor = m_attackItem.instructions.getFirst();
			while (anchor != null) {
				anchor = ASMUtil.next(anchor).opcode(Opcodes.ISTORE).find();
				if (anchor != null && ((VarInsnNode) anchor).var == isCrit_index)
					break;
			}

			if (anchor == null)
				throw new RuntimeException("Couldn't find first ISTORE for isCrit in attackTargetEntityItem");

			// Anchor has ISTORE isCrit
			if (true) {
				// ILOAD 2 would be a boolean for offhand, if that ever becomes necessary
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // EntityPlayer
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1)); // Entity
				inject.add(new VarInsnNode(Opcodes.ILOAD, isCrit_index)); // boolean
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookBetterCombat", "hookCriticalHit", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Z)Z", false));
				inject.add(new VarInsnNode(Opcodes.ISTORE, isCrit_index));

				m_attackItem.instructions.insert(anchor, inject);
			}
		});
	}
}
