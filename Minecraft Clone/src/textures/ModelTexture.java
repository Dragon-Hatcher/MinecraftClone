package textures;

import org.lwjgl.opengl.GL11;

public class ModelTexture {
	
	private int textureID;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	public int getID() {
		return textureID;
	}
	
	public void cleanUp() {
		GL11.glDeleteTextures(textureID);
	}
	
}
