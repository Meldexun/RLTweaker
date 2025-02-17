package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class PatchBetterNether extends PatchManager {

	public PatchBetterNether() {
		add(new Patch(this, "paulevs.betternether.blocks.BlockChestOfDrawers", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {

			@Override
			public void patch(ClassNode clazzNode) {
				String m_breakBlock_name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName("net/minecraft/block/Block", "func_180663_b", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V");
				MethodNode m_breakBlock = new MethodNode(Opcodes.ACC_PUBLIC, m_breakBlock_name, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", null, null);
				m_breakBlock.instructions.add(ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new VarInsnNode(Opcodes.ALOAD, 2),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookBetterNether", "drop", "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new VarInsnNode(Opcodes.ALOAD, 2),
						new VarInsnNode(Opcodes.ALOAD, 3),
						new MethodInsnNode(Opcodes.INVOKESPECIAL, clazzNode.superName, m_breakBlock_name, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false),
						new InsnNode(Opcodes.RETURN)
				));
				clazzNode.methods.add(m_breakBlock);
			}

		});
	}

}
