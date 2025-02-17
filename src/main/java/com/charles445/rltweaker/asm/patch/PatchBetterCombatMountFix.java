package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchBetterCombatMountFix {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("bettercombat.mod.client.handler.EventHandlersClient", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, c_EventHandlersClient -> {
			MethodNode m_getMouseOverExtended = ASMUtil.find(c_EventHandlersClient, "getMouseOverExtended");

			// Get to the canBeCollidedWith check
			MethodInsnNode hookCaller = ASMUtil.first(m_getMouseOverExtended).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("canBeCollidedWith", "func_70067_L").find();

			if (hookCaller == null)
				throw new RuntimeException("Couldn't find canBeCollidedWith or func_70067_L");

			// Add another parameter load before replacing the hook, rvEntity
			LocalVariableNode lvn_rvEntity = ASMUtil.findLocalVariable(m_getMouseOverExtended, "rvEntity");
			if (lvn_rvEntity == null)
				throw new RuntimeException("Couldn't find local variable rvEntity");
			m_getMouseOverExtended.instructions.insertBefore(hookCaller, new VarInsnNode(Opcodes.ALOAD, lvn_rvEntity.index));

			// The hookCaller function now has a stack size of two: entity and rvEntity

			hookCaller.setOpcode(Opcodes.INVOKESTATIC);
			hookCaller.owner = "com/charles445/rltweaker/hook/HookBetterCombat";
			hookCaller.name = "strictCollisionCheck";
			hookCaller.desc = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Z";
		});
	}
}
