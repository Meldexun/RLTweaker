package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class PatchBroadcastSounds {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraft.world.ServerWorldEventHandler", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// this.mcServer.getPlayerList().sendPacketToAllPlayers(new SPacketEffect(soundID, pos, data, true));

			if (true) // func_180440_a broadcastSound
			{
				MethodNode m_broadcastSound = ASMUtil.findObf(clazzNode, "func_180440_a", "broadcastSound");
				if (m_broadcastSound == null)
					throw new RuntimeException("Couldn't find func_180440_a or broadcastSound");

				AbstractInsnNode anchor = ASMUtil.first(m_broadcastSound).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_148540_a", "sendPacketToAllPlayers").find();
				if (anchor == null)
					throw new RuntimeException("Couldn't find func_148540_a or sendPacketToAllPlayers call in broadcastSound");

				MethodInsnNode call = (MethodInsnNode) anchor;
				call.setOpcode(Opcodes.INVOKESTATIC);
				call.owner = "com/charles445/rltweaker/hook/HookMinecraft";
				call.name = "playLimitedBroadcastSound";
				call.desc = "(Lnet/minecraft/server/management/PlayerList;Lnet/minecraft/network/play/server/SPacketEffect;Lnet/minecraft/util/math/BlockPos;)V";

				// Insert a load for the BlockPos
				m_broadcastSound.instructions.insertBefore(anchor, new VarInsnNode(Opcodes.ALOAD, 2));
			}
		});
	}
}
