package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchWaystoneScroll extends PatchManager {
	public PatchWaystoneScroll() {
		super("Waystone Scroll");

		add(new Patch(this, "net.blay09.mods.waystones.item.ItemBoundScroll", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				// Fix bound scrolls trying to activate waystone in the source dimension and not the destination
				// By simply removing the waystone activation from bound and return scrolls
				// As it's inconsistent with warp scrolls anyway

				MethodNode m_onItemUseFinish = this.findMethod(clazzNode, "func_77654_b", "onItemUseFinish");
				if (m_onItemUseFinish == null)
					throw new RuntimeException("Couldn't find method func_77654_b or onItemUseFinish in ItemBoundScroll");

				AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_onItemUseFinish), Opcodes.INVOKEVIRTUAL, "activateWaystone");

				if (anchor == null)
					throw new RuntimeException("Couldn't find activateWaystone call in ItemBoundScroll");

				// Step forward
				anchor = anchor.getNext();

				// Remove activateWaystone call entirely (6 instructions)
				this.removePreviousInsn(m_onItemUseFinish, anchor);
				this.removePreviousInsn(m_onItemUseFinish, anchor);
				this.removePreviousInsn(m_onItemUseFinish, anchor);
				this.removePreviousInsn(m_onItemUseFinish, anchor);
				this.removePreviousInsn(m_onItemUseFinish, anchor);
				this.removePreviousInsn(m_onItemUseFinish, anchor);
			}
		});
	}
}
