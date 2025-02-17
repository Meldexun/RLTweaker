package com.charles445.rltweaker.asm.patch.otg;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class ChunkGeneratorMemoryLeakPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("com.pg85.otg.forge.generator.OTGChunkGenerator", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_setBlock = ASMUtil.find(clazzNode, "setBlock");
			MethodInsnNode target1 = ASMUtil.first(m_setBlock).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/world/chunk/Chunk", "func_177436_a", "setBlockState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;").find();
			FieldInsnNode target = ASMUtil.first(m_setBlock).opcode(Opcodes.PUTFIELD).fieldInsn("net/minecraft/world/World", "captureBlockSnapshots", "Z").find();
			m_setBlock.instructions.insert(target, new InsnNode(Opcodes.POP2));
			m_setBlock.instructions.remove(target);
			target1.setOpcode(Opcodes.INVOKESTATIC);
			target1.owner = "com/charles445/rltweaker/hook/otg/chunkgeneratormemoryleak/OTGChunkGeneratorHook";
			target1.name = "setBlockState";
			target1.desc = "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;";
		});
	}

}
