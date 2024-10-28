package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.charles445.rltweaker.hook.optifine.OptifineConfig;

import meldexun.reflectionutil.ReflectionField;
import net.minecraft.util.StringUtils;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.config.ShaderMacros;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionSwitch;
import net.optifine.shaders.config.ShaderOptionSwitchConst;
import net.optifine.shaders.config.ShaderOptionVariable;
import net.optifine.shaders.config.ShaderOptionVariableConst;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.StrUtils;

public class ShaderPackParserHook {

	private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
	private static final Set<String> setConstNames = new ReflectionField<Set<String>>(ShaderPackParser.class, "setConstNames", "").get(null);
	private static final Map<String, Pair<List<String>, List<String>>> file2lines_includes = new ConcurrentHashMap<>();
	private static final Map<Pair<String, String>, Boolean> option_file2used = new ConcurrentHashMap<>();
	private static final Map<Pair<String, String>, Object> option_file2lock = new ConcurrentHashMap<>();

	public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] programs, List<Integer> dimensions) {
		if (shaderPack == null) {
			return new ShaderOption[0];
		}

		try {
			return Stream.concat(Stream.of("/shaders"), dimensions.stream().map(dimension -> "/shaders/world" + dimension))
					.flatMap(dir -> Arrays.stream(programs).flatMap(program -> Stream.of(dir + "/" + program + ".vsh", dir + "/" + program + ".fsh")))
					.collect(Collectors.toList())
					.parallelStream()
					.reduce(new ConcurrentHashMap<String, ShaderOption>(), (m, s) -> {
						parseShaderOptions(shaderPack, s, s, m);
						return m;
					}, (m1, m2) -> m1)
					.values()
					.stream()
					.map(ExtShaderOption.class::cast)
					.map(ExtShaderOption::collectPaths)
					.sorted(Comparator.comparing(ShaderOption::getName, String.CASE_INSENSITIVE_ORDER))
					.toArray(ShaderOption[]::new);
		} catch (Exception e) {
			OptifineConfig.dbg(e.getClass().getName() + ": " + e.getMessage());
			return new ShaderOption[0];
		} finally {
			file2lines_includes.clear();
			option_file2lock.clear();
			option_file2used.clear();
		}
	}

	private static void parseShaderOptions(IShaderPack shaderPack, String parent, String path, Map<String, ShaderOption> shaderOptions) {
		Pair<List<String>, List<String>> lines_includes = readFile(shaderPack, path);
		for (String line : lines_includes.getLeft()) {
			parseShaderOption(shaderPack, parent, path, line, shaderOptions);
		}
		for (String include : lines_includes.getRight()) {
			parseShaderOptions(shaderPack, parent, include, shaderOptions);
		}
	}

	private static Pair<List<String>, List<String>> readFile(IShaderPack shaderPack, String path) {
		return file2lines_includes.computeIfAbsent(path, k -> {
			try (InputStream in = shaderPack.getResourceAsStream(path)) {
				if (in == null) {
					return Pair.of(Collections.emptyList(), Collections.emptyList());
				}
				List<String> lines = new ArrayList<>();
				List<String> includes = new ArrayList<>();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.isEmpty()) {
							continue;
						}
						Matcher includeMatcher = PATTERN_INCLUDE.matcher(line);
						if (includeMatcher.matches()) {
							includes.add(parseInclude(path, includeMatcher.group(1)));
						} else {
							lines.add(line);
						}
					}
				}
				return Pair.of(lines, includes);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	private static String parseInclude(String file, String target) {
		return target.startsWith("/") ? "/shaders" + target : getParent(file) + "/" + target;
	}

	private static String getParent(String file) {
		int i = file.lastIndexOf("/");
		return i >= 0 ? file.substring(0, i) : "/";
	}

	private static void parseShaderOption(IShaderPack shaderPack, String parent, String path, String line, Map<String, ShaderOption> shaderOptions) {
		ShaderOption shaderOption = parseShaderOption(line, parent);
		if (shaderOption == null) {
			return;
		}
		if (shaderOption.getName().startsWith(ShaderMacros.getPrefixMacro())) {
			return;
		}
		if (shaderOption.checkUsed() && !isUsed(shaderPack, parent, shaderOption)) {
			return;
		}

		if (shaderOption != null) {
			ShaderOption shaderOption1 = shaderOptions.putIfAbsent(shaderOption.getName(), shaderOption);
			if (shaderOption1 != null) {
				if (!Objects.equals(shaderOption1.getValueDefault(), shaderOption.getValueDefault())) {
					OptifineConfig.warn("Ambiguous shader option: " + shaderOption.getName());
					OptifineConfig.warn(" - in " + ((ExtShaderOption) shaderOption1).getPathSet() + ": " + shaderOption1.getValueDefault());
					OptifineConfig.warn(" - in " + ((ExtShaderOption) shaderOption).getPathSet() + ": " + shaderOption.getValueDefault());
					shaderOption1.setEnabled(false);
				}

				if (!StringUtils.isNullOrEmpty(shaderOption.getDescription()) && StringUtils.isNullOrEmpty(shaderOption1.getDescription())) {
					synchronized (shaderOption1) {
						if (StringUtils.isNullOrEmpty(shaderOption1.getDescription())) {
							shaderOption1.setDescription(shaderOption.getDescription());
						}
					}
				}

				((ExtShaderOption) shaderOption1).addPath(StrUtils.removePrefix(parent, "/shaders/"));
			} else {
				((ExtShaderOption) shaderOption).addPath(StrUtils.removePrefix(parent, "/shaders/"));
			}
		}
	}

	private static ShaderOption parseShaderOption(String line, String shader) {
		ShaderOption shaderOption;

		shaderOption = ShaderOptionSwitch.parseOption(line, shader);
		if (shaderOption == null) {
			shaderOption = ShaderOptionVariable.parseOption(line, shader);
		}
		if (shaderOption != null) {
			return shaderOption;
		}

		shaderOption = ShaderOptionSwitchConst.parseOption(line, shader);
		if (shaderOption == null) {
			shaderOption = ShaderOptionVariableConst.parseOption(line, shader);
		}
		if (shaderOption != null) {
			return setConstNames.contains(shaderOption.getName()) ? shaderOption : null;
		}

		return null;
	}

	private static boolean isUsed(IShaderPack shaderPack, String path, ShaderOption shaderOption) {
		Pair<String, String> key = Pair.of(shaderOption.getName(), path);
		Boolean result = option_file2used.get(key);
		if (result == null) {
			synchronized (option_file2lock.computeIfAbsent(key, k -> new Object())) {
				result = option_file2used.get(key);
				if (result == null) {
					option_file2used.put(key, result = isUsed0(shaderPack, path, shaderOption));
				}
			}
		}
		return result;
	}

	private static boolean isUsed0(IShaderPack shaderPack, String path, ShaderOption shaderOption) {
		Pair<List<String>, List<String>> lines_includes = readFile(shaderPack, path);
		for (String include : lines_includes.getRight()) {
			if (isUsed(shaderPack, include, shaderOption)) {
				return true;
			}
		}
		for (String line : lines_includes.getLeft()) {
			if (shaderOption.isUsedInLine(line)) {
				return true;
			}
		}
		return false;
	}

}
