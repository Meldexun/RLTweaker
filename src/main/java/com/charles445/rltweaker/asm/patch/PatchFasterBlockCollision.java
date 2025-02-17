package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchFasterBlockCollision {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.World", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_getCollisionBoxes = ASMUtil.find(clazzNode, "func_191504_a", "getCollisionBoxes", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;ZLjava/util/List;)Z");

			LabelNode labelNode = new LabelNode();
			m_getCollisionBoxes.instructions.insert(ASMUtil.listOf(
					new InsnNode(Opcodes.ICONST_1),
					new JumpInsnNode(Opcodes.IFEQ, labelNode),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new VarInsnNode(Opcodes.ILOAD, 3),
					new VarInsnNode(Opcodes.ALOAD, 4),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookWorld", "getCollisionBoxes", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;ZLjava/util/List;)Z", false),
					new InsnNode(Opcodes.IRETURN),
					labelNode
			));
		});
	}

}
