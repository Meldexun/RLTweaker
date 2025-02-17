package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchDebug extends PatchManager {
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

	public PatchDebug() {
		super("Debug");
	}
}
