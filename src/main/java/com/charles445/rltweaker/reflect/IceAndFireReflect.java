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
	public final Class c_InFCapabilities;
	public final Method m_InFCapabilities_getEntityEffectCapability;
	public final Class c_IEntityEffectCapability;
	public final Method m_IEntityEffectCapability_isStoned;
	
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
	
	//Vanilla
	private final Field f_LootTable_pools;
	private final Field f_LootPool_lootEntries;
	private final Field f_LootEntry_conditions;
	private final Field f_LootEntryItem_functions;
	
	
	public IceAndFireReflect() throws Exception
	{
		//Ice And Fire
		c_EntityDragonBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
		
		c_InFCapabilities = Class.forName("com.github.alexthe666.iceandfire.api.InFCapabilities");
		m_InFCapabilities_getEntityEffectCapability = ReflectUtil.findMethod(c_InFCapabilities, "getEntityEffectCapability");

		c_IEntityEffectCapability = Class.forName("com.github.alexthe666.iceandfire.api.IEntityEffectCapability");
		m_IEntityEffectCapability_isStoned = ReflectUtil.findMethod(c_IEntityEffectCapability, "isStoned");

		c_ItemStoneStatue = Class.forName("com.github.alexthe666.iceandfire.item.ItemStoneStatue");
		
		c_EntityMyrmexQueen = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen");
		
		c_EntityMyrmexBase = Class.forName("com.github.alexthe666.iceandfire.entity.EntityMyrmexBase");
		f_EntityMyrmexBase_buyingList = ReflectUtil.findField(c_EntityMyrmexBase, "buyingList");
		m_EntityMyrmexBase_populateBuyingList = ReflectUtil.findMethod(c_EntityMyrmexBase, "populateBuyingList");
		m_EntityMyrmexBase_isJungle = ReflectUtil.findMethod(c_EntityMyrmexBase, "isJungle");

		c_EntityGorgon = Class.forName("com.github.alexthe666.iceandfire.entity.EntityGorgon");
		c_EntityStoneStatue = Class.forName("com.github.alexthe666.iceandfire.entity.EntityStoneStatue");
		
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
			Object entityEffectCapability = getEntityEffectCapability(entity);
			if(entityEffectCapability == null)
				return false;
			
			return (boolean) m_IEntityEffectCapability_isStoned.invoke(entityEffectCapability);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			ErrorUtil.logSilent("IceAndFire Error getIsStone");
			return false;
		}
	}
	
	@Nullable
	public Object getEntityEffectCapability(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_InFCapabilities_getEntityEffectCapability.invoke(null, entity);
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
