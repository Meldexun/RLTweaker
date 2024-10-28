package com.charles445.rltweaker.hook.otg.nearbystructurecheck;

import com.pg85.otg.forge.world.ForgeWorld;
import com.pg85.otg.util.ChunkCoordinate;

import meldexun.reflectionutil.ReflectionField;
import meldexun.reflectionutil.ReflectionMethod;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;

public class ForgeWorldHook {

	private static final ReflectionField<World> WORLD = new ReflectionField<>(MapGenBase.class, "field_75039_c", "world");
	private static final ReflectionMethod<Boolean> CAN_SPAWN_STRUCTURE_AT_COORDS = new ReflectionMethod<Boolean>(MapGenStructure.class, "func_75047_a", "canSpawnStructureAtCoords", int.class, int.class);

	public static boolean isStructureInRadius(ForgeWorld world, ChunkCoordinate pos, MapGenStructure structure, int radius) {
		WORLD.set(structure, world.world);
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (CAN_SPAWN_STRUCTURE_AT_COORDS.invoke(structure, pos.getChunkX() + x, pos.getChunkZ() + z)) {
					return true;
				}
			}
		}
		return false;
	}

}
