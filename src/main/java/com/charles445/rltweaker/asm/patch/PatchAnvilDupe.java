package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchAnvilDupe extends PatchManager {
	public PatchAnvilDupe() {
		super("Anvil Dupe Fix");

		add(new Patch(this, "net.minecraftforge.common.ForgeHooks", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onAnvilChange = this.findMethod(clazzNode, "onAnvilChange");
				if (m_onAnvilChange == null)
					throw new RuntimeException("Couldn't find onAnvilChange");

				// ClassDisplayer.instance.printMethod(m_onAnvilChange);

				AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_onAnvilChange), Opcodes.INVOKEVIRTUAL, "post");
				if (anchor == null)
					throw new RuntimeException("Couldn't find post in onAnvilChange");

				anchor = ASMHelper.findNextInstructionWithOpcode(anchor, Opcodes.ICONST_0);
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
					this.insertBefore(m_onAnvilChange, anchor, inject);
				}

			}
		});
	}
}
