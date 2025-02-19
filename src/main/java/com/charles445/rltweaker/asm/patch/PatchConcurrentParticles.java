package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchConcurrentParticles {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.client.particle.ParticleManager", ClassWriter.COMPUTE_MAXS, c_ParticleManager -> {
			MethodNode m_init = ASMUtil.find(c_ParticleManager, "<init>");

			AbstractInsnNode anchor = ASMUtil.first(m_init).opcode(Opcodes.PUTFIELD).fieldInsnObf("queue", "field_187241_h").find();

			if (anchor == null)
				throw new RuntimeException("Couldn't find queue or field_187241_h");

			MethodInsnNode hookCaller = ASMUtil.prev(m_init, anchor).opcode(Opcodes.INVOKESTATIC).methodInsn("newArrayDeque").find();

			if (hookCaller == null)
				throw new RuntimeException("Couldn't find newArrayDeque");

			hookCaller.owner = "com/charles445/rltweaker/hook/HookMinecraft";
			hookCaller.name = "newConcurrentLinkedDeque";
			hookCaller.desc = "()Ljava/util/concurrent/ConcurrentLinkedDeque;";
		});
	}
}
