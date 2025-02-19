package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchLessCollisions {
	public static void registerTransformers(IClassTransformerRegistry registry) {
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
		 * //m_getCollisionBoxes.instructions.insert(test);
		 * 
		 * 
		 * 
		 * MethodInsnNode getAABBCall = ASMUtil.first(m_getCollisionBoxes).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_72839_b", "getEntitiesWithinAABBExcludingEntity").find();
		 * 
		 * if(getAABBCall == null)
		 * {
		 * System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b");
		 * ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getCollisionBoxes));
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
		registry.add("net.minecraft.world.World", ClassWriter.COMPUTE_MAXS,
				// Possible issues:
				//
				// Explosion owner
				// Projectiles that use ProjectileHelper forwardsRaycast
				c_World -> {
					if (true) // func_72839_b getEntitiesWithinAABBExcludingEntity
					{
						MethodNode m_getEntWithAABBExclEntity = ASMUtil.findObf(c_World, "func_72839_b", "getEntitiesWithinAABBExcludingEntity", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;)Ljava/util/List;");

						if (m_getEntWithAABBExclEntity == null)
							throw new RuntimeException("Couldn't find getEntitiesWithinAABBExcludingEntity or func_72839_b with matching desc");

						MethodInsnNode getAABBCall = ASMUtil.first(m_getEntWithAABBExclEntity).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_175674_a", "getEntitiesInAABBexcluding").find();
						if (getAABBCall == null) {
							System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or func_175674_a");
							ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getEntWithAABBExclEntity));
							throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175674_a");
						}
						// Stack here should have everything needed for the static call, which is convenient.
						getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
						getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
						getAABBCall.name = "getEntitiesInAABBexcluding";
						getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
					}
				});

		// Sponge compatible, 7.3.0
		registry.add("net.minecraft.entity.EntityLivingBase", ClassWriter.COMPUTE_MAXS, c_EntityLivingBase -> {
			if (true) // func_85033_bc collideWithNearbyEntities
			{
				MethodNode m_collideWithNearbyEntities = ASMUtil.findObf(c_EntityLivingBase, "func_85033_bc", "collideWithNearbyEntities", "()V");

				if (m_collideWithNearbyEntities == null)
					throw new RuntimeException("Couldn't find collideWithNearbyEntities or func_85033_bc with matching desc");

				MethodInsnNode getAABBCall = ASMUtil.first(m_collideWithNearbyEntities).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_175674_a", "getEntitiesInAABBexcluding").find();
				if (getAABBCall == null) {
					System.out.println("Unexpected error, please show the below wall of text to the RLTweaker developer, thanks! Couldn't find getEntitiesInAABBexcluding or func_175674_a");
					ASMUtil.LOGGER.info(ASMUtil.methodToString(m_collideWithNearbyEntities));
					throw new RuntimeException("Couldn't find getEntitiesInAABBexcluding or func_175674_a");
				}
				// Stack here should have everything needed for the static call, which is convenient.
				getAABBCall.setOpcode(Opcodes.INVOKESTATIC);
				getAABBCall.owner = "com/charles445/rltweaker/hook/HookWorld";
				getAABBCall.name = "getEntitiesInAABBexcluding";
				getAABBCall.desc = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;";
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
	 * //ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getEntitiesInAABBexcluding));
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
	 * m_getEntitiesInAABBexcluding.instructions.insert(inject);
	 * 
	 * int mreCount = 0;
	 * 
	 * //Now that that's done, go through any instances where MAX_ENTITY_RADIUS is retrieved and replace it with RLRADIUS
	 * //Is it always called MAX_ENTITY_RADIUS? I guess it's a forge thing?
	 * AbstractInsnNode anchor = ASMUtil.first(m_getEntitiesInAABBexcluding).opcode(Opcodes.GETSTATIC).fieldInsn("MAX_ENTITY_RADIUS").find();
	 * while(anchor!=null)
	 * {
	 * m_getEntitiesInAABBexcluding.instructions.set(anchor, new VarInsnNode(Opcodes.DLOAD, rlradius.index));
	 * anchor = ASMUtil.first(m_getEntitiesInAABBexcluding).opcode(Opcodes.GETSTATIC).fieldInsn("MAX_ENTITY_RADIUS").find();
	 * mreCount++;
	 * }
	 * 
	 * System.out.println("Replaced "+mreCount+" instances of MAX_ENTITY_RADIUS");
	 * 
	 * //ASMUtil.LOGGER.info(ASMUtil.methodToString(m_getEntitiesInAABBexcluding));
	 */
}
