package com.charles445.rltweaker.asm.configloader;

import java.lang.reflect.Field;

class ConfigValueEntry extends ConfigEntry {

	private final Type type;
	private final String value;

	ConfigValueEntry(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	void load(Object object, Field field) throws ReflectiveOperationException {
		type.setField(object, field, value);
	}

}
