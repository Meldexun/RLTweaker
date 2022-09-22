package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchPotionSickness extends PatchManager
{
	public PatchPotionSickness()
	{
		super("Patch Potion Sickness");
		
		add(new Patch(this, "com.tmtravlr.potioncore.potion.PotionPotionSickness", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				//Make effects applied by potion sickness incurable
				
				MethodNode m_lambda$performEffect$0 = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/EntityLivingBase;)V", "lambda$performEffect$0");
				
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
				inject.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/tmtravlr/potioncore/PotionCoreEffects", "BAD_EFFECTS", "Ljava/util/ArrayList;"));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "addIncurableEffect", "(Lnet/minecraft/entity/EntityLivingBase;Ljava/util/List;)V", false));
				inject.add(new InsnNode(Opcodes.RETURN));
				TransformUtil.insertBeforeFirst(m_lambda$performEffect$0, inject);
			}
		});
		
		add(new Patch(this, "com.tmtravlr.potioncore.potion.PotionCure", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				//Don't remove potion sickness or incurable effects
				
				MethodNode m_clearNegativeEffects = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/EntityLivingBase;)V", "clearNegativeEffects");
				JumpInsnNode toCall = TransformUtil.findNextInsnWithType(TransformUtil.findNextCallWithOpcodeAndName(first(m_clearNegativeEffects), Opcodes.INVOKEVIRTUAL, "isBadEffect", "func_76398_f"), JumpInsnNode.class);
				
				InsnList inject = new InsnList();
				inject.add(new VarInsnNode(Opcodes.ALOAD, 5));
				inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookPotionCore", "isCurable", "(Lnet/minecraft/potion/PotionEffect;)Z", false));
				inject.add(new JumpInsnNode(Opcodes.IFEQ, toCall.label));
				this.insert(m_clearNegativeEffects, toCall, inject);
			}
		});
		
		add(new Patch(this, "net.minecraft.potion.PotionEffect", ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
		{
			@Override
			public void patch(ClassNode clazzNode)
			{
				//Add Incurable interface
				
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
					MethodNode m = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/potion/PotionEffect;)V", "<init>");
					AbstractInsnNode toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, Opcodes.RETURN), LabelNode.class);
					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					this.insert(m, toCall, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "func_76452_a", "combine");
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
					TransformUtil.insertBeforeFirst(m, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "toString");
					AbstractInsnNode toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, Opcodes.ARETURN), LabelNode.class);
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
					this.insert(m, toCall, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "equals");
					AbstractInsnNode n = ASMHelper.findLastInstructionWithOpcode(m, Opcodes.IRETURN);
					n = ASMHelper.findPreviousInstructionWithOpcode(n, Opcodes.ICONST_0);
					LabelNode label = TransformUtil.findPreviousInsnWithType(n, LabelNode.class);
					AbstractInsnNode toCall = ASMHelper.findPreviousInstructionWithOpcode(label, Opcodes.ICONST_1);
					InsnList inject = new InsnList();
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 2));
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					inject.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
					this.insertBefore(m, toCall, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "hashCode");
					AbstractInsnNode toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, Opcodes.IRETURN), LabelNode.class);
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
					this.insert(m, toCall, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "func_82719_a", "writeCustomPotionEffectToNBT");
					AbstractInsnNode toCall = ASMHelper.findLastInstructionWithOpcode(m, Opcodes.ARETURN);
					InsnList inject = new InsnList();
					inject.add(new InsnNode(Opcodes.DUP));
					inject.add(new LdcInsnNode("Incurable"));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					inject.add(TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74757_a", "(Ljava/lang/String;Z)V", false));
					this.insertBefore(m, toCall, inject);
				}
				
				{
					MethodNode m = this.findMethod(clazzNode, "func_82722_b", "readCustomPotionEffectFromNBT");
					AbstractInsnNode toCall = ASMHelper.findLastInstructionWithOpcode(m, Opcodes.ARETURN);
					InsnList inject = new InsnList();
					inject.add(new InsnNode(Opcodes.DUP));
					inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					inject.add(new LdcInsnNode("Incurable"));
					inject.add(TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "func_74767_n", "(Ljava/lang/String;)Z", false));
					inject.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
					this.insertBefore(m, toCall, inject);
				}
			}
		});
	}
}
