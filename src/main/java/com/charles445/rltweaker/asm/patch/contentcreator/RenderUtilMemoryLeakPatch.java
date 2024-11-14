package com.charles445.rltweaker.asm.patch.contentcreator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.patch.Patch;
import com.charles445.rltweaker.asm.patch.PatchManager;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class RenderUtilMemoryLeakPatch extends PatchManager {

	public RenderUtilMemoryLeakPatch() {
		this.add(new Patch(this, "surreal.contentcreator.util.RenderUtil", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				for (MethodNode m : clazzNode.methods) {
					if (!m.name.equals("<clinit>")) {
						AbstractInsnNode i = m.instructions.getFirst();
						while (i != null) {
							if (i instanceof FieldInsnNode) {
								FieldInsnNode f = (FieldInsnNode) i;
								if (f.owner.equals(clazzNode.name)) {
									if (f.name.equals("WORLD")) {
										f.name = "MC";
										f.desc = "Lnet/minecraft/client/Minecraft;";
										m.instructions.insert(f, TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71441_e", "Lnet/minecraft/client/multiplayer/WorldClient;"));
									} else if (f.name.equals("PLAYER")) {
										f.name = "MC";
										f.desc = "Lnet/minecraft/client/Minecraft;";
										m.instructions.insert(f, TransformUtil.createObfFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;"));
									}
								}
							}
							i = i.getNext();
						}
					}
				}
			}
		});
	}

}
