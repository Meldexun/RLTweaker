package com.charles445.rltweaker.config.json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class HashMultimapAdapterFactory implements TypeAdapterFactory {

	private static final Class<?> c_AbstractMapBasedMultimap;
	private static final Field f_map;
	static {
		try {
			c_AbstractMapBasedMultimap = Class.forName("com.google.common.collect.AbstractMapBasedMultimap");
			f_map = c_AbstractMapBasedMultimap.getDeclaredField("map");
			f_map.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (!HashMultimap.class.isAssignableFrom(type.getRawType())) {
			return null;
		}
		final Type[] typeArguments = ((ParameterizedType) type.getType()).getActualTypeArguments();
		final TypeToken<?> mapType = TypeToken.getParameterized(HashMap.class, typeArguments[0], TypeToken.getParameterized(HashSet.class, typeArguments[1]).getType());
		final TypeAdapter mapAdapter = gson.getAdapter(mapType);
		return new TypeAdapter<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				try {
					mapAdapter.write(out, f_map.get(value));
				} catch (ReflectiveOperationException e) {
					throw new UnsupportedOperationException(e);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public T read(JsonReader in) throws IOException {
				try {
					T t = (T) HashMultimap.create();
					f_map.set(t, mapAdapter.read(in));
					return t;
				} catch (ReflectiveOperationException e) {
					throw new UnsupportedOperationException(e);
				}
			}
		};
	}

}
