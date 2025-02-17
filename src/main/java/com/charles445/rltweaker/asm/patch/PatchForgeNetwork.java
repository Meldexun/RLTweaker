package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchForgeNetwork {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_channelRead0 = ASMUtil.find(clazzNode, "channelRead0");

			if (m_channelRead0 == null)
				throw new RuntimeException("Couldn't findMethod channelRead0");

			// ClassDisplayer.instance.printAllMethods(clazzNode);

			MethodInsnNode callOnMessage = ASMUtil.first(m_channelRead0).opcode(Opcodes.INVOKEINTERFACE).methodInsn("onMessage").find();

			if (callOnMessage == null)
				throw new RuntimeException("Couldn't find onMessage in channelRead0");

			callOnMessage.setOpcode(Opcodes.INVOKESTATIC);
			callOnMessage.owner = "com/charles445/rltweaker/hook/HookForge";
			callOnMessage.name = "onMessage";
			callOnMessage.desc = "(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessageHandler;Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;";
			callOnMessage.itf = false;
			/*
			 * LocalVariableNode lvn_context = ASMUtil.findLocalVariable(m_channelRead0, "context");
			 * 
			 * if(lvn_context == null)
			 * throw new RuntimeException("Couldn't findLocalVariableWithName context");
			 * 
			 * AbstractInsnNode anchor = m_channelRead0.instructions.getFirst();
			 * 
			 * while(anchor != null)
			 * {
			 * if(anchor.getType() == AbstractInsnNode.VAR_INSN)
			 * {
			 * VarInsnNode vAnchor = (VarInsnNode)anchor;
			 * if(vAnchor.getOpcode() == Opcodes.ASTORE && vAnchor.var == lvn_context.index)
			 * {
			 * break;
			 * }
			 * }
			 * 
			 * anchor = anchor.getNext();
			 * }
			 * 
			 * if(anchor == null)
			 * throw new RuntimeException("Couldn't find 'context' astore");
			 * 
			 * //Anchor has our astore
			 * //We also need msg
			 * LocalVariableNode lvn_msg = ASMUtil.findLocalVariable(m_channelRead0, "msg");
			 * 
			 * if(lvn_msg == null)
			 * throw new RuntimeException("Couldn't findLocalVariableWithName msg");
			 * 
			 * //Create hook...
			 * InsnList insert = new InsnList();
			 * insert.add(new VarInsnNode(Opcodes.ALOAD, lvn_msg.index));
			 * insert.add(new VarInsnNode(Opcodes.ALOAD, lvn_context.index));
			 * //stack has msg, context
			 * insert.add(new MethodInsnNode(
			 * Opcodes.INVOKESTATIC,
			 * "com/charles445/rltweaker/hook/HookForge",
			 * "receiveMessage",
			 * "(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)V",
			 * false));
			 * 
			 * m_channelRead0.instructions.insert(anchor, insert);
			 */
		});
	}
}
