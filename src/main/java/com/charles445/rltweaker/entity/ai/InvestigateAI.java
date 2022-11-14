package com.charles445.rltweaker.entity.ai;

import java.util.Random;

import com.charles445.rltweaker.entity.ai.InvestigateAIConfig.CallForHelpEntry;
import com.charles445.rltweaker.handler.InvestigateAIHandler;
import com.charles445.rltweaker.util.LazyValue;

import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
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
	private static final ReflectionMethod<Boolean> IS_FLYING = new ReflectionMethod<>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "isFlying", "isFlying");
	private static final ReflectionField<BlockPos> AIR_TARGET = new ReflectionField<>("com.github.alexthe666.iceandfire.entity.EntityDragonBase", "airTarget", "airTarget");

	private static final ReflectionMethod<Boolean> CAN_NAVIGATE = new ReflectionMethod<>(PathNavigate.class, "func_75485_k", "canNavigate");
	private static final ReflectionField<Path> CURRENT_PATH = new ReflectionField<>(PathNavigate.class, "field_75514_c", "currentPath");

	private final EntityLiving entity;
	private final InvestigateAIConfig config;
	private BlockPos target;
	private int ticksAtLastPos;
	private Vec3d lastPosCheck;
	private int lastTimeWithPath;

	public InvestigateAI(EntityLiving entity, InvestigateAIConfig config) {
		this.entity = entity;
		this.config = config;
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
		entity.getNavigator().setPath(path, config.getMovementSpeed());
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

		if (CAN_NAVIGATE.invoke(entity.getNavigator()) && (entity.getNavigator().noPath() || entity.getRNG().nextInt(10) == 0)) {
			Path path = getPathToTarget();
			if (path == null) {
				return false;
			}
			entity.getNavigator().setPath(path, config.getMovementSpeed());
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

	public void setTarget(Entity targetEntity) {
		LazyValue<BlockPos> pos = new LazyValue<>(() -> {
			Random rand = this.entity.getRNG();
			double dist = this.entity.getDistance(targetEntity);
			double horizontalOffset = Math.min(config.getHorizontalOffsetBase() + dist * config.getHorizontalOffsetScale(), config.getHorizontalOffsetMax());
			double verticalOffset = Math.min(config.getVerticalOffsetBase() + dist * config.getVerticalOffsetScale(), config.getVerticalOffsetMax());
			double x = targetEntity.posX + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
			double y = targetEntity.posY + (rand.nextDouble() - 0.5D) * 2.0D * verticalOffset;
			double z = targetEntity.posZ + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
			return new BlockPos(x, y, z);
		});

		boolean inCombat = entity.getAttackTarget() != null;
		boolean aboveHealthThreshold = entity.getHealth() / entity.getMaxHealth() > config.getHealthThreshold();
		boolean chanceTestFailed = entity.getRNG().nextFloat() >= config.getExecutionChance();

		if (!inCombat && !aboveHealthThreshold && !chanceTestFailed) {
			this.setTarget(pos.get());
		}

		config.getCallForHelpEntries().forEach(callForHelpEntry -> {
			if (!callForHelpEntry.ignoreParentInCombat() && inCombat) {
				return;
			}
			if (!callForHelpEntry.ignoreParentAboveHealthThreshold() && aboveHealthThreshold) {
				return;
			}
			if (!callForHelpEntry.ignoreParentChanceTestFailed() && chanceTestFailed) {
				return;
			}
			callForHelp(callForHelpEntry, pos);
		});
	}

	private void callForHelp(CallForHelpEntry callForHelpEntry, LazyValue<BlockPos> pos) {
		Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(callForHelpEntry.getEntityName()));
		AxisAlignedBB aabb = new AxisAlignedBB(
				entity.posX - callForHelpEntry.getHorizontalRange(), entity.posY - callForHelpEntry.getVerticalRange(), entity.posZ - callForHelpEntry.getHorizontalRange(),
				entity.posX + callForHelpEntry.getHorizontalRange(), entity.posY + callForHelpEntry.getVerticalRange(), entity.posZ + callForHelpEntry.getHorizontalRange());
		this.entity.world.getEntitiesWithinAABB(EntityLiving.class, aabb).forEach(entity1 -> {
			if (entity1 == entity) {
				return;
			}
			if (entity1.getClass() != entityClass) {
				return;
			}
			if (entity1.getAttackTarget() != null) {
				return;
			}
			if (entity1.getRNG().nextFloat() >= callForHelpEntry.getChance()) {
				return;
			}
			if (callForHelpEntry.requiresVision() && !entity.canEntityBeSeen(entity1)) {
				return;
			}
			InvestigateAIHandler.getInvestigateAI(entity1).ifPresent(investigateAI -> investigateAI.setTarget(pos.get()));
		});
	}

	private void setTarget(BlockPos pos) {
		if (c_EntityDragonBase != null && c_EntityDragonBase.isInstance(this.entity) && IS_FLYING.isPresent() && IS_FLYING.invoke(this.entity) && AIR_TARGET.isPresent()) {
			AIR_TARGET.set(this.entity, pos);
		} else {
			this.target = pos;
		}
	}

	private Path getPathToTarget() {
		IAttributeInstance followRangeAttribute = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		double amount = (Math.sqrt(entity.getDistanceSqToCenter(target)) + 8.0D) / followRangeAttribute.getAttributeValue();
		AttributeModifier mod = new AttributeModifier("RLTweaker Investigate AI Bonus", amount, 2).setSaved(false);
		Path currentPath = CURRENT_PATH.get(entity.getNavigator());

		entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(mod);
		CURRENT_PATH.set(entity.getNavigator(), null);

		Path path = entity.getNavigator().getPathToPos(target);

		CURRENT_PATH.set(entity.getNavigator(), currentPath);
		entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(mod);

		return path;
	}

}
