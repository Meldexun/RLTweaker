package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.NBTUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.util.Constants.NBT;

public enum StructureCleanupMode {

	ALWAYS {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.clear(structureData.getTagCompound());
		}
	},
	ALWAYS_COMPONENTS_ONLY {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.<NBTTagCompound>stream(structureData.getTagCompound())
					.filter(structureStartTag -> NBTUtil.remove(structureStartTag, KEY_CHILDREN))
					.count() > 0L;
		}
	},
	GENERATED {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.<NBTTagCompound>removeIf(structureData.getTagCompound(),
					structureStartTag -> allChunksGenerated(world, structureStartTag.getIntArray(KEY_BB)));
		}
	},
	GENERATED_COMPONENTS {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return GENERATED_COMPONENTS_ONLY.clean(world, structureData) | NBTUtil.<NBTTagCompound>removeIf(structureData.getTagCompound(),
					structureStartTag -> !structureStartTag.hasKey(KEY_CHILDREN, NBT.TAG_LIST));
		}
	},
	GENERATED_COMPONENTS_ONLY {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.<NBTTagCompound>stream(structureData.getTagCompound())
					.filter(structureStartTag -> NBTUtil.<NBTTagCompound>removeIf(structureStartTag.getTagList(KEY_CHILDREN, NBT.TAG_COMPOUND),
							child -> allChunksGenerated(world, child.getIntArray(KEY_BB)))
							| NBTUtil.<NBTTagList>removeIf(structureStartTag, KEY_CHILDREN, NBTTagList::hasNoTags))
					.count() > 0;
		}
	},
	DISABLED {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return false;
		}
	};

	private static final String KEY_CHILDREN = "Children";
	private static final String KEY_BB = "BB";

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
