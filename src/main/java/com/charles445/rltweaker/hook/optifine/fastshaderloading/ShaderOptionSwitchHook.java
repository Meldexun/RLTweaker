package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionSwitch;

public class ShaderOptionSwitchHook {

	public static ShaderOption parseOption(String line, String path) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		boolean enabled = !reader.readOptional("//");
		if (!reader.read("#define")) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		String name = reader.readMatching(FastStringReader::isWord);
		reader.readWhile(FastStringReader::isWhitespace);
		String description = null;
		if (reader.readOptional("//")) {
			description = reader.readAll();
		}
		if (!reader.endReached()) return null;
		return new ShaderOptionSwitch(name, description, String.valueOf(enabled), null);
	}

	public static boolean matchesLine(ShaderOptionSwitch option, String line) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		reader.readOptional("//");
		if (!reader.read("#define")) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read(option.getName())) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		return reader.readOptional("//") || reader.endReached();
	}

	public static boolean isUsedInLine(ShaderOptionSwitch option, String line) {
		FastStringReader stringReader = new FastStringReader(line);
		stringReader.readWhile(FastStringReader::isWhitespace);
		if (!stringReader.read('#')) return false;
		if (!stringReader.read('i')) return false;
		if (!stringReader.read('f')) return false;
		stringReader.readOptional('n');
		if (!stringReader.read('d')) return false;
		if (!stringReader.read('e')) return false;
		if (!stringReader.read('f')) return false;
		stringReader.readWhile(FastStringReader::isWhitespace);
		stringReader.read(option.getName());
		stringReader.readWhile(FastStringReader::isWhitespace);
		return stringReader.endReached();
	}

}
