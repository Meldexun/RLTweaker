package com.charles445.rltweaker.asm.patch.epicsiegemod;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class ReducedDamagePatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("funwayguy.epicsiegemod.ai.ESM_EntityAIAttackMelee", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_updateTask = ASMUtil.findObf(clazzNode, "func_75246_d", "updateTask");
			TypeInsnNode typeInsn = ASMUtil.first(m_updateTask).type(TypeInsnNode.class).opcode(Opcodes.INSTANCEOF).find();
			if (typeInsn.desc.equals("net/minecraft/entity/passive/IAnimals")) {
				typeInsn.desc = "net/minecraft/entity/passive/EntityAnimal";
				return true;
			}
			return false;
		});
	}

}
