package terrain.blocks;

import org.lwjgl.util.vector.Vector2f;

import terrain.blocks.Block.Direction;

public class Air extends Block {
	
	private static final Vector2f[] textureMapPositions = {
		Block.positionInTextureAtlas(0, 0),
		Block.positionInTextureAtlas(0, 0),
		Block.positionInTextureAtlas(0, 0),
		Block.positionInTextureAtlas(0, 0),
		Block.positionInTextureAtlas(0, 0),
		Block.positionInTextureAtlas(0, 0),
	};

	public Vector2f getTextureMapPosition(Direction direction) {
		switch (direction) {
		case NORTH:
			return textureMapPositions[0];
		case EAST:
			return textureMapPositions[1];
		case SOUTH:
			return textureMapPositions[2];
		case WEST:
			return textureMapPositions[3];
		case UP:
			return textureMapPositions[4];
		case DOWN:
			return textureMapPositions[5];
		}
		return null;
	}

	public TransparentType getTransparent() {
		return TransparentType.FULL;
	}

	public boolean getFullBlock() {
		return true;
	}

	public boolean fullFaceInDirection(Direction direction) {
		return false;
	}

	public boolean getSolid() {
		return false;
	}
	
	public String getDescription() {
		return "Air";
	}


}
