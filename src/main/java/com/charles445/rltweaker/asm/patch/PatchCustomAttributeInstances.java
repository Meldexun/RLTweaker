package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchCustomAttributeInstances extends PatchManager {

	public PatchCustomAttributeInstances() {
		super("Patch Custom Attribute Instances");

		add(new Patch(this, "net.minecraft.entity.ai.attributes.AttributeMap",
				ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_createInstance = this.findMethodWithDesc(clazzNode,
						"(Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;",
						"func_180376_c", "createInstance");

				LabelNode label = new LabelNode();

				TransformUtil.insertBeforeFirst(m_createInstance, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 1),
						new TypeInsnNode(Opcodes.INSTANCEOF, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory"),
						new JumpInsnNode(Opcodes.IFEQ, label),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/charles445/rltweaker/entity/attribute/AttributeInstanceFactory", "createInstance", "(Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;Lnet/minecraft/entity/ai/attributes/IAttribute;)Lnet/minecraft/entity/ai/attributes/IAttributeInstance;", true),
						new InsnNode(Opcodes.ARETURN),
						label));
			}
		});
	}

}
