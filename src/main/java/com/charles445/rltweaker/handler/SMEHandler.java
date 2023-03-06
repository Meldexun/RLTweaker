package com.charles445.rltweaker.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.SMEReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ModNames;
import com.charles445.rltweaker.util.ReflectUtil;
import com.charles445.rltweaker.util.VersionDelimiter;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/** Handler for SoManyEnchantments! <br>
 * Wraps around or replaces some enchantment handlers in order to fix bugs.
*/
public class SMEHandler
{
	public static long protectCounter = 0L;
	
	//TODO map used for logging purposes;
	
	private SMEReflect reflector;
	
	
	public SMEHandler()
	{
		try
		{
			//Get the version to decide what to do next
			Class c_somanyenchantments = Class.forName("com.Shultrea.Rin.Main_Sector.somanyenchantments");
			Field f_version = ReflectUtil.findField(c_somanyenchantments, "VERSION");
			VersionDelimiter vd = VersionDelimiter.parse((String)f_version.get(null));
			
			if(vd.isSameOrNewerVersion(0, 5))
			{
				RLTweaker.logger.info("Skipping SMEHandler setup, SME version is 0.5.0 or higher");
				return;
			}
			
			
			
			this.reflector = new SMEReflect();
			
			//Advanced Mending
			tryRegister(AdvancedMending.class, "advancedmending", reflector.c_Enchantmentadvancedmending, reflector.m_Enchantmentadvancedmending_handler, ModConfig.server.somanyenchantments.manageAdvancedMending);
			
			//Arc Slash
			tryRegister(ArcSlash.class, "swiper", reflector.c_EnchantmentSwiper, reflector.m_EnchantmentSwiper_handler, ModConfig.server.somanyenchantments.manageArcSlash);
			
			//Curse of Possession
			tryRegister(CurseOfPossession.class, "CurseofPossession", reflector.c_EnchantmentCurseofPossession, reflector.m_EnchantmentCurseofPossession_onExist, ModConfig.server.somanyenchantments.manageCurseOfPossession);
			
			//Empowered Defence
			tryRegister(EmpoweredDefence.class, "empowereddefence", reflector.c_EnchantmentEmpoweredDefence, reflector.m_EnchantmentEmpoweredDefence_handler, ModConfig.server.somanyenchantments.manageEmpoweredDefence);
			
			//Evasion
			tryRegister(Evasion.class, "evasion", reflector.c_EnchantmentEvasion, reflector.m_EnchantmentEvasion_handler, ModConfig.server.somanyenchantments.manageEvasion);
			
			//Freezing
			tryRegister(Freezing.class, "freezing", reflector.c_EnchantmentFreezing, reflector.m_EnchantmentFreezing_handler, ModConfig.server.somanyenchantments.manageFreezing);
			
			//Parry
			tryRegister(Parry.class, "parry", reflector.c_EnchantmentParry, reflector.m_EnchantmentParry_handler, ModConfig.server.somanyenchantments.manageParry);
			
			//Unreasonable
			tryRegister(Unreasonable.class, "frenzy", reflector.c_EnchantmentFrenzy, reflector.m_EnchantmentFrenzy_handler, ModConfig.server.somanyenchantments.manageUnreasonable);
			
			//Upgraded Potentials
			tryRegister(UpgradedPotentials.class, "upgrade", reflector.c_EnchantmentUpgradedPotentials, reflector.m_EnchantmentUpgradedPotentials_handler, ModConfig.server.somanyenchantments.manageUpgradedPotentials);
			
		}
		catch (Exception e)
		{
			RLTweaker.logger.error("Failed to setup SMEHandler!", e);
			ErrorUtil.logSilent("SME Critical Setup Failure");
			
			//Crash on Critical
			if(e instanceof CriticalException)
				throw new RuntimeException(e);
		}
	}
	
	//
	//
	//
	
	
	
	//Wrapper extension setup is like this:
	//
	//Constructor that is nothing but a super
	//The event handler with the insides surrounded by a try catch
	//Enchantment check
	//(optional pre-handling)
	//Invoking the initial handler
	//(optional post-handling
	
	public class AdvancedMending extends Wrapper
	{
		public AdvancedMending(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent
		public void advancedMendingEnchant(PlayerPickupXpEvent event)
		{
			//Not using the original
			
			//Taken from vanilla, but with constant values
			
			//Double the XP for the player
			//This appears to be an oversight in SME, but has been left in for consistency.
			//It will be run first now to make sure that the presence of advanced mending does not reduce XP gain
			
			EntityPlayer player = event.getEntityPlayer();
			EntityXPOrb orb = event.getOrb();
			
			//DebugUtil.messageAll("XP: "+orb.xpValue);
			
			if(ModConfig.server.somanyenchantments.advancedMendingDoubleXP && orb.xpValue > 0)
			{
				player.addExperience(orb.xpValue);
			}
			
			//Get a random item on the player that has advanced mending
			ItemStack itemstack = EnchantmentHelper.getEnchantedItem(enchantment, player);
			
			//Attempt to repair it using some of the XP from the orb itself
			if(!itemstack.isEmpty() && itemstack.isItemDamaged())
			{
				int value = Math.min(orb.xpValue * 3, itemstack.getItemDamage());
				//DebugUtil.messageAll("Value: "+value);
				//DebugUtil.messageAll("Task: "+itemstack.getItemDamage()+" -> "+(itemstack.getItemDamage() - value));
				itemstack.setItemDamage(itemstack.getItemDamage() - value);
				
				orb.xpValue -= value/2;
				//There is a chance that the orb.xpValue has become negative (for example, an orb.xpValue of 2 can become -1)
				if(orb.xpValue < 0)
					orb.xpValue = 0;
				
				//DebugUtil.messageAll("New XP: "+orb.xpValue);
			}
		}
	}
	
	public class ArcSlash extends Wrapper
	{
		//EnchantmentSwiper
		public ArcSlash(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
		public void arcSlashEnchant(LivingDamageEvent event)
		{
			try
			{	
				//Check attacker and enchantment
				Entity attacker = event.getSource().getTrueSource();
				if(!(attacker instanceof EntityLivingBase))
					return;
				ItemStack weapon = ((EntityLivingBase)attacker).getHeldItemMainhand();
				if(weapon.isEmpty())
					return;
				if(EnchantmentHelper.getEnchantmentLevel(enchantment, weapon)<=0)
					return;
				
				//Run Handler
				invokeOriginal(event);
				
				//Protect entities
				protectMotion(event.getEntityLiving(), attacker);
				
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in ArcSlash Invoke", e);
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
		}
	}
	
	public class CurseOfPossession extends Wrapper
	{
		private final Method onExist;
		private final Method toss;
		private final Map<Integer, Integer> dimensionMap;
		
		public CurseOfPossession(Object handler, String ench, Method original)
		{
			super(handler, ench, original);
			onExist = reflector.m_EnchantmentCurseofPossession_onExist;
			toss = reflector.m_EnchantmentCurseofPossession_toss;
			dimensionMap = new HashMap<Integer,Integer>();
		}
		
		@SubscribeEvent
		public void onExist(TickEvent.WorldTickEvent event)
		{
			//Check tick phase
			if(event.phase == TickEvent.Phase.START)
				return;
			
			//Use separate timers for separate dimensions
			int dimension = event.world.provider.getDimension();
			Integer dimensionCounterObj = dimensionMap.get(dimension);
			int dimCount = dimensionCounterObj==null? 0 : dimensionCounterObj.intValue();
			dimCount++;
			
			//Timer check
			if(dimCount >= ModConfig.server.somanyenchantments.curseOfPossessionDelay)
			{
				//Timer is up, reset timer
				dimensionMap.put(dimension, 0);
				
				try
				{
					onExist.invoke(handler, event);
				}
				catch (Exception e)
				{
					RLTweaker.logger.error("Error in CurseOfPossession onExist Invoke", e);
					ErrorUtil.logEnchantmentHandlerError(enchantment);
				}
			}
			else
			{
				//Increment timer
				dimensionMap.put(dimension, dimCount);
			}
		}
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void toss(ItemTossEvent event)
		{
			try
			{
				toss.invoke(handler, event);
			}
			catch (Exception e)
			{
				RLTweaker.logger.error("Error in CurseOfPossession toss Invoke", e);
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
			
		}
	}
	
	public class EmpoweredDefence extends Wrapper
	{
		public EmpoweredDefence(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.LOW)
		public void empoweredDefenceEnchant(LivingAttackEvent event)
		{
			try
			{
				//Check enchantment
				EntityLivingBase victim = event.getEntityLiving();
				ItemStack shield = victim.getHeldItemOffhand();
				if(shield.isEmpty())
					shield = victim.getHeldItemMainhand();
				if(shield.isEmpty())
					return;
				if(EnchantmentHelper.getEnchantmentLevel(enchantment, shield)<=0)
					return;
				
				//Check attacker
				Entity attacker = event.getSource().getTrueSource();
				if(attacker==null)
					return;
				
				//Run handler
				invokeOriginal(event);
				
				//Protect entities
				protectMotion(victim,attacker);
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in EmpoweredDefence Invoke", e);
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
		}
	}
	
	public class Evasion extends Wrapper
	{
		public Evasion(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.LOW)
		public void evasionEnchant(LivingAttackEvent event)
		{
			try
			{
				//Check enchantment
				EntityLivingBase victim = event.getEntityLiving();
				if(EnchantmentHelper.getMaxEnchantmentLevel(enchantment,victim) <= 0)
					return;
				
				//Check attacker
				Entity attacker = event.getSource().getTrueSource();
				if(attacker==null)
					return;

				//Run handler
				invokeOriginal(event);
				
				//Protect entities
				protectMotion(victim,attacker);
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in Evasion Invoke", e);
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
		}
	}
	
	public class Freezing extends Wrapper
	{

		public Freezing(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void freezingEnchant(LivingDamageEvent event)
		{
			try
			{
				//Check enchantment and attacker
				if(!(event.getSource().getTrueSource() instanceof EntityLivingBase))
					return;
				EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
				ItemStack weapon = attacker.getHeldItemMainhand();
				if(weapon.isEmpty())
					return;
				if(EnchantmentHelper.getEnchantmentLevel(enchantment, weapon)<=0)
					return;
				
				//Run handler
				invokeOriginal(event);
				
				//Protect entities
				EntityLivingBase victim = event.getEntityLiving();
				protectMotion(victim, attacker);
				
				/* MOVED to a miscellaneous handler
				//Prevent Roguelike Dungeons crash
				//Mining fatigue must cap out at 5
				boolean doFatigueReplacement = false;
				int fatigueDuration = 1;
				Collection<PotionEffect> effects = victim.getActivePotionEffects();
				for(PotionEffect effect : effects)
				{
					if(Potion.getIdFromPotion(effect.getPotion()) == 4) //Roguelike specifically checks for 4
					{
						//5 or greater crashes
						if(effect.getAmplifier()>4)
						{
							doFatigueReplacement = true;
							fatigueDuration = effect.getDuration();
							ErrorUtil.logSilent("Freezing Fatigue");
						}
						
						break;
					}
				}
				if(doFatigueReplacement)
				{
					Potion potion = Potion.getPotionById(4);
					victim.removePotionEffect(potion);
					victim.addPotionEffect(new PotionEffect(potion, fatigueDuration, 4));
				}
				*/
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in Freezing Invoke", e);
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
		}
	}
	
	public class Parry extends Wrapper
	{
		public Parry(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
		public void parryEnchant(LivingAttackEvent event)
		{
			try
			{
				//Check enchantment
				EntityLivingBase victim = event.getEntityLiving();
				ItemStack weapon = victim.getHeldItemMainhand();
				if(weapon.isEmpty())
					return;
				if(EnchantmentHelper.getEnchantmentLevel(enchantment, weapon)<=0)
					return;
				
				//Check attacker
				Entity attacker = event.getSource().getTrueSource();
				if(attacker==null)
					return;
				
				//Run Handler
				invokeOriginal(event);
				
				//Protect entities
				protectMotion(victim,attacker);
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in Parry Invoke");
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
		}
	}
	
	public class Unreasonable extends Wrapper
	{
		public Unreasonable(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void unreasonableEnchant(LivingAttackEvent event)
		{
			//Note: unreasonable enchantment's handler sets a revenge target, cancels the event, and only works on creatures with full health
			try
			{
				//Check attacker and enchantment
				if(!(event.getSource().getTrueSource() instanceof EntityLivingBase))
					return;
				EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
				ItemStack weapon = attacker.getHeldItemMainhand();
				if(weapon.isEmpty())
					return;
				if(EnchantmentHelper.getEnchantmentLevel(enchantment, weapon)<=0)
					return;
				
				//Run Handler
				invokeOriginal(event);
				
				//Un-Cancel
				if(event.isCanceled())
				{
					event.setCanceled(false);
					//Criterion has been met, however the victim's health and revenge target must be checked first before scheduling the revenge target change
					EntityLivingBase victim = event.getEntityLiving();
					
					if (victim.getMaxHealth() > victim.getHealth())
						return;
					
					EntityLivingBase revenge = victim.getRevengeTarget();
					if(revenge==null)
						return;
					
					//All the pieces are in place, schedule the revenge target change
					
					//RLTweaker.logger.debug("Current Revenge: "+revenge.getName());
					
					//This seems a bit jank, is there a better way to handle this?
					World world = victim.getEntityWorld();
					if(world instanceof WorldServer)
					{
						((WorldServer)world).addScheduledTask(() -> 
						{
							try
							{
								if(victim!=null && attacker!=null && revenge != null)
								{
									if(!victim.isDead)
									{
										//RLTweaker.logger.debug("Executed Revenge: "+revenge.getName());
										if(victim instanceof EntityLiving)
										{
											//Prevent monsters from targeting themselves
											if(!victim.getUniqueID().equals(revenge.getUniqueID()))
											{
												
												EntityLiving victimLiving = (EntityLiving)victim;
												victimLiving.setRevengeTarget(revenge);
												victimLiving.setAttackTarget(revenge);
											}
										}
									}
								}
							}
							catch(Exception e)
							{
								RLTweaker.logger.error("Error in scheduled Unreasonable task",e);
								ErrorUtil.logSilent("Unreasonable Scheduled");
							}
						});
					}
				}
			}
			catch(Exception e)
			{
				RLTweaker.logger.error("Error in Unreasonable Invoke");
				ErrorUtil.logEnchantmentHandlerError(enchantment);
			}
			
		}
	}
	
	public class UpgradedPotentials extends Wrapper
	{
		public UpgradedPotentials(Object handler, String ench, Method original) { super(handler, ench, original); }
		
		@SubscribeEvent
		public void upgradedPotentialsEnchant(AnvilUpdateEvent event)
		{
			//Not using the original
			
			ItemStack left = event.getLeft();
			ItemStack right = event.getRight();
			if(left.isEmpty() || right.isEmpty())
			{
				//This isn't actually necessary? Doing it anyway
				return;
			}
			if(right.getItem()==Items.ENCHANTED_BOOK)
			{
				//Right is an enchanted book
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(right);
				if(enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= 1)
				{
					//Right has Upgraded Potentials book
					
					if(left.isStackable())
					{
						//Left is stackable, right slot has upgraded potentials, set output to empty and return
						event.setOutput(ItemStack.EMPTY);
						return;
					}
					
					//Left is currently not stackable
					
					if(EnchantmentHelper.getEnchantments(left).size()==0)
					{
						//No enchantments on the left, exiting
						event.setOutput(ItemStack.EMPTY);
						return;
					}
					
					if(EnchantmentHelper.getEnchantmentLevel(enchantment, left) >= 1)
					{
						//Left already has upgraded potentials, set output to empty and return
						event.setOutput(ItemStack.EMPTY);
						return;
					}
					
					//Left is currently not stackable and does not have upgraded potentials
					
					//Repair cost tweaking
					int cost = left.getRepairCost();
					cost = Math.max(0, (cost / 4) - 20);
					
					//Build a copy of the left with upgraded potentials and the modified repair cost
					ItemStack output = left.copy();
					output.setRepairCost(cost);
					output.addEnchantment(enchantment, 1);
					
					//Set the result in the anvil
					event.setOutput(output);
					event.setCost(10);
				}
			}
		}
	}
	
	//
	//
	//
	
	
	
	private void tryRegister(Class toRegisterClazz, String enchName, Class reflectorClazz, Method reflectorMethod, boolean config) throws Exception
	{
		if(config)
		{
			Object handler = CompatUtil.findAndRemoveHandlerFromEventBus(reflectorClazz);
			if(handler!=null)
			{
				RLTweaker.logger.info("Registering "+toRegisterClazz.getName()+" to the event bus");
				MinecraftForge.EVENT_BUS.register(toRegisterClazz.getConstructor(this.getClass(), Object.class, String.class, Method.class).newInstance(this, handler, enchName, reflectorMethod));
			}
		}
	}
	
	private abstract class Wrapper
	{
		protected final Object handler;
		protected final Enchantment enchantment;
		protected final Method original;
		
		public Wrapper(Object handler, String ench, Method original)
		{
			this.handler = handler;
			this.enchantment = getEnchantment(ench);
			this.original = original;
		}
		
		protected void invokeOriginal(Event event) throws Exception
		{
			original.invoke(handler, event);
		}
		
		protected void protectMotion(Entity a, Entity b)
		{
			if(!Double.isFinite(a.motionX)||!Double.isFinite(a.motionY)||!Double.isFinite(a.motionZ))
			{
				//DebugUtil.messageAll("A motion was invalid : "+enchantment.getName());
				a.motionX=0;
				a.motionY=0;
				a.motionZ=0;
				a.velocityChanged=true;
				protectCounter++;
				ErrorUtil.logEnchantmentError(enchantment);
				/*
				if(a instanceof EntityLivingBase)
				{
					Iterable<ItemStack> iterable = ench.getEntityEquipment((EntityLivingBase) a);
					 if (iterable != null)
					{
						 for (ItemStack itemstack : iterable)
						{
							int j = EnchantmentHelper.getEnchantmentLevel(ench, itemstack);
							DebugUtil.messageAll(itemstack.getDisplayName()+" "+j);
						}
					}
				}
			   */
			}
			
			if(!Double.isFinite(b.motionX)||!Double.isFinite(b.motionY)||!Double.isFinite(b.motionZ))
			{
				//DebugUtil.messageAll("B motion was invalid : "+enchantment.getName());
				b.motionX=0;
				b.motionY=0;
				b.motionZ=0;
				b.velocityChanged=true;
				protectCounter++;
				ErrorUtil.logEnchantmentError(enchantment);
			}
			
		}
	}
	
	private Enchantment getEnchantment(String name)
	{
		ResourceLocation loc = new ResourceLocation(ModNames.SOMANYENCHANTMENTS, name);
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(loc);
		if(enchantment==null)
		{
			RLTweaker.logger.error("Failed to find enchantment "+loc);
		}
		return enchantment;
	}
	
}
