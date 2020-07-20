package terrain.blocks;

import org.lwjgl.util.vector.Vector2f;

public abstract class Block {

	public enum Direction {
		NORTH, EAST, SOUTH, WEST, UP, DOWN;
	}
	
	public enum TransparentType {
		OPAQUE, TEXTURED, FULL;
	}
	
	public static final int textureAtlasRows = 4;
	public static final float textureMapBlockSize = 1f/(float)textureAtlasRows;
	
	public static Vector2f positionInTextureAtlas(float x, float y) {
		return new Vector2f(x * textureMapBlockSize, y * textureMapBlockSize);
	}
	
	public abstract Vector2f getTextureMapPosition(Direction direction);	
	public abstract TransparentType getTransparent();
	public abstract boolean getFullBlock();
	public abstract boolean fullFaceInDirection(Direction direction);
	public abstract boolean getSolid();
	public abstract String getDescription();
	
}
