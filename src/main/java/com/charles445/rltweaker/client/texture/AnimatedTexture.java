package com.charles445.rltweaker.client.texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.opengl.GL11;

import de.keksuccino.fancymenu.menu.animation.ResourcePackAnimationRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class AnimatedTexture extends AbstractTexture {

	private final ResourcePackAnimationRenderer animation;
	private final Map<ResourceLocation, CompletableFuture<TextureData>> textureDataLoadTasks = new HashMap<>();
	private ResourceLocation lastFrameTextureLocation;

	public AnimatedTexture(ResourcePackAnimationRenderer animation) {
		if (animation.getAnimationFrames().isEmpty()) {
			throw new IllegalArgumentException("Animation can't be empty");
		}
		this.animation = animation;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		this.deleteGlTexture();

		try (TextureData textureData = TextureDataUtil.loadTextureData(this.animation.getAnimationFrames().get(0))) {
			int id = this.getGlTextureId();
			TextureUtil.allocateTexture(id, textureData.width, textureData.height);
			TextureUtil.bindTexture(id);
			TextureUtil.setTextureBlurred(textureData.blur);
			TextureUtil.setTextureClamped(textureData.clamp);
		}
	}

	public void loadFrame() {
		ResourceLocation currentFrameTextureLocation = this.animation.getAnimationFrames().get(this.animation.currentFrame());
		if (Objects.equals(currentFrameTextureLocation, this.lastFrameTextureLocation)) {
			return;
		}
		this.lastFrameTextureLocation = currentFrameTextureLocation;

		try (TextureData textureData = this.waitForOrLoadTextureData(this.animation.getAnimationFrames().get(this.animation.currentFrame()))) {
			TextureUtil.bindTexture(this.getGlTextureId());
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, textureData.width, textureData.height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
					textureData.buffer.getBuffer());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.preloadNextFrames();
	}

	private void preloadNextFrames() {
		for (int i = (this.animation.currentFrame() + 1) % this.animation.animationFrames();
				i != (this.animation.currentFrame() + 5) % this.animation.animationFrames() && i != this.animation.currentFrame();
				i = (i + 1) % this.animation.animationFrames()) {
			ResourceLocation textureLocation = this.animation.getAnimationFrames().get(i);
			this.textureDataLoadTasks.computeIfAbsent(textureLocation, k -> CompletableFuture.supplyAsync(() -> {
				try {
					return TextureDataUtil.loadTextureData(k);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}));
		}
	}

	private TextureData waitForOrLoadTextureData(ResourceLocation textureLocation) throws IOException {
		CompletableFuture<TextureData> textureData = this.textureDataLoadTasks.remove(textureLocation);
		if (textureData != null) {
			return textureData.join();
		}
		return TextureDataUtil.loadTextureData(textureLocation);
	}

}
