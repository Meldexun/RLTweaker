package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionSwitchConst;

public class ShaderOptionSwitchConstHook {

	public static ShaderOption parseOption(String line, String path) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("const")) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("bool")) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		String name = reader.readMatching(FastStringReader::isWord);
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read('=')) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		String value = reader.readMatching(FastStringReader::isWord);
		if (!value.equals("true") && !value.equals("false")) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read(';')) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		String description = null;
		if (reader.readOptional("//")) {
			description = reader.readAll();
		}
		if (!reader.endReached()) return null;
		return new ShaderOptionSwitchConst(name, description, value, null);
	}

	public static boolean matchesLine(ShaderOptionSwitchConst option, String line) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("const")) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("bool")) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read(option.getName())) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read('=')) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		String value = reader.readMatching(FastStringReader::isWord);
		if (!value.equals("true") && !value.equals("false")) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read(';')) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		return reader.readOptional("//") || reader.endReached();
	}

}
