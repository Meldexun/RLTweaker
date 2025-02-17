package com.charles445.rltweaker.asm.patch.compat;

import org.objectweb.asm.ClassWriter;

import com.charles445.rltweaker.asm.RLTweakerASM;

import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchBrokenTransformers {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		try {
			Class.forName("com.teamwizardry.librarianlib.asm.LibLibTransformer", false, RLTweakerASM.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			return;
		}

		// Mods that recompute frames with modified class writers can cause serious damage to minecraft
		// Including this one! ha
		//
		// This manually recomputes frames for certain classes depending on the mod

		// LibrarianLib Client
		registry.add("net.minecraft.client.renderer.RenderItem", ClassWriter.COMPUTE_FRAMES, classNode -> {});
		registry.add("net.minecraft.client.renderer.entity.layers.LayerArmorBase", ClassWriter.COMPUTE_FRAMES, classNode -> {});
		registry.add("net.minecraft.client.renderer.BlockRendererDispatcher", ClassWriter.COMPUTE_FRAMES, classNode -> {});
		registry.add("net.minecraft.client.particle.Particle", ClassWriter.COMPUTE_FRAMES, classNode -> {});

		// LibrarianLib Server
		registry.add("net.minecraft.world.World", ClassWriter.COMPUTE_FRAMES, classNode -> {});
		registry.add("net.minecraft.network.NetHandlerPlayServer", ClassWriter.COMPUTE_FRAMES, classNode -> {});
	}
}
