package com.charles445.rltweaker.asm.patch.custommainmenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.configuration.GuiConfig;
import lumien.custommainmenu.configuration.elements.Background;
import lumien.custommainmenu.configuration.elements.Slideshow;
import lumien.custommainmenu.gui.GuiCustom;
import lumien.custommainmenu.lib.MODE;
import lumien.custommainmenu.lib.textures.TextureResourceLocation;
import lumien.custommainmenu.util.RenderUtil;
import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.IClassTransformerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class CustomLoadingScreenPatch {

	public static void registerTransformers(IClassTransformerRegistry registry) {
		registry.add("lumien.custommainmenu.configuration.ConfigurationLoader", "load", ClassWriter.COMPUTE_FRAMES, method -> {
			int jsonParser = ASMUtil.findLocalVariable(method, "jsonParser").index;
			int configFolder = ASMUtil.findLocalVariable(method, "configFolder").index;
			method.instructions.insertBefore(ASMUtil.first(method).opcode(Opcodes.RETURN).find(), ASMUtil.listOf(
					new VarInsnNode(Opcodes.ALOAD, jsonParser),
					new VarInsnNode(Opcodes.ALOAD, configFolder),
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "load", "(Lcom/google/gson/JsonParser;Ljava/io/File;)V", false)
			));
		});
		registry.add("net.minecraft.client.LoadingScreenRenderer", "<init>", ClassWriter.COMPUTE_FRAMES, method -> {
			method.instructions.insert(ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "selectNextLoadingScreen", "()V", false)
			));
		});
		registry.addObf("net.minecraft.client.LoadingScreenRenderer", "resetProgressAndMessage", "func_73721_b", ClassWriter.COMPUTE_FRAMES, method -> {
			method.instructions.insert(ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "selectNextLoadingScreen", "()V", false)
			));
		});
		registry.addObf("net.minecraft.client.LoadingScreenRenderer", "setLoadingProgress", "func_73718_a", ClassWriter.COMPUTE_FRAMES, method -> {
			LabelNode target = ASMUtil.first(method).methodInsnObf("begin", "func_181668_a").findThenPrev().type(LabelNode.class).find();
			LabelNode skip = ASMUtil.first(method).methodInsnObf("draw", "func_78381_a").findThenNext().type(LabelNode.class).find();
			method.instructions.insert(target, ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "renderLoadingScreen", "()V", false),
					new JumpInsnNode(Opcodes.GOTO, skip)
			));
		});
		registry.addObf("net.minecraft.client.gui.GuiScreenWorking", "drawScreen", "func_73863_a", ClassWriter.COMPUTE_FRAMES, method -> {
			MethodInsnNode target = ASMUtil.first(method).methodInsnObf("drawDefaultBackground", "func_146276_q_").find();
			ASMUtil.replace(method, target.getPrevious(), target, ASMUtil.listOf(
					new MethodInsnNode(Opcodes.INVOKESTATIC, "com/charles445/rltweaker/asm/patch/custommainmenu/CustomLoadingScreenPatch$Hook", "renderLoadingScreen", "()V", false)
			));
		});
	}

	public static class Hook {

		private static final String BACKGROUND_IMAGE = "image";
		private static final String BACKGROUND_MODE = "mode";
		private static final String BACKGROUND_SLIDESHOW = "slideshow";
		private static final String SLIDESHOW_SYNCED = "synced";
		private static final String SLIDESHOW_IMAGES = "images";
		private static final String SLIDESHOW_DISPLAY_DURATION = "displayDuration";
		private static final String SLIDESHOW_FADE_DURATION = "fadeDuration";
		private static final String SLIDESHOW_SHUFFLE = "shuffle";
		private static final Random RNG = new Random();
		private static final Background DEFAULT_LOADING_SCREEN = new Background(null, new TextureResourceLocation(Gui.OPTIONS_BACKGROUND.toString()));
		private static Background[] loadingScreens = new Background[] { DEFAULT_LOADING_SCREEN };
		private static boolean shuffle;
		private static int index;

		public static void load(JsonParser jsonParser, File configFolder) throws IOException {
			Path loadingScreenFile = configFolder.toPath().resolve("ext").resolve("loading_screen.json");
			if (!Files.exists(loadingScreenFile)) {
				Files.createDirectories(loadingScreenFile.getParent());
				try (InputStream in = Hook.class.getResourceAsStream("/data/loading_screen_default.json")) {
					Files.copy(in, loadingScreenFile);
				}
			}
			JsonObject loadingScreenJson;
			try (Reader reader = Files.newBufferedReader(loadingScreenFile)) {
				loadingScreenJson = jsonParser.parse(reader).getAsJsonObject();
			}
			shuffle = loadingScreenJson.has("shuffle") && loadingScreenJson.get("shuffle").getAsBoolean();
			loadingScreens = StreamSupport.stream(loadingScreenJson.getAsJsonArray("background").spliterator(), false).map(JsonElement::getAsJsonObject).map(Hook::loadBackground).toArray(Background[]::new);
			if (loadingScreens.length == 0) {
				loadingScreens = new Background[] { DEFAULT_LOADING_SCREEN };
			}
		}

		public static void selectNextLoadingScreen() {
			if (shuffle) {
				index = RNG.nextInt(loadingScreens.length);
			} else {
				index = (index + 1) % loadingScreens.length;
			}
		}

		public static void renderLoadingScreen() {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution resolution = new ScaledResolution(mc);
			Background background = loadingScreens[index];
			if (!background.ichBinEineSlideshow) {
				background.image.bind();
				drawImageWithMode(background.mode, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight());
			} else {
				background.slideShow.getCurrentResource1().bind();
				drawImageWithMode(background.mode, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight());
				if (background.slideShow.fading()) {
					GlStateManager.enableBlend();
					background.slideShow.getCurrentResource2().bind();
					GlStateManager.color(1.0f, 1.0f, 1.0f, background.slideShow.getAlphaFade(mc.getRenderPartialTicks()));
					drawImageWithMode(background.mode, 0, 0, resolution.getScaledWidth(), resolution.getScaledHeight());
					GlStateManager.disableBlend();
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				}
			}
		}

		private static void drawImageWithMode(final MODE mode, final int x, final int y, final int width, final int height) {
			final int imageWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			final int imageHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
			int drawWidth = 0;
			int drawHeight = 0;
			final float factorWidth = width / (float) imageWidth;
			final float factorHeight = height / (float) imageHeight;
			switch (mode) {
			case FILL:
				if (factorWidth > factorHeight) {
					drawWidth = (int) (imageWidth * factorWidth);
					drawHeight = (int) (imageHeight * factorWidth);
				} else {
					drawWidth = (int) (imageWidth * factorHeight);
					drawHeight = (int) (imageHeight * factorHeight);
				}
				RenderUtil.drawPartialImage(x, y, 0, 0, drawWidth, drawHeight, imageWidth, imageHeight);
				break;
			case STRETCH:
				RenderUtil.drawCompleteImage(x, y, width, height);
				break;
			case CENTER:
				RenderUtil.drawCompleteImage(x + (int) (width / 2.0f - imageWidth / 2.0f), y + (int) (height / 2.0f - imageHeight / 2.0f), imageWidth, imageHeight);
				break;
			case TILE:
				final int countX = (int) Math.ceil(width / (float) imageWidth);
				final int countY = (int) Math.ceil(height / (float) imageHeight);
				for (int cX = 0; cX < countX; ++cX) {
					for (int cY = 0; cY < countY; ++cY) {
						RenderUtil.drawCompleteImage(x + cX * imageWidth, y + cY * imageHeight, imageWidth, imageHeight);
					}
				}
				break;
			}
		}

		private static Background loadBackground(JsonObject backgroundJson) {
			Background background = new Background(null, GuiConfig.getWantedTexture(getString(backgroundJson.get(BACKGROUND_IMAGE))));
			if (backgroundJson.has(BACKGROUND_MODE)) {
				background.setMode(backgroundJson.get(BACKGROUND_MODE).getAsString());
			}
			if (backgroundJson.has(BACKGROUND_SLIDESHOW)) {
				final JsonObject slideShowObject = backgroundJson.get(BACKGROUND_SLIDESHOW).getAsJsonObject();
				background.ichBinEineSlideshow = true;
				if (slideShowObject.has(SLIDESHOW_SYNCED) && slideShowObject.get(SLIDESHOW_SYNCED).getAsBoolean()) {
					final GuiCustom mainMenu = CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
					background.slideShow = mainMenu.guiConfig.background.slideShow;
				} else {
					final JsonArray imageArray = slideShowObject.get(SLIDESHOW_IMAGES).getAsJsonArray();
					final String[] images = new String[imageArray.size()];
					for (int i = 0; i < images.length; ++i) {
						images[i] = imageArray.get(i).getAsString();
					}
					final Slideshow slideShow = new Slideshow(null, images);
					if (slideShowObject.has(SLIDESHOW_DISPLAY_DURATION)) {
						slideShow.displayDuration = slideShowObject.get(SLIDESHOW_DISPLAY_DURATION).getAsInt();
					}
					if (slideShowObject.has(SLIDESHOW_FADE_DURATION)) {
						slideShow.fadeDuration = slideShowObject.get(SLIDESHOW_FADE_DURATION).getAsInt();
					}
					if (slideShowObject.has(SLIDESHOW_SHUFFLE) && slideShowObject.get(SLIDESHOW_SHUFFLE).getAsBoolean()) {
						slideShow.shuffle();
					}
					background.slideShow = slideShow;
				}
			}
			return background;
		}

		private static String getString(final JsonElement jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				return jsonElement.getAsString();
			}
			if (jsonElement.isJsonArray()) {
				final JsonArray array = jsonElement.getAsJsonArray();
				return array.get(RNG.nextInt(array.size())).getAsString();
			}
			CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Error getting random value out of " + jsonElement.toString());
			return "ERROR";
		}

	}

}
