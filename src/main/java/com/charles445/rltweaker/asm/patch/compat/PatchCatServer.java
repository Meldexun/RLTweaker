package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchCatServer extends PatchManager {
	public PatchCatServer() {
		super("CatServer");

		// For some reason catserver is successfully managing conditions
		// Both recipes and advancements have their _factories conditions loaded in, crashing catserver when running disenchanter
		// How come vanilla forge doesn't do this?

		add(new Patch(this, "net.minecraftforge.common.ForgeHooks", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (true) {
					MethodNode m_loadFactories = this.findMethod(clazzNode, "loadFactories");

					if (m_loadFactories == null)
						throw new RuntimeException("Couldn't find loadFactories method in ForgeHooks");

					// INVOKESTATIC
					// net/minecraftforge/common/crafting/CraftingHelper
					// loadFactories
					// (Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Lnet/minecraftforge/common/crafting/CraftingHelper$FactoryLoader;)V false

					// com/charles445/rltweaker/hook/compat/HookCatServer
					// loadAdvancementFactories
					// (Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V

					MethodInsnNode hookCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_loadFactories), Opcodes.INVOKESTATIC, "loadFactories");

					if (hookCall == null)
						throw new RuntimeException("Couldn't find loadFactories invokestatic in ForgeHooks loadFactories");

					hookCall.setOpcode(Opcodes.INVOKESTATIC);
					hookCall.owner = "com/charles445/rltweaker/hook/compat/HookCatServer";
					hookCall.name = "loadAdvancementFactories";
					hookCall.desc = "(Lnet/minecraftforge/fml/common/ModContainer;Ljava/lang/String;[Ljava/lang/Object;)V";

					announce("Patched loadFactories to avoid CatServer crashes");
				}
			}
		});
	}
}
