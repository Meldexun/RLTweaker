package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchRealBench {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("pw.prok.realbench.WorkbenchTile", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, c_WorkbenchTile -> {
			FieldNode f_mResult = new FieldNode(Opcodes.ASM5, Opcodes.ACC_PROTECTED, "mResult", "Lnet/minecraft/util/NonNullList;", "Lnet/minecraft/item/ItemStack;", null);

			MethodNode m_init = ASMUtil.find(c_WorkbenchTile, "<init>", "()V");
			MethodNode m_writeSlots = ASMUtil.find(c_WorkbenchTile, "writeSlots");
			MethodNode m_readSlots = ASMUtil.find(c_WorkbenchTile, "readSlots");

			// Add mResult field
			c_WorkbenchTile.fields.add(f_mResult);

			if (true) // Add mResult instantiation
			{
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
				insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "getResultSlotInit", "()Lnet/minecraft/util/NonNullList;", false));
				insert.add(new FieldInsnNode(Opcodes.PUTFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;"));
				m_init.instructions.insert(insert);
			}

			if (true) // writeSlots
			{
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); // nbt
				insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); // nbt, this
				insert.add(new FieldInsnNode(Opcodes.GETFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;")); // nbt, mResult
				insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "writeSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
				m_writeSlots.instructions.insert(insert);
			}

			if (true) // readSlots
			{
				InsnList insert = new InsnList();
				insert.add(new VarInsnNode(Opcodes.ALOAD, 1)); // nbt
				insert.add(new VarInsnNode(Opcodes.ALOAD, 0)); // nbt, this
				insert.add(new FieldInsnNode(Opcodes.GETFIELD, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;")); // nbt, mResult
				insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookRealBench", "readSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
				m_readSlots.instructions.insert(insert);
			}
		});

		registry.add("net.minecraft.inventory.ContainerWorkbench", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, c_ContainerWorkbench -> {
			MethodNode m_init = ASMUtil.find(c_ContainerWorkbench, "<init>", "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V");

			// First we need to figure out if RealBench is even installed
			boolean isRealBenchInstalled = false;
			AbstractInsnNode anchor = m_init.instructions.getFirst();
			while (anchor != null) {
				if (anchor.getType() == AbstractInsnNode.METHOD_INSN) {
					if (((MethodInsnNode) anchor).owner.equals("pw/prok/realbench/asm/ASMHooks")) {
						isRealBenchInstalled = true;
					}
				}
				anchor = anchor.getNext();
			}

			if (!isRealBenchInstalled) {
				// Cancel Patch
				return;
			}
			// RealBench is installed, replace InventoryCraftResult constructor with our own

			anchor = m_init.instructions.getFirst();
			while (anchor != null) {
				if (anchor.getOpcode() == Opcodes.NEW) {
					TypeInsnNode newAnchor = (TypeInsnNode) anchor;
					if (newAnchor.desc.equals("net/minecraft/inventory/InventoryCraftResult")) {
						newAnchor.desc = "com/charles445/rltweaker/hook/HookRealBench$Result";
					}
				} else if (anchor.getOpcode() == Opcodes.INVOKESPECIAL) {
					MethodInsnNode initAnchor = (MethodInsnNode) anchor;
					if (initAnchor.name.equals("<init>") && initAnchor.owner.equals("net/minecraft/inventory/InventoryCraftResult")) {
						initAnchor.owner = "com/charles445/rltweaker/hook/HookRealBench$Result";
						initAnchor.desc = "(Lnet/minecraft/inventory/Container;)V";
						// Also push the container to the stack as it needs it
						InsnList qq = new InsnList();
						qq.add(new VarInsnNode(Opcodes.ALOAD, 0));
						m_init.instructions.insertBefore(initAnchor, qq);
					}
				}

				anchor = anchor.getNext();
			}

			// ASMUtil.LOGGER.info("CLASS DISPLAYER: ContainerWorkbench");
			// (new ClassDisplayer()).printMethod(m_init);
		});

		registry.add("pw.prok.realbench.WorkbenchInventory", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			// RealBench has default support for CraftBukkit weirdness
			// But it's broken because you cannot initialize WorkbenchInventory without a capacity
			// So the function call needs to be tweaked

			MethodNode m_init = ASMUtil.find(clazzNode, "<init>", "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V");

			MethodInsnNode toCall = ASMUtil.first(m_init).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("initRealBench").find();
			toCall.desc = "(I)V"; // Everything else is correct

			// Now we need to add that integer to the stack
			// Which is actually the result of multiplying the two integer parameters

			InsnList inject = new InsnList();
			inject.add(new VarInsnNode(Opcodes.ILOAD, 2));
			inject.add(new VarInsnNode(Opcodes.ILOAD, 3));
			inject.add(new InsnNode(Opcodes.IMUL));
			m_init.instructions.insertBefore(toCall, inject);
		});

		registry.add("pw.prok.realbench.asm.ASMHooks", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			// RealBench doesn't clear its inventory when the contents are dropped
			// We call splitStack similar to InventoryHelper from vanilla

			MethodNode m_dropBlockAsItem = ASMUtil.find(clazzNode, "dropBlockAsItem", "(Lnet/minecraft/world/World;IIILnet/minecraft/item/ItemStack;)V");

			MethodInsnNode toCall = ASMUtil.first(m_dropBlockAsItem).opcode(Opcodes.INVOKESPECIAL).methodInsn("<init>").find();

			InsnList inject = new InsnList();
			inject.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I"));
			inject.add(TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "func_77979_a", "(I)Lnet/minecraft/item/ItemStack;", false));
			m_dropBlockAsItem.instructions.insertBefore(toCall, inject);
		});
	}
}
