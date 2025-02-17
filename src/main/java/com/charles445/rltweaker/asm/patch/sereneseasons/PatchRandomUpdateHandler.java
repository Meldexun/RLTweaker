package com.charles445.rltweaker.asm.patch.sereneseasons;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class PatchRandomUpdateHandler {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.WorldServer", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// check if Serene Seasons is loaded
			try {
				Class.forName("sereneseasons.asm.SSLoadingPlugin");
			} catch (ClassNotFoundException e) {
				return;
			}

			MethodNode m_updateBlocks = ASMUtil.findObf(clazzNode, "func_147456_g", "updateBlocks");

			m_updateBlocks.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookSereneSeasons", "head_WorldServer_updateBlocks", "(Lnet/minecraft/world/World;)V", false)
			));

			MethodInsnNode invoke_onTick = ASMUtil.first(m_updateBlocks).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/world/chunk/Chunk", "func_150804_b", "onTick", "(Z)V").ordinal(1).find();
			MethodInsnNode invoke_next = ASMUtil.first(m_updateBlocks).opcode(Opcodes.INVOKEINTERFACE).methodInsn("java/util/Iterator", "next", "()Ljava/lang/Object;").ordinal(1).find();
			m_updateBlocks.instructions.insert(invoke_onTick, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) invoke_next.getNext().getNext()).var),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookSereneSeasons", "post_Chunk_onTick", "(Lnet/minecraft/world/chunk/Chunk;)V", false)
			));
		});
		registry.add("sereneseasons.handler.season.RandomUpdateHandler", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_onWorldTick = ASMUtil.find(clazzNode, "onWorldTick");

			m_onWorldTick.visibleAnnotations.removeIf(annotation -> annotation.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;"));
		});
	}

}
