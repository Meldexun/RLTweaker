package com.charles445.rltweaker.asm.patch.infernalmobs;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.ASMUtil;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class InfernalTargetingCreativePatch extends PatchManager {

	public InfernalTargetingCreativePatch() {
		this.add(new Patch(this, "atomicstryker.infernalmobs.common.MobModifier", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_onUpdate = this.findMethod(clazzNode, "onUpdate");
				MethodInsnNode target = ASMUtil.findMethodInsn(m_onUpdate, Opcodes.INVOKEVIRTUAL, "net/minecraft/world/World", "func_72890_a", "getClosestPlayerToEntity", "(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/EntityPlayer;", 0);
				target.name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(target.owner, "func_184136_b", target.desc); // getNearestPlayerNotCreative
			}
		});
	}

}
