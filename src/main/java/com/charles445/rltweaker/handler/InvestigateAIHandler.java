package com.charles445.rltweaker.handler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.entity.ai.InvestigateAI;
import com.charles445.rltweaker.entity.ai.InvestigateAIConfig;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InvestigateAIHandler {

	private static final Path FILE_NAME = Paths.get("investigateAI");
	private static Map<ResourceLocation, InvestigateAIConfig> configurations = Collections.emptyMap();

	public InvestigateAIHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static Exception loadConfig() {
		if (!ModConfig.server.minecraft.investigateAi) {
			return null;
		}

		try {
			configurations = JsonConfig.readJsons(RLTweaker.jsonDirectory.resolve(FILE_NAME), InvestigateAIConfig.class);
			return null;
		} catch (Exception e) {
			RLTweaker.logger.error("Failed to load investigate AI config", e);
			return e;
		}
	}

	public static Map<ResourceLocation, InvestigateAIConfig> getConfigurations() {
		return Collections.unmodifiableMap(configurations);
	}

	@SubscribeEvent
	public void addInvestigateAiHandler(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote) {
			return;
		}
		if (!(event.getEntity() instanceof EntityLiving)) {
			return;
		}
		EntityLiving entity = (EntityLiving) event.getEntity();
		InvestigateAIConfig config = configurations.get(EntityList.getKey(entity));
		if (config != null) {
			entity.tasks.addTask(config.priority, new InvestigateAI(entity, config));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void updateInvestigateAiHandler(LivingDamageEvent event) {
		if (event.getEntity().world.isRemote) {
			return;
		}
		if (!(event.getEntity() instanceof EntityLiving)) {
			return;
		}
		if (ModConfig.server.minecraft.investigateNonPlayerAttacks) {
			if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
				return;
			}
		} else {
			if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
				return;
			}
		}
		EntityLiving entity = (EntityLiving) event.getEntity();
		EntityLivingBase source = (EntityLivingBase) event.getSource().getTrueSource();
		getInvestigateAI(entity).ifPresent(investigateAi -> investigateAi.setTarget(source));
	}

	public static Optional<InvestigateAI> getInvestigateAI(EntityLiving entity) {
		return entity.tasks.taskEntries.stream()
				.map(taskEntry -> taskEntry.action)
				.filter(InvestigateAI.class::isInstance)
				.map(InvestigateAI.class::cast)
				.findFirst();
	}

}
