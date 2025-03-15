package com.charles445.rltweaker.hook.minecraft.structurecleanup;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.NBTUtil;

import meldexun.memoryutil.MemoryAccess;
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
			return NBTUtil.<NBTTagCompound>forEach(structureData.getTagCompound(), structureStart -> NBTUtil.remove(structureStart, KEY_CHILDREN));
		}
	},
	GENERATED {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.<NBTTagCompound>removeIf(structureData.getTagCompound(), (key, structureStart) -> allChunksGenerated(world, structureData, key, -1, structureStart));
		}
	},
	GENERATED_COMPONENTS {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return GENERATED_COMPONENTS_ONLY.clean(world, structureData) | NBTUtil.<NBTTagCompound>removeIf(structureData.getTagCompound(), structureStart -> !structureStart.hasKey(KEY_CHILDREN, NBT.TAG_LIST));
		}
	},
	GENERATED_COMPONENTS_ONLY {
		@Override
		public boolean clean(WorldServer world, MapGenStructureData structureData) {
			return NBTUtil.<NBTTagCompound>forEach(structureData.getTagCompound(), (key, structureStart) -> {
				return NBTUtil.<NBTTagCompound>removeIf(structureStart.getTagList(KEY_CHILDREN, NBT.TAG_COMPOUND), (index, structureComponent) -> allChunksGenerated(world, structureData, key, index, structureComponent))
						| NBTUtil.removeIf(structureStart, KEY_CHILDREN, NBTTagList::isEmpty);
			});
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
	private static final LazyLong2ObjectOpenHashMap<SavedChunkProvider> SAVED_CHUNK_PROVIDER_CACHE = new LazyLong2ObjectOpenHashMap<>();

	public abstract boolean clean(WorldServer world, MapGenStructureData structureData);

	private static boolean allChunksGenerated(WorldServer world, MapGenStructureData structureData, String key, int index, NBTTagCompound tag) {
		if (!tag.hasKey(KEY_BB, NBT.TAG_INT_ARRAY)) {
			RLTweaker.logger.warn("Structure tag is missing bounding box! Deleting structure tag name={} key={} index={} tag={}", structureData.mapName, key, index, tag);
			return true;
		}
		int[] bb = tag.getIntArray(KEY_BB);
		if (bb.length != 6) {
			RLTweaker.logger.warn("Structure tag bounding box is invalid! Deleting structure tag name={} key={} index={} tag={}", structureData.mapName, key, index, tag);
			return true;
		}
		int minX = bb[0];
		int minZ = bb[2];
		int maxX = bb[3];
		int maxZ = bb[5];
		if (minX > maxX || minZ > maxZ) {
			RLTweaker.logger.warn("Structure tag bounding box is invalid! Deleting structure tag name={} key={} index={} tag={}", structureData.mapName, key, index, tag);
			return true;
		}
		if ((long) maxX - (long) minX > ModConfig.server.minecraft.cleanupStructureWorldgenFilesSizeLimit
				|| (long) maxZ - (long) minZ > ModConfig.server.minecraft.cleanupStructureWorldgenFilesSizeLimit) {
			RLTweaker.logger.warn("Structure tag bounding box is too big! Deleting structure tag name={} key={} index={} tag={}", structureData.mapName, key, index, tag);
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
		if (chunkProvider.loadedChunks.containsKey(ChunkPos.asLong(x, z))) {
			return true;
		}
		if (((AnvilChunkLoader) chunkProvider.chunkLoader).chunksToSave.containsKey(new ChunkPos(x, z))) {
			return true;
		}
		return SAVED_CHUNK_PROVIDER_CACHE.computeIfAbsent(((x >> 5) & 0xFFFFFFFFL) | ((z >> 5) & 0xFFFFFFFFL) << 32, k -> {
			File file = new File(((AnvilChunkLoader) chunkProvider.chunkLoader).chunkSaveLocation, "region/r." + (int) k + "." + (int) (k >> 32) + ".mca");
			RegionFile regionFile;
			synchronized (RegionFileCache.class) {
				regionFile = RegionFileCache.REGIONS_BY_FILE.get(file);
			}
			if (regionFile != null) {
				return regionFile::isChunkSaved;
			}
			if (!file.exists()) {
				return (x3, z3) -> false;
			}
			byte[] offsets = new byte[1024 * 4];
			try (InputStream in = new FileInputStream(file)) {
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
			MemoryAccess offsetsAccess = MemoryAccess.of(offsets);
			return (x1, z1) -> offsetsAccess.getInt((x1 + z1 * 32) * 4) != 0;
		}).isChunkSaved(x & 31, z & 31);
	}

	public static void clearCache() {
		SAVED_CHUNK_PROVIDER_CACHE.clear();
	}

}
