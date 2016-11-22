/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PickingRay;
import org.joml.Vector3f;
/**
 *
 * @author David Joshua Dibble
 * borrowed liberally from  http://schabby.de/picking-opengl-ray-tracing/
 */
public class PickingRay {
    private Vector3f clickPosInWorld = new Vector3f();
    private Vector3f direction = new Vector3f();
 
	/**
	 * Computes the intersection of this ray with the X-Y Plane (where Z = 0)
	 * and writes it back to the provided vector.
	 */
	public void intersectionWithXyPlane(float[] worldPos)
	{
		float s = -clickPosInWorld.z / direction.z;
		worldPos[0] = clickPosInWorld.x+direction.x*s;
		worldPos[1] = clickPosInWorld.y+direction.y*s;
		worldPos[2] = 0;
	}
 
	public Vector3f getClickPosInWorld() {
		return clickPosInWorld;
	}
	public Vector3f getDirection() {
		return direction;
	}	
}
