package com.charles445.rltweaker.asm.patch.minecraft;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class PreventStructureRecreationPatch extends PatchManager {

	public PreventStructureRecreationPatch() {
		this.add(new Patch(this, "net.minecraft.world.gen.ChunkProviderServer", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_loadChunkFromFile = this.findMethod(clazzNode, "func_73239_e", "loadChunkFromFile");
				MethodInsnNode i_recreateStructures = ASMUtil.findMethodInsn(m_loadChunkFromFile, Opcodes.INVOKEINTERFACE, "net/minecraft/world/gen/IChunkGenerator", "func_180514_a", "recreateStructures", "(Lnet/minecraft/world/chunk/Chunk;II)V", 0);
				m_loadChunkFromFile.instructions.insert(i_recreateStructures, ASMUtil.listOf(
						new InsnNode(Opcodes.POP2),
						new InsnNode(Opcodes.POP2)
				));
				m_loadChunkFromFile.instructions.remove(i_recreateStructures);
			}
		});
		this.add(new Patch(this, "net.minecraftforge.common.chunkio.ChunkIOProvider", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_syncCallback = this.findMethod(clazzNode, "syncCallback");
				MethodInsnNode i_recreateStructures = ASMUtil.findMethodInsn(m_syncCallback, Opcodes.INVOKEINTERFACE, "net/minecraft/world/gen/IChunkGenerator", "func_180514_a", "recreateStructures", "(Lnet/minecraft/world/chunk/Chunk;II)V", 0);
				m_syncCallback.instructions.insert(i_recreateStructures, ASMUtil.listOf(
						new InsnNode(Opcodes.POP2),
						new InsnNode(Opcodes.POP2)
				));
				m_syncCallback.instructions.remove(i_recreateStructures);
			}
		});
	}

}
