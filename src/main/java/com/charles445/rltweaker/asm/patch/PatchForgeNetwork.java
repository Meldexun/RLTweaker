package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchForgeNetwork extends PatchManager {
	public PatchForgeNetwork() {
		super("Forge Network");

		add(new Patch(this, "net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_channelRead0 = findMethod(clazzNode, "channelRead0");

				if (m_channelRead0 == null)
					throw new RuntimeException("Couldn't findMethod channelRead0");

				// ClassDisplayer.instance.printAllMethods(clazzNode);

				MethodInsnNode callOnMessage = TransformUtil.findNextCallWithOpcodeAndName(first(m_channelRead0), Opcodes.INVOKEINTERFACE, "onMessage");

				if (callOnMessage == null)
					throw new RuntimeException("Couldn't find onMessage in channelRead0");

				callOnMessage.setOpcode(Opcodes.INVOKESTATIC);
				callOnMessage.owner = "com/charles445/rltweaker/hook/HookForge";
				callOnMessage.name = "onMessage";
				callOnMessage.desc = "(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessageHandler;Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;";
				callOnMessage.itf = false;
				/*
				 * LocalVariableNode lvn_context = TransformUtil.findLocalVariableWithName(m_channelRead0, "context");
				 * 
				 * if(lvn_context == null)
				 * throw new RuntimeException("Couldn't findLocalVariableWithName context");
				 * 
				 * AbstractInsnNode anchor = first(m_channelRead0);
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
				 * LocalVariableNode lvn_msg = TransformUtil.findLocalVariableWithName(m_channelRead0, "msg");
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
			}
		});
	}
}
