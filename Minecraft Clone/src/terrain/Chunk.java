package terrain;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import entities.RenderedObject;
import main.GameLoop;
import models.RawModel;
import models.TexturedModel;
import terrain.blocks.*;
import textures.ModelTexture;
import toolbox.SimplexNoise;

public class Chunk extends RenderedObject {

	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 128;

	private Block[][][] blocks = new Block[CHUNK_WIDTH][CHUNK_WIDTH][CHUNK_HEIGHT];

	public Chunk(Block[][][] blocks, int cx, int cy) {
		super(null, new Vector3f(cx * 16, 0, cy * 16), new Vector3f(0, 0, 0), 1);
		this.blocks = blocks;
		updateModel();
	}

	public Chunk(SimplexNoise noise, int cx, int cy) {
		super(null, new Vector3f(cx * 16, 0, cy * 16), new Vector3f(0, 0, 0), 1);

		double scale = 0.007d;
		
		for (int x = 0; x < blocks.length; x++) {
			for (int y = 0; y < blocks[x].length; y++) {
				double xyNoise = noise.sumOctive(16, (double)(cx*16+x), (double)(cy*16+y), .5d, scale, 20d, 50d);
				for (int z = 0; z < blocks[x][y].length; z++) {
					if(z == (int)xyNoise) {
						blocks[x][y][z] = new Grass();
					} else if (z < xyNoise) {
						blocks[x][y][z] = new Dirt();
					} else {
						blocks[x][y][z] = new Air();
					}
				}
			}
		}
		updateModel();

	}

	private void updateModel() {
		ArrayList<Float> verticies = new ArrayList<Float>();
		ArrayList<Float> textureCoords = new ArrayList<Float>();
		ArrayList<Integer> indicies = new ArrayList<Integer>();

		for (int x = 0; x < blocks.length; x++) {
			for (int y = 0; y < blocks[x].length; y++) {
				for (int z = 0; z < blocks[x][y].length; z++) {

					if (blocks[x][y][z].getTransparent() == Block.TransparentType.FULL) {
						continue;
					}
					for (Block.Direction direction : Block.Direction.values()) {
						boolean onEdge = onEdge(x, y, z, direction);
						Block.TransparentType neighborTransparentType = null;
						if (!onEdge) {
							Vector3f neighborPos = neighbor(x, y, z, direction);
							Block neighbor = blocks[(int) neighborPos.x][(int) neighborPos.y][(int) neighborPos.z];
							neighborTransparentType = neighbor.getTransparent();
						}
						if (onEdge || neighborTransparentType == Block.TransparentType.TEXTURED
								|| neighborTransparentType == Block.TransparentType.FULL) {
							indicies.addAll(Arrays.asList(indicies(verticies.size())));
							verticies.addAll(Arrays.asList(cubeCoords(x, y, z, direction)));
							textureCoords.addAll(Arrays.asList(textureCoords(blocks[x][y][z], direction)));
						}
					}

				}
			}
		}

		float[] vert = new float[verticies.size()];
		float[] text = new float[textureCoords.size()];
		int[] indi = new int[indicies.size()];

		for (int i = 0; i < vert.length; i++) {
			vert[i] = verticies.get(i);
		}
		;
		for (int i = 0; i < text.length; i++) {
			text[i] = textureCoords.get(i);
		}
		;
		for (int i = 0; i < indi.length; i++) {
			indi[i] = indicies.get(i);
		}
		;

		RawModel rawModel = GameLoop.loader.loadToVAO(vert, text, indi);
		ModelTexture modelTexture = new ModelTexture(GameLoop.loader.loadTexture("grass"));
		TexturedModel texturedModel = new TexturedModel(rawModel, modelTexture);
		super.setModel(texturedModel);
	}

	private Integer[] indicies(int sizeIn) {
		int size = sizeIn / 3;
		return new Integer[] { size, size + 1, size + 3, size + 3, size + 1, size + 2 };
	}

	private Float[] textureCoords(Block block, Block.Direction direction) {
		Vector2f topLeft = block.getTextureMapPosition(direction);
		float size = Block.textureMapBlockSize;

		return new Float[] { topLeft.x, topLeft.y + size, topLeft.x + size, topLeft.y + size, topLeft.x + size,
				topLeft.y, topLeft.x, topLeft.y, };
	}

	private Float[] cubeCoords(int x, int y, int z, Block.Direction direction) {
		Float[] ret = { 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f };
		switch (direction) {
		case NORTH:
			ret = new Float[] { -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, };
			break;
		case EAST:
			ret = new Float[] { 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, };
			break;
		case SOUTH:
			ret = new Float[] { -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, };
			break;
		case WEST:
			ret = new Float[] { -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, };
			break;
		case UP:
			ret = new Float[] { -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, };
			break;
		case DOWN:
			ret = new Float[] { -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, };
			break;
		}
		ret[0] += (float) x;
		ret[1] += (float) z;
		ret[2] += (float) y;
		ret[3] += (float) x;
		ret[4] += (float) z;
		ret[5] += (float) y;
		ret[6] += (float) x;
		ret[7] += (float) z;
		ret[8] += (float) y;
		ret[9] += (float) x;
		ret[10] += (float) z;
		ret[11] += (float) y;
		return ret;
	}

	private boolean onEdge(int x, int y, int z, Block.Direction direction) {
		switch (direction) {
		case NORTH:
			return y == 0;
		case EAST:
			return x == CHUNK_WIDTH - 1;
		case SOUTH:
			return y == CHUNK_WIDTH - 1;
		case WEST:
			return x == 0;
		case UP:
			return z == CHUNK_HEIGHT - 1;
		case DOWN:
			return z == 0;
		}
		return false;
	}

	private Vector3f neighbor(int x, int y, int z, Block.Direction direction) {
		switch (direction) {
		case NORTH:
			return new Vector3f(x, y - 1, z);
		case EAST:
			return new Vector3f(x + 1, y, z);
		case SOUTH:
			return new Vector3f(x, y + 1, z);
		case WEST:
			return new Vector3f(x - 1, y, z);
		case UP:
			return new Vector3f(x, y, z + 1);
		case DOWN:
			return new Vector3f(x, y, z - 1);
		}
		return null;
	}

}
