package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchCatServer {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		// For some reason catserver is successfully managing conditions
		// Both recipes and advancements have their _factories conditions loaded in, crashing catserver when running disenchanter
		// How come vanilla forge doesn't do this?

		registry.add("net.minecraftforge.common.ForgeHooks", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			if (true) {
				MethodNode m_loadFactories = ASMUtil.find(clazzNode, "loadFactories");

				if (m_loadFactories == null)
					throw new RuntimeException("Couldn't find loadFactories method in ForgeHooks");

				// INVOKESTATIC
				// net/minecraftforge/common/crafting/CraftingHelper
				// loadFactories
				// (Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Lnet/minecraftforge/common/crafting/CraftingHelper$FactoryLoader;)V false

				// com/charles445/rltweaker/hook/compat/HookCatServer
				// loadAdvancementFactories
				// (Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V

				MethodInsnNode hookCall = ASMUtil.first(m_loadFactories).opcode(Opcodes.INVOKESTATIC).methodInsn("loadFactories").find();

				if (hookCall == null)
					throw new RuntimeException("Couldn't find loadFactories invokestatic in ForgeHooks loadFactories");

				hookCall.setOpcode(Opcodes.INVOKESTATIC);
				hookCall.owner = "com/charles445/rltweaker/hook/compat/HookCatServer";
				hookCall.name = "loadAdvancementFactories";
				hookCall.desc = "(Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V";

				ASMUtil.LOGGER.info("Patched loadFactories to avoid CatServer crashes");
			}
		});
	}
}
