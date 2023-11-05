package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.config.ModConfig;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.util.Constants.NBT;

public enum StructureCleanupMode {

	ALWAYS {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			NBTTagCompound structureStartsNBT = structureData.getTagCompound();
			if (structureStartsNBT.hasNoTags()) {
				return false;
			}
			structureStartsNBT.getKeySet()
					.clear();
			return true;
		}
	},
	GENERATED {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			NBTTagCompound structureStartsNBT = structureData.getTagCompound();
			return structureStartsNBT.getKeySet()
					.removeIf(k -> {
						NBTTagCompound structureStartNBT = structureStartsNBT.getCompoundTag(k);
						int[] bb = structureStartNBT.getIntArray("BB");
						return allChunksGenerated(world, bb);
					});
		}
	},
	GENERATED_COMPONENTS {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			NBTTagCompound structureStartsNBT = structureData.getTagCompound();
			return structureStartsNBT.getKeySet()
					.stream()
					.map(structureStartsNBT::getCompoundTag)
					.map(structureStartNBT -> structureStartNBT.getTagList("Children", NBT.TAG_COMPOUND))
					.mapToLong(children -> {
						long childrenRemoved = 0L;

						for (int i = 0; i < children.tagCount(); i++) {
							NBTTagCompound child = children.getCompoundTagAt(i);
							int[] bb = child.getIntArray("BB");

							if (allChunksGenerated(world, bb)) {
								children.removeTag(i);
								i--;
								childrenRemoved++;
							}
						}

						return childrenRemoved;
					})
					.sum() > 0L | structureStartsNBT.getKeySet()
							.removeIf(k -> {
								NBTTagCompound structureStartNBT = structureStartsNBT.getCompoundTag(k);
								return structureStartNBT.getTagList("Children", NBT.TAG_COMPOUND)
										.tagCount() == 0;
							});
		}
	},
	GENERATED_COMPONENTS_ONLY {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			NBTTagCompound structureStartsNBT = structureData.getTagCompound();
			return structureStartsNBT.getKeySet()
					.stream()
					.map(structureStartsNBT::getCompoundTag)
					.map(structureStartNBT -> structureStartNBT.getTagList("Children", NBT.TAG_COMPOUND))
					.mapToLong(children -> {
						long childrenRemoved = 0L;

						for (int i = 0; i < children.tagCount(); i++) {
							NBTTagCompound child = children.getCompoundTagAt(i);
							int[] bb = child.getIntArray("BB");

							if (allChunksGenerated(world, bb)) {
								children.removeTag(i);
								i--;
								childrenRemoved++;
							}
						}

						return childrenRemoved;
					})
					.sum() > 0L;
		}
	};

	public abstract boolean clean(WorldServer world, MapGenStructureData structureData);

	private static boolean allChunksGenerated(WorldServer world, int[] bb) {
		int minX = bb[0];
		int minZ = bb[2];
		int maxX = bb[3];
		int maxZ = bb[5];
		if (minX > maxX || minZ > maxZ) {
			return true;
		}
		if ((long) maxX - (long) minX > ModConfig.server.minecraft.cleanupStructureWorldgenFilesSizeLimit
				|| (long) maxZ - (long) minZ > ModConfig.server.minecraft.cleanupStructureWorldgenFilesSizeLimit) {
			return true;
		}
		for (int x = (minX >> 4); x <= (maxX >> 4); x++) {
			for (int z = (minZ >> 4); z <= (maxZ >> 4); z++) {
				if (!world.isChunkGeneratedAt(x, z))
					return false;

			}
		}
		return true;
	}

}
