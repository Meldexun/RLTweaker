package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchHopper {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.tileentity.TileEntityHopper", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// Inject Head TileEntityHopper getInventoryAtPosition with something to return null if denied
			MethodNode m_getInventoryAtPosition = ASMUtil.findObf(clazzNode, "func_145893_b", "getInventoryAtPosition");
			if (m_getInventoryAtPosition == null)
				throw new RuntimeException("Couldn't find func_145893_b or getInventoryAtPosition");

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getInventoryAtPosition));
			// ClassDisplayer.instance.printMethodLocalVariables(m_getInventoryAtPosition);

			if (true) {
				// Skip ahead to the tile entity check
				AbstractInsnNode anchor = ASMUtil.first(m_getInventoryAtPosition).methodInsn("hasTileEntity").findThenNext().type(JumpInsnNode.class).find().label;
				anchor = ASMUtil.next(m_getInventoryAtPosition, anchor).predicate(insn -> !(insn instanceof LabelNode) && !(insn instanceof LineNumberNode) && !(insn instanceof FrameNode), null).find();
				// Load up a bunch of variables
				LocalVariableNode lvn_iinventory = ASMUtil.findLocalVariable(m_getInventoryAtPosition, "iinventory");
				LocalVariableNode lvn_block = ASMUtil.findLocalVariable(m_getInventoryAtPosition, "block");
				LocalVariableNode lvn_state = ASMUtil.findLocalVariable(m_getInventoryAtPosition, "state");
				LocalVariableNode lvn_blockpos = ASMUtil.findLocalVariable(m_getInventoryAtPosition, "blockpos");
				if (lvn_iinventory == null || lvn_block == null || lvn_state == null || lvn_blockpos == null)
					throw new RuntimeException("Necessay LVN are missing in getInventoryAtPosition");

				// Insert an iinventory replacement hook
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // World
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_blockpos.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_block.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_state.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_iinventory.index));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "hopperInventoryAtPosition", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/inventory/IInventory;)Lnet/minecraft/inventory/IInventory;", false));
				inject.add(new VarInsnNode(Opcodes.ASTORE, lvn_iinventory.index));
				m_getInventoryAtPosition.instructions.insertBefore(anchor, inject);
			}

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getInventoryAtPosition));
		});

		registry.add("net.minecraftforge.items.VanillaInventoryCodeHooks", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// Inject Head VanillaInventoryCodeHooks getItemHandler with something to return null if denied
			MethodNode m_getItemHandler = ASMUtil.find(clazzNode, "getItemHandler", "(Lnet/minecraft/world/World;DDDLnet/minecraft/util/EnumFacing;)Lorg/apache/commons/lang3/tuple/Pair;");
			if (m_getItemHandler == null)
				throw new RuntimeException("Couldn't find getItemHandler with matching desc");

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getItemHandler));
			// ClassDisplayer.instance.printMethodLocalVariables(m_getItemHandler);

			if (true) {
				// Skip ahead to the tile entity check
				AbstractInsnNode anchor = ASMUtil.first(m_getItemHandler).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("hasTileEntity").find();
				if (anchor == null)
					throw new RuntimeException("Couldn't find call to hasTileEntity in getItemHandler");
				anchor = anchor.getNext();
				// Find the branch
				if (!(anchor instanceof JumpInsnNode))
					throw new RuntimeException("Unexpected lack of jump instruction after hasTileEntity");
				// Grab the destination node for the branch
				LabelNode lvn_tileBlockEnd = ((JumpInsnNode) anchor).label;
				// Go to that label
				anchor = lvn_tileBlockEnd;
				if (anchor == null)
					throw new RuntimeException("Couldn't follow label in getItemHandler");
				// Move to the first real instruction
				anchor = ASMUtil.next(m_getItemHandler, anchor).predicate(insn -> !(insn instanceof LabelNode) && !(insn instanceof LineNumberNode) && !(insn instanceof FrameNode), null).find();
				// Load up a bunch of variables
				LocalVariableNode lvn_destination = ASMUtil.findLocalVariable(m_getItemHandler, "destination");
				LocalVariableNode lvn_block = ASMUtil.findLocalVariable(m_getItemHandler, "block");
				LocalVariableNode lvn_state = ASMUtil.findLocalVariable(m_getItemHandler, "state");
				LocalVariableNode lvn_blockpos = ASMUtil.findLocalVariable(m_getItemHandler, "blockpos");
				if (lvn_destination == null || lvn_block == null || lvn_state == null || lvn_blockpos == null)
					throw new RuntimeException("Necessay LVN are missing in getItemHandler");

				// Insert an iinventory replacement hook
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // World
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_blockpos.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_block.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_state.index));
				inject.add(new VarInsnNode(Opcodes.ALOAD, lvn_destination.index));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "hopperItemHandler", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lorg/apache/commons/lang3/tuple/Pair;)Lorg/apache/commons/lang3/tuple/Pair;", false));
				inject.add(new VarInsnNode(Opcodes.ASTORE, lvn_destination.index));
				m_getItemHandler.instructions.insertBefore(anchor, inject);
			}

			// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getItemHandler));
		});
	}
}
