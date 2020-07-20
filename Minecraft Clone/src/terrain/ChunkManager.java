package terrain;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.Point;

import camera.Camera;
import toolbox.SimplexNoise;

public class ChunkManager implements Runnable {

	private static final int RENDER_DISTANCE = 10;
	private static ArrayList<Point> relativeChunkPositions = new ArrayList<Point>();
	
	private static int MAX_STORED_CHUNKS = 1000;
	
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
	
	public void run() {
		update();
	}
	
	public void update() {
		Point chunkCoords = Chunk.chunkPositionFromCoords(camera.getPosition().x, camera.getPosition().z);
		int chunkX = chunkCoords.getX();
		int chunkY = chunkCoords.getY();
		
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
		//System.out.println(chunks.size());
		
		while(chunks.size() > MAX_STORED_CHUNKS) {
			Point maxPoint = new Point(0, 0);
			int maxDistance = -1;

			for(Point i : chunks.keySet()) {
				int distance = Math.abs(i.getX() - chunkX) + Math.abs(i.getY() - chunkY);
				if(distance > maxDistance) {
					maxPoint = i;
					maxDistance = distance;
				}
			}
			
			chunks.get(maxPoint).cleanUp();
			chunks.remove(maxPoint);
		}
	}
	
	public ArrayList<Chunk> getChunksToRender() {
		ArrayList<Chunk> ret = new ArrayList<Chunk>();
		
		Point chunkCoords = Chunk.chunkPositionFromCoords(camera.getPosition().x, camera.getPosition().z);
		int chunkX = chunkCoords.getX();
		int chunkY = chunkCoords.getY();

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
