package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchWaystoneScroll {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.blay09.mods.waystones.item.ItemBoundScroll", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// Fix bound scrolls trying to activate waystone in the source dimension and not the destination
			// By simply removing the waystone activation from bound and return scrolls
			// As it's inconsistent with warp scrolls anyway

			MethodNode m_onItemUseFinish = ASMUtil.findObf(clazzNode, "func_77654_b", "onItemUseFinish");
			if (m_onItemUseFinish == null)
				throw new RuntimeException("Couldn't find method func_77654_b or onItemUseFinish in ItemBoundScroll");

			AbstractInsnNode anchor = ASMUtil.first(m_onItemUseFinish).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("activateWaystone").find();

			if (anchor == null)
				throw new RuntimeException("Couldn't find activateWaystone call in ItemBoundScroll");

			// Step forward
			anchor = anchor.getNext();

			// Remove activateWaystone call entirely (6 instructions)
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
			m_onItemUseFinish.instructions.remove(anchor.getPrevious());
		});
	}
}
