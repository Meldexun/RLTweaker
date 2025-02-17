package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchPushReaction {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.entity.Entity", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			MethodNode m_getPushReaction = ASMUtil.findObf(clazzNode, "func_184192_z", "getPushReaction");
			if (m_getPushReaction == null)
				throw new RuntimeException("Couldn't find method func_184192_z or getPushReaction");

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getPushReaction));
			FieldInsnNode fieldStatic = ASMUtil.last(m_getPushReaction).opcode(Opcodes.GETSTATIC).fieldInsn("NORMAL").find();
			if (fieldStatic == null) {
				ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getPushReaction));
				throw new RuntimeException("Couldn't find NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
			}

			// Make sure it's EnumPushReaction
			while (!fieldStatic.owner.equals("net/minecraft/block/material/EnumPushReaction")) {
				fieldStatic = ASMUtil.prev(fieldStatic).opcode(Opcodes.GETSTATIC).fieldInsn("NORMAL").find();
				if (fieldStatic == null) {
					ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getPushReaction));
					throw new RuntimeException("Couldn't find EnumPushReaction specific NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
				}
			}

			// Make sure next call is ARETURN
			if (fieldStatic.getNext().getOpcode() != Opcodes.ARETURN) {
				ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getPushReaction));
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
				m_getPushReaction.instructions.insert(anchor, inject);
			}
		});
	}
}
