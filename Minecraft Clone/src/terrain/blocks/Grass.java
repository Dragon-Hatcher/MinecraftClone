package terrain.blocks;

import org.lwjgl.util.vector.Vector2f;

public class Grass extends Block {

	private static final Vector2f[] textureMapPositions = {
		new Vector2f(Block.textureMapBlockSize * 0, Block.textureMapBlockSize * 3),
		new Vector2f(Block.textureMapBlockSize * 0, Block.textureMapBlockSize * 3),
		new Vector2f(Block.textureMapBlockSize * 0, Block.textureMapBlockSize * 3),
		new Vector2f(Block.textureMapBlockSize * 0, Block.textureMapBlockSize * 3),
		new Vector2f(Block.textureMapBlockSize * 0, Block.textureMapBlockSize * 0),
		new Vector2f(Block.textureMapBlockSize * 3, Block.textureMapBlockSize * 0),
	};
	
	public Vector2f getTextureMapPosition(Direction direction) {
		switch(direction) {
		case NORTH: return textureMapPositions[0];
		case EAST: return textureMapPositions[1];
		case SOUTH: return textureMapPositions[2];
		case WEST: return textureMapPositions[3];
		case UP: return textureMapPositions[4];
		case DOWN: return textureMapPositions[5];
		}
		return null;
	}

	public TransparentType getTransparent() {
		return TransparentType.OPAQUE;
	}
	
}
