package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchLessCollisions extends PatchManager {
	public PatchLessCollisions() {
		super("Less Collisions");

		// Sponge overwrites this entirely, so it doesn't work
		/*
		 * add(new Patch(this, "net.minecraft.world.World", ClassWriter.COMPUTE_MAXS)
		 * {
		 * 
		 * @Override
		 * public void patch(ClassNode c_World)
		 * {
		 * if(true) //"func_184144_a", "getCollisionBoxes"
		 * {
		 * MethodNode m_getCollisionBoxes = findMethodWithDesc(c_World,
		 * "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;",
		 * "func_184144_a", "getCollisionBoxes");
		 * 
		 * if(m_getCollisionBoxes == null)
		 * throw new RuntimeException("Couldn't find getCollisionBoxes or func_184144_a with matching desc");
		 * 
		 * //LocalVariableNode rlradius = TransformUtil.createNewLocalVariable(m_getCollisionBoxes, "RLRADIUS", "D");
		 * //if(rlradius == null)
		 * // throw new RuntimeException("Couldn't create new local variable RLRADIUS for getCollisionBoxes");
		 * //m_getCollisionBoxes.localVariables.add(rlradius);
		 * 
		 * //InsnList test = new InsnList();
		 * //test.add(new InsnNode(Opcodes.ICONST_5));
		 * //test.add(new InsnNode(Opcodes.I2D));
		 * //test.add(new VarInsnNode(Opcodes.DSTORE,rlradius.index));
		 * //test.add(new VarInsnNode(Opcodes.DLOAD,rlradius.index));
		 * //test.add(new InsnNode(Opcodes.POP2));
		 * //TransformUtil.insertBeforeFirst(m_getCollisionBoxes, test);
		 * 
		 * 
		 * 
		 * MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_getCollisionBoxes), Opcodes.INVOKEVIRTUAL, "func_72839_b", "getEntitiesWithinAABBExcludingEntity");
		 * 
		 * if(getAABBCall == null)
		 * {
		 * System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b");
		 * ClassDisplayer.instance.printMethod(m_getCollisionBoxes);
		 * throw new RuntimeException("Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b");
		 * }
		 * //Stack here should have everything needed for the static call, which is convenient.
		 * getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
		 * getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
		 * getAABBCall.name = "getEntitiesWithinAABBExcludingEntity";
		 * getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;";
		 * }
		 * 
		 * }
		 * });
		 */

		// Sponge compatible, 7.3.0
		add(new Patch(this, "net.minecraft.world.World", ClassWriter.COMPUTE_MAXS) {
			// Possible issues:
			//
			// Explosion owner
			// Projectiles that use ProjectileHelper forwardsRaycast

			@Override
			public void patch(ClassNode c_World) {
				if (true) // func_72839_b getEntitiesWithinAABBExcludingEntity
				{
					MethodNode m_getEntWithAABBExclEntity = findMethodWithDesc(c_World, "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;", "func_72839_b", "getEntitiesWithinAABBExcludingEntity");

					if (m_getEntWithAABBExclEntity == null)
						throw new RuntimeException("Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b with matching desc");

					MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_getEntWithAABBExclEntity), Opcodes.INVOKEVIRTUAL, "func_175674_a", "getEntitiesInAABBexcluding");
					if (getAABBCall == null) {
						System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or func_175674_a");
						ClassDisplayer.instance.printMethod(m_getEntWithAABBExclEntity);
						throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175674_a");
					}
					// Stack here should have everything needed for the static call, which is convenient.
					getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
					getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
					getAABBCall.name = "getEntitiesInAABBexcluding";
					getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
				}
			}
		});

		// Sponge compatible, 7.3.0
		add(new Patch(this, "net.minecraft.entity.EntityLivingBase", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode c_EntityLivingBase) {
				if (true) // func_85033_bc collideWithNearbyEntities
				{
					MethodNode m_collideWithNearbyEntities = findMethodWithDesc(c_EntityLivingBase, "()V", "func_85033_bc", "collideWithNearbyEntities");

					if (m_collideWithNearbyEntities == null)
						throw new RuntimeException("Couldn't find collideWithNearbyEntities or func_85033_bc with matching desc");

					MethodInsnNode getAABBCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_collideWithNearbyEntities), Opcodes.INVOKEVIRTUAL, "func_175674_a", "getEntitiesInAABBexcluding");
					if (getAABBCall == null) {
						System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or func_175674_a");
						ClassDisplayer.instance.printMethod(m_collideWithNearbyEntities);
						throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175674_a");
					}
					// Stack here should have everything needed for the static call, which is convenient.
					getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
					getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
					getAABBCall.name = "getEntitiesInAABBexcluding";
					getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
				}
			}
		});
	}

	// Another implementation, this one works pretty well but isn't collisions specific...
	/*
	 * MethodNode m_getEntitiesInAABBexcluding = findMethod(c_World, "func_175674_a","getEntitiesInAABBexcluding");
	 * 
	 * if(m_getEntitiesInAABBexcluding == null)
	 * throw new RuntimeException("Couldn't find func_175674_a or getEntitiesInAABBexcluding");
	 * 
	 * //ClassDisplayer.instance.printMethod(m_getEntitiesInAABBexcluding);
	 * 
	 * LocalVariableNode rlradius = TransformUtil.createNewLocalVariable(m_getEntitiesInAABBexcluding, "RLRADIUS", "D");
	 * if(rlradius == null)
	 * throw new RuntimeException("Couldn't create new local variable RLRADIUS for getEntitiesInAABBexcluding");
	 * m_getEntitiesInAABBexcluding.localVariables.add(rlradius);
	 * 
	 * //Now that new local variable is registered, initialize it
	 * InsnList inject = new InsnList();
	 * //HookWorld, getAABExcludingSizeFor
	 * inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
	 * inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
	 * inject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookWorld","getAABExcludingSizeFor", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)D", false));
	 * inject.add(new VarInsnNode(Opcodes.DSTORE, rlradius.index));
	 * TransformUtil.insertBeforeFirst(m_getEntitiesInAABBexcluding, inject);
	 * 
	 * int mreCount = 0;
	 * 
	 * //Now that that's done, go through any instances where MAX_ENTITY_RADIUS is retrieved and replace it with RLRADIUS
	 * //Is it always called MAX_ENTITY_RADIUS? I guess it's a forge thing?
	 * AbstractInsnNode anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_getEntitiesInAABBexcluding), Opcodes.GETSTATIC, "MAX_ENTITY_RADIUS");
	 * while(anchor!=null)
	 * {
	 * m_getEntitiesInAABBexcluding.instructions.set(anchor, new VarInsnNode(Opcodes.DLOAD, rlradius.index));
	 * anchor = TransformUtil.findNextFieldWithOpcodeAndName(first(m_getEntitiesInAABBexcluding), Opcodes.GETSTATIC, "MAX_ENTITY_RADIUS");
	 * mreCount++;
	 * }
	 * 
	 * System.out.println("Replaced "+mreCount+" instances of MAX_ENTITY_RADIUS");
	 * 
	 * //ClassDisplayer.instance.printMethod(m_getEntitiesInAABBexcluding);
	 */
}
