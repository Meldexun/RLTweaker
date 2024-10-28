package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionVariable;

public class ShaderOptionVariableHook {

	public static ShaderOption parseOption(String line, String path) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("#define")) return null;
		reader.readWhile(FastStringReader::isWhitespace);
		String name = reader.readMatching(FastStringReader::isWord);
		reader.readWhile(FastStringReader::isWhitespace);
		String value = reader.readMatching(FastStringReader::isWord);
		reader.readWhile(FastStringReader::isWhitespace);
		String description = null;
		if (reader.readOptional("//")) {
			description = reader.readAll();
		}
		if (!reader.endReached()) return null;
		String[] values = null;
		if (description != null) {
			int i0 = description.indexOf('[');
			int i1 = description.indexOf(']');
			if (i0 >= 0 && i1 >= 0 && i0 <= i1) {
				ObjectArrayList<String> valuesList = new ObjectArrayList<>();
				int start = i0 + 1;
				int pos = i0 + 1;
				while (true) {
					if (pos == i1) {
						valuesList.add(description.substring(start, pos));
						break;
					}
					if (description.charAt(pos) == ' ') {
						valuesList.add(description.substring(start, pos));
						start = pos + 1;
					}
					pos++;
				}
				if (!valuesList.contains(value)) {
					values = new String[valuesList.size() + 1];
					values[0] = value;
					valuesList.getElements(0, values, 1, valuesList.size());
				} else {
					values = new String[valuesList.size()];
					valuesList.getElements(0, values, 0, valuesList.size());
				}
				description = description.substring(0, i0) + description.substring(i1 + 1);
			}
		}
		if (values == null) {
			values = new String[] { value };
		}
		return new ShaderOptionVariable(name, description, value, values, null);
	}

	public static boolean matchesLine(ShaderOptionVariable option, String line) {
		FastStringReader reader = new FastStringReader(line);
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read("#define")) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		if (!reader.read(option.getName())) return false;
		reader.readWhile(FastStringReader::isWhitespace);
		reader.readWhile(FastStringReader::isWord);
		reader.readWhile(FastStringReader::isWhitespace);
		return reader.readOptional("//") || reader.endReached();
	}

}
