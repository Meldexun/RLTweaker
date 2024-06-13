package com.charles445.rltweaker.hook;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

public class HookSereneSeasons {

	private static boolean isDimensionWhitelisted = false;
	private static SubSeason subSeason = SubSeason.EARLY_SPRING;

	public static void head_WorldServer_updateBlocks(World world) {
		isDimensionWhitelisted = SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension());
		subSeason = SeasonHelper.getSeasonState(world).getSubSeason();

		if (ModConfig.seasons.changeWeatherFrequency) {
			switch (subSeason.getSeason()) {
			case WINTER:
				if (world.getWorldInfo().isThundering()) {
					world.getWorldInfo().setThundering(false);
				}
				if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 36000) {
					world.getWorldInfo().setRainTime(world.rand.nextInt(24000) + 12000);
				}
				break;
			case SPRING:
				if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 96000) {
					world.getWorldInfo().setRainTime(world.rand.nextInt(84000) + 12000);
				}
				break;
			case SUMMER:
				if (!world.getWorldInfo().isThundering() && world.getWorldInfo().getThunderTime() > 36000) {
					world.getWorldInfo().setThunderTime(world.rand.nextInt(24000) + 12000);
				}
				break;
			default:
				break;
			}
		}
	}

	public static void post_Chunk_onTick(Chunk chunk) {
		if (!ModConfig.seasons.generateSnowAndIce) {
			return;
		}
		if (!isDimensionWhitelisted) {
			return;
		}
		if (subSeason.getSeason() == Season.WINTER) {
			return;
		}

		World world = chunk.getWorld();

		int chance;
		switch (subSeason) {
		case EARLY_SPRING:
			chance = 16;
			break;
		case MID_SPRING:
			chance = 12;
			break;
		case LATE_SPRING:
			chance = 8;
			break;
		default:
			chance = 4;
			break;
		}
		if (world.rand.nextInt(chance) != 0) {
			return;
		}

		MutableBlockPos pos = randomInChunk(world, chunk);
		int precipitationHeight = chunk.getPrecipitationHeight(pos).getY();
		pos.setY(precipitationHeight);

		Biome biome = chunk.getBiome(pos, world.getBiomeProvider());

		if (!BiomeConfig.enablesSeasonalEffects(biome)) {
			return;
		}

		for (; pos.getY() >= 0; pos.setY(pos.getY() - 1)) {
			Block block = chunk.getBlockState(pos).getBlock();

			if (block == Blocks.SNOW_LAYER) {
				if (getTemperature(biome, pos) >= 0.15F) {
					world.setBlockToAir(pos);
					break;
				}
			}

			if (pos.getY() != precipitationHeight && block == Blocks.ICE) {
				if (getTemperature(biome, pos) >= 0.15F) {
					((BlockIce) Blocks.ICE).turnIntoWater(world, pos);
					break;
				}
			}
		}
	}

	private static MutableBlockPos randomInChunk(World world, Chunk chunk) {
		int offset = (world.updateLCG = world.updateLCG * 3 + 1013904223) >> 2;
		return new MutableBlockPos((chunk.x << 4) + (offset & 15), 0, (chunk.z << 4) + (offset >> 8 & 15));
	}

	private static float getTemperature(Biome biome, BlockPos pos) {
		// Seasonal effects are enabled
		// We don't need to clamp temperatures
		// It's not winter
		float baseTemp = biome.getTemperature(pos);

		if (biome.getDefaultTemperature() > 0.8F) {
			return baseTemp;
		}
		if (BiomeConfig.usesTropicalSeasons(biome)) {
			return baseTemp;
		}

		switch (subSeason) {
		case EARLY_AUTUMN:
		case LATE_SPRING:
			return baseTemp - 0.1F;
		case MID_AUTUMN:
		case MID_SPRING:
			return baseTemp - 0.2F;
		case EARLY_SPRING:
		case LATE_AUTUMN:
			return baseTemp - 0.4F;
		default:
			return baseTemp;
		}
	}

}
