package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class PatchPotionUpdate {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.entity.player.EntityPlayerMP", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_updatePotionMetadata = ASMUtil.findObf(clazzNode, "func_175135_B", "updatePotionMetadata");

			MethodInsnNode invoke_updateVisibility = ASMUtil.first(m_updatePotionMetadata).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityTracker", "func_180245_a", "updateVisibility", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V").find();
			m_updatePotionMetadata.instructions.insertBefore(invoke_updateVisibility, ASMUtil.listOf(
					new InsnNode(Opcodes.POP2),
					new InsnNode(Opcodes.RETURN)
			));
		});
	}

}
