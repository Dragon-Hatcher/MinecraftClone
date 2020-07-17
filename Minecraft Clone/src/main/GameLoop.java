package main;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import camera.Camera;
import entities.RenderedObject;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import terrain.Chunk;
import terrain.blocks.Air;
import terrain.blocks.Block;
import terrain.blocks.Dirt;
import terrain.blocks.Grass;
import textures.ModelTexture;
import toolbox.SimplexNoise;

public class GameLoop {

	public static Loader loader;
	private static Renderer renderer;
	private static StaticShader shader;

	private static Camera camera;

	private static Chunk[][] chunks = new Chunk[4][4];

	private static Random random = new Random(new Random().nextLong());

	private static SimplexNoise noise;
	
	public static void main(String[] args) {

		DisplayManager.createDisplay();
		loader = new Loader();
		shader = new StaticShader();
		renderer = new Renderer(shader);

		camera = new Camera();

		noise = new SimplexNoise(System.nanoTime());

		for (int cx = 0; cx < 4; cx++) {
			for (int cy = 0; cy < 4; cy++) {
				chunks[cx][cy] = new Chunk(noise, cx, cy);
			}
		}

		try {
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		Mouse.setGrabbed(true);

		while (!Display.isCloseRequested()) {
			update();
			render();
			draw();
		}

		Mouse.destroy();

		shader.cleanUp();
		loader.cleanUp();

		DisplayManager.closeDisplay();
	}

	private static void update() {		
		if (Mouse.isGrabbed()) {
			float dx = Mouse.getDX();
			float dy = Mouse.getDY();

			camera.rotate(-dy / 5f, dx / 5f, 0);
			camera.move();
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
		} else {
			Mouse.setGrabbed(true);
		}

	}

	private static void render() {
		renderer.prepare();
		shader.start();
		shader.loadViewMatrix(camera);

		// Render
		for (int cx = 0; cx < 4; cx++) {
			for (int cy = 0; cy < 4; cy++) {
				renderer.render(chunks[cx][cy], shader);
			}
		}

		shader.stop();
	}

	private static void draw() {
		DisplayManager.updateDisplay();
	}

}
