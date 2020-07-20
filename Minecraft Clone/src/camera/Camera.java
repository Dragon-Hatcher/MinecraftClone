package camera;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector3f;

import terrain.Chunk;
import terrain.ChunkManager;
import terrain.blocks.Block;

public class Camera {

	private Vector3f position = new Vector3f(0, 100, 0);
	private Vector3f rotation = new Vector3f(0, 0, 0);

	private float defualtSpeed = 0.05f;
	private float sprintMulti = 1.5f;
	private float jumpSpeed = 0.075f;
	private float gravity = 0.002f;
	private float friction = 0.8f;
	
	private float dx = 0;
	private float dy = 0;
	private float dz = 0;
	private boolean onGround = true;
	
	public Camera() {
	}

	public void rotate(float rx, float ry, float rz) {
		rotation.x += rx;
		if (rotation.x > 360.0) {
			rotation.x -= 360;
		}
		if (rotation.x < 0) {
			rotation.x += 360;
		}
		rotation.y += ry;
		if (rotation.y > 360.0) {
			rotation.y -= 360;
		}
		if (rotation.y < 0) {
			rotation.y += 360;
		}
		rotation.z += rz;
		if (rotation.z > 360.0) {
			rotation.z -= 360;
		}
		if (rotation.z < 0) {
			rotation.z += 360;
		}
	}

	public void collide(ChunkManager manager) {
		HashMap<Vector3f, Block> sorroundingBlocks = new HashMap<Vector3f, Block>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -3; k <= 1; k++) {
					if (position.y + k < 0 || position.y + k > Chunk.CHUNK_HEIGHT) {
						continue;
					}
					//System.out.printf("Position %2f, %2f, %2f | ", position.x, position.y, position.z);

					Point chunkCoords = Chunk.chunkPositionFromCoords(position.x + i, position.z + j);
					Chunk chunk = manager.getChunk(chunkCoords);

					//System.out.printf("Chunk %d, %d | ", chunkCoords.getX(), chunkCoords.getY());

					Vector3f blockCoords = Chunk.positionInChunkFromCoords(position.x + i, position.z + j,
							position.y + k);
					Block block = chunk.getBlock((int) blockCoords.getX(), (int) blockCoords.getY(),
							(int) blockCoords.getZ());

					//System.out.printf("Block %d, %d, %d | ", (int) blockCoords.getX(), (int) blockCoords.getY(),
					//		(int) blockCoords.getZ());
					//System.out.println(block.getDescription());

					if (block.getSolid()) {
						sorroundingBlocks.put(
								new Vector3f((int) position.x + i, (int) position.y + k, (int) position.z + j), block);
					}
				}
			}
		}

		// for (Vector3f i : sorroundingBlocks.keySet()) {
		// System.out.println(i.x + ", " + i.y + ", " + i.z + ": " +
		// sorroundingBlocks.get(i).getDescription());
		// }
		// System.out.println("-------");

		for (Vector3f i : sorroundingBlocks.keySet()) {
			collideBlock(i);
		}

		//System.out.println("---");

	}

	private void collideBlock(Vector3f block) {
		//System.out.printf("Block: %f, %f, %f | Position: %f, %f, %f\n", block.x, block.y, block.z, position.x,
		//		position.y, position.z);

		if (block.y > position.y - 3 && block.y - 1 < position.y - 3 && position.x >= block.x && position.x <= block.x + 1
				&& position.z >= block.z && position.z <= block.z + 1) {
			position.y = block.y + 3;
			dy = 0;
			onGround = true;
		}
	}

	public void move() {
		position.x += dx;
		position.y += dy;
		position.z += dz;
		
		if(onGround) {
			dx *= friction;		
			dz *= friction;
		}
		dy -= gravity;
		
		float speed = defualtSpeed * (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? sprintMulti : 1f);
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			dz = (float)-Math.sin(Math.toRadians(90 + rotation.y)) * speed;
			dx = (float)-Math.cos(Math.toRadians(90 + rotation.y)) * speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			dz = (float)Math.sin(Math.toRadians(90 + rotation.y)) * speed;
			dx = (float)Math.cos(Math.toRadians(90 + rotation.y)) * speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			dz = (float)Math.sin(Math.toRadians(rotation.y)) * speed;
			dx = (float)Math.cos(Math.toRadians(rotation.y)) * speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			dz = (float)-Math.sin(Math.toRadians(rotation.y)) * speed;
			dx = (float)-Math.cos(Math.toRadians(rotation.y)) * speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround) {
			dy = jumpSpeed;
			onGround = false;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

}
