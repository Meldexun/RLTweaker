package com.charles445.rltweaker.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.config.json.JsonLoader;
import com.google.gson.reflect.TypeToken;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public final class CollisionUtil {

	private static final Path FILE_NAME = Paths.get("lessCollisions.json");
	private static final Type TYPE = new TypeToken<Object2DoubleOpenHashMap<String>>() {}.getType();
	private static Object2DoubleMap<String> lessCollisions = Object2DoubleMaps.emptyMap();

	public static void loadConfig() {
		if (!ModConfig.patches.lessCollisions) {
			return;
		}

		try {
			lessCollisions = JsonLoader.readJson(RLTweaker.jsonDirectory.resolve(FILE_NAME), TYPE, getDefaults());
		} catch (IOException e) {
			RLTweaker.logger.error("Failed to load less collisions config", e);
		}
	}

	private static Object2DoubleOpenHashMap<String> getDefaults() {
		// No combat allies or offensive tools
		// No mountables, except for pigs.
		// No projectiles of any kind
		// Caution with entities that may become the owner of explosions

		Object2DoubleOpenHashMap<String> defaults = new Object2DoubleOpenHashMap<>();

		// Default value is 2.0D
		double defaultValue = 2.0D;

		if (Loader.isModLoaded(ModNames.DEFILEDLANDS)) {
			defaults.put("lykrast.defiledlands.common.entity.boss.EntityDestroyer", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.boss.EntityMourner", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.monster.EntityHost", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.monster.EntityScuttler", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.monster.EntityShambler", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled", defaultValue);
			defaults.put("lykrast.defiledlands.common.entity.passive.EntityBookWyrm", defaultValue);
		}

		if (Loader.isModLoaded(ModNames.FAMILIARFAUNA)) {
			defaults.put("familiarfauna.entities.EntityButterfly", defaultValue);
			defaults.put("familiarfauna.entities.EntityDeer", defaultValue);
			defaults.put("familiarfauna.entities.EntityDragonfly", defaultValue);
			defaults.put("familiarfauna.entities.EntityPixie", defaultValue);
			defaults.put("familiarfauna.entities.EntitySnail", defaultValue);
			defaults.put("familiarfauna.entities.EntityTurkey", defaultValue);
		}

		if (Loader.isModLoaded(ModNames.ICEANDFIRE)) {
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityCyclops", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityCyclopsEye", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityDragonEgg", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityDragonSkull", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityGorgon", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityHippocampus", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityHydra", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityPixie", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpent", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntitySiren", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntitySnowVillager", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityStymphalianBird", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.EntityTroll", defaultValue);
			defaults.put("com.github.alexthe666.iceandfire.entity.util.EntityMultipartPart", defaultValue);
		}

		if (Loader.isModLoaded(ModNames.LYCANITESMOBS)) {
			defaults.put("com.lycanitesmobs.core.entity.EntityItemCustom", defaultValue);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityArisaur", 6.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityAspid", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityCephignis", 2.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityChupacabra", 8.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityCinder", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityDjinn", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityGrue", 10.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityIka", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityJengu", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityJoust", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityJoustAlpha", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityKobold", 2.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityKrake", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityLacedon", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityMaka", 6.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityMakaAlpha", 6.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityNymph", 2.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityPixen", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityQuillbeast", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityReaper", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityReaver", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntitySilex", 2.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityWisp", 2.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityWraamon", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityYale", 4.0D);
			defaults.put("com.lycanitesmobs.core.entity.creature.EntityYeti", 4.0D);
		}

		if (Loader.isModLoaded(ModNames.TRUMPETSKELETON)) {
			defaults.put("com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton", defaultValue);
		}

		if (Loader.isModLoaded(ModNames.TUMBLEWEED)) {
			defaults.put("net.konwboy.tumbleweed.common.EntityTumbleweed", defaultValue);
		}

		// Minecraft
		defaults.put("net.minecraft.entity.item.EntityArmorStand", defaultValue);
		defaults.put("net.minecraft.entity.item.EntityItem", defaultValue);
		defaults.put("net.minecraft.entity.item.EntityItemFrame", defaultValue);
		defaults.put("net.minecraft.entity.item.EntityPainting", defaultValue);
		defaults.put("net.minecraft.entity.item.EntityXPOrb", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityBlaze", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityCaveSpider", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityCreeper", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityElderGuardian", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityEnderman", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityEndermite", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityEvoker", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityGhast", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityGuardian", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityHusk", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityIllusionIllager", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityMagmaCube", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityPigZombie", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityPolarBear", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityShulker", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntitySilverfish", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntitySkeleton", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntitySlime", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntitySpider", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityStray", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityVex", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityVindicator", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityWitch", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityWitherSkeleton", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityZombie", defaultValue);
		defaults.put("net.minecraft.entity.monster.EntityZombieVillager", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityBat", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityChicken", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityCow", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityMooshroom", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityOcelot", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityParrot", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityPig", defaultValue); // Take a pig ride through asmodeus
		defaults.put("net.minecraft.entity.passive.EntityRabbit", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntitySheep", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntitySquid", defaultValue);
		defaults.put("net.minecraft.entity.passive.EntityVillager", defaultValue);

		return defaults;
	}

	public static double getRadiusForEntity(@Nonnull Entity entity) {
		double v = lessCollisions.getDouble(entity.getClass().getName());
		if (v < 0.0D || v > World.MAX_ENTITY_RADIUS) {
			v = World.MAX_ENTITY_RADIUS;
		}
		return v;
	}

}
