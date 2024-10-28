package com.charles445.rltweaker.asm.patch.otg;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class NearbyStructureCheckPatch extends PatchManager {

	public NearbyStructureCheckPatch() {
		this.add(new Patch(this, "com.pg85.otg.forge.world.ForgeWorld", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_isStructureInRadius = this.findMethod(clazzNode, "isStructureInRadius");
				m_isStructureInRadius.instructions.clear();
				m_isStructureInRadius.tryCatchBlocks.clear();
				m_isStructureInRadius.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new VarInsnNode(Opcodes.ILOAD, 3),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/otg/nearbystructurecheck/ForgeWorldHook", "isStructureInRadius", "(Lcom/pg85/otg/forge/world/ForgeWorld;Lcom/pg85/otg/util/ChunkCoordinate;Lnet/minecraft/world/gen/structure/MapGenStructure;I)Z", false),
					new InsnNode(Opcodes.IRETURN)
				));
			}
		});
	}

}
