package main;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

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
	
	private static int frameCount = 0;
	
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
		int renderCount = 0;
		int updateCount = 0;
		
		double targetTime = 1_000_000_000d / (double)DisplayManager.FPS_CAP;
		double delta = 0;
		long previousTime = System.nanoTime();
		
		while (!Display.isCloseRequested()) {
			long now = System.nanoTime();
			delta += (now - previousTime)/targetTime;
			previousTime = now;
			
			while(delta >= 1) {
				update();
				delta--;
				updateCount++;
			}
			render();
			draw();
			
			if(oldSecond != second) {
				System.out.println("Render: " + renderCount + ", Update: " + updateCount);
				renderCount = 0;
				updateCount = 0;
			}
			renderCount++;
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
		
		camera.collide(chunkManager);
		
		frameCount++;
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
