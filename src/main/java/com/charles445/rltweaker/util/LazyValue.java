package com.charles445.rltweaker.util;

import java.util.function.Supplier;

public class LazyValue<T> {

	private Supplier<T> factory;
	private T value;

	public LazyValue(Supplier<T> factory) {
		this.factory = factory;
	}

	public T get() {
		if (factory != null) {
			value = factory.get();
			factory = null;
		}
		return value;
	}

}
