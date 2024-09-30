package com.charles445.rltweaker.entity.ai;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.charles445.rltweaker.handler.InvestigateAIHandler;

import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
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
	private InvestigateAIConfig.Entry configEntry;
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
		entity.getNavigator().setPath(path, configEntry.movementSpeed);
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (entity.getAttackTarget() != null) {
			return false;
		}
		if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed()) {
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
			entity.getNavigator().setPath(path, configEntry.movementSpeed);
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
		setTarget(targetEntity.getPositionVector());
	}

	public void setTarget(Vec3d target) {
		setTarget(target, null, Stream.of(entity).collect(Collectors.toSet()));
	}

	private void setTarget(Vec3d target, @Nullable Entity parent, Set<Entity> checked) {
		config.entries.stream().filter(entry -> {
			if (parent != null) {
				if (!entry.executeOnCalled) {
					return false;
				}
			} else {
				if (!entry.executeOnAttacked) {
					return false;
				}
			}
			return entity.getHealth() / entity.getMaxHealth() <= entry.healthThreshold;
		}).findFirst().ifPresent(entry -> {
			boolean inCombat = entity.getAttackTarget() != null;
			boolean chanceTestFailed = entity.getRNG().nextFloat() >= entry.executionChance;
			if (!inCombat && !chanceTestFailed) {
				this.setTarget(calculateTarget(target, entry), entry);
			}
			this.callForHelp(target, parent, inCombat, chanceTestFailed, checked, entry);
		});
	}

	private void callForHelp(Vec3d target, @Nullable Entity parent, boolean parentInCombat, boolean parentChanceTestFailed, Set<Entity> checked, InvestigateAIConfig.Entry entry) {
		entry.callForHelpEntries.stream().filter(callForHelpEntry -> {
			if (!callForHelpEntry.ignoreParentInCombat && parentInCombat) {
				return false;
			}
			return callForHelpEntry.ignoreParentChanceTestFailed || !parentChanceTestFailed;
		}).forEach(callForHelpEntry -> {
			AxisAlignedBB aabb = new AxisAlignedBB(
					entity.posX - callForHelpEntry.horizontalRange, entity.posY - callForHelpEntry.verticalRange, entity.posZ - callForHelpEntry.horizontalRange,
					entity.posX + callForHelpEntry.horizontalRange, entity.posY + callForHelpEntry.verticalRange, entity.posZ + callForHelpEntry.horizontalRange);
			List<EntityLiving> entities = this.entity.world.getEntitiesWithinAABB(EntityLiving.class, aabb, entity1 -> {
				if (!callForHelpEntry.entityNames.contains(EntityList.getKey(entity1))) {
					return false;
				}
				if (callForHelpEntry.requiresVision && !entity.canEntityBeSeen(entity1)) {
					return false;
				}
				return checked.add(entity1);
			});
			entities.stream()
					.map(InvestigateAIHandler::getInvestigateAI)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(investigateAI -> investigateAI.setTarget(target, entity, checked));
		});
	}

	private BlockPos calculateTarget(Vec3d target, InvestigateAIConfig.Entry entry) {
		Random rand = this.entity.getRNG();
		double dist = this.entity.getDistance(target.x, target.y, target.z);
		double horizontalOffset = Math.min(entry.horizontalOffsetBase + dist * entry.horizontalOffsetScale, entry.horizontalOffsetMax);
		double verticalOffset = Math.min(entry.verticalOffsetBase + dist * entry.verticalOffsetScale, entry.verticalOffsetMax);
		double x = target.x + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
		double y = target.y + (rand.nextDouble() - 0.5D) * 2.0D * verticalOffset;
		double z = target.z + (rand.nextDouble() - 0.5D) * 2.0D * horizontalOffset;
		return new BlockPos(x, y, z);
	}

	private void setTarget(BlockPos pos, InvestigateAIConfig.Entry entry) {
		if (c_EntityDragonBase != null && c_EntityDragonBase.isInstance(this.entity) && IS_FLYING.isPresent() && IS_FLYING.invoke(this.entity) && AIR_TARGET.isPresent()) {
			AIR_TARGET.set(this.entity, pos);
		} else {
			this.target = pos;
		}
		this.configEntry = entry;
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
