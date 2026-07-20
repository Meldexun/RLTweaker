package com.charles445.rltweaker.asm.patch.multimine;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class MultiMineMemoryLeakPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("atomicstryker.multimine.client.MultiMineClient", 0, classNode -> {
			for (MethodNode method : classNode.methods) {
				ASMUtil.stream(method)
						.filter(FieldInsnNode.class::isInstance)
						.map(FieldInsnNode.class::cast)
						.filter(i -> i.owner.equals(classNode.name) && i.name.equals("thePlayer"))
						.collect(Collectors.toList())
						.forEach(getPlayer -> {
							if (getPlayer.getOpcode() == Opcodes.PUTSTATIC) {
								ASMUtil.replace(method, getPlayer, ASMUtil.listOf(
										new InsnNode(Opcodes.POP)));
							} else {
								ASMUtil.replace(method, getPlayer, ASMUtil.listOf(
										new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, "mc", "Lnet/minecraft/client/Minecraft;"),
										TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/player/EntityPlayerSP;"))); // player
							}
						});
			}
		});
		registry.add("atomicstryker.multimine.common.MultiMineServer", ClassWriter.COMPUTE_FRAMES, classNode -> {
			MethodNode onUnloadServer = new MethodNode(Opcodes.ACC_PUBLIC, "onServerStopping", "(Lnet/minecraftforge/fml/common/event/FMLServerStoppingEvent;)V", null, null);
			onUnloadServer.visibleAnnotations = new ArrayList<>();
			onUnloadServer.visibleAnnotations.add(new AnnotationNode("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;"));
			onUnloadServer.instructions.insert(ASMUtil.listOf(
					new InsnNode(Opcodes.ACONST_NULL),
					new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, "serverInstance", "Lnet/minecraft/server/MinecraftServer;"),
					new InsnNode(Opcodes.RETURN)));
			classNode.methods.add(onUnloadServer);
		});
	}

}
