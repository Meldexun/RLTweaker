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

public class PatchAnvilDupe {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraftforge.common.ForgeHooks", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_onAnvilChange = ASMUtil.find(clazzNode, "onAnvilChange");
			if (m_onAnvilChange == null)
				throw new RuntimeException("Couldn't find onAnvilChange");

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_onAnvilChange));

			AbstractInsnNode anchor = ASMUtil.first(m_onAnvilChange).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("post").find();
			if (anchor == null)
				throw new RuntimeException("Couldn't find post in onAnvilChange");

			anchor = ASMUtil.next(anchor).opcode(Opcodes.ICONST_0).find();
			if (anchor == null)
				throw new RuntimeException("Couldn't find next ICONST_0 in onAnvilChange");

			if (anchor.getNext().getOpcode() != Opcodes.IRETURN)
				throw new RuntimeException("Unexpected ICONST_0 in onAnvilChange not paired with IRETURN");

			if (true) {
				InsnList inject = new InsnList();

				inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // ContainerRepair
				inject.add(new VarInsnNode(Opcodes.ALOAD, 3)); // IInventory
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "clearAnvilResult", "(Lnet/minecraft/inventory/ContainerRepair;Lnet/minecraft/inventory/IInventory;)V", false));

				// Insert before the ICONST_0
				m_onAnvilChange.instructions.insertBefore(anchor, inject);
			}

		});
	}
}
