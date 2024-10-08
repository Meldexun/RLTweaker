package com.charles445.rltweaker.asm.patch.fancymenu;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchAnimationLoading extends PatchManager {

	public PatchAnimationLoading() {
		// @formatter:off
		this.add(new Patch(this, "de.keksuccino.fancymenu.menu.animation.ResourcePackAnimationRenderer", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode classNode) {
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

				MethodNode m_init = ASMHelper.findMethodNodeOfClass(classNode, "<init>", "(Ljava/lang/String;Ljava/util/List;IZIIII)V");
				AbstractInsnNode target1 = ASMUtil.findInsnWithOpcode(m_init, Opcodes.RETURN, 0);
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

				MethodNode m_render = ASMHelper.findMethodNodeOfClass(classNode, "render", "()V");
				AbstractInsnNode target2 = ASMUtil.findMethodInsn(m_render, Opcodes.INVOKEVIRTUAL, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "renderFrame", "()V", 0);
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

				MethodNode m_renderFrame = ASMHelper.findMethodNodeOfClass(classNode, "renderFrame", "()V");
				AbstractInsnNode target3 = ASMUtil.findMethodInsn(m_renderFrame, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureManager", "func_110577_a", "(Lnet/minecraft/util/ResourceLocation;)V", 0); // bindTexture
				m_renderFrame.instructions.insertBefore(target3, ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "de/keksuccino/fancymenu/menu/animation/ResourcePackAnimationRenderer", "animatedTextureLocation", "Lnet/minecraft/util/ResourceLocation;")
				));
			}
		});

		this.add(new Patch(this, "de.keksuccino.fancymenu.menu.animation.AnimationHandler", 0) {
			@Override
			public void patch(ClassNode classNode) {
				MethodNode m_preloadAnimations = ASMHelper.findMethodNodeOfClass(classNode, "preloadAnimations", "()V");
				AbstractInsnNode target = ASMUtil.findMethodInsn(m_preloadAnimations, Opcodes.INVOKEVIRTUAL, "de/keksuccino/konkrete/config/Config", "getOrDefault", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", 0);
				m_preloadAnimations.instructions.insert(target, ASMUtil.listOf(
					new InsnNode(Opcodes.POP),
					new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;")
				));
			}
		});
		// @formatter:on
	}

}
