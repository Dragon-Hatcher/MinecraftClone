package camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private Vector3f rotation = new Vector3f(0, 0, 0);
		
	private float defualtSpeed = 0.05f;
	private float rotateSpeed = 0.5f;
	
	public Camera() {}
		
	public void rotate(float rx, float ry, float rz) {
		rotation.x += rx;
		rotation.y += ry;
		rotation.z += rz;
	}
	
	public void move() {
		float speed = defualtSpeed * (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 2.5f : 1f);
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= Math.sin(Math.toRadians(90 + rotation.y)) * speed;
			position.x -= Math.cos(Math.toRadians(90 + rotation.y)) * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += Math.sin(Math.toRadians(90 + rotation.y)) * speed;
			position.x += Math.cos(Math.toRadians(90 + rotation.y)) * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.z += Math.sin(Math.toRadians(rotation.y)) * speed;
			position.x += Math.cos(Math.toRadians(rotation.y)) * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.z -= Math.sin(Math.toRadians(rotation.y)) * speed;
			position.x -= Math.cos(Math.toRadians(rotation.y)) * speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			position.y += speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			position.y -= speed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			rotation.y -= rotateSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			rotation.y += rotateSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			rotation.x -= rotateSpeed;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			rotation.x += rotateSpeed;
		}
	}
	
	public void rotate() {
		rotation.x += 360;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	public Vector3f getRotation() {
		return rotation;
	}
	
}
