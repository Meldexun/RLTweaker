package com.charles445.rltweaker.hook;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.NBTUtil;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import meldexun.memoryutil.UnsafeUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.util.Constants.NBT;
import sun.misc.Unsafe;

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
				if (!isChunkGeneratedAt(world.getChunkProvider(), x, z))
					return false;

			}
		}
		return true;
	}

	private static boolean isChunkGeneratedAt(ChunkProviderServer chunkProvider, int x, int z) {
		if (chunkProvider.id2ChunkMap.containsKey(ChunkPos.asLong(x, z))) {
			return true;
		}
		AnvilChunkLoader chunkLoader = (AnvilChunkLoader) chunkProvider.chunkLoader;
		if (chunkLoader.chunksToSave.containsKey(new ChunkPos(x, z))) {
			return true;
		}
		File file = new File(chunkLoader.chunkSaveLocation, "region/r" + (x >> 5) + "." + (z >> 5));
		RegionFile regionFile;
		synchronized (RegionFileCache.class) {
			regionFile = RegionFileCache.REGIONS_BY_FILE.get(file);
		}
		if (regionFile != null) {
			return regionFile.isChunkSaved(x, z);
		}
		ChunkGenerationInfo dummyRegionFile = ChunkGenerationInfo.fromRegionFile(file);
		if (dummyRegionFile != null) {
			return dummyRegionFile.isChunkSaved(x, z);
		}
		return false;
	}

	public static void clearCache() {
		ChunkGenerationInfo.clearCache();
	}

	private static class ChunkGenerationInfo {

		private static final Map<File, ChunkGenerationInfo> CACHE = new Object2ObjectOpenHashMap<>();
		private final byte[] offsets;

		private ChunkGenerationInfo(byte[] offsets) {
			this.offsets = offsets;
		}

		public static ChunkGenerationInfo fromRegionFile(File file) {
			return CACHE.computeIfAbsent(file, k -> {
				if (!k.exists()) {
					return null;
				}
				byte[] offsets = new byte[1024 * 4];
				try (InputStream in = new FileInputStream(k)) {
					int total = 0;
					while (total < offsets.length) {
						int now = in.read(offsets, total, offsets.length - total);
						if (now < 0) {
							throw new EOFException();
						}
						total += now;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return new ChunkGenerationInfo(offsets);
			});
		}

		public static void clearCache() {
			CACHE.clear();
		}

		public boolean isChunkSaved(int x, int z) {
			int i = (x + z * 32) * 4;
			return UnsafeUtil.UNSAFE.getInt(this.offsets, (long) (Unsafe.ARRAY_BYTE_BASE_OFFSET + i)) != 0;
		}

	}

}
