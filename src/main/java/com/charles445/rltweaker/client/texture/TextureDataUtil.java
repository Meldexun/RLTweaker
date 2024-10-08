package com.charles445.rltweaker.client.texture;

import static meldexun.memoryutil.UnsafeUtil.UNSAFE;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import com.charles445.rltweaker.RLTweaker;

import meldexun.memoryutil.UnsafeBufferUtil;
import meldexun.memoryutil.UnsafeByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import sun.misc.Unsafe;

public class TextureDataUtil {

	public static TextureData loadTextureData(ResourceLocation textureLocation) throws IOException {
		try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(textureLocation)) {
			BufferedImage bufferedImage = TextureUtil.readBufferedImage(resource.getInputStream());
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			boolean blur = false;
			boolean clamp = false;

			if (resource.hasMetadata()) {
				try {
					TextureMetadataSection texturemetadatasection = (TextureMetadataSection) resource.getMetadata("texture");

					if (texturemetadatasection != null) {
						blur = texturemetadatasection.getTextureBlur();
						clamp = texturemetadatasection.getTextureClamp();
					}
				} catch (RuntimeException runtimeexception) {
					RLTweaker.logger.warn("Failed reading metadata of: {}", textureLocation, runtimeexception);
				}
			}

			UnsafeByteBuffer buffer = UnsafeBufferUtil.allocateByte(width * height * 4);

			switch (bufferedImage.getType()) {
			case BufferedImage.TYPE_3BYTE_BGR: {
				byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
				for (int i = 0; i < width * height; i++) {
					int c = 0xFF000000;
					c |= (data[i * 3 + 0] & 0xFF) << 16;
					c |= (data[i * 3 + 1] & 0xFF) << 8;
					c |= (data[i * 3 + 2] & 0xFF) << 0;
					buffer.putInt(i * 4, c);
				}
				break;
			}
			case BufferedImage.TYPE_4BYTE_ABGR: {
				byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
				for (int i = 0; i < width * height; i++) {
					int c = UNSAFE.getInt(data, (long) (Unsafe.ARRAY_BYTE_BASE_OFFSET + i * 4));
					c = (c & 0x000000FF) << 24 | (c & 0x0000FF00) << 8 | (c & 0x00FF0000) >>> 8 | (c & 0xFF000000) >>> 24;
					buffer.putInt(i * 4, c);
				}
				break;
			}
			default: {
				int[] data = new int[width * height];
				bufferedImage.getRGB(0, 0, width, height, data, 0, width);
				for (int i = 0; i < width * height; i++) {
					int c = data[i];
					c = (c & 0xFF00FF00) | (c & 0x000000FF) << 16 | (c & 0x00FF0000) >>> 16;
					buffer.putInt(i * 4, c);
				}
				break;
			}
			}

			return new TextureData(width, height, blur, clamp, buffer);
		}
	}

}
