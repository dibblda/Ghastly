/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import org.joml.Vector3f;
/**
 *
 * @author David Joshua Dibble
 */
public class GhostAtom {

    // atom set is to identify the ghost atom within as belonging particular collection
    // non-specific as the collection should be specified elsewhere
    public int Identifier;
    // AtomIndex is to give each atom a unique identifier within a collection
    public int AtomIndex;
    //coordinates of the ghost atom
    public float x,y,z;
    
    public boolean IsHighlighted = false;
    public int HighlightLevel = 0;
    
    public GhostAtom(){        
    }
    public GhostAtom(float new_x, float new_y, float new_z){
        x = new_x;
        y = new_y;
        z = new_z;        
    }
    public GhostAtom(Vector3f Coordinates){
        x = Coordinates.x;
        y = Coordinates.y;
        z = Coordinates.z;
    }
    
    public Vector3f ReturnCoordinates(){
        return new Vector3f(x,y,z);    
    }
    
    public void Highlight(){
        IsHighlighted = true;
         if(HighlightLevel < 2){
            HighlightLevel++;
        }
    }
    
    public void UnHighlight(){
        IsHighlighted = false;
        if(HighlightLevel > 0){
            HighlightLevel--;
        }
    }
}
