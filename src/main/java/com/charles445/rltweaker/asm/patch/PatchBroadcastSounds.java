package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchBroadcastSounds extends PatchManager {
	public PatchBroadcastSounds() {
		super("Broadcast Sounds");

		add(new Patch(this, "net.minecraft.world.ServerWorldEventHandler", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				// this.mcServer.getPlayerList().sendPacketToAllPlayers(new SPacketEffect(soundID, pos, data, true));

				if (true) // func_180440_a broadcastSound
				{
					MethodNode m_broadcastSound = findMethod(clazzNode, "func_180440_a", "broadcastSound");
					if (m_broadcastSound == null)
						throw new RuntimeException("Couldn't find func_180440_a or broadcastSound");

					AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(first(m_broadcastSound), Opcodes.INVOKEVIRTUAL, "func_148540_a", "sendPacketToAllPlayers");
					if (anchor == null)
						throw new RuntimeException("Couldn't find func_148540_a or sendPacketToAllPlayers call in broadcastSound");

					MethodInsnNode call = (MethodInsnNode) anchor;
					call.setOpcode(Opcodes.INVOKESTATIC);
					call.owner = "com/charles445/rltweaker/hook/HookMinecraft";
					call.name = "playLimitedBroadcastSound";
					call.desc = "(Lnet/minecraft/server/management/PlayerList;Lnet/minecraft/network/play/server/SPacketEffect;Lnet/minecraft/util/math/BlockPos;)V";

					// Insert a load for the BlockPos
					insertBefore(m_broadcastSound, anchor, new VarInsnNode(Opcodes.ALOAD, 2));
				}
			}
		});
	}
}
