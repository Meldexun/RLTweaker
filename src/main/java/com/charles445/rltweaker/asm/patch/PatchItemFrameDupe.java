package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchItemFrameDupe {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.entity.item.EntityItemFrame", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_onBroken = ASMUtil.findObf(clazzNode, "func_110128_b", "onBroken");
			if (m_onBroken == null)
				throw new RuntimeException("Couldn't find func_110128_b or onBroken");

			AbstractInsnNode dropItemCall = ASMUtil.first(m_onBroken).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_146065_b", "dropItemOrSelf").find();

			if (dropItemCall == null)
				throw new RuntimeException("Couldn't find call to func_146065_b or dropItemOrSelf");

			if (true) {
				InsnList inject = new InsnList();

				inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // EntityItemFrame
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "clearItemFrame", "(Lnet/minecraft/entity/item/EntityItemFrame;)V", false));

				m_onBroken.instructions.insert(dropItemCall, inject);
			}
		});
	}
}
