package com.charles445.rltweaker.asm.patch.compat;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.charles445.rltweaker.asm.patch.IPatchManager;
import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ModTransformer;

public class PatchBrokenTransformers extends PatchManager {
	public PatchBrokenTransformers() {
		super("Broken Transformers");

		// Mods that recompute frames with modified class writers can cause serious damage to minecraft
		// Including this one! ha
		//
		// This manually recomputes frames for certain classes depending on the mod

		// LibrarianLib Client
		add(new RecomputePatch(this, "net.minecraft.client.renderer.RenderItem", ModTransformer.LIBRARIANLIB));
		add(new RecomputePatch(this, "net.minecraft.client.renderer.entity.layers.LayerArmorBase", ModTransformer.LIBRARIANLIB));
		add(new RecomputePatch(this, "net.minecraft.client.renderer.BlockRendererDispatcher", ModTransformer.LIBRARIANLIB));
		add(new RecomputePatch(this, "net.minecraft.client.particle.Particle", ModTransformer.LIBRARIANLIB));

		// LibrarianLib Server
		add(new RecomputePatch(this, "net.minecraft.world.World", ModTransformer.LIBRARIANLIB));
		add(new RecomputePatch(this, "net.minecraft.network.NetHandlerPlayServer", ModTransformer.LIBRARIANLIB));
	}

	public class RecomputePatch extends Patch {
		@Nullable
		private ModTransformer mod;

		public RecomputePatch(IPatchManager manager, String target, @Nullable ModTransformer mod) {
			super(manager, target, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			this.mod = mod;
		}

		@Override
		public void patch(ClassNode clazzNode) {
			if (this.mod != null && !this.hasModTransformer(mod)) {
				this.cancelled = true;
				return;
			}
		}

	}
}
