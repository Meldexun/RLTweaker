package com.charles445.rltweaker.hook;

import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldRadiusUtil;
import com.google.common.base.Predicate;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class HookWorld
{
	public static boolean warnItemHook = false;
	
	public static boolean warnSearchHook = false;
	
	//HOOK into World.getCollisionBoxes
	public static List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entity, AxisAlignedBB bb)
	{
		if(!ModConfig.server.minecraft.lessCollisions)
			return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
		
		if(entity != null)
		{
			try
			{
				return WorldRadiusUtil.getEntitiesWithinAABBExcludingEntity(world, entity, bb, CollisionUtil.getRadiusForEntity(entity));
			}
			catch(Exception e)
			{
				if(!warnItemHook)
				{
					warnItemHook = true;
					RLTweaker.logger.error("Error running CollisionUtil!",e);
					ErrorUtil.logSilent("CollisionUtil Critical Failure");
				}
			}
					
		}
		
		return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
	}
	
	//HOOK into EntityLivingBase.collideWithNearbyEntities
	public static List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entity, AxisAlignedBB bb, @Nullable Predicate <? super Entity > predicate)
	{
		if(!ModConfig.server.minecraft.lessCollisions)
			return world.getEntitiesInAABBexcluding(entity, bb, predicate);
		
		if(entity != null)
		{
			try
			{
				return WorldRadiusUtil.getEntitiesInAABBexcluding(world, entity, bb, predicate, CollisionUtil.getRadiusForEntity(entity));
			}
			catch(Exception e)
			{
				if(!warnItemHook)
				{
					warnItemHook = true;
					RLTweaker.logger.error("Error running CollisionUtil!",e);
					ErrorUtil.logSilent("CollisionUtil Critical Failure");
				}
			}
		}
		
		return world.getEntitiesInAABBexcluding(entity, bb, predicate);
	}
	
	//HOOK into World.getEntitiesWithinAABB
	public static List<Entity> getEntitiesWithinAABB(World world, Class<Entity> clazz, AxisAlignedBB bb, @Nullable Predicate <? super Entity > predicate)
	{
		try
		{
			if(clazz.equals(EntityItem.class) || clazz.equals(EntityPlayer.class))
			{
				return WorldRadiusUtil.getEntitiesWithinAABB(world, clazz, bb, predicate, 2.0d);
			}
		}
		catch(Exception e)
		{
			if(!warnSearchHook)
			{
				warnSearchHook = true;
				RLTweaker.logger.error("Error running ReducedSearchSize!",e);
				ErrorUtil.logSilent("ReducedSearchSize Critical Failure");
			}
		}
		
		
		return world.getEntitiesWithinAABB(clazz, bb, predicate);
	}
	
	//UNUSED
	//HOOK
	//com/charles445/rltweaker/hook/HookWorld
	//getAABExcludingSizeFor
	//(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;)D
	
	/////////////////
	//ChunkTick
	/////////////////
	
	public static boolean chunkTickPatchEnabled = false;
	
	//Serene Seasons
	public static IChunkTickPost sereneSeasonsPost = null;
	
	//com/charles445/rltweaker/hook/HookWorld
		//onPreUpdateBlocks
		//(Lnet/minecraft/world/WorldServer;)Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;
	public static ChunkTickContainer onPreUpdateBlocks(WorldServer world)
	{
		if(!chunkTickPatchEnabled)
			chunkTickPatchEnabled = true;
		
		return new ChunkTickContainer(world);
	}
	
	//com/charles445/rltweaker/hook/HookWorld
	//postBlockTickChunk
	//(Lnet/minecraft/world/chunk/Chunk;Lcom/charles445/rltweaker/hook/HookWorld$ChunkTickContainer;)V
	public static void postBlockTickChunk(Chunk chunk, ChunkTickContainer container)
	{
		if(container.sereneSeasonsCompanionPost != null)
			sereneSeasonsPost.invoke(chunk, container.sereneSeasonsCompanionPost);
	}
	
	public static class ChunkTickContainer
	{
		public Object sereneSeasonsCompanionPost;
		
		public ChunkTickContainer(WorldServer world)
		{
			sereneSeasonsCompanionPost = sereneSeasonsPost == null ? null : sereneSeasonsPost.preUpdate(world);
		}
	}
	
	public static interface IChunkTickPost<T>
	{
		@Nullable
		public T preUpdate(WorldServer world);
		
		public void invoke(Chunk c, T companion);
	}

	/////////////////
	/////////////////
	
	
	private static final GetCollisionBoxesEvent EVENT = new GetCollisionBoxesEvent(null, null, null, null);
	private static final int EVENT_BUS_ID = new ReflectionField<Integer>(EventBus.class, "busID", null).getInt(MinecraftForge.EVENT_BUS);

	private static boolean hasListeners() {
		return EVENT.getListenerList().getListeners(EVENT_BUS_ID).length > 0;
	}

	public static boolean getCollisionBoxes(World world, @Nullable Entity entity, AxisAlignedBB aabb, boolean p_191504_3_, @Nullable List<AxisAlignedBB> list) {
		if (p_191504_3_) {
			if (aabb.minX < -30_000_000 || aabb.maxX > 30_000_000 || aabb.minZ < -30_000_000 || aabb.maxZ > 30_000_000) {
				return true;
			}
			if (hasListeners()) {
				MinecraftForge.EVENT_BUS.post(new GetCollisionBoxesEvent(world, entity, aabb, list));
				if (!list.isEmpty()) {
					return true;
				}
			}
		} else if (entity != null && entity.isOutsideBorder() == world.isInsideWorldBorder(entity)) {
			entity.setOutsideBorder(!entity.isOutsideBorder());
		}

		int minX = MathHelper.floor(aabb.minX) - 1;
		int maxX = MathHelper.floor(aabb.maxX) + 1;
		int minZ = MathHelper.floor(aabb.minZ) - 1;
		int maxZ = MathHelper.floor(aabb.maxZ) + 1;
		int minY = Math.max(MathHelper.floor(aabb.minY) - 1, 0);
		int maxY = Math.min(MathHelper.floor(aabb.maxY) + 1, 255);
		int minChunkX = minX >> 4;
		int maxChunkX = maxX >> 4;
		int minChunkZ = minZ >> 4;
		int maxChunkZ = maxZ >> 4;
		int minChunkY = minY >> 4;
		int maxChunkY = maxY >> 4;
		WorldBorder border = world.getWorldBorder();
		boolean checkBorder = !p_191504_3_
				&& entity != null
				&& !entity.isOutsideBorder()
				&& (minX < border.minX() || maxX + 1 > border.maxX() || minZ < border.minZ() || maxZ + 1 > border.maxZ());
		MutableBlockPos pos = new MutableBlockPos();

		for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
			int x0 = Math.max(minX, chunkX << 4);
			int x1 = Math.min(maxX, (chunkX << 4) | 15);

			for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
				if (chunk == null) continue;

				int z0 = Math.max(minZ, chunkZ << 4);
				int z1 = Math.min(maxZ, (chunkZ << 4) | 15);

				for (int chunkY = minChunkY; chunkY <= maxChunkY; chunkY++) {
					ExtendedBlockStorage section = chunk.getBlockStorageArray()[chunkY];
					if (section == null) continue;

					int y0 = Math.max(minY, chunkY << 4);
					int y1 = Math.min(maxY, (chunkY << 4) | 15);

					for (int x = x0; x <= x1; x++) {
						boolean xBorder = x == minX || x == maxX;
						
						for (int z = z0; z <= z1; z++) {
							boolean zBorder = z == minZ || z == maxZ;
							if (xBorder && zBorder) continue;
							
							for (int y = y0; y <= y1; y++) {
								if ((xBorder || zBorder) && (y == minY || y == maxY)) continue;

								pos.setPos(x, y, z);

								IBlockState state;
								if (checkBorder && !border.contains(pos)) {
									state = Blocks.STONE.getDefaultState();
								} else {
									state = section.get(x & 15, y & 15, z & 15);
								}

								state.addCollisionBoxToList(world, pos, aabb, list, entity, false);

								if (p_191504_3_ && !list.isEmpty()) {
									return true;
								}
							}
						}
					}
				}
			}
		}

		return !list.isEmpty();
	}

}
