package terrain;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

import camera.Camera;
import toolbox.SimplexNoise;

public class ChunkManager {

	private static final int RENDER_DISTANCE = 10;
	private static ArrayList<Point> relativeChunkPositions = new ArrayList<Point>();
	
	private HashMap<Point, Chunk> chunks = new HashMap<Point, Chunk>();
	private SimplexNoise noise;
	private Camera camera;
	
	public ChunkManager(SimplexNoise noise, Camera camera) {
		this.noise = noise;
		this.camera = camera;
		
		findRelativeChunkPositions();
	}
	
	private void findRelativeChunkPositions() {
		for(int i = -RENDER_DISTANCE; i <= RENDER_DISTANCE; i++) {
			for(int j = -RENDER_DISTANCE; j <= RENDER_DISTANCE; j++) {
				if(Math.sqrt(i * i + j * j) <= RENDER_DISTANCE) {
					relativeChunkPositions.add(new Point(i, j));
				}
			}			
		}
	}
	
	public void update() {
		int chunkX = (int)camera.getPosition().x / Chunk.CHUNK_WIDTH;
		int chunkY = (int)camera.getPosition().z / Chunk.CHUNK_WIDTH;
		
		for(Point relPosition : relativeChunkPositions) {
			Point absPosition = new Point(chunkX + relPosition.getX(), chunkY + relPosition.getY());
			if(!chunks.containsKey(absPosition)) {
				chunks.put(absPosition, new Chunk(noise, (int)absPosition.getX(), (int)absPosition.getY(), this));
				chunks.get(absPosition).updateModel();
				break;
			} else if (chunks.get(absPosition).getModel() == null) {
				chunks.get(absPosition).updateModel();				
			}
		}
	}
	
	public ArrayList<Chunk> getChunksToRender() {
		ArrayList<Chunk> ret = new ArrayList<Chunk>();
		
		int chunkX = (int)camera.getPosition().x / Chunk.CHUNK_WIDTH;
		int chunkY = (int)camera.getPosition().z / Chunk.CHUNK_WIDTH;
		for(Point relPosition : relativeChunkPositions) {
			Point absPosition = new Point(chunkX + relPosition.getX(), chunkY  + relPosition.getY());
			if(chunks.containsKey(absPosition) && chunks.get(absPosition).getModel() != null) {
				ret.add(chunks.get(absPosition));
			}
		}

		return ret;
	}
	
	public Chunk getChunk(Point point) {
		if(!chunks.containsKey(point)) {
			chunks.put(point, new Chunk(noise, (int)point.getX(), (int)point.getY(), this));
		}
		return chunks.get(point);
	}

}
