package com.charles445.rltweaker.config;

import com.charles445.rltweaker.RLTweaker;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = RLTweaker.MODID)
public class ModConfig
{
	//Keeping these lower case and uniform to avoid unexpected parsing issues
	//Fun fact, categories get run through toLowerCase(Locale.ENGLISH)
	//But properties do not. Okay...
	
	@Config.Comment("Client options")
	@Config.Name("Client")
	public static ClientConfig client = new ClientConfig();
	
	@Config.Comment("Patch options for the coremod")
	@Config.Name("patches")
	public static PatchConfig patches = new PatchConfig();
	
	@Config.Comment("Server options")
	@Config.Name("Server")
	public static ServerConfig server = new ServerConfig();
	
	public static class ServerConfig
	{
		@Config.Comment("Minecraft tweaks, or anything that isn't mod specific")
		@Config.Name("Minecraft")
		public ConfigMinecraft minecraft = new ConfigMinecraft();
		
		@Config.Comment("Aquaculture tweaks")
		@Config.Name("Aquaculture")
		public ConfigAquaculture aquaculture = new ConfigAquaculture();
		
		@Config.Comment("Battle Towers tweaks")
		@Config.Name("Battle Towers")
		public ConfigBattleTowers battletowers = new ConfigBattleTowers();
		
		@Config.Comment("Baubles tweaks")
		@Config.Name("Baubles")
		public ConfigBaubles baubles = new ConfigBaubles();
		
		@Config.Comment("Better Survival tweaks")
		@Config.Name("Better Survival")
		public ConfigBetterSurvival bettersurvival = new ConfigBetterSurvival();
		
		@Config.Comment("Carry On tweaks")
		@Config.Name("Carry On")
		public ConfigCarryOn carryon = new ConfigCarryOn();
		
		@Config.Comment("Charm tweaks")
		@Config.Name("Charm")
		public ConfigCharm charm = new ConfigCharm();
		
		@Config.Comment("Classy Hats tweaks")
		@Config.Name("Classy Hats")
		public ConfigClassyHats classyhats = new ConfigClassyHats();
		
		@Config.Comment("Dynamic Surroundings tweaks")
		@Config.Name("Dynamic Surroundings")
		public ConfigDynamicSurroundings dynamicsurroundings = new ConfigDynamicSurroundings();
		
		@Config.Comment("Grappling Hook Mod tweaks")
		@Config.Name("Grappling Hook Mod")
		public ConfigGrapplemod grapplemod = new ConfigGrapplemod();
		
		@Config.Comment("Ice and Fire tweaks")
		@Config.Name("Ice and Fire")
		public ConfigIceAndFire iceandfire = new ConfigIceAndFire();
		
		@Config.Comment("Infernal Mobs tweaks")
		@Config.Name("Infernal Mobs")
		public ConfigInfernalMobs infernalmobs = new ConfigInfernalMobs();
		
		@Config.Comment("Level Up Reloaded tweaks")
		@Config.Name("Level Up Reloaded")
		public ConfigLevelUpTwo leveluptwo = new ConfigLevelUpTwo();
		
		@Config.Comment("Lost Cities tweaks")
		@Config.Name("Lost Cities")
		public ConfigLostCities lostcities = new ConfigLostCities();
		
		@Config.Comment("Lycanites Mobs tweaks")
		@Config.Name("Lycanites Mobs")
		public ConfigLycanites lycanitesmobs = new ConfigLycanites();
		
		@Config.Comment("Multi Mine tweaks")
		@Config.Name("Multi Mine")
		public ConfigMultiMine multimine = new ConfigMultiMine();
		
		@Config.Comment("Quark tweaks")
		@Config.Name("Quark")
		public ConfigQuark quark = new ConfigQuark();
		
		@Config.Comment("Potion Core tweaks")
		@Config.Name("Potion Core")
		public ConfigPotionCore potioncore = new ConfigPotionCore();
		
		@Config.Comment("Recurrent Complex tweaks")
		@Config.Name("Recurrent Complex")
		public ConfigRecurrent recurrentcomplex = new ConfigRecurrent();
		
		@Config.Comment("Reskillable tweaks")
		@Config.Name("Reskillable")
		public ConfigReskillable reskillable = new ConfigReskillable();
		
		@Config.Comment("Roguelike Dungeons tweaks")
		@Config.Name("Roguelike Dungeons")
		public ConfigRoguelike roguelike = new ConfigRoguelike();
		
		@Config.Comment("Ruins tweaks")
		@Config.Name("Ruins")
		public ConfigRuins ruins = new ConfigRuins();
		
		@Config.Comment("Rustic tweaks")
		@Config.Name("Rustic")
		public ConfigRustic rustic = new ConfigRustic();
		
		@Config.Comment("Serene Seasons tweaks")
		@Config.Name("Serene Seasons")
		public ConfigSereneSeasons sereneseasons = new ConfigSereneSeasons();
		
		@Config.Comment("Scape and Run Parasites tweaks")
		@Config.Name("Scape and Run Parasites")
		public ConfigSRParasites srparasites = new ConfigSRParasites();
		
		@Config.Comment("So Many Enchantments tweaks")
		@Config.Name("So Many Enchantments")
		public ConfigSME somanyenchantments = new ConfigSME();

		@Config.Comment("Spawner Control tweaks")
		@Config.Name("Spawner Control")
		public ConfigSpawnerControl spawnercontrol = new ConfigSpawnerControl();
		
		@Config.Comment("Tough As Nails tweaks")
		@Config.Name("Tough As Nails")
		public ConfigTAN toughasnails = new ConfigTAN();
		
		/*
		@Config.Comment("Varied Commodities tweaks")
		@Config.Name("Varied Commodities")
		public ConfigVariedCommodities variedcommodities = new ConfigVariedCommodities();
		*/
		
		@Config.Comment("Waystones tweaks")
		@Config.Name("Waystones")
		public ConfigWaystones waystones = new ConfigWaystones();
	}
	
	public static class ClientConfig
	{
		@Config.Comment("Classy Hats tweaks")
		@Config.Name("Classy Hats")
		public ConfigClassyHatsClient classyhats = new ConfigClassyHatsClient();
		
		@Config.Comment("Fancy Block Particles tweaks")
		@Config.Name("Fancy Block Particles")
		public ConfigFBPClient fbp = new ConfigFBPClient();
		
		@Config.Comment("Googly Eyes tweaks")
		@Config.Name("Googly Eyes")
		public ConfigGooglyEyesClient googlyeyes = new ConfigGooglyEyesClient();
		
		@Config.Comment("Mantle tweaks")
		@Config.Name("Mantle")
		public ConfigMantleClient mantle = new ConfigMantleClient();
		
		@Config.Comment("Minecraft tweaks")
		@Config.Name("Minecraft")
		public ConfigMinecraftClient minecraft = new ConfigMinecraftClient();
		
		@Config.Comment("Potion Core tweaks")
		@Config.Name("Potion Core")
		public ConfigPotionCoreClient potioncore = new ConfigPotionCoreClient();
		
		@Config.Comment("Reskillable tweaks")
		@Config.Name("Reskillable")
		public ConfigReskillableClient reskillable = new ConfigReskillableClient();
	}
	
	public static void onConfigChanged()
	{
		ModConfig.server.minecraft.investigateAiImpl.load(ModConfig.server.minecraft.investigateAiWhitelist);
	}
	
	@Mod.EventBusSubscriber(modid = RLTweaker.MODID)
	private static class EventHandler
	{
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if(event.getModID().equals(RLTweaker.MODID))
			{
				ConfigManager.sync(RLTweaker.MODID, Config.Type.INSTANCE);
				ModConfig.onConfigChanged();
			}
		}
	}
}
