package com.charles445.rltweaker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import net.minecraft.util.ResourceLocation;

public class ClassResourceLocationMap<K, V> {

	private final Map<String, V> namespace2valueMap = new HashMap<>();
	private final Map<String, V> resourceLocation2valueMap = new HashMap<>();
	private final Map<Class<? extends K>, V> class2valueMap = new HashMap<>();
	private final Function<Class<K>, ResourceLocation> resourceLocationFunc;
	private final V defaultValue;
	private final Function<String[], V> valueParser;

	public ClassResourceLocationMap(Function<Class<K>, ResourceLocation> func, V defaultValue, Function<String[], V> valueParser) {
		this.resourceLocationFunc = func;
		this.defaultValue = defaultValue;
		this.valueParser = valueParser;
	}

	@SuppressWarnings("unchecked")
	public V get(K t) {
		if (namespace2valueMap.isEmpty() && resourceLocation2valueMap.isEmpty()) {
			return defaultValue;
		}
		return class2valueMap.computeIfAbsent((Class<? extends K>) t.getClass(), k -> {
			ResourceLocation resourceLocation = resourceLocationFunc.apply((Class<K>) k);
			if (resourceLocation == null) {
				return defaultValue;
			}
			V value = resourceLocation2valueMap.get(resourceLocation.toString());
			if (value != null) {
				return value;
			}
			value = namespace2valueMap.get(resourceLocation.getResourceDomain());
			if (value != null) {
				return value;
			}
			return defaultValue;
		});
	}

	public void load(String[] data) {
		this.namespace2valueMap.clear();
		this.resourceLocation2valueMap.clear();
		this.class2valueMap.clear();

		for (String entry : data) {
			int indexKeyDelimiter = entry.indexOf(':');
			int indexEntryDelimiter = entry.indexOf('=');
			String key = indexEntryDelimiter != -1 ? entry.substring(0, indexEntryDelimiter) : entry;
			String[] rawValue;
			if (indexEntryDelimiter != -1) {
				rawValue = Arrays.stream(entry.substring(indexEntryDelimiter + 1).split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
			} else {
				rawValue = new String[0];
			}
			V value = valueParser.apply(rawValue);
			if (value == null) {
				continue;
			}

			if (indexKeyDelimiter == -1 || indexEntryDelimiter != -1 && indexKeyDelimiter > indexEntryDelimiter) {
				namespace2valueMap.put(key, value);
			} else {
				resourceLocation2valueMap.put(key, value);
			}
		}
	}

	public static class Builder<A> {

		private final List<BiConsumer<A, String[]>> valueParsers = new ArrayList<>();
		private int index;

		public <T> Builder<A> requiredValue(Function<String, T> parser, BiConsumer<A, T> accumulator) {
			final int i = index++;
			this.valueParsers.add((v, s) -> accumulator.accept(v, parser.apply(s[i])));
			return this;
		}

		public <T> Builder<A> optionalValue(Function<String, T> parser, BiConsumer<A, T> accumulator, T defaultValue) {
			final int i = index++;
			this.valueParsers.add((v, s) -> {
				T t = null;
				try {
					t = parser.apply(s[i]);
				} catch (RuntimeException e) {
					// ignore
				}
				if (t == null) {
					t = defaultValue;
				}
				accumulator.accept(v, t);
			});
			return this;
		}

		public <K> ClassResourceLocationMap<K, A> build(Function<Class<K>, ResourceLocation> resourceLocationParser, A defaultValue, Supplier<A> supplier) {
			return build(resourceLocationParser, defaultValue, supplier, Function.identity());
		}

		public <K, V> ClassResourceLocationMap<K, V> build(Function<Class<K>, ResourceLocation> resourceLocationParser, V defaultValue, Supplier<A> supplier, Function<A, V> finisher) {
			return new ClassResourceLocationMap<K, V>(resourceLocationParser, defaultValue, s -> {
				try {
					return valueParsers.stream().collect(new Collector<BiConsumer<A, String[]>, A, V>() {

						@Override
						public Supplier<A> supplier() {
							return supplier;
						}

						@Override
						public BiConsumer<A, BiConsumer<A, String[]>> accumulator() {
							return (a, p) -> p.accept(a, s);
						}

						@Override
						public BinaryOperator<A> combiner() {
							return null;
						}

						@Override
						public Function<A, V> finisher() {
							return finisher;
						}

						@Override
						public Set<Characteristics> characteristics() {
							return Collections.emptySet();
						}

					});
				} catch (Exception e) {
					return null;
				}
			});
		}

	}

}
