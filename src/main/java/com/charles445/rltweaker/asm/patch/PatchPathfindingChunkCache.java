package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.RLTweakerClassTransformer;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchPathfindingChunkCache {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.ChunkCache", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			try {
				Class.forName("org.spongepowered.mod.SpongeMod", false, RLTweakerClassTransformer.class.getClassLoader());
			} catch (ClassNotFoundException e) {
				return;
			}

			MethodNode m_init = ASMUtil.find(clazzNode, "<init>");

			if (m_init == null)
				throw new RuntimeException("Couldn't find init for ChunkCache... that's not good");

			MethodInsnNode toCall = ASMUtil.first(m_init).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_72964_e", "getChunkFromChunkCoords").find();

			if (toCall == null)
				throw new RuntimeException("Couldn't find func_72964_e or getChunkFromChunkCoords in ChunkCache init");

			m_init.instructions.insertBefore(toCall, new VarInsnNode(Opcodes.ALOAD, 0));

			toCall.setOpcode(Opcodes.INVOKESTATIC);
			toCall.owner = "com/charles445/rltweaker/hook/HookMinecraft";
			toCall.name = "cacheGetChunkFromChunkCoords";
			toCall.desc = "(Lnet/minecraft/world/World;IILnet/minecraft/world/ChunkCache;)Lnet/minecraft/world/chunk/Chunk;";

		});

		registry.add("net.minecraft.pathfinding.PathNavigate", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			try {
				Class.forName("org.spongepowered.mod.SpongeMod", false, RLTweakerClassTransformer.class.getClassLoader());
			} catch (ClassNotFoundException e) {
				return;
			}

			if (true) {
				MethodNode m_getPathToPos = ASMUtil.findObf(clazzNode, "func_179680_a", "getPathToPos");

				if (m_getPathToPos == null)
					throw new RuntimeException("Couldn't find func_179680_a or getPathToPos");

				TypeInsnNode newNode = (TypeInsnNode) ASMUtil.first(m_getPathToPos).opcode(Opcodes.NEW).find();

				if (newNode == null)
					throw new RuntimeException("Couldn't find any instantiation in func_179680_a or getPathToPos");

				while (!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
					newNode = (TypeInsnNode) ASMUtil.next(m_getPathToPos, newNode).opcode(Opcodes.NEW).find();
					if (newNode == null)
						throw new RuntimeException("Failed to find ChunkCache instantiation new in func_179680_a or getPathToPo");
				}

				newNode.desc = ("com/charles445/rltweaker/hook/NullableChunkCache");

				MethodInsnNode callInit = ASMUtil.next(m_getPathToPos, newNode).opcode(Opcodes.INVOKESPECIAL).methodInsn("<init>").find();
				while (!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
					callInit = ASMUtil.next(m_getPathToPos, callInit).opcode(Opcodes.INVOKESPECIAL).methodInsn("<init>").find();
					if (callInit == null)
						throw new RuntimeException("Failed to find ChunkCache instantiation call in func_179680_a or getPathToPo");
				}

				callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
			}

			if (true) {
				MethodNode m_getPathToEntityLiving = ASMUtil.findObf(clazzNode, "func_75494_a", "getPathToEntityLiving");

				TypeInsnNode newNode = (TypeInsnNode) ASMUtil.first(m_getPathToEntityLiving).opcode(Opcodes.NEW).find();

				while (!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
					newNode = (TypeInsnNode) ASMUtil.next(m_getPathToEntityLiving, newNode).opcode(Opcodes.NEW).find();
					if (newNode == null)
						throw new RuntimeException("Failed to find ChunkCache instantiation new in func_75494_a or getPathToEntityLiving");
				}

				newNode.desc = ("com/charles445/rltweaker/hook/NullableChunkCache");

				MethodInsnNode callInit = ASMUtil.next(m_getPathToEntityLiving, newNode).opcode(Opcodes.INVOKESPECIAL).methodInsn("<init>").find();
				while (!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
					callInit = ASMUtil.next(m_getPathToEntityLiving, callInit).opcode(Opcodes.INVOKESPECIAL).methodInsn("<init>").find();
					if (callInit == null)
						throw new RuntimeException("Failed to find ChunkCache instantiation call in func_75494_a or getPathToEntityLiving");
				}

				callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
			}
		});
	}
}
