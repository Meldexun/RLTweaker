package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchReducedSearchSize {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.World", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			if (true) // func_72872_a getEntitiesWithinAABB
			{
				MethodNode m_getEntitiesWithinAABB = ASMUtil.find(clazzNode, "func_72872_a", "getEntitiesWithinAABB", "(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;");
				if (m_getEntitiesWithinAABB == null)
					throw new RuntimeException("Couldn't find getEntitiesWithinAABB or func_72872_a with matching desc");

				MethodInsnNode getAABBCall = ASMUtil.first(m_getEntitiesWithinAABB).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_175647_a", "getEntitiesWithinAABB").find();
				if (getAABBCall == null) {
					ASMUtil.LOGGER.warn("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesWithinAABB or func_175647_a");
					ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getEntitiesWithinAABB));
					throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175647_a");
				}

				// Stack here should have everything needed for the static call, which is convenient.
				getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
				getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
				getAABBCall.name = "getEntitiesWithinAABB";
				getAABBCall.desc = "(Lnet/minecraft/world/World;Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
			}
		});
	}
}
