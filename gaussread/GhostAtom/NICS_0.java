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
public class NICS_0 extends GhostAtomType{
    
    public void NICS_0(int Identity, Plane CurrentPlane){
        SetType = new String("NICS(0)");
        Identifier = Identity;
        Atoms = new ArrayList();       
        Atoms.add(new GhostAtom(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(0.0f,0.0f,0.0f), true)));       
        Atoms.get(0).Identifier = Identity;
        Atoms.get(0).AtomIndex = 1;
    }
    
    
}
