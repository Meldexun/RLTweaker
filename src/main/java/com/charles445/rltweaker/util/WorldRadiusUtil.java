package com.charles445.rltweaker.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldRadiusUtil {

	public static List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entityIn, AxisAlignedBB bb, double size) {
		return getEntitiesInAABBexcluding(world, entityIn, bb, EntitySelectors.NOT_SPECTATING, size);
	}

	public static List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entity, AxisAlignedBB bb, @Nullable Predicate<? super Entity> predicate,
			double size) {
		List<Entity> list = new ArrayList<>();
		int x0 = MathHelper.floor(bb.minX - size) >> 4;
		int x1 = MathHelper.floor(bb.maxX + size) >> 4;
		int z0 = MathHelper.floor(bb.minZ - size) >> 4;
		int z1 = MathHelper.floor(bb.maxZ + size) >> 4;

		for (int x = x0; x <= x1; x++) {
			for (int z = z0; z <= z1; z++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
				if (chunk != null) {
					getEntitiesWithinAABBForEntity(chunk, entity, bb, list, predicate, size);
				}
			}
		}

		return list;
	}

	public static <T extends Entity> List<T> getEntitiesWithinAABB(World world, Class<? extends T> classEntity, AxisAlignedBB bb, double size) {
		return getEntitiesWithinAABB(world, classEntity, bb, EntitySelectors.NOT_SPECTATING, size);
	}

	public static <T extends Entity> List<T> getEntitiesWithinAABB(World world, Class<? extends T> clazz, AxisAlignedBB aabb,
			@Nullable Predicate<? super T> filter, double size) {
		List<T> list = new ArrayList<>();
		int x0 = MathHelper.floor(aabb.minX - size) >> 4;
		int x1 = MathHelper.floor(aabb.maxX + size) >> 4;
		int z0 = MathHelper.floor(aabb.minZ - size) >> 4;
		int z1 = MathHelper.floor(aabb.maxZ + size) >> 4;

		for (int x = x0; x <= x1; x++) {
			for (int z = z0; z <= z1; z++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
				if (chunk != null) {
					getEntitiesOfTypeWithinAABB(chunk, clazz, aabb, list, filter, size);
				}
			}
		}

		return list;
	}

	public static <T extends Entity> T findNearestEntityWithinAABB(World world, Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo, double size) {
		List<T> entities = getEntitiesWithinAABB(world, entityType, aabb, size);
		T nearestEntity = null;
		double minDist = Double.MAX_VALUE;

		for (T entity : entities) {
			if (entity != closestTo) {
				double dist = closestTo.getDistanceSq(entity);

				if (dist <= minDist) {
					nearestEntity = entity;
					minDist = dist;
				}
			}
		}

		return nearestEntity;
	}

	private static <T extends Entity> void getEntitiesOfTypeWithinAABB(Chunk chunk, Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill,
			Predicate<? super T> filter, double size) {
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		if (aabb.maxY < 0.0D || aabb.minY >= entityLists.length << 4)
			return;
		int y0 = Math.max(MathHelper.floor(aabb.minY - size) >> 4, 0);
		int y1 = Math.min(MathHelper.floor(aabb.maxY + size) >> 4, entityLists.length - 1);

		for (int y = y0; y <= y1; y++) {
			for (T entity : entityLists[y].getByClass(entityClass)) {
				if (!entity.getEntityBoundingBox().intersects(aabb))
					continue;
				if (filter == null || filter.apply(entity))
					listToFill.add(entity);
			}
		}
	}

	private static void getEntitiesWithinAABBForEntity(Chunk chunk, @Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill,
			Predicate<? super Entity> filter, double size) {
		ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
		if (aabb.maxY < 0.0D || aabb.minY >= entityLists.length << 4)
			return;
		int y0 = Math.max(MathHelper.floor(aabb.minY - size) >> 4, 0);
		int y1 = Math.min(MathHelper.floor(aabb.maxY + size) >> 4, entityLists.length - 1);

		for (int y = y0; y <= y1; y++) {
			ClassInheritanceMultiMap<Entity> entityList = entityLists[y];
			if (entityList.isEmpty())
				continue;
			for (Entity entity : entityList) {
				if (!entity.getEntityBoundingBox().intersects(aabb))
					continue;
				if (entity == entityIn)
					continue;
				if (filter == null || filter.apply(entity))
					listToFill.add(entity);

				Entity[] partEntities = entity.getParts();
				if (partEntities != null) {
					for (Entity partEntity : partEntities) {
						if (!partEntity.getEntityBoundingBox().intersects(aabb))
							continue;
						if (partEntity == entityIn)
							continue;
						if (filter == null || filter.apply(partEntity))
							listToFill.add(partEntity);
					}
				}
			}
		}
	}

}
