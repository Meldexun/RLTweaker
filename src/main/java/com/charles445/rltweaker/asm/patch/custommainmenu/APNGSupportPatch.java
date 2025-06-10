package com.charles445.rltweaker.asm.patch.custommainmenu;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class APNGSupportPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("lumien.custommainmenu.lib.textures.TextureApng", "bind", ClassWriter.COMPUTE_FRAMES, method -> {
			method.instructions.insert(ASMUtil.listWithLabel(label -> ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "lumien/custommainmenu/lib/textures/TextureApng", "rl", "Lnet/minecraft/util/ResourceLocation;"),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/custommainmenu/APNGSupportHook", "bind", "(Lnet/minecraft/util/ResourceLocation;)V", false),
					new InsnNode(Opcodes.ICONST_0),
					new JumpInsnNode(Opcodes.IFNE, label),
					new InsnNode(Opcodes.RETURN),
					label
			)));
		});
	}

}
