package com.charles445.rltweaker.util;

import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {

	public static boolean remove(NBTTagCompound compound, String key) {
		if (!compound.hasKey(key)) {
			return false;
		}
		compound.removeTag(key);
		return true;
	}

	public static boolean clear(NBTTagCompound compound) {
		if (compound.hasNoTags()) {
			return false;
		}
		compound.getKeySet()
				.clear();
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> Stream<T> stream(NBTTagCompound compound) {
		return compound.getKeySet()
				.stream()
				.map(compound::getTag)
				.map(tag -> (T) tag);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagCompound compound, Predicate<T> predicate) {
		return compound.getKeySet()
				.removeIf(key -> predicate.test((T) compound.getTag(key)));
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagCompound compound, String key, Predicate<T> predicate) {
		if (!compound.hasKey(key)) {
			return false;
		}
		if (predicate.test((T) compound.getTag(key))) {
			compound.removeTag(key);
			return true;
		}
		return false;
	}

	public static boolean clear(NBTTagList list) {
		if (list.hasNoTags()) {
			return false;
		}
		for (int i = list.tagCount() - 1; i >= 0; i--) {
			list.removeTag(i);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> Stream<T> stream(NBTTagList list) {
		return IntStream.range(0, list.tagCount())
				.mapToObj(list::get)
				.map(tag -> (T) tag);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NBTBase> boolean removeIf(NBTTagList list, Predicate<T> predicate) {
		boolean anythingRemoved = false;
		for (int i = list.tagCount() - 1; i >= 0; i--) {
			if (predicate.test((T) list.get(i))) {
				list.removeTag(i);
				anythingRemoved = true;
			}
		}
		return anythingRemoved;
	}

}
