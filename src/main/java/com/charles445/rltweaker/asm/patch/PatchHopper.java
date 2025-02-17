package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchHopper extends PatchManager {
	public PatchHopper() {
		super("Hopper");

		add(new Patch(this, "net.minecraft.tileentity.TileEntityHopper", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				// Inject Head TileEntityHopper getInventoryAtPosition with something to return null if denied
				MethodNode m_getInventoryAtPosition = findMethod(clazzNode, "func_145893_b", "getInventoryAtPosition");
				if (m_getInventoryAtPosition == null)
					throw new RuntimeException("Couldn't find func_145893_b or getInventoryAtPosition");

				// ClassDisplayer.instance.printMethod(m_getInventoryAtPosition);
				// ClassDisplayer.instance.printMethodLocalVariables(m_getInventoryAtPosition);

				if (true) {
					// Skip ahead to the tile entity check
					AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_getInventoryAtPosition), Opcodes.INVOKEVIRTUAL, "hasTileEntity");
					if (anchor == null)
						throw new RuntimeException("Couldn't find call to hasTileEntity in getInventoryAtPosition");
					anchor = ASMHelper.findNextInstruction(anchor);
					// Find the branch
					if (anchor.getType() != anchor.JUMP_INSN)
						throw new RuntimeException("Unexpected lack of jump instruction after hasTileEntity");
					// Grab the destination node for the branch
					LabelNode lvn_tileBlockEnd = ((JumpInsnNode) anchor).label;
					// Go to that label
					anchor = TransformUtil.gotoLabel(m_getInventoryAtPosition, lvn_tileBlockEnd);
					if (anchor == null)
						throw new RuntimeException("Couldn't follow label in getInventoryAtPosition");
					// Move to the first real instruction
					anchor = ASMHelper.findNextInstruction(anchor);
					// Load up a bunch of variables
					LocalVariableNode lvn_iinventory = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "iinventory");
					LocalVariableNode lvn_block = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "block");
					LocalVariableNode lvn_state = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "state");
					LocalVariableNode lvn_blockpos = TransformUtil.findLocalVariableWithName(m_getInventoryAtPosition, "blockpos");
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
					this.insertBefore(m_getInventoryAtPosition, anchor, inject);
				}

				// ClassDisplayer.instance.printMethod(m_getInventoryAtPosition);
			}
		});

		add(new Patch(this, "net.minecraftforge.items.VanillaInventoryCodeHooks", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				// Inject Head VanillaInventoryCodeHooks getItemHandler with something to return null if denied
				MethodNode m_getItemHandler = findMethodWithDesc(clazzNode, "(Lnet/minecraft/world/World;DDDLnet/minecraft/util/EnumFacing;)Lorg/apache/commons/lang3/tuple/Pair;", "getItemHandler");
				if (m_getItemHandler == null)
					throw new RuntimeException("Couldn't find getItemHandler with matching desc");

				// ClassDisplayer.instance.printMethod(m_getItemHandler);
				// ClassDisplayer.instance.printMethodLocalVariables(m_getItemHandler);

				if (true) {
					// Skip ahead to the tile entity check
					AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_getItemHandler), Opcodes.INVOKEVIRTUAL, "hasTileEntity");
					if (anchor == null)
						throw new RuntimeException("Couldn't find call to hasTileEntity in getItemHandler");
					anchor = ASMHelper.findNextInstruction(anchor);
					// Find the branch
					if (anchor.getType() != anchor.JUMP_INSN)
						throw new RuntimeException("Unexpected lack of jump instruction after hasTileEntity");
					// Grab the destination node for the branch
					LabelNode lvn_tileBlockEnd = ((JumpInsnNode) anchor).label;
					// Go to that label
					anchor = TransformUtil.gotoLabel(m_getItemHandler, lvn_tileBlockEnd);
					if (anchor == null)
						throw new RuntimeException("Couldn't follow label in getItemHandler");
					// Move to the first real instruction
					anchor = ASMHelper.findNextInstruction(anchor);
					// Load up a bunch of variables
					LocalVariableNode lvn_destination = TransformUtil.findLocalVariableWithName(m_getItemHandler, "destination");
					LocalVariableNode lvn_block = TransformUtil.findLocalVariableWithName(m_getItemHandler, "block");
					LocalVariableNode lvn_state = TransformUtil.findLocalVariableWithName(m_getItemHandler, "state");
					LocalVariableNode lvn_blockpos = TransformUtil.findLocalVariableWithName(m_getItemHandler, "blockpos");
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
					this.insertBefore(m_getItemHandler, anchor, inject);
				}

				// ClassDisplayer.instance.printMethod(m_getItemHandler);
			}
		});
	}
}
