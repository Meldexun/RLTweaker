package com.charles445.rltweaker.asm.patch.sereneseasons;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class PatchMelting extends PatchManager {

	public PatchMelting() {
		this.add(new Patch(this, "net.minecraft.world.WorldServer", ClassWriter.COMPUTE_MAXS) {

			@Override
			public void patch(ClassNode clazzNode) {
				// check if Serene Seasons is loaded
				try {
					Class.forName("sereneseasons.asm.SSLoadingPlugin");
				} catch (ClassNotFoundException e) {
					return;
				}

				MethodNode m_updateBlocks = this.findMethod(clazzNode, "func_147456_g", "updateBlocks");

				m_updateBlocks.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookSereneSeasons", "head_WorldServer_updateBlocks", "(Lnet/minecraft/world/World;)V", false)
				));

				MethodInsnNode invoke_onTick = ASMUtil.findMethodInsn(m_updateBlocks, Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/Chunk", "func_150804_b", "onTick", "(Z)V", 1);
				MethodInsnNode invoke_next = ASMUtil.findMethodInsn(m_updateBlocks, Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", 1);
				m_updateBlocks.instructions.insert(invoke_onTick, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) invoke_next.getNext().getNext()).var),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookSereneSeasons", "post_Chunk_onTick", "(Lnet/minecraft/world/chunk/Chunk;)V", false)
				));
			}

		});
	}

}
