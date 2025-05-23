package com.charles445.rltweaker.asm.patch.optifine;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.charles445.rltweaker.hook.optifine.fastshaderloading.ExtShaderOption;

import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;

public class FastShaderLoadingPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("net.minecraftforge.client.model.ModelLoader", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_init = ASMUtil.find(clazzNode, "<init>");
			AbstractInsnNode target = ASMUtil.first(m_init).opcode(Opcodes.RETURN).find();
			m_init.instructions.insertBefore(target, ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.PUTSTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShadersHook", "modelLoader", "Lnet/minecraftforge/client/model/ModelLoader;")
			));
		});
		registry.add("net.optifine.shaders.Shaders", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_loadShaderPack = ASMUtil.find(clazzNode, "loadShaderPack");
			MethodInsnNode target = ASMUtil.first(m_loadShaderPack).opcode(Opcodes.INVOKEVIRTUAL).methodInsnObf("net/minecraft/client/Minecraft", "func_175603_A", "scheduleResourcesRefresh", "()Lcom/google/common/util/concurrent/ListenableFuture;").find();
			target.setOpcode(Opcodes.INVOKESTATIC);
			target.owner = "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShadersHook";
			target.name = "reloadResources";
			target.desc = "(Lnet/minecraft/client/Minecraft;)Ljava/lang/Object;";
		});
		registry.add("net.optifine.shaders.config.ShaderPackParser", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_parseShaderPackOptions = ASMUtil.find(clazzNode, "parseShaderPackOptions");
			m_parseShaderPackOptions.instructions.clear();
			m_parseShaderPackOptions.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShaderPackParserHook", m_parseShaderPackOptions.name, m_parseShaderPackOptions.desc, false),
					new InsnNode(Opcodes.ARETURN)
			));
		});
		for (String name : new String[] { "ShaderOptionSwitch", "ShaderOptionVariable", "ShaderOptionSwitchConst", "ShaderOptionVariableConst" }) {
			registry.add("net.optifine.shaders.config." + name, ClassWriter.COMPUTE_FRAMES, clazzNode -> {
				MethodNode m_parseOption = ASMUtil.find(clazzNode, "parseOption");
				m_parseOption.instructions.clear();
				m_parseOption.instructions.insert(ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/" + name + "Hook", m_parseOption.name, m_parseOption.desc, false),
						new InsnNode(Opcodes.ARETURN)
				));

				MethodNode m_matchesLine = ASMUtil.find(clazzNode, "matchesLine");
				m_matchesLine.instructions.clear();
				m_matchesLine.instructions.insert(ASMUtil.listOf(
						new VarInsnNode(Opcodes.ALOAD, 0),
						new VarInsnNode(Opcodes.ALOAD, 1),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/" + name + "Hook", m_matchesLine.name, "(Lnet/optifine/shaders/config/" + name + ";Ljava/lang/String;)Z", false),
						new InsnNode(Opcodes.IRETURN)
				));
			});
		}
		registry.add("net.optifine.shaders.config.ShaderOptionSwitch", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			MethodNode m_isUsedInLine = ASMUtil.find(clazzNode, "isUsedInLine");
			m_isUsedInLine.instructions.clear();
			m_isUsedInLine.instructions.insert(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShaderOptionSwitchHook", m_isUsedInLine.name, "(Lnet/optifine/shaders/config/ShaderOptionSwitch;Ljava/lang/String;)Z", false),
					new InsnNode(Opcodes.IRETURN)
			));
		});
		registry.add("net.optifine.shaders.config.ShaderOption", ClassWriter.COMPUTE_FRAMES, clazzNode -> {
			clazzNode.interfaces.add(Type.getInternalName(ExtShaderOption.class));
			FieldNode f_pathSet = new FieldNode(Opcodes.ACC_PRIVATE, "pathSet", "Ljava/util/Set;", null, null);
			MethodNode m_addPath = new MethodNode(Opcodes.ACC_PUBLIC, "addPath", "(Ljava/lang/String;)V", null, null);
			LabelNode label = new LabelNode();
			m_addPath.instructions.add(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					new JumpInsnNode(Opcodes.IFNONNULL, label),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShaderOptionHook", "createPathSet", "()Ljava/util/Set;", false),
					new FieldInsnNode(Opcodes.PUTFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					label,
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true),
					new InsnNode(Opcodes.POP),
					new InsnNode(Opcodes.RETURN)
			));
			MethodNode m_getPathSet = new MethodNode(Opcodes.ACC_PUBLIC, "getPathSet", "()Ljava/util/Set;", null, null);
			m_getPathSet.instructions.add(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					new InsnNode(Opcodes.ARETURN)
			));
			MethodNode m_collectPaths = new MethodNode(Opcodes.ACC_PUBLIC, "collectPaths", "()Lnet/optifine/shaders/config/ShaderOption;", null, null);
			m_collectPaths.instructions.add(ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/hook/optifine/fastshaderloading/ShaderOptionHook", "toArray", "(Ljava/util/Set;)[Ljava/lang/String;", false),
					new FieldInsnNode(Opcodes.PUTFIELD, "net/optifine/shaders/config/ShaderOption", "paths", "[Ljava/lang/String;"),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new InsnNode(Opcodes.ACONST_NULL),
					new FieldInsnNode(Opcodes.PUTFIELD, "net/optifine/shaders/config/ShaderOption", "pathSet", "Ljava/util/Set;"),
					new VarInsnNode(Opcodes.ALOAD, 0),
					new InsnNode(Opcodes.ARETURN)
			));
			clazzNode.fields.add(f_pathSet);
			clazzNode.methods.add(m_addPath);
			clazzNode.methods.add(m_getPathSet);
			clazzNode.methods.add(m_collectPaths);
		});
	}
}
