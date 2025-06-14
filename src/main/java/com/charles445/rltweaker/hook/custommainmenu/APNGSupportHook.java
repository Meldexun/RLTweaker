package com.charles445.rltweaker.hook.custommainmenu;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.charles445.rltweaker.RLTweaker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import meldexun.imageutil.Color;
import meldexun.imageutil.png.CompressedAPNG;
import meldexun.imageutil.png.PNGDecoder;
import meldexun.memoryutil.UnsafeBuffer;
import meldexun.memoryutil.UnsafeBufferUtil;
import meldexun.memoryutil.UnsafeByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;

public class APNGSupportHook {

	public static void bind(ResourceLocation textureLocation) {
		Minecraft mc = Minecraft.getMinecraft();
		TextureManager textureManager = mc.getTextureManager();
		ITextureObject texture = textureManager.getTexture(textureLocation);
		if (!(texture instanceof AnimatedTexture)) {
			if (texture != null) {
				textureManager.deleteTexture(textureLocation);
			}
			texture = new AnimatedTexture(textureLocation);
			textureManager.loadTexture(textureLocation, texture);
		}
		((AnimatedTexture) texture).startAnimation();
		((AnimatedTexture) texture).updateAnimation();
		TextureUtil.bindTexture(texture.getGlTextureId());
	}

	public static class AnimatedTexture extends AbstractTexture {

		private static final int PRELOAD_FRAME_COUNT = 4;

		private final ResourceLocation textureLocation;
		private CompressedAPNG apng;
		private UnsafeByteBuffer textureBuffer;
		private final LoadingCache<Integer, CompletableFuture<UnsafeBuffer>> textureDataCache = CacheBuilder.newBuilder()
				.expireAfterAccess(30, TimeUnit.SECONDS)
				.<Integer, CompletableFuture<UnsafeBuffer>>removalListener(notification -> {
					if (notification.wasEvicted()) {
						notification.getValue().thenAcceptAsync(UnsafeBuffer::close);
					}
				})
				.build(new CacheLoader<Integer, CompletableFuture<UnsafeBuffer>>() {
					@Override
					public CompletableFuture<UnsafeBuffer> load(Integer frame) throws Exception {
						return CompletableFuture.supplyAsync(() -> {
							CompressedAPNG.Frame frameInfo = AnimatedTexture.this.apng.frames.get(frame);
							UnsafeBuffer buffer = UnsafeBufferUtil.allocate(frameInfo.width * frameInfo.height * 4);
							try {
								PNGDecoder.decodeFrame(AnimatedTexture.this.apng, frameInfo, buffer, Color.RGBA);
							} catch (IOException e) {
								buffer.close();
								throw new UncheckedIOException(e);
							}
							return buffer;
						});
					}
				});
		private int frame;
		private boolean paused;
		private static final long START = System.nanoTime();
		private long nextFrame = -1;

		public AnimatedTexture(ResourceLocation textureLocation) {
			this.textureLocation = textureLocation;
		}

		@Override
		public void loadTexture(IResourceManager resourceManager) throws IOException {
			this.deleteGlTexture();

			boolean blur = false;
			boolean clamp = false;
			try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(this.textureLocation)) {
				this.apng = PNGDecoder.readCompressed(resource.getInputStream());

				if (resource.hasMetadata()) {
					try {
						TextureMetadataSection texturemetadatasection = (TextureMetadataSection) resource.getMetadata("texture");

						if (texturemetadatasection != null) {
							blur = texturemetadatasection.getTextureBlur();
							clamp = texturemetadatasection.getTextureClamp();
						}
					} catch (RuntimeException e) {
						RLTweaker.logger.warn("Failed reading metadata of: {}", textureLocation, e);
					}
				}
			}
			this.textureBuffer = UnsafeBufferUtil.allocateByte(this.apng.width * this.apng.height * 4);
			TextureUtil.bindTexture(this.getGlTextureId());
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.apng.width, this.apng.height, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);
			TextureUtil.setTextureBlurred(blur);
			TextureUtil.setTextureClamped(clamp);

			this.stopAnimation();
		}

		public void startAnimation() {
			if (this.paused) {
				this.paused = false;
				this.nextFrame = -1;
			}
		}

		public void stopAnimation() {
			this.paused = true;
		}

		public void updateAnimation() {
			if (this.paused) {
				return;
			}

			long time = System.nanoTime() - START;
			if (this.nextFrame < 0) {
				this.nextFrame = time + this.getFrameTime();
			} else {
				if (time < this.nextFrame) {
					return;
				}
				this.frame = (this.frame + 1) % this.apng.frames.size();
				this.nextFrame = Math.max(this.nextFrame, time) + this.getFrameTime();
			}

			try (UnsafeBuffer frameData = this.getFrameData(this.frame)) {
				CompressedAPNG.Frame frameInfo = this.apng.frames.get(this.frame);
				for (int y = 0; y < frameInfo.height; y++) {
					for (int x = 0; x < frameInfo.width; x++) {
						int c = frameData.getInt((y * frameInfo.width + x) * 4);
						if (c != 0) {
							this.textureBuffer.putInt(((frameInfo.y_offset + y) * this.apng.width + (frameInfo.x_offset + x)) * 4, c);
						}
					}
				}
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getGlTextureId());
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, this.apng.width, this.apng.height, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, this.textureBuffer.getBuffer());
			this.preloadFrameData();
		}

		private long getFrameTime() {
			return 1_000_000_000L * (long) this.apng.frames.get(this.frame).delay_num / (long) this.apng.frames.get(this.frame).delay_den;
		}

		private UnsafeBuffer getFrameData(int frame) {
			CompletableFuture<UnsafeBuffer> result = this.textureDataCache.getUnchecked(frame);
			this.textureDataCache.invalidate(frame);
			return result.join();
		}

		private void preloadFrameData() {
			for (int i = 0; i < PRELOAD_FRAME_COUNT; i++) {
				this.textureDataCache.getUnchecked((this.frame + i + 1) % this.apng.frames.size());
			}
		}

	}

}
