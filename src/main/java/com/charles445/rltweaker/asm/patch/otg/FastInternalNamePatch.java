package com.charles445.rltweaker.asm.patch.otg;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

public class FastInternalNamePatch extends PatchManager {

	public FastInternalNamePatch() {
		this.add(new Patch(this, "com.pg85.otg.util.minecraft.defaults.EntityNames", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_toInternalName = this.findMethod(clazzNode, "toInternalName");
				m_toInternalName.instructions.clear();
				LabelNode label1 = new LabelNode();
				LabelNode label2 = new LabelNode();
				m_toInternalName.instructions.insert(ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new IntInsnNode(Opcodes.BIPUSH, ':'),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "indexOf", "(I)I", false),
						new JumpInsnNode(Opcodes.IFGE, label1),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/pg85/otg/logging/LogMarker", "WARN", "Lcom/pg85/otg/logging/LogMarker;"),
						new LdcInsnNode("Entity id \"{}\" is missing namespace!"),
						new InsnNode(Opcodes.ICONST_1),
						new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"),
						new InsnNode(Opcodes.DUP),
						new InsnNode(Opcodes.ICONST_0),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new InsnNode(Opcodes.AASTORE),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/pg85/otg/OTG", "log", "(Lcom/pg85/otg/logging/LogMarker;Ljava/lang/String;[Ljava/lang/Object;)V", false),
						new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"),
						new InsnNode(Opcodes.DUP),
						new LdcInsnNode("minecraft:"),
						new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false),
						new VarInsnNode(Opcodes.ASTORE, 0),
						label1,
						new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/registry/ForgeRegistries", "ENTITIES", "Lnet/minecraftforge/registries/IForgeRegistry;"),
						new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/ResourceLocation"),
						new InsnNode(Opcodes.DUP),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/ResourceLocation", "<init>", "(Ljava/lang/String;)V", false),
						new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minecraftforge/registries/IForgeRegistry", "containsKey", "(Lnet/minecraft/util/ResourceLocation;)Z", true),
						new JumpInsnNode(Opcodes.IFNE, label2),
						new FieldInsnNode(Opcodes.GETSTATIC, "com/pg85/otg/logging/LogMarker", "ERROR", "Lcom/pg85/otg/logging/LogMarker;"),
						new LdcInsnNode("Entity with id \"{}\" does not exist!"),
						new InsnNode(Opcodes.ICONST_1),
						new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"),
						new InsnNode(Opcodes.DUP),
						new InsnNode(Opcodes.ICONST_0),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new InsnNode(Opcodes.AASTORE),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/pg85/otg/OTG", "log", "(Lcom/pg85/otg/logging/LogMarker;Ljava/lang/String;[Ljava/lang/Object;)V", false),
						label2,
						new VarInsnNode(Opcodes.ALOAD, 0),
						new InsnNode(Opcodes.ARETURN)
				));
			}
		});
	}

}
