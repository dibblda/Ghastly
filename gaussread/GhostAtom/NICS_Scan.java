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
public class NICS_Scan extends GhostAtomType{


    // using in the floating point loop check below
    private float AcceptableErrorPercent = .01f;
    // as more atoms are added into the grid they will overlap when displayed
    // adjust the radius to a percentage of the delta so that they dont do that
    private float RadiusDeltaPercent = 20.0f;
    
    
    // set the parameters
    public void Define(int Identity, Plane CurrentPlane, float MinRange, float MaxRange, float Delta, float theta, float phi) throws ArithmeticException{
        
        SetType = 2;
        Identifier = Identity;
        Atoms = new ArrayList();      
        float X, Y, Z;
        float itor;
        float radius;
        int index_itor = 0;
        Vector3f TempAtom;
        // sanity check the input just in case
        if(MaxRange <= MinRange) throw new ArithmeticException();
        if((MaxRange - MinRange) < Delta) throw new ArithmeticException();
        if(Delta == 0.0) throw new ArithmeticException();
        // calculate adjustable radius
        radius = Delta * RadiusDeltaPercent / 100.0f;
        // don't want to be too large
        if(radius > 0.2f)radius = 0.2f;
        
        for(itor = MinRange; QuitLoop(itor,MaxRange); itor += Delta){
            
            X = (float)(itor * Math.sin(Math.toRadians(phi)) * Math.cos(Math.toRadians(theta)));
            Y = (float)(itor * Math.sin(Math.toRadians(phi)) * Math.sin(Math.toRadians(theta)));
            Z = (float)(itor * Math.cos(Math.toRadians(phi)));
            TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(X,Y,Z), true);
            Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, radius)));
            Atoms.get(index_itor).Identifier = Identity;
            Atoms.get(index_itor).AtomIndex = (index_itor++);            
        }
        NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
        return;

    }
    
    // floating point math can be finicky and sometimes the last point isn't included in the loop,
    // this is probably due to rounding errors
    // include it if it within a small *range* of values, say + or - X percent (set at top of this file)
    private boolean QuitLoop(float itor, float MaxRange){        
        float AcceptableErrorValue = itor * AcceptableErrorPercent / 100;
        if(itor > (MaxRange + AcceptableErrorValue))return false;
        return true;        
    }
   
}
