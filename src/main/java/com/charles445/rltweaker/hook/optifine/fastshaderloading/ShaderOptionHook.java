package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ShaderOptionHook {

	public static Set<String> createPathSet() {
		return Collections.newSetFromMap(new ConcurrentHashMap<>());
	}

	public static String[] toArray(Set<String> pathSet) {
		return pathSet.toArray(new String[pathSet.size()]);
	}

}
