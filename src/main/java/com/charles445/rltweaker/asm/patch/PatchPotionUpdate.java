package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.ASMUtil;

public class PatchPotionUpdate extends PatchManager {

	public PatchPotionUpdate() {
		this.add(new Patch(this, "net.minecraft.entity.player.EntityPlayerMP", ClassWriter.COMPUTE_FRAMES) {

			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_updatePotionMetadata = this.findMethod(clazzNode, "func_175135_B", "updatePotionMetadata");

				MethodInsnNode invoke_updateVisibility = ASMUtil.findMethodInsn(m_updatePotionMetadata, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityTracker", "func_180245_a", "updateVisibility", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V", 0);
				m_updatePotionMetadata.instructions.insertBefore(invoke_updateVisibility, ASMUtil.listOf(
						new InsnNode(Opcodes.POP2),
						new InsnNode(Opcodes.RETURN)
				));
			}

		});
	}

}
