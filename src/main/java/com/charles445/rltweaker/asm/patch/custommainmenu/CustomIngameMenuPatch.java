package com.charles445.rltweaker.asm.patch.custommainmenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.gson.JsonParser;

import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.lib.actions.IAction;
import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.realms.RealmsBridge;

public class CustomIngameMenuPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("lumien.custommainmenu.configuration.ConfigurationLoader", "load", ClassWriter.COMPUTE_FRAMES, method -> {
			int jsonParser = ASMUtil.findLocalVariable(method, "jsonParser").index;
			int configFolder = ASMUtil.findLocalVariable(method, "configFolder").index;
			method.instructions.insertBefore(ASMUtil.first(method).opcode(Opcodes.RETURN).find(), ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, jsonParser),
					new VarInsnNode(Opcodes.ALOAD, configFolder),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomIngameMenuPatch$Hook", "load", "(Lcom/google/gson/JsonParser;Ljava/io/File;)V", false)
			));
		});
		registry.add("lumien.custommainmenu.handler.CMMEventHandler", "openGui", ClassWriter.COMPUTE_FRAMES, method -> {
			LabelNode start = new LabelNode();
			LabelNode end = new LabelNode();
			ASMUtil.addLocalVariable(method, "customIngameMenu", "Llumien/custommainmenu/gui/GuiCustom;", start, end);
			int customIngameMenu = ASMUtil.findLocalVariable(method, "customIngameMenu").index;
			method.instructions.insert(ASMUtil.listWithLabel(label -> ASMUtil.listOf(
					start,
					new VarInsnNode(Opcodes.ALOAD, 1),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/GuiOpenEvent", "getGui", "()Lnet/minecraft/client/gui/GuiScreen;", false),
					new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/client/gui/GuiIngameMenu"),
					new JumpInsnNode(Opcodes.IFEQ, end),
					new FieldInsnNode(Opcodes.GETSTATIC, "lumien/custommainmenu/CustomMainMenu", "INSTANCE", "Llumien/custommainmenu/CustomMainMenu;"),
					new FieldInsnNode(Opcodes.GETFIELD, "lumien/custommainmenu/CustomMainMenu", "config", "Llumien/custommainmenu/configuration/Config;"),
					new LdcInsnNode("ingame_menu"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "lumien/custommainmenu/configuration/Config", "getGUI", "(Ljava/lang/String;)Llumien/custommainmenu/gui/GuiCustom;", false),
					new VarInsnNode(Opcodes.ASTORE, customIngameMenu),
					new VarInsnNode(Opcodes.ALOAD, customIngameMenu),
					new JumpInsnNode(Opcodes.IFNULL, label),
					new VarInsnNode(Opcodes.ALOAD, 1),
					new VarInsnNode(Opcodes.ALOAD, customIngameMenu),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/client/event/GuiOpenEvent", "setGui", "(Lnet/minecraft/client/gui/GuiScreen;)V", false),
					label,
					new InsnNode(Opcodes.RETURN),
					end
			)));
		});
		registry.addObf("lumien.custommainmenu.gui.GuiCustom", "drawScreen", "func_73863_a", ClassWriter.COMPUTE_FRAMES, method -> {
			ASMUtil.remove(method, ASMUtil.first(method).opcode(Opcodes.BIPUSH).intInsn(7).find(), ASMUtil.first(method).methodInsn("glEnd").find());
		});
		registry.add("lumien.custommainmenu.configuration.GuiConfig", "getWantedAction", ClassWriter.COMPUTE_FRAMES, method -> {
			int actionType = ASMUtil.findLocalVariable(method, "actionType").index;
			AbstractInsnNode target = ASMUtil.last(method).opcode(Opcodes.ARETURN).find().getPrevious();
			method.instructions.insertBefore(target, ASMUtil.listWithLabels((label1, label2) -> ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, actionType),
					new LdcInsnNode("continue"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false),
					new JumpInsnNode(Opcodes.IFEQ, label1),
					new TypeInsnNode(Opcodes.NEW, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomIngameMenuPatch$ActionContinue"),
					new InsnNode(Opcodes.DUP),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomIngameMenuPatch$ActionContinue", "<init>", "()V", false),
					new InsnNode(Opcodes.ARETURN),
					label1,
					new VarInsnNode(Opcodes.ALOAD, actionType),
					new LdcInsnNode("saveAndQuit"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false),
					new JumpInsnNode(Opcodes.IFEQ, label2),
					new TypeInsnNode(Opcodes.NEW, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomIngameMenuPatch$ActionSaveAndQuit"),
					new InsnNode(Opcodes.DUP),
					new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomIngameMenuPatch$ActionSaveAndQuit", "<init>", "()V", false),
					new InsnNode(Opcodes.ARETURN),
					label2
			)));
		});
		registry.add("lumien.custommainmenu.lib.actions.ActionOpenGUI", "perform", ClassWriter.COMPUTE_FRAMES, method -> {
			int gui = ASMUtil.findLocalVariable(method, "gui").index;
			VarInsnNode target = ASMUtil.last(method).varInsn(gui).ordinal(1).find();
			method.instructions.insertBefore(target, ASMUtil.listWithLabels((label1, label2, label3) -> ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "lumien/custommainmenu/lib/actions/ActionOpenGUI", "guiName", "Ljava/lang/String;"),
					new LdcInsnNode("advancements"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false),
					new JumpInsnNode(Opcodes.IFEQ, label1),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "createAdvancementsScreen", "(Lnet/minecraft/client/gui/GuiScreen;)Lnet/minecraft/client/gui/GuiScreen;", false),
					new VarInsnNode(Opcodes.ASTORE, gui),
					label1,
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "lumien/custommainmenu/lib/actions/ActionOpenGUI", "guiName", "Ljava/lang/String;"),
					new LdcInsnNode("statistics"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false),
					new JumpInsnNode(Opcodes.IFEQ, label2),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "createStatsScreen", "(Lnet/minecraft/client/gui/GuiScreen;)Lnet/minecraft/client/gui/GuiScreen;", false),
					new VarInsnNode(Opcodes.ASTORE, gui),
					label2,
					new VarInsnNode(Opcodes.ALOAD, 0),
					new FieldInsnNode(Opcodes.GETFIELD, "lumien/custommainmenu/lib/actions/ActionOpenGUI", "guiName", "Ljava/lang/String;"),
					new LdcInsnNode("openToLan"),
					new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false),
					new JumpInsnNode(Opcodes.IFEQ, label3),
					new VarInsnNode(Opcodes.ALOAD, 2),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "createOpenToLanScreen", "(Lnet/minecraft/client/gui/GuiScreen;)Lnet/minecraft/client/gui/GuiScreen;", false),
					new VarInsnNode(Opcodes.ASTORE, gui),
					label3
			)));
		});
	}

	public static class Hook {

		public static void load(JsonParser parser, File configFolder) throws IOException {
			Path ingameMenuFile = configFolder.toPath().resolve("ingame_menu.json");
			if (!Files.exists(ingameMenuFile)) {
				Files.createDirectories(ingameMenuFile.getParent());
				try (InputStream in = Hook.class.getResourceAsStream("/data/ingame_menu_default.json")) {
					Files.copy(in, ingameMenuFile);
				}
			}
		}

		public static GuiScreen createAdvancementsScreen(GuiScreen parent) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.player == null)
				return null;
			return new GuiScreenAdvancements(mc.player.connection.getAdvancementManager());
		}

		public static GuiScreen createStatsScreen(GuiScreen parent) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.player == null)
				return null;
			return new GuiStats(parent, mc.player.getStatFileWriter());
		}

		public static GuiScreen createOpenToLanScreen(GuiScreen parent) {
			return new GuiShareToLan(parent);
		}

	}

	public static class ActionContinue implements IAction {

		@Override
		public void perform(Object source, GuiCustom menu) {
			Minecraft mc = Minecraft.getMinecraft();
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

	}

	public static class ActionSaveAndQuit implements IAction {

		@Override
		public void perform(Object source, GuiCustom menu) {
			Minecraft mc = Minecraft.getMinecraft();
			boolean integratedServerRunning = mc.isIntegratedServerRunning();
			boolean connectedToRealms = mc.isConnectedToRealms();
			mc.world.sendQuittingDisconnectingPacket();
			mc.loadWorld(null);

			if (integratedServerRunning) {
				mc.displayGuiScreen(new GuiMainMenu());
			} else if (connectedToRealms) {
				RealmsBridge realmsbridge = new RealmsBridge();
				realmsbridge.switchToRealms(new GuiMainMenu());
			} else {
				mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
			}
		}

	}

}
