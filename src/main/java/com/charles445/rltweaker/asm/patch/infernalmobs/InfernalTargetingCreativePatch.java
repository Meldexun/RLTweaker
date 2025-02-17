package com.charles445.rltweaker.asm.patch.infernalmobs;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class InfernalTargetingCreativePatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("atomicstryker.infernalmobs.common.MobModifier", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_onUpdate = ASMUtil.find(clazzNode, "onUpdate");
			MethodInsnNode target = ASMUtil.first(m_onUpdate).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/world/World", "func_72890_a", "getClosestPlayerToEntity", "(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/EntityPlayer;").find();
			target.name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(target.owner, "func_184136_b", target.desc); // getNearestPlayerNotCreative
		});
	}

}
