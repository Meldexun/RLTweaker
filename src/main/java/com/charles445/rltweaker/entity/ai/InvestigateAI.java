package com.charles445.rltweaker.entity.ai;

import com.charles445.rltweaker.config.ModConfig;

import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InvestigateAI extends EntityAIBase {

	private static final Class<?> c_EntityDragonBase;
	static {
		Class<?> c;
		try {
			c = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
		} catch (ClassNotFoundException e) {
			c = null;
		}
		c_EntityDragonBase = c;
	}
	private static final ReflectionMethod<Boolean> IS_FLYING = new ReflectionMethod<>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "isFlying", null);
	private static final ReflectionField<BlockPos> AIR_TARGET = new ReflectionField<>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "airTarget", null);

	private static final ReflectionMethod<Boolean> CAN_NAVIGATE = new ReflectionMethod<>(PathNavigate.class, "func_75485_k", "canNavigate");
	private static final ReflectionField<Path> CURRENT_PATH = new ReflectionField<>(PathNavigate.class, "field_75514_c", "currentPath");

	private final EntityLiving entity;
	private final float healthThreshold;
	private final float executionChance;
	private BlockPos target;
	private int ticksAtLastPos;
	private Vec3d lastPosCheck;
	private int lastTimeWithPath;

	public InvestigateAI(EntityLiving entity, float healthThreshold, float executionChance) {
		this.entity = entity;
		this.healthThreshold = healthThreshold;
		this.executionChance = executionChance;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (target == null) {
			return false;
		}
		Path path = getPathToTarget();
		if (path == null) {
			return false;
		}
		entity.getNavigator().setPath(path, ModConfig.server.minecraft.investigateAiSpeed);
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (entity.getAttackTarget() != null) {
			return false;
		}

		if (entity.ticksExisted - ticksAtLastPos >= 60) {
			if (entity.getDistanceSq(lastPosCheck.x, lastPosCheck.y, lastPosCheck.z) < 1.0D) {
				return false;
			}
			ticksAtLastPos = entity.ticksExisted;
			lastPosCheck = entity.getPositionVector();
		}

		if (CAN_NAVIGATE.invoke(entity.getNavigator()) && (entity.getNavigator().noPath() || entity.getRNG().nextInt(5) == 0)) {
			Path path = getPathToTarget();
			if (path == null) {
				return false;
			}
			entity.getNavigator().setPath(path, ModConfig.server.minecraft.investigateAiSpeed);
		}

		if (!entity.getNavigator().noPath()) {
			lastTimeWithPath = entity.ticksExisted;
		}
		return entity.ticksExisted - lastTimeWithPath < 20;
	}

	@Override
	public void startExecuting() {
		ticksAtLastPos = entity.ticksExisted;
		lastPosCheck = entity.getPositionVector();
		lastTimeWithPath = entity.ticksExisted;
	}

	@Override
	public void resetTask() {
		target = null;
		entity.getNavigator().clearPath();
	}

	public void setTarget(BlockPos pos) {
		if (this.entity.getAttackTarget() != null) {
			return;
		}
		if (this.entity.getHealth() / this.entity.getMaxHealth() > this.healthThreshold) {
			return;
		}
		if (this.entity.getRNG().nextFloat() >= this.executionChance) {
			return;
		}
		if (c_EntityDragonBase != null && c_EntityDragonBase.isInstance(this.entity) && IS_FLYING.invoke(this.entity)) {
			AIR_TARGET.set(this.entity, pos);
			return;
		}
		this.target = pos;
	}

	private Path getPathToTarget() {
		AttributeModifier mod = new AttributeModifier("temp", 128.0D, 0);
		Path currentPath = entity.getNavigator().getPath();

		entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(mod);
		CURRENT_PATH.set(entity.getNavigator(), null);

		Path path = entity.getNavigator().getPathToPos(target);

		CURRENT_PATH.set(entity.getNavigator(), currentPath);
		entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(mod);

		return path;
	}

}
