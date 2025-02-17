package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchEnchant extends PatchManager {
	// Various enchantment handling

	private static final String owner_HookEnchant = "com/charles445/rltweaker/hook/HookEnchant";

	public PatchEnchant() {
		super("Enchant");

		add(new Patch(this, "net.minecraft.enchantment.EnchantmentHelper", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_getEnchantmentDatas = findMethod(clazzNode, "func_185291_a", "getEnchantmentDatas");

				if (m_getEnchantmentDatas == null)
					throw new RuntimeException("Couldn't find func_185291_a or getEnchantmentDatas");

				AbstractInsnNode aret = ASMHelper.findLastInstructionWithOpcode(m_getEnchantmentDatas, Opcodes.ARETURN);
				if (aret == null)
					throw new RuntimeException("Couldn't find ARETURN in getEnchantmentDatas");

				// Stack has the list before the areturn
				// Good place for a tweak

				m_getEnchantmentDatas.instructions.insertBefore(aret, new MethodInsnNode(Opcodes.INVOKESTATIC, owner_HookEnchant, "restrictEnchantmentDatas", "(Ljava/util/List;)Ljava/util/List;", false));
			}
		});

		add(new Patch(this, "net.minecraft.entity.passive.EntityVillager$ListEnchantedBookForEmeralds", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_addMerchantRecipe = findMethod(clazzNode, "func_190888_a", "addMerchantRecipe");
				if (m_addMerchantRecipe == null)
					throw new RuntimeException("Couldn't find func_190888_a or addMerchantRecipe");

				MethodInsnNode getRandomObjectNode = TransformUtil.findNextCallWithOpcodeAndName(first(m_addMerchantRecipe), Opcodes.INVOKEVIRTUAL, "func_186801_a", "getRandomObject");
				if (getRandomObjectNode == null)
					throw new RuntimeException("Couldn't find getRandomObject in addMerchantRecipe");

				// getRandomObjectNode is getRandomObject of Enchantment.REGISTRY.getRandomObject(random);
				// Stack is: registry, random

				getRandomObjectNode.setOpcode(Opcodes.INVOKESTATIC);
				getRandomObjectNode.owner = owner_HookEnchant;
				getRandomObjectNode.name = "getRandomRestricted";
				getRandomObjectNode.desc = "(Ljava/lang/Object;Ljava/util/Random;)Lnet/minecraft/enchantment/Enchantment;";
			}

		});

		add(new Patch(this, "net.minecraft.world.storage.loot.functions.EnchantRandomly", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_apply = findMethod(clazzNode, "func_186553_a", "apply");

				AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_apply), Opcodes.INVOKEVIRTUAL, "func_92089_a", "canApply");
				if (anchor == null)
					throw new RuntimeException("Couldn't find func_92089_a or canApply");

				MethodInsnNode addNode = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEINTERFACE, "add");
				if (addNode == null)
					throw new RuntimeException("Couldn't find add in apply");
				if (!addNode.owner.equals("java/util/List"))
					throw new RuntimeException("Unexpected add found in apply: " + addNode.owner);

				// addNode is the add of list.add(enchantment1);
				// Stack is: list, enchantment1

				addNode.setOpcode(Opcodes.INVOKESTATIC);
				addNode.owner = owner_HookEnchant;
				addNode.name = "addEnchantmentRestricted";
				addNode.desc = "(Ljava/util/List;Lnet/minecraft/enchantment/Enchantment;)Z";
				addNode.itf = false;
			}
		});

		/*
		 * Okay no maybe not this one, might act funny
		 * add(new Patch(this, "net.minecraft.item.ItemEnchantedBook", ClassWriter.COMPUTE_MAXS)
		 * {
		 * // getSubItems
		 * 
		 * @Override
		 * public void patch(ClassNode clazzNode)
		 * {
		 * MethodNode m_getSubItems = findMethod(clazzNode, "func_150895_a", "getSubItems");
		 * 
		 * 
		 * 
		 * ClassDisplayer.instance.printMethod(m_getSubItems);
		 * }
		 * });
		 */
	}
}
