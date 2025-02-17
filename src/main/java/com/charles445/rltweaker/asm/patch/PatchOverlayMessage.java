package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class PatchOverlayMessage {
	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraftforge.client.GuiIngameForge", ClassWriter.COMPUTE_MAXS, clazzNode -> {
			// ClassDisplayer.instance.printAllMethods(clazzNode);

			MethodNode m_renderGameOverlay = ASMUtil.find(clazzNode, "renderRecordOverlay");
			if (m_renderGameOverlay == null)
				throw new RuntimeException("Couldn't find renderRecordOverlay");

			if (true) {
				// Y offset
				AbstractInsnNode anchor = ASMUtil.first(m_renderGameOverlay).intInsn(68).find();
				if (anchor == null)
					throw new RuntimeException("Couldn't find BIPUSH 68 in renderRecordOverlay");
				// Add hook
				m_renderGameOverlay.instructions.insert(anchor, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextYOffset", "(I)I", false));

				// Dropshadow
				MethodInsnNode textCall = ASMUtil.next(anchor).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("func_78276_b", "drawString").find();
				if (textCall == null)
					throw new RuntimeException("Couldn't find func_78276_b or drawString call in renderRecordOverlay");
				// Change call to fontrenderer to the one that includes a boolean for dropshadow
				// func_175065_a (Ljava/lang/String;FFIZ)I
				textCall.desc = "(Ljava/lang/String;FFIZ)I";
				textCall.name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(textCall.owner, "func_175065_a", textCall.desc);
				// Add hook
				m_renderGameOverlay.instructions.insertBefore(textCall, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextDropShadow", "()Z", false));
				// Add some I2F for casting
				anchor = ASMUtil.prev(textCall).intInsn(-4).find();
				if (anchor == null)
					throw new RuntimeException("Couldn't find BIPUSH -4 in renderRecordOverlay, please report to RLTweaker dev");
				m_renderGameOverlay.instructions.insertBefore(anchor, new InsnNode(Opcodes.I2F));
				m_renderGameOverlay.instructions.insert(anchor, new InsnNode(Opcodes.I2F));

				// ASMUtil.LOGGER.info(ASMUtil.methodToString(m_renderGameOverlay));
			}

		});
	}
}
