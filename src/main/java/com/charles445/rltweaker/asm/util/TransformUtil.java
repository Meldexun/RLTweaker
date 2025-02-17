package com.charles445.rltweaker.asm.util;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class TransformUtil {

	public static FieldInsnNode createObfFieldInsn(int opcode, String owner, String name, String desc) {
		return new FieldInsnNode(opcode, owner, FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, name, desc), desc);
	}

	public static MethodInsnNode createObfMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		return new MethodInsnNode(opcode, owner, FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc), desc, itf);
	}

}
