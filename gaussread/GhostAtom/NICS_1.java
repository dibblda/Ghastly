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
public class NICS_1 extends GhostAtomType{
    
    // set the parameters
    public void Define(int Identity, Plane CurrentPlane, int sign){
        SetType = 1;
        Identifier = Identity;
        Atoms = new ArrayList();       
        Vector3f TempAtom;
        if(sign == 1){
            TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,1.0f), true);
            // -1.0 makes render ending use the default radius
            Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, -1.0f)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
            PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;
        }     
        if(sign == -1){
            TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,-1.0f), true);
            Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, -1.0f)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
            PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;
        }      
        
        if(sign == 0){
            TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,1.0f), true);
            // -1.0 makes render ending use the default radius
            Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, -1.0f)));            
            TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,-1.0f), true);
            // -1.0 makes render ending use the default radius
            Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, -1.0f)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            Atoms.get(1).Identifier = Identity;
            Atoms.get(1).AtomIndex = 2;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
            PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;            
        }
       
    }
    
    
}
