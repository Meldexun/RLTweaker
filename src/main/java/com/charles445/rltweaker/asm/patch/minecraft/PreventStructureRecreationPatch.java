package com.charles445.rltweaker.asm.patch.minecraft;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class PreventStructureRecreationPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.gen.ChunkProviderServer", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_loadChunkFromFile = ASMUtil.findObf(clazzNode, "func_73239_e", "loadChunkFromFile");
			MethodInsnNode i_recreateStructures = ASMUtil.first(m_loadChunkFromFile).opcode(Opcodes.INVOKEINTERFACE).methodInsnObf("net/minecraft/world/gen/IChunkGenerator", "func_180514_a", "recreateStructures", "(Lnet/minecraft/world/chunk/Chunk;II)V").find();
			m_loadChunkFromFile.instructions.insert(i_recreateStructures, ASMUtil.listOf(
					new InsnNode(Opcodes.POP2),
					new InsnNode(Opcodes.POP2)
			));
			m_loadChunkFromFile.instructions.remove(i_recreateStructures);
		});
		registry.add("net.minecraftforge.common.chunkio.ChunkIOProvider", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_syncCallback = ASMUtil.find(clazzNode, "syncCallback");
			MethodInsnNode i_recreateStructures = ASMUtil.first(m_syncCallback).opcode(Opcodes.INVOKEINTERFACE).methodInsnObf("net/minecraft/world/gen/IChunkGenerator", "func_180514_a", "recreateStructures", "(Lnet/minecraft/world/chunk/Chunk;II)V").find();
			m_syncCallback.instructions.insert(i_recreateStructures, ASMUtil.listOf(
					new InsnNode(Opcodes.POP2),
					new InsnNode(Opcodes.POP2)
			));
			m_syncCallback.instructions.remove(i_recreateStructures);
		});
	}

}
