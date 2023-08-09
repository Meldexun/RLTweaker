package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchItemLinking extends PatchManager {

	public PatchItemLinking() {
		super("Patch Item Linking");

		this.add(new Patch(this, "vazkii.quark.management.feature.LinkItems", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_linkItem = this.findMethod(clazzNode, "linkItem");

				MethodInsnNode onDisconnect = ASMUtil.findMethodInsn(m_linkItem, Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "func_147231_a", "onDisconnect", "(Lnet/minecraft/util/text/ITextComponent;)V", 0);
				m_linkItem.instructions.set(onDisconnect, TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "func_194028_b", "(Lnet/minecraft/util/text/ITextComponent;)V", false));

				LabelNode labelNode = new LabelNode();
				TransformUtil.insertBeforeFirst(m_linkItem, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookQuark", "tryLinkItem", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false),
						new JumpInsnNode(Opcodes.IFNE, labelNode),
						new InsnNode(Opcodes.RETURN),
						labelNode));
			}
		});
	}

}
