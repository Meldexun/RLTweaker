package com.charles445.rltweaker.asm.patch.epicsiegemod;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class ReducedDamagePatch extends PatchManager {

	public ReducedDamagePatch() {
		this.add(new Patch(this, "funwayguy.epicsiegemod.ai.ESM_EntityAIAttackMelee", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_updateTask = this.findMethod(clazzNode, "func_75246_d", "updateTask");
				TypeInsnNode typeInsn = ASMUtil.findInsn(m_updateTask, TypeInsnNode.class, insn -> insn.getOpcode() == Opcodes.INSTANCEOF && insn.desc.equals("net/minecraft/entity/passive/IAnimals"), 0);
				typeInsn.desc = "net/minecraft/entity/passive/EntityAnimal";
			}
		});
	}

}
