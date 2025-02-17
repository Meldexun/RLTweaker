package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchFallingBlockPortalDupe extends PatchManager {

	public PatchFallingBlockPortalDupe() {
		super("Patch Falling Block Portal Dupe");

		this.add(new Patch(this, "net.minecraft.entity.item.EntityFallingBlock", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onUpdate = this.findMethod(clazzNode, "func_70071_h_", "onUpdate");

				AbstractInsnNode target = ASMUtil.findMethodInsn(m_onUpdate, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/item/EntityFallingBlock", "func_70091_d", "move", "(Lnet/minecraft/entity/MoverType;DDD)V", 0);

				LabelNode labelNode = new LabelNode();
				m_onUpdate.instructions.insert(target, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/entity/item/EntityFallingBlock", "field_70128_L", "Z"),
						new JumpInsnNode(Opcodes.IFEQ, labelNode),
						new InsnNode(Opcodes.RETURN),
						labelNode
				));
			}
		});
	}

}
