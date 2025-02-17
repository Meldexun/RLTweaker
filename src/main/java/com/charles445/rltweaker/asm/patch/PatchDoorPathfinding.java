package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchDoorPathfinding extends PatchManager {
	// Debug

	public PatchDoorPathfinding() {
		super("Door Pathfinding");
		add(new Patch(this, "net.minecraft.pathfinding.WalkNodeProcessor", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_getPathNodeTypeRaw = findMethod(clazzNode, "func_189553_b", "getPathNodeTypeRaw");

				if (m_getPathNodeTypeRaw == null)
					throw new RuntimeException("Couldn't find func_189553_b or getPathNodeTypeRaw");

				if (true) {
					AbstractInsnNode doorWoodAnchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_getPathNodeTypeRaw), Opcodes.GETSTATIC, "DOOR_WOOD_CLOSED");
					InsnList inj = new InsnList();
					inj.add(new VarInsnNode(Opcodes.ALOAD, 1));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 2));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 3));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 4));
					inj.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
					this.insert(m_getPathNodeTypeRaw, doorWoodAnchor, inj);
				}

				if (true) {
					AbstractInsnNode doorIronAnchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_getPathNodeTypeRaw), Opcodes.GETSTATIC, "DOOR_IRON_CLOSED");
					InsnList inj = new InsnList();
					inj.add(new VarInsnNode(Opcodes.ALOAD, 1));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 2));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 3));
					inj.add(new VarInsnNode(Opcodes.ILOAD, 4));
					inj.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "verifyPathNodeType", "(Lnet/minecraft/pathfinding/PathNodeType;Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/pathfinding/PathNodeType;", false));
					this.insert(m_getPathNodeTypeRaw, doorIronAnchor, inj);
				}
			}
		});
	}
}
