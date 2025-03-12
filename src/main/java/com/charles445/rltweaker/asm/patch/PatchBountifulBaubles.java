package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class PatchBountifulBaubles {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("cursedflames.bountifulbaubles.recipe.AnvilRecipes", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_onAnvilUpdate = ASMUtil.find(clazzNode, "onAnvilUpdate");

			m_onAnvilUpdate.instructions.insert(ASMUtil.first(m_onAnvilUpdate).opcode(Opcodes.INVOKESTATIC).methodInsn("cursedflames/bountifulbaubles/recipe/AnvilRecipes", "getResultModifierBook", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;").ordinal(1).find(), new InsnNode(Opcodes.RETURN));

			MethodNode m_getResultModifierBook = ASMUtil.find(clazzNode, "getResultModifierBook");

			m_getResultModifierBook.instructions.insert(ASMUtil.first(m_getResultModifierBook).type(VarInsnNode.class).predicate(insn -> insn.var == 1, null).ordinal(1).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new VarInsnNode(Opcodes.ALOAD, 2)
			));

			m_getResultModifierBook.instructions.insert(ASMUtil.first(m_getResultModifierBook).type(VarInsnNode.class).predicate(insn -> insn.var == 1, null).ordinal(2).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new VarInsnNode(Opcodes.ALOAD, 2)
			));

			m_getResultModifierBook.instructions.insert(ASMUtil.first(m_getResultModifierBook).type(VarInsnNode.class).predicate(insn -> insn.var == 1, null).ordinal(3).find(), ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new VarInsnNode(Opcodes.ALOAD, 2)
			));
		});
	}

}
