package com.charles445.rltweaker.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {

	private static final ReflectionField<Map<String, NBTBase>> NBTTagCompound_tagMap = new ReflectionField<>(NBTTagCompound.class, "field_74784_a", "tagMap");
	private static final ReflectionField<List<NBTBase>> NBTTagList_tagList = new ReflectionField<>(NBTTagList.class, "field_74747_a", "tagList");

	public static boolean remove(NBTTagCompound compound, String key) {
		return NBTTagCompound_tagMap.get(compound).remove(key) != null;
	}

	public static boolean clear(NBTTagCompound compound) {
		if (compound.isEmpty()) {
			return false;
		}
		NBTTagCompound_tagMap.get(compound).clear();
		return true;
	}

	public static <T extends NBTBase> boolean forEach(NBTTagCompound compound, Predicate<T> predicate) {
		return NBTUtil.<T>forEach(compound, (k, v) -> predicate.test(v));
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean forEach(NBTTagCompound compound, BiPredicate<String, T> predicate) {
		return ((Map<String, T>) NBTTagCompound_tagMap.get(compound))
				.entrySet()
				.stream()
				.filter(entry -> predicate.test(entry.getKey(), entry.getValue()))
				.count() > 0;
	}

	public static <T extends NBTBase> boolean removeIf(NBTTagCompound compound, Predicate<T> predicate) {
		return NBTUtil.<T>removeIf(compound, (k, v) -> predicate.test(v));
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagCompound compound, BiPredicate<String, T> predicate) {
		return ((Map<String, T>) NBTTagCompound_tagMap.get(compound)).entrySet().removeIf(entry -> predicate.test(entry.getKey(), entry.getValue()));
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagCompound compound, String key, Predicate<T> predicate) {
		NBTBase tag = compound.getTag(key);
		if (tag != null && predicate.test((T) tag)) {
			compound.removeTag(key);
			return true;
		}
		return false;
	}

	public static boolean clear(NBTTagList list) {
		if (list.isEmpty()) {
			return false;
		}
		NBTTagList_tagList.get(list).clear();
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagList list, Predicate<T> predicate) {
		return ((List<T>) NBTTagList_tagList.get(list)).removeIf(predicate);
	}

	public static <T extends NBTBase> boolean removeIf(NBTTagList list, IntObjPredicate<T> predicate) {
		AtomicInteger index = new AtomicInteger();
		return NBTUtil.<T>removeIf(list, element -> predicate.test(index.getAndIncrement(), element));
	}

}
