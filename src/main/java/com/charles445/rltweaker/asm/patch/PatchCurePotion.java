package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchCurePotion {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("com.tmtravlr.potioncore.potion.PotionPotionSickness", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			// Make effects applied by potion sickness incurable

			MethodNode m_lambda$performEffect$0 = ASMUtil.find(clazzNode, "lambda$performEffect$0", "(Lnet/minecraft/entity/EntityLivingBase;)V");

			InsnList inject = new InsnList();
			inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
			inject.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/tmtravlr/potioncore/PotionCoreEffects", "BAD_EFFECTS", "Ljava/util/ArrayList;"));
			inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "addPotionSicknessEffect", "(Lnet/minecraft/entity/EntityLivingBase;Ljava/util/List;)V", false));
			inject.add(new InsnNode(Opcodes.RETURN));
			m_lambda$performEffect$0.instructions.insert(inject);
		});

		registry.add("com.tmtravlr.potioncore.potion.PotionCure", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_clearNegativeEffects = ASMUtil.find(clazzNode, "clearNegativeEffects", "(Lnet/minecraft/entity/EntityLivingBase;)V");

			// Check if cure is disabled

			LabelNode label = new LabelNode();

			InsnList inject = new InsnList();
			inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
			inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "isCureDisabled", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
			inject.add(new JumpInsnNode(Opcodes.IFEQ, label));
			inject.add(new InsnNode(Opcodes.RETURN));
			inject.add(label);
			m_clearNegativeEffects.instructions.insert(inject);

			// Don't remove potion sickness or incurable effects

			JumpInsnNode toCall = ASMUtil.next(m_clearNegativeEffects, ASMUtil.first(m_clearNegativeEffects).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("isBadEffect", "func_76398_f").find()).type(JumpInsnNode.class).find();

			InsnList inject1 = new InsnList();
			inject1.add(new VarInsnNode(Opcodes.ALOAD, 5));
			inject1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "isCurable", "(Lnet/minecraft/potion/PotionEffect;)Z", false));
			inject1.add(new JumpInsnNode(Opcodes.IFEQ, toCall.label));
			m_clearNegativeEffects.instructions.insert(toCall, inject1);
		});

		registry.add("net.minecraft.potion.PotionEffect", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			// Add Incurable interface

			clazzNode.interfaces.add("com/charles445/rltweaker/hook/HookPotionCore$Incurable");

			clazzNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "incurable", "Z", null, false));

			MethodNode methodIsCurable = new MethodNode(Opcodes.ACC_PUBLIC, "isIncurable", "()Z", null, null);
			methodIsCurable.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			methodIsCurable.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
			methodIsCurable.instructions.add(new InsnNode(Opcodes.IRETURN));
			clazzNode.methods.add(methodIsCurable);

			MethodNode methodSetCurable = new MethodNode(Opcodes.ACC_PUBLIC, "setIncurable", "(Z)V", null, null);
			methodSetCurable.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			methodSetCurable.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
			methodSetCurable.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
			methodSetCurable.instructions.add(new InsnNode(Opcodes.RETURN));
			clazzNode.methods.add(methodSetCurable);

			{
				MethodNode m = ASMUtil.find(clazzNode, "<init>", "(Lnet/minecraft/potion/PotionEffect;)V");
				AbstractInsnNode toCall = ASMUtil.prev(m, ASMUtil.last(m).opcode(Opcodes.RETURN).find()).type(LabelNode.class).find();
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				m.instructions.insert(toCall, inject);
			}

			{
				MethodNode m = ASMUtil.findObf(clazzNode, "func_76452_a", "combine");
				LabelNode label1 = new LabelNode();
				LabelNode label2 = new LabelNode();
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76461_c", "I"));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76461_c", "I"));
				inject.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label1));

				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76461_c", "I"));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76461_c", "I"));
				inject.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label2));

				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76460_b", "I"));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "field_76460_b", "I"));
				inject.add(new JumpInsnNode(Opcodes.IF_ICMPGE, label2));

				inject.add(label1);
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(label2);
				m.instructions.insert(inject);
			}

			{
				MethodNode m = ASMUtil.find(clazzNode, "toString");
				AbstractInsnNode toCall = ASMUtil.prev(m, ASMUtil.last(m).opcode(Opcodes.ARETURN).find()).type(LabelNode.class).find();
				LabelNode label = new LabelNode();
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new JumpInsnNode(Opcodes.IFEQ, label));
				inject.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
				inject.add(new InsnNode(Opcodes.DUP));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
				inject.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false));
				inject.add(new LdcInsnNode(", Incurable: true"));
				inject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
				inject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
				inject.add(new VarInsnNode(Opcodes.ASTORE, 1));
				inject.add(label);
				m.instructions.insert(toCall, inject);
			}

			{
				MethodNode m = ASMUtil.find(clazzNode, "equals");
				AbstractInsnNode n = ASMUtil.last(m).opcode(Opcodes.IRETURN).find();
				n = ASMUtil.prev(m, n).opcode(Opcodes.ICONST_0).find();
				LabelNode label = ASMUtil.prev(m, n).type(LabelNode.class).find();
				AbstractInsnNode toCall = ASMUtil.prev(m, label).opcode(Opcodes.ICONST_1).find();
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 2));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
				m.instructions.insertBefore(toCall, inject);
			}

			{
				MethodNode m = ASMUtil.find(clazzNode, "hashCode");
				AbstractInsnNode toCall = ASMUtil.prev(m, ASMUtil.last(m).opcode(Opcodes.IRETURN).find()).type(LabelNode.class).find();
				LabelNode label0 = new LabelNode();
				LabelNode label1 = new LabelNode();
				InsnList inject = new InsnList();
				inject.add(new IntInsnNode(Opcodes.BIPUSH, 31));
				inject.add(new VarInsnNode(Opcodes.ILOAD, 1));
				inject.add(new InsnNode(Opcodes.IMUL));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(new JumpInsnNode(Opcodes.IFEQ, label0));
				inject.add(new InsnNode(Opcodes.ICONST_1));
				inject.add(new JumpInsnNode(Opcodes.GOTO, label1));
				inject.add(label0);
				inject.add(new InsnNode(Opcodes.ICONST_0));
				inject.add(label1);
				inject.add(new InsnNode(Opcodes.IADD));
				inject.add(new IntInsnNode(Opcodes.ISTORE, 1));
				m.instructions.insert(toCall, inject);
			}

			{
				MethodNode m = ASMUtil.findObf(clazzNode, "func_82719_a", "writeCustomPotionEffectToNBT");
				AbstractInsnNode toCall = ASMUtil.last(m).opcode(Opcodes.ARETURN).find();
				InsnList inject = new InsnList();
				inject.add(new InsnNode(Opcodes.DUP));
				inject.add(new LdcInsnNode("Incurable"));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				inject.add(TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74757_a", "(Ljava/lang/String;Z)V", false));
				m.instructions.insertBefore(toCall, inject);
			}

			{
				MethodNode m = ASMUtil.findObf(clazzNode, "func_82722_b", "readCustomPotionEffectFromNBT");
				AbstractInsnNode toCall = ASMUtil.last(m).opcode(Opcodes.ARETURN).find();
				InsnList inject = new InsnList();
				inject.add(new InsnNode(Opcodes.DUP));
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new LdcInsnNode("Incurable"));
				inject.add(TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74767_n", "(Ljava/lang/String;)Z", false));
				inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
				m.instructions.insertBefore(toCall, inject);
			}
		});

		registry.add("rustic.common.potions.PotionTipsy", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			// Make effects applied by tipsy incurable

			MethodNode m_performEffect = ASMUtil.findObf(clazzNode, "func_76394_a", "performEffect");

			m_performEffect.instructions.insertBefore(ASMUtil.first(m_performEffect).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V").find(), ASMUtil.listOf(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
			m_performEffect.instructions.insertBefore(ASMUtil.first(m_performEffect).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V").ordinal(1).find(), ASMUtil.listOf(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
			m_performEffect.instructions.insertBefore(ASMUtil.first(m_performEffect).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V").ordinal(2).find(), ASMUtil.listOf(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
			m_performEffect.instructions.insertBefore(ASMUtil.first(m_performEffect).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V").ordinal(3).find(), ASMUtil.listOf(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
			m_performEffect.instructions.insertBefore(ASMUtil.first(m_performEffect).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/entity/EntityLivingBase", "func_70690_d", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V").ordinal(4).find(), ASMUtil.listOf(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
		});
	}
}
