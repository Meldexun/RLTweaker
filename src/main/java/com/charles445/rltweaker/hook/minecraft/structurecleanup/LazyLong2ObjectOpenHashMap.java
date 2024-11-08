package com.charles445.rltweaker.hook.minecraft.structurecleanup;

import java.util.Objects;
import java.util.function.LongFunction;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@SuppressWarnings("serial")
class LazyLong2ObjectOpenHashMap<T> extends Long2ObjectOpenHashMap<T> {
	
	T computeIfAbsent(long k, LongFunction<T> mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		int pos;
		if (k == 0) {
			if (containsNullKey) return value[n];
			containsNullKey = true;
			pos = n;
		} else {
			pos = (int) HashCommon.mix(k) & mask;
			long curr;
			while ((curr = key[pos]) != 0) {
				if (curr == k) return value[pos];
				pos = (pos + 1) & mask;
			}
		}
		T v = mappingFunction.apply(k);
		key[pos] = k;
		value[pos] = v;
		if (size++ >= maxFill) rehash(HashCommon.arraySize(size + 1, f));
		return v;
	}

}
