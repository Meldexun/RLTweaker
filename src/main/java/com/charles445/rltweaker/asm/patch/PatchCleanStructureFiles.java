package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchCleanStructureFiles {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.WorldServer", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_saveLevel = ASMUtil.findObf(clazzNode, "func_73042_a", "saveLevel");

			m_saveLevel.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "preSaveWorld", "(Lnet/minecraft/world/WorldServer;)V", false)
			));
		});
	}

}
