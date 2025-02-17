package com.charles445.rltweaker.asm.patch.epicsiegemod;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;


public class ChunkCacheMemoryLeakPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("funwayguy.epicsiegemod.ai.hooks.ChunkCacheFixed", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			FieldNode f_EMPTY_CHUNK = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, "EMPTY_CHUNK", "Lfunwayguy/epicsiegemod/ai/hooks/EsmEmptyChunk;", null, null);
			clazzNode.fields.add(f_EMPTY_CHUNK);

			MethodNode m_clinit = ASMUtil.find(clazzNode, "<clinit>");
			AbstractInsnNode target = ASMUtil.first(m_clinit).opcode(Opcodes.RETURN).find();
			LabelNode start = new LabelNode();
			LabelNode end = new LabelNode();
			LabelNode handler = new LabelNode();
			LabelNode skip = new LabelNode();
			m_clinit.instructions.insertBefore(target, ASMUtil.listOf(
					start,
					new FieldInsnNode(Opcodes.GETSTATIC, "meldexun/memoryutil/UnsafeUtil", "UNSAFE", "Lsun/misc/Unsafe;"),
					new LdcInsnNode(Type.getObjectType("funwayguy/epicsiegemod/ai/hooks/EsmEmptyChunk")),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "sun/misc/Unsafe", "allocateInstance", "(Ljava/lang/Class;)Ljava/lang/Object;", false),
					new TypeInsnNode(Opcodes.CHECKCAST, "funwayguy/epicsiegemod/ai/hooks/EsmEmptyChunk"),
					new FieldInsnNode(Opcodes.PUTSTATIC, clazzNode.name, f_EMPTY_CHUNK.name, f_EMPTY_CHUNK.desc),
					end,
					new JumpInsnNode(Opcodes.GOTO, skip),
					handler,
					new VarInsnNode(Opcodes.ASTORE, 0),
					new TypeInsnNode(Opcodes.NEW, "java/lang/UnsupportedOperationException"),
					new InsnNode(Opcodes.DUP),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/Throwable;)V", false),
					new InsnNode(Opcodes.ATHROW),
					skip
			));
			m_clinit.tryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, "java/lang/InstantiationException"));

			MethodNode m_getCachedEmpty = ASMUtil.find(clazzNode, "getCachedEmpty");
			m_getCachedEmpty.instructions.clear();
			m_getCachedEmpty.instructions.insert(ASMUtil.listOf(
					new FieldInsnNode(Opcodes.GETSTATIC, clazzNode.name, f_EMPTY_CHUNK.name, f_EMPTY_CHUNK.desc),
					new InsnNode(Opcodes.ARETURN)
			));

			MethodNode m_removeEmpty = ASMUtil.find(clazzNode, "removeEmpty");
			m_removeEmpty.instructions.clear();
			m_removeEmpty.instructions.insert(new InsnNode(Opcodes.RETURN));
		});
	}

}
