package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchCleanStructureFiles extends PatchManager {

	public PatchCleanStructureFiles() {
		super("Patch Clean Structure Files");

		this.add(new Patch(this, "net.minecraft.world.WorldServer", ClassWriter.COMPUTE_FRAMES) {
			@Override
			public void patch(ClassNode clazzNode) {
				MethodNode m_saveLevel = this.findMethod(clazzNode, "func_73042_a", "saveLevel");

				TransformUtil.insertBeforeFirst(m_saveLevel, ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "preSaveWorld", "(Lnet/minecraft/world/WorldServer;)V", false)
				));
			}
		});
	}

}
