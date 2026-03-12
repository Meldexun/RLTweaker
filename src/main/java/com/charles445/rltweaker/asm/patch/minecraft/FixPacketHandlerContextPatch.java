package com.charles445.rltweaker.asm.patch.minecraft;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.network.INetHandler;

public class FixPacketHandlerContextPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.addObf("net.minecraftforge.fml.common.network.internal.FMLProxyPacket", "processPacket", "func_148833_a", ClassWriter.COMPUTE_FRAMES, methodNode -> {
			methodNode.instructions.insert(ASMUtil.first(methodNode).methodInsn("set").find(), ASMUtil.listOf(
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/asm/patch/minecraft/FixPacketHandlerContextPatch$Hook", "NET_HANDLER", "Ljava/lang/ThreadLocal;"),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "set", "(Ljava/lang/Object;)V", false)));
			methodNode.instructions.insert(ASMUtil.first(methodNode).type(JumpInsnNode.class).find().label, ASMUtil.listOf(
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/asm/patch/minecraft/FixPacketHandlerContextPatch$Hook", "NET_HANDLER", "Ljava/lang/ThreadLocal;"),
					new InsnNode(Opcodes.ACONST_NULL),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "set", "(Ljava/lang/Object;)V", false)));
		});
		registry.add("net.minecraftforge.fml.common.network.internal.EntitySpawnHandler", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraftforge/fml/common/network/internal/FMLMessage$EntityMessage;)V", 0, methodNode -> {
			methodNode.instructions.insertBefore(ASMUtil.first(methodNode).methodInsn("getWorldThread").find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/asm/patch/minecraft/FixPacketHandlerContextPatch$Hook", "NET_HANDLER", "Ljava/lang/ThreadLocal;"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false),
					new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/network/INetHandler")));
		});
		registry.add("net.minecraftforge.fml.common.network.internal.OpenGuiHandler", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraftforge/fml/common/network/internal/FMLMessage$OpenGui;)V", 0, methodNode -> {
			methodNode.instructions.insertBefore(ASMUtil.first(methodNode).methodInsn("getWorldThread").find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/asm/patch/minecraft/FixPacketHandlerContextPatch$Hook", "NET_HANDLER", "Ljava/lang/ThreadLocal;"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false),
					new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/network/INetHandler")));
		});
		registry.add("net.minecraftforge.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;)V", 0, methodNode -> {
			LocalVariableNode iNetHandler = ASMUtil.findLocalVariable(methodNode, "iNetHandler", "Lnet/minecraft/network/INetHandler;");
			methodNode.instructions.insert(ASMUtil.first(methodNode).opcode(Opcodes.ASTORE).varInsn(iNetHandler.index).find(), ASMUtil.listOf(
					new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/asm/patch/minecraft/FixPacketHandlerContextPatch$Hook", "NET_HANDLER", "Ljava/lang/ThreadLocal;"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/ThreadLocal", "get", "()Ljava/lang/Object;", false),
					new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/network/INetHandler"),
					new VarInsnNode(Opcodes.ASTORE, iNetHandler.index)));
		});
	}

	public static class Hook {

		public static final ThreadLocal<INetHandler> NET_HANDLER = new ThreadLocal<>();

	}

}
