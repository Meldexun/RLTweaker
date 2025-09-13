package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;

public class BetterSurvivalReflect
{
	
	public final Class c_NunchakuComboProvider;
	public final Field f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP;
	public final Capability o_NUNCHAKUCOMBO_CAP;
	
	public final Class c_INunchakuCombo;
	public final Method m_INunchakuCombo_getComboPower;
	public final Method m_INunchakuCombo_countDown;
	public final Method m_INunchakuCombo_setComboTime;
	
	public final Class c_ItemNunchaku;
	
	public final Class c_ModEnchantments;
	public final Field f_ModEnchantments_agility;
	public final Field f_ModEnchantments_vitality;
	public final Enchantment o_ModEnchantments_agility;
	public final Enchantment o_ModEnchantments_vitality;
	
	public final Class c_ConfigHandler;
	public final Field f_ConfigHandler_agilitylevel;
	public final Field f_ConfigHandler_vitalitylevel;
	public final int o_ConfigHandler_agilitylevel;
	public final int o_ConfigHandler_vitalitylevel;
	
	
	public BetterSurvivalReflect() throws Exception
	{
		c_NunchakuComboProvider = Class.forName("com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuComboProvider");
		f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP = ReflectUtil.findField(c_NunchakuComboProvider, "NUNCHAKUCOMBO_CAP");
		o_NUNCHAKUCOMBO_CAP = (Capability) f_NunchakuComboProvider_NUNCHAKUCOMBO_CAP.get(null);
		
		c_INunchakuCombo = Class.forName("com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.INunchakuCombo");
		m_INunchakuCombo_getComboPower = ReflectUtil.findMethod(c_INunchakuCombo, "getComboPower");
		m_INunchakuCombo_countDown = ReflectUtil.findMethod(c_INunchakuCombo, "countDown");
		m_INunchakuCombo_setComboTime = ReflectUtil.findMethod(c_INunchakuCombo, "setComboTime");
	
		c_ItemNunchaku = Class.forName("com.mujmajnkraft.bettersurvival.items.ItemNunchaku");
		
		c_ModEnchantments = Class.forName("com.mujmajnkraft.bettersurvival.init.ModEnchantments");
		f_ModEnchantments_agility = ReflectUtil.findField(c_ModEnchantments, "agility");
		f_ModEnchantments_vitality = ReflectUtil.findField(c_ModEnchantments, "vitality");
		o_ModEnchantments_agility = (Enchantment) f_ModEnchantments_agility.get(null);
		o_ModEnchantments_vitality = (Enchantment) f_ModEnchantments_vitality.get(null);
		
		c_ConfigHandler = Class.forName("com.mujmajnkraft.bettersurvival.config.ConfigHandler");
		f_ConfigHandler_agilitylevel = ReflectUtil.findField(c_ConfigHandler, "agilitylevel");
		f_ConfigHandler_vitalitylevel = ReflectUtil.findField(c_ConfigHandler, "vitalitylevel");
		o_ConfigHandler_agilitylevel = f_ConfigHandler_agilitylevel.getInt(null);
		o_ConfigHandler_vitalitylevel = f_ConfigHandler_vitalitylevel.getInt(null);
	}
	
	public boolean hasINunchakuCombo(EntityPlayer player)
	{
		return player.hasCapability(o_NUNCHAKUCOMBO_CAP, null);
	}
	
	public Object getINunchakuCombo(EntityPlayer player)
	{
		return player.getCapability(o_NUNCHAKUCOMBO_CAP, null);
	}
	
	public float getComboPower(Object inunchakucombo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (float) m_INunchakuCombo_getComboPower.invoke(inunchakucombo);
	}
	
	public void countDown(Object inunchakucombo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_INunchakuCombo_countDown.invoke(inunchakucombo);
	}
	
	public void setComboTime(Object inunchakucombo, int time) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		m_INunchakuCombo_setComboTime.invoke(inunchakucombo, time);
	}
}
