package com.charles445.rltweaker.hook.optifine.fastshaderloading;

import java.util.Set;

import net.optifine.shaders.config.ShaderOption;

public interface ExtShaderOption {

	void addPath(String path);

	Set<String> getPathSet();

	ShaderOption collectPaths();

}
