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

public class GameLoop {

	public static Loader loader;
	private static Renderer renderer;
	private static StaticShader shader;

	private static Camera camera;

	private static Chunk[][] chunks = new Chunk[4][4];

	private static Random random = new Random(new Random().nextLong());

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		loader = new Loader();
		shader = new StaticShader();
		renderer = new Renderer(shader);

		camera = new Camera();

		float[][] noise = generatePerlinNoise(generateWhiteNoise(16 * 4, 16 * 4), 6);

		for (int cx = 0; cx < 4; cx++) {
			for (int cy = 0; cy < 4; cy++) {
				Block[][][] blocks = new Block[Chunk.CHUNK_WIDTH][Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT];
				for (int x = 0; x < blocks.length; x++) {
					for (int y = 0; y < blocks[x].length; y++) {
						for (int z = 0; z < blocks[x][y].length; z++) {
							System.out.println(noise[x][y]);
							if (z == noise[cx * 16 + x][cy * 16 + y]) {
								blocks[x][y][z] = new Grass();
							} else if (z < noise[cx * 16 + x][cy * 16 + y]) {
								blocks[x][y][z] = new Dirt();
							} else {
								blocks[x][y][z] = new Air();
							}
						}
					}
				}

				chunks[cx][cy] = new Chunk(blocks, cx, cy);
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
		System.out.println(Mouse.isGrabbed());
		if (Mouse.isGrabbed()) {
			float dx = Mouse.getDX();
			float dy = Mouse.getDY();

			camera.rotate(-dy / 5f, dx / 5f, 0);
			camera.move();
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
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

	private static float[][] generateWhiteNoise(int width, int height) {
		float[][] noise = new float[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				noise[i][j] = (float) random.nextDouble() % 1;
			}
		}

		return noise;
	}

	private static float[][] generateSmoothNoise(float[][] baseNoise, int octave) {
		int width = baseNoise.length;
		int height = baseNoise[0].length;

		float[][] smoothNoise = new float[width][height];

		int samplePeriod = 1 << octave; // calculates 2 ^ k
		float sampleFrequency = 1.0f / samplePeriod;

		for (int i = 0; i < width; i++) {
			// calculate the horizontal sampling indices
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % width; // wrap around
			float horizontal_blend = (i - sample_i0) * sampleFrequency;

			for (int j = 0; j < height; j++) {
				// calculate the vertical sampling indices
				int sample_j0 = (j / samplePeriod) * samplePeriod;
				int sample_j1 = (sample_j0 + samplePeriod) % height; // wrap
																		// around
				float vertical_blend = (j - sample_j0) * sampleFrequency;

				// blend the top two corners
				float top = interpolate(baseNoise[sample_i0][sample_j0], baseNoise[sample_i1][sample_j0],
						horizontal_blend);

				// blend the bottom two corners
				float bottom = interpolate(baseNoise[sample_i0][sample_j1], baseNoise[sample_i1][sample_j1],
						horizontal_blend);

				// final blend
				smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
			}
		}

		return smoothNoise;
	}

	private static float interpolate(float x0, float x1, float alpha) {
		return x0 * (1 - alpha) + alpha * x1;
	}

	private static float[][] generatePerlinNoise(float[][] baseNoise, int octaveCount) {
		int width = baseNoise.length;
		int height = baseNoise[0].length;

		float[][][] smoothNoise = new float[octaveCount][][]; // an array of 2D
																// arrays
																// containing

		float persistance = 0.5f;

		// generate smooth noise
		for (int i = 0; i < octaveCount; i++) {
			smoothNoise[i] = generateSmoothNoise(baseNoise, i);
		}

		float[][] perlinNoise = new float[width][height];
		float amplitude = 1.0f; // the bigger, the more big mountains
		float totalAmplitude = 0.0f;

		// blend noise together
		for (int octave = octaveCount - 1; octave >= 0; octave--) {
			amplitude *= persistance;
			totalAmplitude += amplitude;

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
				}
			}
		}

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				perlinNoise[i][j] /= totalAmplitude;
				perlinNoise[i][j] = (float) (Math.floor(perlinNoise[i][j] * 25));
			}
		}

		return perlinNoise;
	}
}
