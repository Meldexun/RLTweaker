package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.RLTweakerASM;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchCraftBukkit {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.tileentity.TileEntityBeacon", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			try {
				Class.forName("svenhjol.charm.base.CharmClassTransformer", false, RLTweakerASM.class.getClassLoader());
			} catch (ClassNotFoundException e) {
				return;
			}

			MethodNode m_addEffectsToPlayers = ASMUtil.findObf(clazzNode, "func_146000_x", "addEffectsToPlayers");

			if (ASMUtil.stream(m_addEffectsToPlayers).anyMatch(insn -> insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.equals("svenhjol/charm/base/ASMHooks") || insn instanceof FieldInsnNode && ((FieldInsnNode) insn).owner.equals("svenhjol/charm/base/ASMHooks"))) {
				ASMUtil.LOGGER.info("Charm succeeded at patching already, skipping...");
				return;
			}

			// Charm failed because of CraftBukkit, presumably
			// Fix it

			if (true) {
				AbstractInsnNode anchor = ASMUtil.first(m_addEffectsToPlayers).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("getHumansInRange").find();
				if (anchor == null) {
					ASMUtil.LOGGER.info("CraftBukkit was not located in TileEntityBeacon, skipping...");
					return;
				}

				// CraftBukkit is confirmed to exist, any further errors will be exceptions
				anchor = anchor.getNext();

				InsnList inject = new InsnList();

				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/compat/HookCraftBukkit$Charm", "handleAnimalBeacon", "(Lnet/minecraft/tileentity/TileEntityBeacon;)V", false));
				m_addEffectsToPlayers.instructions.insert(anchor, inject);

				ASMUtil.LOGGER.info("TileEntityBeacon was patched to make Charm and CraftBukkit compatible");
			}

		});
	}
}
