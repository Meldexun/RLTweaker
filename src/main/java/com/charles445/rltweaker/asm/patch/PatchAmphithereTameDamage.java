package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.ASMUtil;

public class PatchAmphithereTameDamage extends PatchManager {

	public PatchAmphithereTameDamage() {
		super("Patch Amphithere Tame Damage");

		add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityAmphithere", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_updatePassenger = this.findMethod(clazzNode, "func_184232_k", "updatePassenger");

				this.insert(m_updatePassenger, ASMUtil.findInsnWithOpcode(m_updatePassenger, Opcodes.FCONST_1, 0), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "iceandfire", "Lcom/charles445/rltweaker/config/ConfigIceAndFire;"),
						new FieldInsnNode(Opcodes.GETFIELD, "com/charles445/rltweaker/config/ConfigIceAndFire", "amphithereTameDamage", "F")));
			}
		});
	}

}
