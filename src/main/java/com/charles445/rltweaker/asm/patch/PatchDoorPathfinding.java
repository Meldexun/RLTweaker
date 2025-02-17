package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchDoorPathfinding {
	// Debug

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.pathfinding.WalkNodeProcessor", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_getPathNodeTypeRaw = ASMUtil.findObf(clazzNode, "func_189553_b", "getPathNodeTypeRaw");

			if (m_getPathNodeTypeRaw == null)
				throw new RuntimeException("Couldn't find func_189553_b or getPathNodeTypeRaw");

			if (true) {
				AbstractInsnNode doorWoodAnchor = ASMUtil.first(m_getPathNodeTypeRaw).opcode(Opcodes.GETSTATIC).fieldInsn("DOOR_WOOD_CLOSED").find();
				InsnList inj = new InsnList();
				inj.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 2));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 3));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 4));
				inj.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
				m_getPathNodeTypeRaw.instructions.insert(doorWoodAnchor, inj);
			}

			if (true) {
				AbstractInsnNode doorIronAnchor = ASMUtil.first(m_getPathNodeTypeRaw).opcode(Opcodes.GETSTATIC).fieldInsn("DOOR_IRON_CLOSED").find();
				InsnList inj = new InsnList();
				inj.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 2));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 3));
				inj.add(new VarInsnNode(Opcodes.ILOAD, 4));
				inj.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
				m_getPathNodeTypeRaw.instructions.insert(doorIronAnchor, inj);
			}
		});
	}
}
