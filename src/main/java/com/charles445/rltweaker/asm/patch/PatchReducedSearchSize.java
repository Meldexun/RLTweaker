package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchReducedSearchSize extends PatchManager {
	public PatchReducedSearchSize() {
		super("Reduced Search Size");

		add(new Patch(this, "net.minecraft.world.World", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (true) // func_72872_a getEntitiesWithinAABB
				{
					MethodNode m_getEntitiesWithinAABB = findMethodWithDesc(clazzNode, "(Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", "func_72872_a", "getEntitiesWithinAABB");
					if (m_getEntitiesWithinAABB == null)
						throw new RuntimeException("Couldn't find getEntitiesWithinAABB or func_72872_a with matching desc");

					MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_getEntitiesWithinAABB), Opcodes.INVOKEVIRTUAL, "func_175647_a", "getEntitiesWithinAABB");
					if (getAABBCall == null) {
						ASMLogger.warn("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesWithinAABB or func_175647_a");
						ClassDisplayer.instance.printMethod(m_getEntitiesWithinAABB);
						throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175647_a");
					}

					// Stack here should have everything needed for the static call, which is convenient.
					getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
					getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
					getAABBCall.name = "getEntitiesWithinAABB";
					getAABBCall.desc = "(Lnet/minecraft/world/World;Ljava/lang/Class;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
				}
			}
		});
	}
}
