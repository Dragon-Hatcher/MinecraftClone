package models;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class RawModel {

	private int vaoID;
	private int[] vboIDs;
	private int vertexCount;
	
	public RawModel(int vaoId, int[] vboIDs, int vertexCount) {
		this.vaoID = vaoId;
		this.vboIDs = vboIDs;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public void cleanUp() {
		GL30.glDeleteVertexArrays(vaoID);
		for(int vbo : vboIDs) {
			GL15.glDeleteBuffers(vbo);
		}
	}
	
}
