package com.charles445.rltweaker.asm.patch.otg;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class FastInternalNamePatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("com.pg85.otg.util.minecraft.defaults.EntityNames", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_toInternalName = ASMUtil.find(clazzNode, "toInternalName");
			m_toInternalName.instructions.clear();
			LabelNode label1 = new LabelNode();
			m_toInternalName.instructions.insert(ASMUtil.listOf(
					new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/ResourceLocation"),
					new InsnNode(Opcodes.DUP),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/ResourceLocation", "<init>", "(Ljava/lang/String;)V", false),
					new VarInsnNode(Opcodes.ASTORE, 1),
					new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/registry/ForgeRegistries", "ENTITIES", "Lnet/minecraftforge/registries/IForgeRegistry;"),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/registries/IForgeRegistry", "containsKey", "(Lnet/minecraft/util/ResourceLocation;)Z", true),
					new JumpInsnNode(Opcodes.IFNE, label1),
					new FieldInsnNode(Opcodes.GETSTATIC, "com/pg85/otg/logging/LogMarker", "ERROR", "Lcom/pg85/otg/logging/LogMarker;"),
					new LdcInsnNode("Entity with id \"{}\" does not exist!"),
					new InsnNode(Opcodes.ICONST_1),
					new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"),
					new InsnNode(Opcodes.DUP),
					new InsnNode(Opcodes.ICONST_0),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new InsnNode(Opcodes.AASTORE),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/pg85/otg/OTG", "log", "(Lcom/pg85/otg/logging/LogMarker;Ljava/lang/String;[Ljava/lang/Object;)V", false),
					label1,
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false),
					new InsnNode(Opcodes.ARETURN)
			));
		});
	}

}
