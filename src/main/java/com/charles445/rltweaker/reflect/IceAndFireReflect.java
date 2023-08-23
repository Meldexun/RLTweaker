package com.charles445.rltweaker.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;

import net.minecraft.entity.Entity;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class IceAndFireReflect
{
	//Ice And Fire
	public final Class c_EntityEffectProperties;
	public final Method m_EntityEffectProperties_isStone;
	
	public final Class c_ItemStoneStatue;
	
	public final Class c_EntityDragonBase;
	
	public final Class c_EntityMyrmexQueen;
	
	public final Class c_EntityMyrmexBase;
	public final Field f_EntityMyrmexBase_buyingList;
	public final Method m_EntityMyrmexBase_populateBuyingList;
	public final Method m_EntityMyrmexBase_isJungle;
	
	public final Class c_EntityGorgon;

	public final Class c_EntityStoneStatue;
	
	@Nullable
	public Class c_ItemDragonHornStatic;
	
	//LLibrary
	public final Class c_EntityPropertiesHandler;
	public final Field f_EntityPropertiesHandler_INSTANCE;
	public final Method m_EntityPropertiesHandler_getProperties;
	public final Object o_EntityPropertiesHandler_INSTANCE;
	
	//Vanilla
	private final Field f_LootTable_pools;
	private final Field f_LootPool_lootEntries;
	private final Field f_LootEntry_conditions;
	private final Field f_LootEntryItem_functions;
	
	
	public IceAndFireReflect() throws Exception
	{
		//Ice And Fire
		c_EntityDragonBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
		
		c_EntityEffectProperties = Class.forName("com.github.alexthe666.iceandfire.entity.EntityEffectProperties");
		m_EntityEffectProperties_isStone = ReflectUtil.findMethod(c_EntityEffectProperties, "isStone");
		
		c_ItemStoneStatue = Class.forName("com.github.alexthe666.iceandfire.item.ItemStoneStatue");
		
		c_EntityMyrmexQueen = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen");
		
		c_EntityMyrmexBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexBase");
		f_EntityMyrmexBase_buyingList = ReflectUtil.findField(c_EntityMyrmexBase, "buyingList");
		m_EntityMyrmexBase_populateBuyingList = ReflectUtil.findMethod(c_EntityMyrmexBase, "populateBuyingList");
		m_EntityMyrmexBase_isJungle = ReflectUtil.findMethod(c_EntityMyrmexBase, "isJungle");

		c_EntityGorgon = Class.forName("com.github.alexthe666.iceandfire.entity.EntityGorgon");
		c_EntityStoneStatue = Class.forName("com.github.alexthe666.iceandfire.entity.EntityStoneStatue");

		//LLibrary
		c_EntityPropertiesHandler = Class.forName("net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler");
		f_EntityPropertiesHandler_INSTANCE = ReflectUtil.findField(c_EntityPropertiesHandler, "INSTANCE");
		m_EntityPropertiesHandler_getProperties = ReflectUtil.findMethod(c_EntityPropertiesHandler, "getProperties");
		o_EntityPropertiesHandler_INSTANCE = f_EntityPropertiesHandler_INSTANCE.get(null); //public static final (Enum)
		
		//Ice And Fire
		try
		{
			c_ItemDragonHornStatic = Class.forName("com.github.alexthe666.iceandfire.item.ItemDragonHornStatic");
		}
		catch(Exception e)
		{
			c_ItemDragonHornStatic = null;
		}
		
		//Vanilla
		f_LootTable_pools = ReflectUtil.findFieldAny(LootTable.class, "field_186466_c", "pools");
		f_LootPool_lootEntries = ReflectUtil.findFieldAny(LootPool.class, "field_186453_a", "lootEntries");
		f_LootEntry_conditions = ReflectUtil.findFieldAny(LootEntry.class, "field_186366_e", "conditions");
		f_LootEntryItem_functions = ReflectUtil.findFieldAny(LootEntryItem.class, "field_186369_b", "functions");
	}
	
	public boolean getIsStone(Entity entity)
	{
		try
		{
			Object stoneProperty = getEntityEffectProperties(entity);
			if(stoneProperty == null)
				return false;
			
			return (boolean) m_EntityEffectProperties_isStone.invoke(stoneProperty);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			ErrorUtil.logSilent("IceAndFire Error getIsStone");
			return false;
		}
	}
	
	@Nullable
	public Object getEntityEffectProperties(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return getProperties(entity, c_EntityEffectProperties);
	}
	
	protected Object getProperties(Entity entity, Class propertiesClazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_EntityPropertiesHandler_getProperties.invoke(o_EntityPropertiesHandler_INSTANCE, entity, propertiesClazz);
	}
	
	@Nullable
	public MerchantRecipeList getMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException
	{
		return (MerchantRecipeList) f_EntityMyrmexBase_buyingList.get(myrmexBase);
	}
	
	public void resetMyrmexTrades(Object myrmexBase) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		f_EntityMyrmexBase_buyingList.set(myrmexBase, null);
		m_EntityMyrmexBase_populateBuyingList.invoke(myrmexBase);
	}
	
	public boolean isMyrmexJungle(Object myrmexBase) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return (boolean) m_EntityMyrmexBase_isJungle.invoke(myrmexBase);
	}
	
	@Nullable
	public List<LootPool> getPools(LootTable table) throws IllegalArgumentException, IllegalAccessException
	{
		if(table == null)
			return null;
		
		return (List<LootPool>) f_LootTable_pools.get(table);
	}
	
	@Nullable
	public List<LootEntry> getEntries(LootPool pool) throws IllegalArgumentException, IllegalAccessException
	{
		if(pool == null)
			return null;
		
		return (List<LootEntry>) f_LootPool_lootEntries.get(pool);
	}
	
	@Nullable
	public LootCondition[] getConditions(LootEntry entry) throws IllegalArgumentException, IllegalAccessException
	{
		if(entry == null)
			return null;
		
		return (LootCondition[]) f_LootEntry_conditions.get(entry);
	}
	
	public LootFunction[] getFunctions(LootEntryItem entry) throws IllegalArgumentException, IllegalAccessException
	{
		if(entry == null)
			return null;
		
		return (LootFunction[]) f_LootEntryItem_functions.get(entry);
	}
}
