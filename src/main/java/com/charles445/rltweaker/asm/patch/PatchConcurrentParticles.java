package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchConcurrentParticles extends PatchManager {
	public PatchConcurrentParticles() {
		super("Concurrent Particles");

		add(new Patch(this, "net.minecraft.client.particle.ParticleManager", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode c_ParticleManager) {
				MethodNode m_init = findMethod(c_ParticleManager, "<init>");

				AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_init), Opcodes.PUTFIELD, "queue", "field_187241_h");

				if (anchor == null)
					throw new RuntimeException("Couldn't find queue or field_187241_h");

				MethodInsnNode hookCaller = TransformUtil.findPreviousCallWithOpcodeAndName(anchor, Opcodes.INVOKESTATIC, "newArrayDeque");

				if (hookCaller == null)
					throw new RuntimeException("Couldn't find newArrayDeque");

				hookCaller.owner = "com/charles445/rltweaker/hook/HookMinecraft";
				hookCaller.name = "newConcurrentLinkedDeque";
				hookCaller.desc = "()Ljava/util/concurrent/ConcurrentLinkedDeque;";
			}
		});
	}
}
