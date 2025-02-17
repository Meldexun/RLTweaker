package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchBetterCombatCriticalsFix extends PatchManager {
	public PatchBetterCombatCriticalsFix() {
		super("BetterCombat Criticals Fix");

		add(new Patch(this, "bettercombat.mod.util.Helpers", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_attackItem = this.findMethod(clazzNode, "attackTargetEntityItem");
				if (m_attackItem == null)
					throw new RuntimeException("Couldn't find attackTargetEntityItem in Helpers");

				LocalVariableNode lvn_isCrit = TransformUtil.findLocalVariableWithName(m_attackItem, "isCrit");
				if (lvn_isCrit == null)
					throw new RuntimeException("Couldn't find local variable isCrit in attackTargetEntityItem");

				int isCrit_index = lvn_isCrit.index;

				AbstractInsnNode anchor = first(m_attackItem);
				while (anchor != null) {
					anchor = ASMHelper.findNextInstructionWithOpcode(anchor, Opcodes.ISTORE);
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

					this.insert(m_attackItem, anchor, inject);
				}
			}
		});
	}
}
