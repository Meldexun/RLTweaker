package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchLootManagement {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		// Fix loot table path because some servers break LootTweaker
		registry.add("net.minecraft.world.storage.loot.LootTableManager$Loader", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			if (true) {
				MethodNode m_loadLootTable = ASMUtil.findObf(clazzNode, "func_186517_b", "loadLootTable");

				if (m_loadLootTable == null)
					throw new RuntimeException("Couldn't find loadLootTable in LootTableManager$Loader");

				if (m_loadLootTable.localVariables == null)
					throw new RuntimeException("Couldn't find any local variables in loadLootTable in LootTableManager$Loader");

				LocalVariableNode lvn_file1 = ASMUtil.findLocalVariableDesc(m_loadLootTable, "Ljava/io/File;");

				if (lvn_file1 == null)
					throw new RuntimeException("Couldn't find local variable node for File in loadLootTable in LootTableManager$Loader");

				AbstractInsnNode anchor = m_loadLootTable.instructions.getFirst();

				while (anchor != null) {
					if (anchor.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) anchor).var == lvn_file1.index) {
						break;
					}

					anchor = anchor.getNext();
				}

				if (anchor == null)
					throw new RuntimeException("Couldn't find earliest ASTORE for File in loadLootTable in LootTableManager$Loader");

				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_file1.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/compat/HookLootManagement", "fixLootFilePath", "(Ljava/io/File;Lnet/minecraft/util/ResourceLocation;)Ljava/io/File;", false));
				inject.add(new VarInsnNode(Opcodes.ASTORE, lvn_file1.index));

				m_loadLootTable.instructions.insert(anchor, inject);
			}
		});
	}
}
