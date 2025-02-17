package com.charles445.rltweaker.asm.configloader;

import java.lang.reflect.Field;

abstract class ConfigEntry {

	abstract void load(Object object, Field field) throws ReflectiveOperationException;

}
