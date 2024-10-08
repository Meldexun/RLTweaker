package com.charles445.rltweaker.client.texture;

import meldexun.memoryutil.UnsafeByteBuffer;

public class TextureData implements AutoCloseable {

	public final int width;
	public final int height;
	public final boolean blur;
	public final boolean clamp;
	public final UnsafeByteBuffer buffer;

	public TextureData(int width, int height, boolean blur, boolean clamp, UnsafeByteBuffer buffer) {
		this.width = width;
		this.height = height;
		this.blur = blur;
		this.clamp = clamp;
		this.buffer = buffer;
	}

	@Override
	public void close() {
		this.buffer.close();
	}

}
