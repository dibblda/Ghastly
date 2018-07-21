/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import MeanPlane.*;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author David Joshua Dibble
 * default NICS at the geometric center of the ring 
 * simplest extended class
 */
public class NICS_0 extends GhostAtomType{
    
    // set the parameters
    public void Define(int Identity, Plane CurrentPlane){
        
        Vector3f TempAtom;
        
        SetType = 0;
        Identifier = Identity;
        Atoms = new ArrayList();
        TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,0.0f), true);
        // set radius to default (-1.0 equals choose default of graphics engine 
        Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, -1.0f)));       
        Atoms.get(0).Identifier = Identity;
        Atoms.get(0).AtomIndex = 1;
        NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
                
    }
    
    
    
    
    
    
    
}
