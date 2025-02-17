package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ModTransformer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchCraftBukkit extends PatchManager {
	public PatchCraftBukkit() {
		super("CraftBukkit");

		add(new Patch(this, "net.minecraft.tileentity.TileEntityBeacon", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (!this.hasModTransformer(ModTransformer.CHARM)) {
					this.cancelled = true;
					return;
				}

				MethodNode m_addEffectsToPlayers = this.findMethod(clazzNode, "func_146000_x", "addEffectsToPlayers");

				if (this.methodReferencesOwner(m_addEffectsToPlayers, "svenhjol/charm/base/ASMHooks")) {
					announce("Charm succeeded at patching already, skipping...");
					this.cancelled = true;
					return;
				}

				// Charm failed because of CraftBukkit, presumably
				// Fix it

				if (true) {
					AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_addEffectsToPlayers), Opcodes.INVOKEVIRTUAL, "getHumansInRange");
					if (anchor == null) {
						announce("CraftBukkit was not located in TileEntityBeacon, skipping...");
						this.cancelled = true;
						return;
					}

					// CraftBukkit is confirmed to exist, any further errors will be exceptions
					anchor = this.nextInsn(anchor);

					InsnList inject = new InsnList();

					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/compat/HookCraftBukkit$Charm", "handleAnimalBeacon", "(Lnet/minecraft/tileentity/TileEntityBeacon;)V", false));
					this.insert(m_addEffectsToPlayers, anchor, inject);

					announce("TileEntityBeacon was patched to make Charm and CraftBukkit compatible");
				}

			}
		});
	}
}
