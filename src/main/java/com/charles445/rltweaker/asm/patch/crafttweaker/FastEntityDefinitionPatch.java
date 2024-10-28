package com.charles445.rltweaker.asm.patch.crafttweaker;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class FastEntityDefinitionPatch extends PatchManager {

	public FastEntityDefinitionPatch() {
		this.add(new Patch(this, "crafttweaker.mc1120.entity.MCEntity", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_getDefinition = this.findMethod(clazzNode, "getDefinition");
				m_getDefinition.instructions.clear();
				LabelNode label1 = new LabelNode();
				LabelNode label2 = new LabelNode();
				m_getDefinition.instructions.insert(ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new FieldInsnNode(Opcodes.GETFIELD, clazzNode.name, "entity", "Lnet/minecraft/entity/Entity;"),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/registry/EntityRegistry", "getEntry", "(Ljava/lang/Class;)Lnet/minecraftforge/fml/common/registry/EntityEntry;", false),
						new VarInsnNode(Opcodes.ASTORE, 1),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new JumpInsnNode(Opcodes.IFNULL, label1),
						new TypeInsnNode(Opcodes.NEW, "crafttweaker/mc1120/entity/MCEntityDefinition"),
						new InsnNode(Opcodes.DUP),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKESPECIAL, "crafttweaker/mc1120/entity/MCEntityDefinition", "<init>", "(Lnet/minecraftforge/fml/common/registry/EntityEntry;)V", false),
						new JumpInsnNode(Opcodes.GOTO, label2),
						label1,
						new InsnNode(Opcodes.ACONST_NULL),
						label2,
						new InsnNode(Opcodes.ARETURN)
				));
			}
		});
	}

}
