package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchItemLinking {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("vazkii.quark.management.feature.LinkItems", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_linkItem = ASMUtil.find(clazzNode, "linkItem");

			MethodInsnNode onDisconnect = ASMUtil.first(m_linkItem).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/network/NetHandlerPlayServer", "func_147231_a", "onDisconnect", "(Lnet/minecraft/util/text/ITextComponent;)V").find();
			m_linkItem.instructions.set(onDisconnect, TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/NetHandlerPlayServer", "func_194028_b", "(Lnet/minecraft/util/text/ITextComponent;)V", false));

			LabelNode labelNode = new LabelNode();
			m_linkItem.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookQuark", "tryLinkItem", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false),
					new JumpInsnNode(Opcodes.IFNE, labelNode),
					new InsnNode(Opcodes.RETURN),
					labelNode
			));
		});
	}

}
