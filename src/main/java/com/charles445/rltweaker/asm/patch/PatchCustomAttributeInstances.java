package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchCustomAttributeInstances {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.entity.ai.attributes.AttributeMap", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_createInstance = ASMUtil.findObf(clazzNode, "func_180376_c", "createInstance", "(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;");

			LabelNode label = new LabelNode();

			m_createInstance.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 1),
					new TypeInsnNode(Opcodes.INSTANCEOF, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory"),
					new JumpInsnNode(Opcodes.IFEQ, label),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory", "createInstance", "(Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", true),
					new InsnNode(Opcodes.ARETURN),
					label
			));
		});
	}

}
