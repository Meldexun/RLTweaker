package com.charles445.rltweaker.asm.patch.fancymenu;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
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

public class PatchAnimationLoading {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		// @formatter:off
		registry.add("de.keksuccino.fancymenu.menu.animation.ResourcePackAnimationRenderer", ClassWriter.COMPUTE_FRAMES, classNode -> {
			classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "NEXT_ANIMATION_ID", "Ljava/util/concurrent/atomic/AtomicInteger;", null, null));
			classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;", null, null));
			classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "animatedTexture", "Lcom/charles445/rltweaker/client/texture/AnimatedTexture;", null, null));

			MethodNode m_clinit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
			m_clinit.instructions.insert(ASMUtil.listOf(
					// NEXT_ANIMATION_ID = new AtomicInteger();
					new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/atomic/AtomicInteger"),
					new InsnNode(Opcodes.DUP),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/atomic/AtomicInteger", "<init>", "()V", false),
					new FieldInsnNode(Opcodes.PUTSTATIC, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "NEXT_ANIMATION_ID", "Ljava/util/concurrent/atomic/AtomicInteger;"),

					new InsnNode(Opcodes.RETURN)
			));
			classNode.methods.add(m_clinit);

			MethodNode m_init = ASMUtil.find(classNode, "<init>", "(Ljava/lang/String;Ljava/util/List;IZIIII)V");
			AbstractInsnNode target1 = ASMUtil.first(m_init).opcode(Opcodes.RETURN).find();
			m_init.instructions.insertBefore(target1, ASMUtil.listOf(
					// animatedTextureLocation = new ResourceLocation("fancymenu", "animation" + NEXT_ANIMATION_ID.getAndIncrement());
					new VarInsnNode(Opcodes.ALOAD, 0),
					new TypeInsnNode(Opcodes.NEW, "net/minecraft/util/ResourceLocation"),
					new InsnNode(Opcodes.DUP),
					new LdcInsnNode("fancymenu"),
					new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"),
					new InsnNode(Opcodes.DUP),
					new LdcInsnNode("animation"),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false),
					new FieldInsnNode(Opcodes.GETSTATIC, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "NEXT_ANIMATION_ID", "Ljava/util/concurrent/atomic/AtomicInteger;"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/atomic/AtomicInteger", "getAndIncrement", "()I", false),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/util/ResourceLocation", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false),
					new FieldInsnNode(Opcodes.PUTFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;"),

					// animatedTexture = new AnimatedTexture(this);
					new VarInsnNode(Opcodes.ALOAD, 0),
					new TypeInsnNode(Opcodes.NEW, "com/charles445/rltweaker/client/texture/AnimatedTexture"),
					new InsnNode(Opcodes.DUP),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/charles445/rltweaker/client/texture/AnimatedTexture", "<init>", "(Lde/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer;)V", false),
					new FieldInsnNode(Opcodes.PUTFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTexture", "Lcom/charles445/rltweaker/client/texture/AnimatedTexture;")
			));

			MethodNode m_render = ASMUtil.find(classNode, "render", "()V");
			AbstractInsnNode target2 = ASMUtil.first(m_render).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "renderFrame", "()V").find();
			LabelNode label = new LabelNode();
			m_render.instructions.insertBefore(target2, ASMUtil.listOf(
					// TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
					TransformUtil.createObfMethodInsn(Opcodes.INVOKESTATIC, "net/minecraft/client/Minecraft", "func_71410_x", "()Lnet/minecraft/client/Minecraft;", false), // getMinecraft
					TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "func_110434_K", "()Lnet/minecraft/client/renderer/texture/TextureManager;", false), // getTextureManager
					new VarInsnNode(Opcodes.ASTORE, 2),

					// if (textureManager.getTexture(animatedTextureLocation) == null) {
					new VarInsnNode(Opcodes.ALOAD, 2),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;"),
					TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureManager", "func_110581_b", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/texture/ITextureObject;", false), // getTexture
					new JumpInsnNode(Opcodes.IFNONNULL, label),

					// textureManager.loadTexture(animatedTextureLocation, animatedTexture);
					new VarInsnNode(Opcodes.ALOAD, 2),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;"),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTexture", "Lcom/charles445/rltweaker/client/texture/AnimatedTexture;"),
					TransformUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureManager", "func_110579_a", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/ITextureObject;)Z", false), // loadTexture
					new InsnNode(Opcodes.POP),

					// }
					label,

					// animatedTexture.loadFrame();
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTexture", "Lcom/charles445/rltweaker/client/texture/AnimatedTexture;"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/charles445/rltweaker/client/texture/AnimatedTexture", "loadFrame", "()V", false)
			));

			MethodNode m_renderFrame = ASMUtil.find(classNode, "renderFrame", "()V");
			AbstractInsnNode target3 = ASMUtil.first(m_renderFrame).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("net/minecraft/client/renderer/texture/TextureManager", "func_110577_a", "(Lnet/minecraft/util/ResourceLocation;)V").find(); // bindTexture
			m_renderFrame.instructions.insertBefore(target3, ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;")
			));
		});

		registry.add("de.keksuccino.fancymenu.menu.animation.AnimationHandler", 0, classNode -> {
			MethodNode m_preloadAnimations = ASMUtil.find(classNode, "preloadAnimations", "()V");
			AbstractInsnNode target = ASMUtil.first(m_preloadAnimations).opcode(Opcodes.INVOKEVIRTUAL).methodInsn("de/keksuccino/konkrete/config/Config", "getOrDefault", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;").find();
			m_preloadAnimations.instructions.insert(target, ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;")
			));
		});
		// @formatter:on
	}

}
