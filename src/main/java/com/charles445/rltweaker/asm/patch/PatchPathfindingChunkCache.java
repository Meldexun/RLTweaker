package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ASMInfo;
import com.charles445.rltweaker.asm.util.ASMLogger;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchPathfindingChunkCache extends PatchManager {
	public PatchPathfindingChunkCache() {
		super("Pathfinding Chunk Cache");

		add(new Patch(this, "net.minecraft.world.ChunkCache", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (ASMInfo.hasSponge) {
					ASMLogger.info("Sponge was detected, skipping patch");
					this.cancelled = true;
					return;
				}

				MethodNode m_init = this.findMethod(clazzNode, "<init>");

				if (m_init == null)
					throw new RuntimeException("Couldn't find init for ChunkCache... that's not good");

				MethodInsnNode toCall = TransformUtil.findNextCallWithOpcodeAndName(first(m_init), Opcodes.INVOKEVIRTUAL, "func_72964_e", "getChunkFromChunkCoords");

				if (toCall == null)
					throw new RuntimeException("Couldn't find func_72964_e or getChunkFromChunkCoords in ChunkCache init");

				this.insertBefore(m_init, toCall, new VarInsnNode(Opcodes.ALOAD, 0));

				toCall.setOpcode(Opcodes.INVOKESTATIC);
				toCall.owner = "com/charles445/rltweaker/hook/HookMinecraft";
				toCall.name = "cacheGetChunkFromChunkCoords";
				toCall.desc = "(Lnet/minecraft/world/World;IILnet/minecraft/world/ChunkCache;)Lnet/minecraft/world/chunk/Chunk;";

			}
		});

		add(new Patch(this, "net.minecraft.pathfinding.PathNavigate", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				if (ASMInfo.hasSponge) {
					ASMLogger.info("Sponge was detected, skipping patch");
					this.cancelled = true;
					return;
				}

				if (true) {
					MethodNode m_getPathToPos = this.findMethod(clazzNode, "func_179680_a", "getPathToPos");

					if (m_getPathToPos == null)
						throw new RuntimeException("Couldn't find func_179680_a or getPathToPos");

					TypeInsnNode newNode = (TypeInsnNode) ASMHelper.findNextInstructionWithOpcode(first(m_getPathToPos), Opcodes.NEW);

					if (newNode == null)
						throw new RuntimeException("Couldn't find any instantiation in func_179680_a or getPathToPos");

					while (!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
						newNode = (TypeInsnNode) ASMHelper.findNextInstructionWithOpcode(newNode, Opcodes.NEW);
						if (newNode == null)
							throw new RuntimeException("Failed to find ChunkCache instantiation new in func_179680_a or getPathToPo");
					}

					newNode.desc = ("com/charles445/rltweaker/hook/NullableChunkCache");

					MethodInsnNode callInit = TransformUtil.findNextCallWithOpcodeAndName(newNode, Opcodes.INVOKESPECIAL, "<init>");
					while (!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
						callInit = TransformUtil.findNextCallWithOpcodeAndName(callInit, Opcodes.INVOKESPECIAL, "<init>");
						if (callInit == null)
							throw new RuntimeException("Failed to find ChunkCache instantiation call in func_179680_a or getPathToPo");
					}

					callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
				}

				if (true) {
					MethodNode m_getPathToEntityLiving = this.findMethod(clazzNode, "func_75494_a", "getPathToEntityLiving");

					TypeInsnNode newNode = (TypeInsnNode) ASMHelper.findNextInstructionWithOpcode(first(m_getPathToEntityLiving), Opcodes.NEW);

					while (!newNode.desc.equals("net/minecraft/world/ChunkCache")) {
						newNode = (TypeInsnNode) ASMHelper.findNextInstructionWithOpcode(newNode, Opcodes.NEW);
						if (newNode == null)
							throw new RuntimeException("Failed to find ChunkCache instantiation new in func_75494_a or getPathToEntityLiving");
					}

					newNode.desc = ("com/charles445/rltweaker/hook/NullableChunkCache");

					MethodInsnNode callInit = TransformUtil.findNextCallWithOpcodeAndName(newNode, Opcodes.INVOKESPECIAL, "<init>");
					while (!callInit.owner.equals("net/minecraft/world/ChunkCache")) {
						callInit = TransformUtil.findNextCallWithOpcodeAndName(callInit, Opcodes.INVOKESPECIAL, "<init>");
						if (callInit == null)
							throw new RuntimeException("Failed to find ChunkCache instantiation call in func_75494_a or getPathToEntityLiving");
					}

					callInit.owner = "com/charles445/rltweaker/hook/NullableChunkCache";
				}
			}
		});
	}
}
