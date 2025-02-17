package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchBetterCombatMountFix extends PatchManager {
	public PatchBetterCombatMountFix() {
		super("BetterCombat Mount Fix");

		add(new Patch(this, "bettercombat.mod.client.handler.EventHandlersClient", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode c_EventHandlersClient) {
				MethodNode m_getMouseOverExtended = findMethod(c_EventHandlersClient, "getMouseOverExtended");

				// Get to the canBeCollidedWith check
				MethodInsnNode hookCaller = TransformUtil.findNextCallWithOpcodeAndName(first(m_getMouseOverExtended), Opcodes.INVOKEVIRTUAL, "canBeCollidedWith", "func_70067_L");

				if (hookCaller == null)
					throw new RuntimeException("Couldn't find canBeCollidedWith or func_70067_L");

				// Add another parameter load before replacing the hook, rvEntity
				LocalVariableNode lvn_rvEntity = TransformUtil.findLocalVariableWithName(m_getMouseOverExtended, "rvEntity");
				if (lvn_rvEntity == null)
					throw new RuntimeException("Couldn't find local variable rvEntity");
				m_getMouseOverExtended.instructions.insertBefore(hookCaller, new VarInsnNode(Opcodes.ALOAD, lvn_rvEntity.index));

				// The hookCaller function now has a stack size of two: entity and rvEntity

				hookCaller.setOpcode(Opcodes.INVOKESTATIC);
				hookCaller.owner = "com/charles445/rltweaker/hook/HookBetterCombat";
				hookCaller.name = "strictCollisionCheck";
				hookCaller.desc = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Z";
			}
		});
	}
}
