package com.charles445.rltweaker.config.init;

import com.charles445.rltweaker.util.ModNames;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraftforge.fml.common.Loader;

public class JsonConfigLessCollisions {

	public static Object2DoubleMap<String> getDefaults() {
		// No combat allies or offensive tools
		// No mountables, except for pigs.
		// No projectiles of any kind
		// Caution with entities that may become the owner of explosions

		Object2DoubleMap<String> map = new Object2DoubleOpenHashMap<>();

		// Default value is 2.0D
		double dfv = 2.0D;

		if (Loader.isModLoaded(ModNames.DEFILEDLANDS)) {
			map.put("lykrast.defiledlands.common.entity.boss.EntityDestroyer", dfv);
			map.put("lykrast.defiledlands.common.entity.boss.EntityMourner", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityHost", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityScuttler", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityShambler", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted", dfv);
			map.put("lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled", dfv);
			map.put("lykrast.defiledlands.common.entity.passive.EntityBookWyrm", dfv);
		}

		if (Loader.isModLoaded(ModNames.FAMILIARFAUNA)) {
			map.put("familiarfauna.entities.EntityButterfly", dfv);
			map.put("familiarfauna.entities.EntityDeer", dfv);
			map.put("familiarfauna.entities.EntityDragonfly", dfv);
			map.put("familiarfauna.entities.EntityPixie", dfv);
			map.put("familiarfauna.entities.EntitySnail", dfv);
			map.put("familiarfauna.entities.EntityTurkey", dfv);
		}

		if (Loader.isModLoaded(ModNames.ICEANDFIRE)) {
			map.put("com.github.alexthe666.iceandfire.entity.EntityCyclops", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityCyclopsEye", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDragonEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityDragonSkull", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityGorgon", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityHippocampus", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityHydra", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityPixie", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySeaSerpent", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySiren", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntitySnowVillager", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityStymphalianBird", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.EntityTroll", dfv);
			map.put("com.github.alexthe666.iceandfire.entity.util.EntityMultipartPart", dfv);
		}

		if (Loader.isModLoaded(ModNames.LYCANITESMOBS)) {
			map.put("com.lycanitesmobs.core.entity.EntityItemCustom", dfv);
			map.put("com.lycanitesmobs.core.entity.creature.EntityArisaur", 6.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityAspid", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityCephignis", 2.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityChupacabra", 8.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityCinder", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityDjinn", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityGrue", 10.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityIka", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityJengu", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityJoust", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityJoustAlpha", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityKobold", 2.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityKrake", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityLacedon", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityMaka", 6.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityMakaAlpha", 6.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityNymph", 2.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityPixen", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityQuillbeast", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityReaper", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityReaver", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntitySilex", 2.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityWisp", 2.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityWraamon", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityYale", 4.0d);
			map.put("com.lycanitesmobs.core.entity.creature.EntityYeti", 4.0d);
		}

		if (Loader.isModLoaded(ModNames.TRUMPETSKELETON)) {
			map.put("com.jamieswhiteshirt.trumpetskeleton.common.entity.EntityTrumpetSkeleton", dfv);
		}

		if (Loader.isModLoaded(ModNames.TUMBLEWEED)) {
			map.put("net.konwboy.tumbleweed.common.EntityTumbleweed", dfv);
		}

		// Minecraft
		map.put("net.minecraft.entity.item.EntityArmorStand", dfv);
		map.put("net.minecraft.entity.item.EntityItem", dfv);
		map.put("net.minecraft.entity.item.EntityItemFrame", dfv);
		map.put("net.minecraft.entity.item.EntityPainting", dfv);
		map.put("net.minecraft.entity.item.EntityXPOrb", dfv);
		map.put("net.minecraft.entity.monster.EntityBlaze", dfv);
		map.put("net.minecraft.entity.monster.EntityCaveSpider", dfv);
		map.put("net.minecraft.entity.monster.EntityCreeper", dfv);
		map.put("net.minecraft.entity.monster.EntityElderGuardian", dfv);
		map.put("net.minecraft.entity.monster.EntityEnderman", dfv);
		map.put("net.minecraft.entity.monster.EntityEndermite", dfv);
		map.put("net.minecraft.entity.monster.EntityEvoker", dfv);
		map.put("net.minecraft.entity.monster.EntityGhast", dfv);
		map.put("net.minecraft.entity.monster.EntityGuardian", dfv);
		map.put("net.minecraft.entity.monster.EntityHusk", dfv);
		map.put("net.minecraft.entity.monster.EntityIllusionIllager", dfv);
		map.put("net.minecraft.entity.monster.EntityMagmaCube", dfv);
		map.put("net.minecraft.entity.monster.EntityPigZombie", dfv);
		map.put("net.minecraft.entity.monster.EntityPolarBear", dfv);
		map.put("net.minecraft.entity.monster.EntityShulker", dfv);
		map.put("net.minecraft.entity.monster.EntitySilverfish", dfv);
		map.put("net.minecraft.entity.monster.EntitySkeleton", dfv);
		map.put("net.minecraft.entity.monster.EntitySlime", dfv);
		map.put("net.minecraft.entity.monster.EntitySpider", dfv);
		map.put("net.minecraft.entity.monster.EntityStray", dfv);
		map.put("net.minecraft.entity.monster.EntityVex", dfv);
		map.put("net.minecraft.entity.monster.EntityVindicator", dfv);
		map.put("net.minecraft.entity.monster.EntityWitch", dfv);
		map.put("net.minecraft.entity.monster.EntityWitherSkeleton", dfv);
		map.put("net.minecraft.entity.monster.EntityZombie", dfv);
		map.put("net.minecraft.entity.monster.EntityZombieVillager", dfv);
		map.put("net.minecraft.entity.passive.EntityBat", dfv);
		map.put("net.minecraft.entity.passive.EntityChicken", dfv);
		map.put("net.minecraft.entity.passive.EntityCow", dfv);
		map.put("net.minecraft.entity.passive.EntityMooshroom", dfv);
		map.put("net.minecraft.entity.passive.EntityOcelot", dfv);
		map.put("net.minecraft.entity.passive.EntityParrot", dfv);
		map.put("net.minecraft.entity.passive.EntityPig", dfv); // Take a pig ride through asmodeus
		map.put("net.minecraft.entity.passive.EntityRabbit", dfv);
		map.put("net.minecraft.entity.passive.EntitySheep", dfv);
		map.put("net.minecraft.entity.passive.EntitySquid", dfv);
		map.put("net.minecraft.entity.passive.EntityVillager", dfv);

		return map;
	}

}
