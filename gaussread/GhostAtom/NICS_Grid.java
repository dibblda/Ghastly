/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;

import MeanPlane.Plane;
import java.util.ArrayList;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
/**
 *
 * @author djosh
 */
public class NICS_Grid extends GhostAtomType{
    // using in the floating point loop check below
    private float AcceptableErrorPercent = .01f;
    // as more atoms are added into the grid they will overlap when displayed
    // adjust the radius to a percentage of the delta so that they dont do that
    private float RadiusDeltaPercent = 20.0f;
    
    public int Number_Of_Points = 0;

    //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
    // for only a z-plane: 8,7,3,4
    // for only a y plane: 1,5,8,4
    // for only an x-plane: 1,2,3,4 
    // variable to describe the cube that encloses the grid of atoms used for rendering
    public Vector3f[] CubePoints = new Vector3f[9];
    
    // variable to describe only the atoms that exist on the surface plane bounding the grid. 
    // allows to illustrate the grid without displaying every single atom involved (possibly just as points
    // to save rendering time
    public ArrayList<Vector3f> SurfaceAtomCoordinates = new ArrayList();
   
    // variable to describe the highlight of the cube that encloses the grid of atoms used for rendering
    public Vector3f[] CubeSelectedPoints = new Vector3f[9];
    
    // variable to describe the change in size to highlight the cube as a percent
    private float CubeDeltaPercent = 20.0f;
    
    // to determine if only plotting a plane
    public boolean Z_Plane = false;
    public boolean Y_Plane = false;
    public boolean X_Plane = false;
    
    // variables for function to get coordinates to draw a simple quad rather than a sphere or point    
    public ArrayList<Vector3f[]> QuadPoints = new ArrayList(); 
    private float X_Delta_Val, Y_Delta_Val, Z_Delta_Val;
    float Quad_WidthDeltaPercent = 20.0f;
    
    
    public void Define(int Identity, Plane CurrentPlane, 
                     float X_MinRange, float X_MaxRange, float X_Delta, 
                     float Y_MinRange, float Y_MaxRange, float Y_Delta, 
                     float Z_MinRange, float Z_MaxRange, float Z_Delta,                      
                     float theta, float phi) throws ArithmeticException{
        SetType = 3;
        Identifier = Identity;
        Atoms = new ArrayList();      
        float MinDelta;
        float radius;
        float X_itor = X_MinRange, Y_itor = Y_MinRange, Z_itor = Z_MinRange;
        float X_MinRangeActual = X_MinRange, Y_MinRangeActual = Y_MinRange, Z_MinRangeActual = Z_MinRange;
        // check if a plane is has been selected (only one point in a dimension
        boolean x_plane = false, y_plane = false, z_plane = false;
        int index_itor = 0;
        // check the input 
        if((X_MaxRange <= X_MinRange)&&(X_Delta != 0)) throw new ArithmeticException();
        if((X_MaxRange - X_MinRange) < X_Delta) throw new ArithmeticException();
        if(X_Delta == 0.0) x_plane = true;
        
        if((Y_MaxRange <= Y_MinRange)&&(Y_Delta != 0)) throw new ArithmeticException();
        if((Y_MaxRange - Y_MinRange) < Y_Delta) throw new ArithmeticException();
        if(Y_Delta == 0.0) y_plane = true;
        
        if((Z_MaxRange <= Z_MinRange)&&(Z_Delta != 0)) throw new ArithmeticException();
        if((Z_MaxRange - Z_MinRange) < Z_Delta) throw new ArithmeticException();
        if(Z_Delta == 0.0) z_plane = true;
        
        // only one plane allowed, otherwise this is a NICS SCAN                
        if(x_plane && y_plane)throw new ArithmeticException();
        if(x_plane && z_plane)throw new ArithmeticException();
        if(z_plane && y_plane)throw new ArithmeticException();
        
        
        X_Delta_Val = X_Delta;
        Y_Delta_Val =  Y_Delta;
        Z_Delta_Val =  Z_Delta;
        
        // calculate the smallest delta for adjustable ghost atom size to avoid overlap in display
        // if a plane is present make sure that the 0 delta doesn't count
        // this is not so important anymore as the atoms are not actually displayed 
        if(X_Delta == 0.0){
            MinDelta = Math.min(Y_Delta, Z_Delta);
        }else if(Y_Delta == 0.0){
            MinDelta = Math.min(X_Delta, Z_Delta);
        }else if(Z_Delta == 0.0){
            MinDelta = Math.min(Y_Delta, X_Delta);
        }else{        
            MinDelta = Math.min(X_Delta, Math.min(Y_Delta, Z_Delta));
        }
        // calculate adjustable radius
        radius = MinDelta * RadiusDeltaPercent / 100.0f;
        // don't want to be too large
        if(radius > 0.2f)radius = 0.2f;
        
        Vector3f InputVector = new Vector3f(0,0,0);
        Vector3f OutputVector = new Vector3f(0,0,0);
        Vector3f TempAtom;
        // define the Z rotation matrices Vector
        Matrix3f Z_Rotation = new Matrix3f();         
        Matrix3f Y_Rotation = new Matrix3f();
        // full rotation matrix for rotation about "yaw" and "pitch" should give similar results to 
        // a spherical ccordinate transformation used in NICS_Scan and is "mathematically equivalent"
        Matrix3f RotationMatrix;
        //rotation counterclockwise about Y axis = theta angle
        // rotation counterclockwise about the Z azis = phi angle
        theta = (float)Math.toRadians(theta);
        phi = (float)Math.toRadians(phi);
        
        Y_Rotation.m00 = (float)Math.cos(theta);
        Y_Rotation.m10 = 0;
        Y_Rotation.m20 = (float)Math.sin(theta);
        Y_Rotation.m01 = 0;
        Y_Rotation.m11 = 1;
        Y_Rotation.m21 = 0;
        Y_Rotation.m02 = -(float)Math.sin(theta);
        Y_Rotation.m12 = 0;
        Y_Rotation.m22 = (float)Math.cos(theta);        
        Z_Rotation.m00 = (float)Math.cos(phi);
        Z_Rotation.m10 = -(float)Math.sin(phi);
        Z_Rotation.m20 = 0;
        Z_Rotation.m01 = (float)Math.sin(phi);
        Z_Rotation.m11 = (float)Math.cos(phi);
        Z_Rotation.m21 = 0;
        Z_Rotation.m02 = 0;
        Z_Rotation.m12 = 0;
        Z_Rotation.m22 = 1;        
        // multiply to obtain the pseudo-spherical coordinate rotations
        RotationMatrix = Z_Rotation.mul(Y_Rotation);
        
        // look at all possible cases for a plane (3) and one case without a plane
        if(X_Delta == 0.0){
           
    // for only an x-plane: 1,2,3,4 
  
            X_itor = X_MinRange;            
            for(Y_itor = Y_MinRange; QuitLoop(Y_itor,Y_MaxRange); Y_itor += Y_Delta){
                for(Z_itor = Z_MinRange; QuitLoop(Z_itor,Z_MaxRange); Z_itor += Z_Delta){
                    // get coordinates for a normal grid    
                    InputVector.x = X_itor;
                    InputVector.y = Y_itor;
                    InputVector.z = Z_itor;
                    // transform according to arbitrary rotation matrix specified by user
                    OutputVector = RotationMatrix.transform(InputVector);
                    // transform according to rotation plan defined by selected atoms
                    TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true);
                    // add into list
                    Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, radius)));
                    Atoms.get(index_itor).Identifier = Identity;
                    Atoms.get(index_itor).AtomIndex = (index_itor++); 
                    // create surface coordinates for visualizing
                    SurfaceAtomCoordinates.add(new Vector3f(TempAtom.x, TempAtom.y, TempAtom.z));
                    // also draw as quad
                    DrawCubePointQuad(X_itor, Y_itor, Z_itor, true, false, false, false, false, false, CurrentPlane, RotationMatrix);  
                    // to set the max range of the plane properly
                    if(WillQuitNext(Y_itor, Y_Delta, Y_MaxRange))Y_MinRangeActual = Y_itor;
                    if(WillQuitNext(Z_itor, Z_Delta, Z_MaxRange))Z_MinRangeActual = Z_itor;                   
                }                
            }
            
            // might not have made it all to max range depending on the delta value chosen, 
            // might overshoot it. set these values after running through the loop
            Z_MaxRange = Z_MinRangeActual;
            Y_MaxRange = Y_MinRangeActual;
                    
            // assign values to the cube points 
            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[4] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            
            Z_Plane = false;
            Y_Plane = false;
            X_Plane = true;            
        }else if(Y_Delta == 0.0){
        
            Y_itor = Y_MinRange;
            for(X_itor = X_MinRange; QuitLoop(X_itor,X_MaxRange); X_itor += X_Delta){               
                for(Z_itor = Z_MinRange; QuitLoop(Z_itor,Z_MaxRange); Z_itor += Z_Delta){
                    // get coordinates for a normal grid 
                    InputVector.x = X_itor;
                    InputVector.y = Y_itor;
                    InputVector.z = Z_itor;
                    // transform according to arbitrary rotation matrix specified by user
                    OutputVector = RotationMatrix.transform(InputVector);
                    // transform according to rotation plan defined by selected atoms
                    TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true);
                    // add into list
                    Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, radius)));
                    Atoms.get(index_itor).Identifier = Identity;
                    Atoms.get(index_itor).AtomIndex = (index_itor++);
                    // create surface coordinates for visualizing
                    SurfaceAtomCoordinates.add(new Vector3f(TempAtom.x, TempAtom.y, TempAtom.z));
                    // also draw as quad
                    DrawCubePointQuad(X_itor, Y_itor, Z_itor,false, true, false, false, false, false, CurrentPlane, RotationMatrix);  
                    // to set range of plane properly
                    if(WillQuitNext(X_itor, X_Delta, X_MaxRange))X_MinRangeActual = X_itor;                        
                    if(WillQuitNext(Z_itor, Z_Delta, Z_MaxRange))Z_MinRangeActual = Z_itor;
                }                
            }
               
            // for only a y plane: 1,5,8,4
            // might not have made it all to max range depending on the delta value chosen, 
            // might overshoot it. set these values after running through the loop
            Z_MaxRange = Z_MinRangeActual;
            X_MaxRange = X_MinRangeActual;
            // assign values to the cube points 
            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[4] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[5] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[8] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            Z_Plane = false;
            Y_Plane = true;
            X_Plane = false;
        }else if(Z_Delta == 0.0){
            
            
            Z_itor = Z_MinRange;
            for(X_itor = X_MinRange; QuitLoop(X_itor,X_MaxRange); X_itor += X_Delta){
                for(Y_itor = Y_MinRange; QuitLoop(Y_itor,Y_MaxRange); Y_itor += Y_Delta){
                    // get coordinates for a normal grid 
                    InputVector.x = X_itor;
                    InputVector.y = Y_itor;
                    InputVector.z = Z_itor;
                    // transform according to arbitrary rotation matrix specified by user
                    OutputVector = RotationMatrix.transform(InputVector);
                    // transform according to rotation plan defined by selected atoms
                    TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true);
                    // add into list
                    Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, radius)));
                    Atoms.get(index_itor).Identifier = Identity;
                    Atoms.get(index_itor).AtomIndex = (index_itor++);
                    // create surface coordinates for visualizing
                    SurfaceAtomCoordinates.add(new Vector3f(TempAtom.x, TempAtom.y, TempAtom.z));
                    // also draw as quad
                    DrawCubePointQuad(X_itor, Y_itor, Z_itor, false, false, true, false, false, false, CurrentPlane, RotationMatrix);  
                    // to set max range of plane draw properly
                    if(WillQuitNext(X_itor, X_Delta, X_MaxRange))X_MinRangeActual = X_itor;
                    if(WillQuitNext(Y_itor, Y_Delta, Y_MaxRange))Y_MinRangeActual = Y_itor;
                        
                }
            }
            // for only a z-plane: 8,7,3,4
   
            // assign values to the cube points 
            // might not have made it all to max range depending on the delta value chosen, 
            // might overshoot it. set these values after running through the loop
            X_MaxRange = X_MinRangeActual;
            Y_MaxRange = Y_MinRangeActual;
            InputVector.x = X_MinRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[4] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            InputVector.x = X_MaxRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[7] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[8] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            Z_Plane = true;
            Y_Plane = false;
            X_Plane = false;
        }else{
            boolean Is_X_Min = false, Is_Y_Min = false, Is_Z_Min = false,
                    Is_X_Max = false, Is_Y_Max = false, Is_Z_Max = false;
            for(X_itor = X_MinRange; QuitLoop(X_itor,X_MaxRange); X_itor += X_Delta){
                for(Y_itor = Y_MinRange; QuitLoop(Y_itor,Y_MaxRange); Y_itor += Y_Delta){
                    for(Z_itor = Z_MinRange; QuitLoop(Z_itor,Z_MaxRange); Z_itor += Z_Delta){
                        // get coordinates for a normal grid 
                        InputVector.x = X_itor;
                        InputVector.y = Y_itor;
                        InputVector.z = Z_itor;
                        
                        // transform according to arbitrary rotation matrix specified by user
                        OutputVector = RotationMatrix.transform(InputVector);
                        // transform according to rotation plan defined by selected atoms
                        TempAtom = CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true);
                        // add into list
                          
                        Atoms.add(new GhostAtom(new Vector4f(TempAtom.x, TempAtom.y, TempAtom.z, radius)));
                        Atoms.get(index_itor).Identifier = Identity;
                        Atoms.get(index_itor).AtomIndex = (index_itor++); 
                        // only add in surface coordinates if at least *1* of the coordinate triplets has a min or a max value
                        // the max may be before the MaxRange as the next itoration may go beyond that value
                        // test each then do general test
                        if(X_itor == X_MinRange)Is_X_Min = true;
                        if(Y_itor == Y_MinRange)Is_Y_Min = true;
                        if(Z_itor == Z_MinRange)Is_Z_Min = true;                       
                        if(WillQuitNext(X_itor, X_Delta, X_MaxRange)){Is_X_Max = true; X_MinRangeActual = X_itor;}
                        if(WillQuitNext(Y_itor, Y_Delta, Y_MaxRange)){Is_Y_Max = true; Y_MinRangeActual = Y_itor;}
                        if(WillQuitNext(Z_itor, Z_Delta, Z_MaxRange)){Is_Z_Max = true; Z_MinRangeActual = Z_itor;}
                     
                        // general test and assign atoms
                        if(Is_X_Min||Is_Y_Min||Is_Z_Min||Is_X_Max||Is_Y_Max||Is_Z_Max){
                            // create surface coordinates for visualizing
                            SurfaceAtomCoordinates.add(new Vector3f(TempAtom.x, TempAtom.y, TempAtom.z));
                            // Also create as a quad
                            DrawCubePointQuad(X_itor, Y_itor, Z_itor,Is_X_Min, Is_Y_Min, Is_Z_Min, Is_X_Max, Is_Y_Max, Is_Z_Max, CurrentPlane, RotationMatrix);                            
                        }
                        
                        //System.out.println("YMin " + Y_MinRange + " YMax " + Y_MaxRange + " CurrentY " + Y_itor + " YMinBool " + Is_Y_Min + " YMaxBool" + Is_Y_Max);
                        //System.out.println("XMin " +   X_MinRange + " XMax " + X_MaxRange + " CurrentX " + X_itor + " XMinBool " + Is_X_Min + " XMaxBool" + Is_X_Max);
                        //System.out.println("ZMin " +   Z_MinRange + " ZMax " + Z_MaxRange + " CurrentZ " + Z_itor + " ZMinBool " + Is_Z_Min + " ZMaxBool" + Is_Z_Max);
                        //System.out.println("plot? " + (Is_X_Min||Is_Y_Min||Is_Z_Min||Is_X_Max||Is_Y_Max||Is_Z_Max));
                        
                        
                        // reset test for cube edge
                        Is_X_Min = false;
                        Is_Y_Min = false;
                        Is_Z_Min = false;
                        Is_X_Max = false;
                        Is_Y_Max = false;
                        Is_Z_Max = false;
                        
                    }
                }
            }
            
            
            //System.out.println("Number Plot: "+ SurfaceAtomCoordinates.size());
            
            /*
            for(int itor = 0; itor < Atoms.size(); itor++){
                System.out.println("Atom X: " + Atoms.get(itor).x + "Atom Y: " + Atoms.get(itor).y + "Atom Z: " + Atoms.get(itor).z);
            }
            */
            
            
            
            
            
            
            
            // calculations for the size of the "selected" cube volume        
            float X_MinRange_Cube_H, X_MaxRange_Cube_H, 
                  Y_MinRange_Cube_H, Y_MaxRange_Cube_H,
                  Z_MinRange_Cube_H, Z_MaxRange_Cube_H;
            
            // might not have made it all to max range depending on the delta value chosen, 
            // might overshoot it. set these values after running through the loop
            
            
            X_MaxRange = X_MinRangeActual;
            Y_MaxRange = Y_MinRangeActual;
            Z_MaxRange = Z_MinRangeActual;
            
            
            
            X_MinRange_Cube_H = X_MinRange - (X_Delta * CubeDeltaPercent / 100);      
            Y_MinRange_Cube_H = Y_MinRange - (Y_Delta * CubeDeltaPercent / 100); 
            Z_MinRange_Cube_H = Z_MinRange - (Z_Delta * CubeDeltaPercent / 100); 

            X_MaxRange_Cube_H = X_MaxRange + (X_Delta * CubeDeltaPercent / 100);      
            Y_MaxRange_Cube_H = Y_MaxRange + (Y_Delta * CubeDeltaPercent / 100); 
            Z_MaxRange_Cube_H = Z_MaxRange + (Z_Delta * CubeDeltaPercent / 100); 


            // assign values to the cube points 
            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[4] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[5] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MaxRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[6] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MaxRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[7] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange;
            InputVector.y = Y_MinRange;
            InputVector.z = Z_MinRange;
            OutputVector = RotationMatrix.transform(InputVector);
            CubePoints[8] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            // variables to describe the highlight of the cube that encloses the grid of atoms used for rendering
            InputVector.x = X_MinRange_Cube_H;
            InputVector.y = Y_MinRange_Cube_H;
            InputVector.z = Z_MaxRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange_Cube_H;
            InputVector.y = Y_MaxRange_Cube_H;
            InputVector.z = Z_MaxRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange_Cube_H;
            InputVector.y = Y_MaxRange_Cube_H;
            InputVector.z = Z_MinRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MinRange_Cube_H;
            InputVector.y = Y_MinRange_Cube_H;
            InputVector.z = Z_MinRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[4] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange_Cube_H;
            InputVector.y = Y_MinRange_Cube_H;
            InputVector.z = Z_MaxRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[5] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange_Cube_H;
            InputVector.y = Y_MaxRange_Cube_H;
            InputVector.z = Z_MaxRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[6] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange_Cube_H;
            InputVector.y = Y_MaxRange_Cube_H;
            InputVector.z = Z_MinRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[7] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            InputVector.x = X_MaxRange_Cube_H;
            InputVector.y = Y_MinRange_Cube_H;
            InputVector.z = Z_MinRange_Cube_H;
            OutputVector = RotationMatrix.transform(InputVector);
            CubeSelectedPoints[8] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            Z_Plane = false;
            Y_Plane = false;
            X_Plane = false;
        }
        
        // put in the number of atoms
        Number_Of_Points = Atoms.size();
        
        NormalToPlane = CurrentPlane.GetPlaneNormal();
        PlaneEquation = CurrentPlane.GetPlaneEquation();
        return;
        
    }  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void DrawCubePointQuad(float X, float Y, float Z, 
                                    boolean Is_X_Min, boolean Is_Y_Min, 
                                    boolean Is_Z_Min, boolean Is_X_Max, 
                                    boolean Is_Y_Max, boolean Is_Z_Max,
                                    Plane CurrentPlane,
                                    Matrix3f RotationMatrix){
        // first determine the size of the quad
        float MinDelta, Quad_Width;
        float Quad_HalfWidth;
        Vector3f InputVector = new Vector3f();
        Vector3f OutputVector = new Vector3f();
        
        
        float X_Calc, Y_Calc, Z_Calc;
        
        if(X_Delta_Val == 0.0){
            MinDelta = Math.min(Y_Delta_Val, Z_Delta_Val);
        }else if(Y_Delta_Val == 0.0){
            MinDelta = Math.min(X_Delta_Val, Z_Delta_Val);
        }else if(Z_Delta_Val == 0.0){
            MinDelta = Math.min(Y_Delta_Val, X_Delta_Val);
        }else{        
            MinDelta = Math.min(X_Delta_Val, Math.min(Y_Delta_Val, Z_Delta_Val));
        }
        // calculate adjustable radius
        Quad_Width = MinDelta * Quad_WidthDeltaPercent / 100.0f;
        // don't want to be too large
        if(Quad_Width > Quad_WidthDeltaPercent / 100.0f)Quad_Width = Quad_WidthDeltaPercent / 100.0f;
        Quad_HalfWidth = Quad_Width / 2.0f;
       
         
        
        
        
        // go through each possible plane it could be on
        // nothing fancy, edges may have multiple planes drawn and that's OK for now
        // may double draw planes on edges, that's OK for now...check later for slow down
        
        // 8 corner points to draw
        
        
        
        
        
        
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        // now deal with vertex cases edges
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        
        
        
        
        
        //point 1 
        if(Is_X_Min&&Is_Y_Min&&Is_Z_Min){
            // 3 special quadrants to draw terminating at the point
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            // quad 1                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y + Quad_Width;
              // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));  
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X + Quad_Width;
              // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));   
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            
            //quad 3
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y + Quad_Width;
               // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));    
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
 
            return;
        
        }
        

        // point 2
        if(Is_X_Max&&Is_Y_Max&&Is_Z_Max){
            // 3 special quadrants to draw terminating at the point
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            // quad 1                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y - Quad_Width;
               // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));    
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X - Quad_Width;
                  // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));   
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y - Quad_Width;
                 // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));   
                                         
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));                                    
            
            return;
       
        }
        

         // point 3
        if(Is_X_Min&&Is_Y_Min&&Is_Z_Max){
                        // 3 special quadrants to draw terminating at the point
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            // quad 1   
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);

            //quad 2
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X;
            // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
     
            return;
          
        }
    
        // point 4
        if(Is_X_Min&&Is_Y_Max&&Is_Z_Min){
            
           
            
             // quad 1   
             // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X;
            // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            
            
            return;
        }
        

        // point 5
        if(Is_X_Max&&Is_Y_Min&&Is_Z_Min){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
             // quad 1   
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);

            //quad 2
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X;
            // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            return;
        }
        

        // point 6
        if(Is_X_Min&&Is_Y_Max&&Is_Z_Max){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
             // quad 1   
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X; 
            // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
             Z_Calc =Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
        }

        
        // point 7
        if(Is_X_Max&&Is_Y_Min&&Is_Z_Max){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
             // quad 1   
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
            X_Calc = X;
  // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
             Z_Calc =Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
        }
        

        // point 8
        if(Is_X_Max&&Is_Y_Max&&Is_Z_Min){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
             // quad 1   
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 2
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
            X_Calc = X;
            // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);
            
            //quad 3
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
            X_Calc = X - Quad_Width;
            Y_Calc = Y;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
        }
        
        
        
        
        
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        // now deal with edge cases edges
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        
        
        
        
        
        
        
        
        
        if(Is_Y_Max && Is_Z_Max && (!Is_X_Max) && (!Is_X_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        
        
        
        
        
        if(Is_X_Max && Is_Z_Max && (!Is_Y_Max) && (!Is_Y_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_Width; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - Quad_Width; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
         if(Is_X_Max && Is_Y_Max && (!Is_Z_Max) && (!Is_Z_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - Quad_Width; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_Width; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
//------------------------------------------------------------------------------
        if(Is_Y_Min && Is_Z_Min && (!Is_X_Max) && (!Is_X_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        
        
        
        
        
        if(Is_X_Min && Is_Z_Min && (!Is_Y_Max) && (!Is_Y_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_Width; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + Quad_Width; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        if(Is_X_Min && Is_Y_Min && (!Is_Z_Max) && (!Is_Z_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + Quad_Width; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_Width; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        //----------------------------------------------------------------------
        
        if(Is_Z_Max && Is_Y_Min && (!Is_X_Max) && (!Is_X_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        //----------------------------------------------------------------------
        
        
        if(Is_Z_Min && Is_Y_Max && (!Is_X_Max) && (!Is_X_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_HalfWidth; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        //----------------------------------------------------------------------
        
        
        if(Is_Z_Max && Is_X_Min && (!Is_Y_Max) && (!Is_Y_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + Quad_Width; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + Quad_Width; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        //----------------------------------------------------------------------
        
        
        if(Is_Z_Min && Is_X_Max && (!Is_Y_Max) && (!Is_Y_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - Quad_Width; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - Quad_Width; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_Width;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        //----------------------------------------------------------------------
        
        
        if(Is_X_Min && Is_Y_Max && (!Is_Z_Max) && (!Is_Z_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + Quad_Width; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + Quad_Width; 
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_Width;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        //----------------------------------------------------------------------
        
        
        if(Is_X_Max && Is_Y_Min && (!Is_Z_Max) && (!Is_Z_Min)){
            
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - Quad_Width; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - Quad_Width; 
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


            //quad 2 - z face
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                         
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_Width;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
            return;
            
        }
        
        
        //----------------------------------------------------------------------
        
         //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        // now deal with face cases two for each to make it show up better looking from both sides of the plane
        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        
        if(Is_X_Min){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
                
        // second---------------------------------------------------------------


            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
        
            return;
        }
        
        
        if(Is_X_Max){
             
           
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X + OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            // second---------------------------------------------------------------

             // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            X_Calc = X - OpenGLEngine.RenderSettings.Z_fight_delta; 
            Y_Calc = Y - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            return;
        }
        
        if(Is_Y_Min){
           
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

        // second---------------------------------------------------------------
        
        // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

        
        
            return;
        }
        
        
        if(Is_Y_Max){
            //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Y_Calc = Y + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

       // second---------------------------------------------------------------
       
       //quad 1 - y face
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Y_Calc = Y - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Z_Calc = Z + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

       
            return;
        }
        
        if(Is_Z_Min){
            
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

        // second---------------------------------------------------------------

// add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

            return;
        }
        
        
        if(Is_Z_Max){
            // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Z_Calc = Z + OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

        // second---------------------------------------------------------------

        // add a point into the list
            QuadPoints.add(new Vector3f[4]);                        
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[0] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
            
                               
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X + Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[1] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));
           
                                
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y - Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[2] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));

                             
            Z_Calc = Z - OpenGLEngine.RenderSettings.Z_fight_delta; 
            X_Calc = X - Quad_HalfWidth;
            Y_Calc = Y + Quad_HalfWidth;
             // input for transformation      
            InputVector.x = X_Calc;
            InputVector.y = Y_Calc;
            InputVector.z = Z_Calc;
            // transform according to arbitrary rotation matrix specified by user
            OutputVector = RotationMatrix.transform(InputVector);
            // transform according to rotation plan defined by selected atoms
            QuadPoints.get(QuadPoints.size()-1)[3] = new Vector3f(CurrentPlane.CalculateTransformedCoordinates(new Vector3f(OutputVector.x,OutputVector.y,OutputVector.z), true));


        }
                
    }
    
    

    
    // floating point math can be finicky and sometimes the last point isn't included in the loop,
    // this is probably due to rounding errors
    // include it if it within a small *range* of values, say + or - X percent (set at top of this file)
    private boolean QuitLoop(float itor, float MaxRange){        
        float AcceptableErrorValue = itor * AcceptableErrorPercent / 100;
        if(itor > (MaxRange + AcceptableErrorValue))return false;
        return true;        
    }
    // check if this is the last itoration that will happen
    private boolean WillQuitNext(float itor, float Delta, float MaxRange){
        
        return !QuitLoop(itor+Delta, MaxRange);                
    }
    

}