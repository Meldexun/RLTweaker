package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchEntityBlockDestroy extends PatchManager {
	public PatchEntityBlockDestroy() {
		super("Entity Breakable");

		add(new Patch(this, "net.minecraft.block.Block", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_canEntityDestroy = findMethod(clazzNode, "canEntityDestroy");
				if (m_canEntityDestroy == null)
					throw new RuntimeException("Couldn't findMethod canEntityDestroy");

				if (true) {
					InsnList inject = new InsnList();
					LabelNode continueLabel = new LabelNode();
					// stack: empty
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 2));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 3));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 4));
					// stack: 5 objects
					inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "shouldEntityDestroyBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z", false));
					// stack: 1 boolean
					// If hook returned true, continue
					inject.add(new JumpInsnNode(Opcodes.IFNE, continueLabel));
					// stack: empty
					inject.add(new InsnNode(Opcodes.ICONST_0));
					// stack: boolean
					inject.add(new InsnNode(Opcodes.IRETURN));
					inject.add(continueLabel);
					// stack: empty
					TransformUtil.insertBeforeFirst(m_canEntityDestroy, inject);
				}
			}
		});
	}
}
