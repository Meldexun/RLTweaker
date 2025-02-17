package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ObfHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchLycanitesDupe extends PatchManager {
	// Port of LycanitesDupePatch to RLTweaker

	public PatchLycanitesDupe() {
		super("Lycanites Dupe Patch");

		add(new Patch(this, "com.lycanitesmobs.core.inventory.InventoryCreature", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				announce("Patching InventoryCreature");
				announce("If your game crashes immediately after this, change the config rltweaker/patches/lycanitesPetDupeFix to false!");

				AbstractInsnNode anchor = null;

				String deathsave = "DEATHSAVE";
				String deathsave_T = "I";

				for (FieldNode f : clazzNode.fields) {
					if (f != null && f.name.equals("DEATHSAVE")) {
						System.out.println("Dupe has already been patched! Skipping...");
						this.cancelled = true;
						return;
					}
				}

				// Create a new public integer DEATHSAVE
				clazzNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, deathsave, deathsave_T, null, 0));

				// DEATHSAVE starts at 0
				// DEATHSAVE is increased by 1 every time nbt attempts to save a dead mob's data

				// The save routine in Lycanites saves the inventory twice every time the mob is saved once
				// So DEATHSAVE waits to be 2 or above to make sure that it is at the end of the routine and not in the middle

				// First set up the DEATHSAVE rules in writeToNBT
				MethodNode m_writeToNBT = findMethod(clazzNode, "writeToNBT");
				anchor = first(m_writeToNBT);
				LabelNode wtnSetContinueNode = new LabelNode();
				InsnList wtnInsert = new InsnList();

				// writeToNBT start
				// Get DEATHSAVE
				wtnInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				wtnInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
				// Skip writeToNBT if DEATHSAVE >= 2
				wtnInsert.add(new InsnNode(Opcodes.ICONST_2));
				wtnInsert.add(new JumpInsnNode(Opcodes.IF_ICMPLT, wtnSetContinueNode));
				wtnInsert.add(new InsnNode(Opcodes.RETURN));
				wtnInsert.add(wtnSetContinueNode);
				insert(m_writeToNBT, anchor, wtnInsert);

				// writeToNBT end
				InsnList wotInsert = new InsnList();
				anchor = previous(last(m_writeToNBT));
				LabelNode wotContinueNode = new LabelNode();

				// Check if creature is alive
				wotInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				wotInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", "creature", "Lcom/lycanitesmobs/core/entity/BaseCreatureEntity;"));
				wotInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityLivingBase", ObfHelper.isObfuscated() ? "func_70089_S" : "isEntityAlive", "()Z", false));
				wotInsert.add(new JumpInsnNode(Opcodes.IFGT, wotContinueNode));

				// Creature is dead!

				// Add 1 to DEATHSAVE
				wotInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				wotInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				wotInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
				wotInsert.add(new InsnNode(Opcodes.ICONST_1));
				wotInsert.add(new InsnNode(Opcodes.IADD));
				wotInsert.add(new FieldInsnNode(Opcodes.PUTFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));

				// Continue
				wotInsert.add(wotContinueNode);

				insert(m_writeToNBT, anchor, wotInsert);

				// getStackInSlot start
				MethodNode m_getStackInSlot = findMethod(clazzNode, "func_70301_a", "getStackInSlot");
				anchor = first(m_getStackInSlot);

				anchor = previous(TransformUtil.findNextFieldWithOpcodeAndName(anchor, Opcodes.GETFIELD, "inventoryContents"));
				LabelNode gsisContinueNode = new LabelNode();
				InsnList gsisInsert = new InsnList();

				// Get DEATHSAVE
				gsisInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				gsisInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
				gsisInsert.add(new InsnNode(Opcodes.ICONST_2));

				// Set slot to empty if DEATHSAVE >= 2
				gsisInsert.add(new JumpInsnNode(Opcodes.IF_ICMPLT, gsisContinueNode));
				gsisInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/item/ItemStack", ObfHelper.isObfuscated() ? "field_190927_a" : "EMPTY", "Lnet/minecraft/item/ItemStack;"));
				gsisInsert.add(new InsnNode(Opcodes.ARETURN));
				gsisInsert.add(gsisContinueNode);
				insert(m_getStackInSlot, anchor, gsisInsert);
				anchor = previous(last(m_writeToNBT)); // ?? what was this about?

				// onInventoryChanged start
				MethodNode m_onInventoryChanged = findMethod(clazzNode, "onInventoryChanged");
				anchor = first(m_onInventoryChanged);
				LabelNode oicContinueNode = new LabelNode();
				InsnList oicInsert = new InsnList();

				// Get DEATHSAVE
				oicInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
				oicInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
				oicInsert.add(new InsnNode(Opcodes.ICONST_2));

				// Skip onInventoryChanged if DEATHSAVE >= 2
				oicInsert.add(new JumpInsnNode(Opcodes.IF_ICMPLT, oicContinueNode));
				oicInsert.add(new InsnNode(Opcodes.RETURN));
				oicInsert.add(oicContinueNode);
				insert(m_onInventoryChanged, anchor, oicInsert);
			}
		});
	}

}
