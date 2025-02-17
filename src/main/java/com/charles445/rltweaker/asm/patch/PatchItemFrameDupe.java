package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchItemFrameDupe extends PatchManager {
	public PatchItemFrameDupe() {
		super("Item Frame Dupe");

		add(new Patch(this, "net.minecraft.entity.item.EntityItemFrame", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onBroken = findMethod(clazzNode, "func_110128_b", "onBroken");
				if (m_onBroken == null)
					throw new RuntimeException("Couldn't find func_110128_b or onBroken");

				AbstractInsnNode dropItemCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_onBroken), Opcodes.INVOKEVIRTUAL, "func_146065_b", "dropItemOrSelf");

				if (dropItemCall == null)
					throw new RuntimeException("Couldn't find call to func_146065_b or dropItemOrSelf");

				if (true) {
					InsnList inject = new InsnList();

					inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // EntityItemFrame
					inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "clearItemFrame", "(Lnet/minecraft/entity/item/EntityItemFrame;)V", false));

					insert(m_onBroken, dropItemCall, inject);
				}
			}
		});
	}
}
