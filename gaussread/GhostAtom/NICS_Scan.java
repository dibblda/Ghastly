/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import MeanPlane.*;
import java.util.ArrayList;
import org.joml.Vector3f;

/**
 *
 * @author David Joshua Dibble
 * default NICS at the geometric center of the ring 
 * simplest extended class
 */
public class NICS_Scan extends GhostAtomType{
    
    // set the parameters
    public void Define(int Identity, Plane CurrentPlane, int sign){
        SetType = new String("NICS(1)");
        Identifier = Identity;
        Atoms = new ArrayList();       
        if(sign == 1){
            /*
            Atoms.add(new GhostAtom(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,1.0f), true)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;
            
            */
        }     
        if(sign == -1){
            /*
            Atoms.add(new GhostAtom(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,-1.0f), true)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;
            */
        }      
        
        if(sign == 0){
            /*
            Atoms.add(new GhostAtom(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,1.0f), true)));
            Atoms.add(new GhostAtom(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,-1.0f), true)));
            Atoms.get(0).Identifier = Identity;
            Atoms.get(0).AtomIndex = 1;
            Atoms.get(1).Identifier = Identity;
            Atoms.get(1).AtomIndex = 2;
            NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
            return;
            */
        }
       
    }
    private int Number_of_Points;
    private float interval;
    private float max_Z;        
}
