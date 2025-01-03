package com.charles445.rltweaker.asm.patch.otg;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class ChunkGeneratorMemoryLeakPatch extends PatchManager {

	public ChunkGeneratorMemoryLeakPatch() {
		this.add(new Patch(this, "com.pg85.otg.forge.generator.OTGChunkGenerator", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_setBlock = this.findMethod(clazzNode, "setBlock");
				FieldInsnNode target = ASMUtil.findFieldInsn(m_setBlock, Opcodes.PUTFIELD, "net/minecraft/world/World", "captureBlockSnapshots", "Z", 0);
				m_setBlock.instructions.insert(target, new InsnNode(Opcodes.POP2));
				m_setBlock.instructions.remove(target);
			}
		});
	}

}
