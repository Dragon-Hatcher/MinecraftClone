package main;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Point;

import camera.Camera;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.Renderer;
import shaders.StaticShader;
import terrain.Chunk;
import terrain.ChunkManager;
import toolbox.SimplexNoise;

public class GameLoop {

	public static Loader loader;
	private static Renderer renderer;
	private static StaticShader shader;

	private static Camera camera;

	private static SimplexNoise noise;

	private static ChunkManager chunkManager;
	
	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		loader = new Loader();
		shader = new StaticShader();
		renderer = new Renderer(shader);

		camera = new Camera();

		noise = new SimplexNoise(System.nanoTime());

		chunkManager = new ChunkManager(noise, camera);
		
		try {
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		Mouse.setGrabbed(true);

		long second = System.currentTimeMillis() / 1000;
		long oldSecond = second;
		int count = 0;
		
		while (!Display.isCloseRequested()) {
			update();
			render();
			draw();
			
			if(oldSecond != second) {
				//System.out.println(count);
				count = 0;
			}
			count++;
			oldSecond = second;
			second = System.currentTimeMillis() / 1000;
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

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Mouse.setGrabbed(false);
		} else {
			Mouse.setGrabbed(true);
		}
		
		chunkManager.update();
	}

	private static void render() {
		renderer.prepare();
		shader.start();
		shader.loadViewMatrix(camera);

		// Render
		ArrayList<Chunk> chunksToRender = chunkManager.getChunksToRender();
		for(Chunk i : chunksToRender) {
			renderer.render(i, shader);
		}
		
		shader.stop();
	}

	private static void draw() {
		DisplayManager.updateDisplay();
	}

}
