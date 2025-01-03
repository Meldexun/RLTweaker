package com.charles445.rltweaker.hook.otg.chunkgeneratormemoryleak;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class OTGChunkGeneratorHook {

	public static IBlockState setBlockState(Chunk chunk, BlockPos pos, IBlockState state) {
		boolean oldCaptureBlockStates = chunk.getWorld().captureBlockSnapshots;
		try {
			chunk.getWorld().captureBlockSnapshots = !state.getBlock().hasTileEntity(state);
			return chunk.setBlockState(pos, state);
		} finally {
			chunk.getWorld().captureBlockSnapshots = oldCaptureBlockStates;
		}
	}

}
