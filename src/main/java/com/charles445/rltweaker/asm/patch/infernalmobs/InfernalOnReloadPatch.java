package com.charles445.rltweaker.asm.patch.infernalmobs;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class InfernalOnReloadPatch extends PatchManager {

	public InfernalOnReloadPatch() {
		this.add(new Patch(this, "atomicstryker.infernalmobs.common.InfernalMobsCore", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_processEntitySpawn = this.findMethod(clazzNode, "processEntitySpawn");
				JumpInsnNode target = ASMUtil.findInsnWithType(m_processEntitySpawn, JumpInsnNode.class, 0);
				LabelNode label = new LabelNode();
				m_processEntitySpawn.instructions.insert(target, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;", false),
						new LdcInsnNode("InfernalMobsInitialized"),
						TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74767_n", "(Ljava/lang/String;)Z", false), // getBoolean
						new JumpInsnNode(Opcodes.IFEQ, label),
						new InsnNode(Opcodes.RETURN),
						label,
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/Entity", "getEntityData", "()Lnet/minecraft/nbt/NBTTagCompound;", false),
						new LdcInsnNode("InfernalMobsInitialized"),
						new InsnNode(Opcodes.ICONST_1),
						TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74757_a", "(Ljava/lang/String;Z)V", false) // setBoolean
				));
			}
		});
	}

}
