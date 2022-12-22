package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;

public class PatchBountifulBaubles extends PatchManager {

	public PatchBountifulBaubles() {
		add(new Patch(this, "cursedflames.bountifulbaubles.recipe.AnvilRecipes", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {

			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onAnvilUpdate = findMethod(clazzNode, "onAnvilUpdate");

				insert(m_onAnvilUpdate, ASMUtil.findMethodInsn(m_onAnvilUpdate, Opcodes.INVOKESTATIC,
						"cursedflames/bountifulbaubles/recipe/AnvilRecipes",
						"getResultModifierBook",
						"(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", 1),
						new InsnNode(Opcodes.RETURN));

				MethodNode m_getResultModifierBook = findMethod(clazzNode, "getResultModifierBook");

				insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, insn -> insn.var == 1, 1), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new VarInsnNode(Opcodes.ALOAD, 2)));

				insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, insn -> insn.var == 1, 2), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new VarInsnNode(Opcodes.ALOAD, 2)));

				insert(m_getResultModifierBook, ASMUtil.findInsn(m_getResultModifierBook, VarInsnNode.class, insn -> insn.var == 1, 3), ASMUtil.listOf(
						new InsnNode(Opcodes.POP),
						new VarInsnNode(Opcodes.ALOAD, 2)));
			}

		});
	}

}
