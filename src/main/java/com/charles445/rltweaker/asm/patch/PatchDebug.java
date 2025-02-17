package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchDebug {
	private MethodInsnNode objectDebug() {
		return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookDebug", "printObject", "(Ljava/lang/Object;)V", false);
	}

	private LdcInsnNode stringObject(String s) {
		return new LdcInsnNode(s);
	}

	private InsnList debugPrint(String s) {
		InsnList inject = new InsnList();
		inject.add(stringObject(s));
		inject.add(objectDebug());
		return inject;
	}

	public static void registerTransformers(IClassTransformerRegistry registry) {
	}
}
