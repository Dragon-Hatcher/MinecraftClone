package terrain.blocks;

import org.lwjgl.util.vector.Vector2f;

public abstract class Block {

	public enum Direction {
		NORTH, EAST, SOUTH, WEST, UP, DOWN;
	}
	
	public enum TransparentType {
		OPAQUE, TEXTURED, FULL;
	}
	
	public static final float textureMapBlockSize = 0.25f;
	
	public abstract Vector2f getTextureMapPosition(Direction direction);	
	public abstract TransparentType getTransparent();
	
}
