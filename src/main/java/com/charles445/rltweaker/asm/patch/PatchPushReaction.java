package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchPushReaction extends PatchManager {
	public PatchPushReaction() {
		super("Push Reaction");

		add(new Patch(this, "net.minecraft.entity.Entity", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_getPushReaction = findMethod(clazzNode, "func_184192_z", "getPushReaction");
				if (m_getPushReaction == null)
					throw new RuntimeException("Couldn't find method func_184192_z or getPushReaction");

				// ClassDisplayer.instance.printMethod(m_getPushReaction);
				FieldInsnNode fieldStatic = TransformUtil.findPreviousFieldWithOpcodeAndName(last(m_getPushReaction), Opcodes.GETSTATIC, "NORMAL");
				if (fieldStatic == null) {
					ClassDisplayer.instance.printMethod(m_getPushReaction);
					throw new RuntimeException("Couldn't find NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
				}

				// Make sure it's EnumPushReaction
				while (!fieldStatic.owner.equals("net/minecraft/block/material/EnumPushReaction")) {
					fieldStatic = TransformUtil.findPreviousFieldWithOpcodeAndName(fieldStatic, Opcodes.GETSTATIC, "NORMAL");
					if (fieldStatic == null) {
						ClassDisplayer.instance.printMethod(m_getPushReaction);
						throw new RuntimeException("Couldn't find EnumPushReaction specific NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
					}
				}

				// Make sure next call is ARETURN
				if (ASMHelper.findNextInstruction(fieldStatic).getOpcode() != Opcodes.ARETURN) {
					ClassDisplayer.instance.printMethod(m_getPushReaction);
					throw new RuntimeException("Last EnumPushReaction.NORMAL was not the return value, somehow, observe above for mod compatibility errors");
				}

				// Remove the field get
				AbstractInsnNode anchor = fieldStatic.getPrevious();
				m_getPushReaction.instructions.remove(anchor.getNext());

				// Replace it with our own method get
				if (true) {
					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "hookPushReaction", "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/block/material/EnumPushReaction;", false));
					this.insert(m_getPushReaction, anchor, inject);
				}
			}
		});
	}
}
