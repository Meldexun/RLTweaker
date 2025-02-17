package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.charles445.rltweaker.asm.helper.ObfHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;

public class PatchOverlayMessage extends PatchManager {
	public PatchOverlayMessage() {
		super("Overlay Message");

		add(new Patch(this, "net.minecraftforge.client.GuiIngameForge", ClassWriter.COMPUTE_MAXS) {
			@Override
			public void patch(ClassNode clazzNode) {
				// ClassDisplayer.instance.printAllMethods(clazzNode);

				MethodNode m_renderGameOverlay = this.findMethod(clazzNode, "renderRecordOverlay");
				if (m_renderGameOverlay == null)
					throw new RuntimeException("Couldn't find renderRecordOverlay");

				if (true) {
					// Y offset
					AbstractInsnNode anchor = TransformUtil.findNextIntInsnNodeWithValue(first(m_renderGameOverlay), 68);
					if (anchor == null)
						throw new RuntimeException("Couldn't find BIPUSH 68 in renderRecordOverlay");
					// Add hook
					this.insert(m_renderGameOverlay, anchor, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextYOffset", "(I)I", false));

					// Dropshadow
					MethodInsnNode textCall = TransformUtil.findNextCallWithOpcodeAndName(anchor, Opcodes.INVOKEVIRTUAL, "func_78276_b", "drawString");
					if (textCall == null)
						throw new RuntimeException("Couldn't find func_78276_b or drawString call in renderRecordOverlay");
					// Change call to fontrenderer to the one that includes a boolean for dropshadow
					// func_175065_a (Ljava/lang/String;FFIZ)I
					textCall.name = ObfHelper.isObfuscated() ? "func_175065_a" : "drawString";
					textCall.desc = "(Ljava/lang/String;FFIZ)I";
					// Add hook
					this.insertBefore(m_renderGameOverlay, textCall, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/HookMinecraft", "overlayTextDropShadow", "()Z", false));
					// Add some I2F for casting
					anchor = TransformUtil.findPreviousIntInsnNodeWithValue(textCall, -4);
					if (anchor == null)
						throw new RuntimeException("Couldn't find BIPUSH -4 in renderRecordOverlay, please report to RLTweaker dev");
					this.insertInsnBefore(m_renderGameOverlay, anchor, Opcodes.I2F);
					this.insertInsn(m_renderGameOverlay, anchor, Opcodes.I2F);

					// ClassDisplayer.instance.printMethod(m_renderGameOverlay);
				}

			}
		});
	}
}
