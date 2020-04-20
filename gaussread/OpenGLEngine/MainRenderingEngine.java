package OpenGLEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.Cylinder;

// required for lighting
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
// required for mouse input
import org.lwjgl.input.Mouse;
// required for picking buffer
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.joml.camera.ArcBallCamera;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import org.lwjgl.util.vector.Vector4f;
import GhostAtom.*;
//import MeanPlane.*;
import java.util.ArrayList;
// divided up rendering functions and variable into additional static classes for

import OpenGLEngine.RenderSettings;
import OpenGLEngine.RenderNICSGrid;














public class MainRenderingEngine {

    
// parameters for obtaining the data from the read inb file for display
// variable for putting ghost atoms into a display list for quick rendering 
//------------------------------------------------------------------------------    
private boolean moleculeGeometryLoaded = false;
private float [][] internalAtomArray = null;
private int [][] internalBondArray = null;
private float [][][] internalBondGeometry = null;
GhostAtomSet Ghastly = null;
Object GhastlyLock;
// for opengl display list
Integer ListIndex = 0;

//------------------------------------------------------------------------------

// display and lighting parameters
//------------------------------------------------------------------------------
private DisplayMode displayMode;
private boolean fullscreen = false;
private FloatBuffer ambientLight;
private FloatBuffer diffuseLight; 
private FloatBuffer specularLight; 
private FloatBuffer positionLight; 
private FloatBuffer matSpecular;
//------------------------------------------------------------------------------

//window parameters and camera FOV
//------------------------------------------------------------------------------
// variable for the opengl window width and height, field of view
float windowWidth, windowHeight;

//------------------------------------------------------------------------------

// for picking algorithm based on ray tracing and 
// atom selection / plane variables
//------------------------------------------------------------------------------
Vector3f RayOrigin = new Vector3f();
Vector3f RayDirection = new Vector3f();

// variables for UI

// quick note, the input atomic coordinates start at 1 (0 is null) and have 
// a matrix size one larger than the total number of atoms
private int [] atomSelectedArray = null;
// storage of coordinates for the extra highlighted atom
Vector3f HighlightedAtom = null;

//------------------------------------------------------------------------------

// for arc ball camera, rotation of the model + zoom
//------------------------------------------------------------------------------
 Matrix4f mat = new Matrix4f();
 // FloatBuffer for transferring matrices to OpenGL
 FloatBuffer fb = BufferUtils.createFloatBuffer(16);
 //FloatBuffer Pickerfb = BufferUtils.createFloatBuffer(16);
 ArcBallCamera cam = new ArcBallCamera();
//------------------------------------------------------------------------------
 
// Variables for moving the model around in a plane parallel to the screen
//------------------------------------------------------------------------------
 boolean DragAtomSelected = false; 
Vector3f AtomSelectionVector = new Vector3f(0, 0, 0);
Vector3f RayOriginZero = new Vector3f();
Vector3f RayDirectionZero = new Vector3f();
boolean planeDefined = false;
Vector4f PlaneCoefficients = new Vector4f(0, 0, 0, 0);
// used to draw a vector from the mouse through the plane
Vector3f MouseRayOrigin = new Vector3f();
Vector3f MouseRayDirection = new Vector3f();
// the coordinates of the mouse in the plane
Vector3f MousePlaneCoordinates = new Vector3f(0, 0, 0);
Vector3f DragRayVector = new Vector3f(0, 0, 0);
//------------------------------------------------------------------------------

// variables for the size of atoms, scaling, and colors
//------------------------------------------------------------------------------
// default color map for each atom
private float [][] atomColorMap = new float[RenderSettings.colorMapArraySize][3];










public void PassInterfaceParameters(float [][] atomArray, int [][] bondArray, float [][][] bondGeometry, GhostAtomSet GhastlyPassed, Object GhastlyLockPassed){
	assert(atomArray != null);
	assert(bondArray != null);
	assert(bondGeometry != null);

	internalAtomArray = atomArray;
	internalBondArray = bondArray;
	internalBondGeometry = bondGeometry;
        GhastlyLock = GhastlyLockPassed;
	atomSelectedArray = new int [internalAtomArray.length];
       
	for(int itor = 0; itor < internalAtomArray.length; itor++){
		atomSelectedArray[itor] = 0;               
	}
	
   
         
	moleculeGeometryLoaded = true;
        
        Ghastly = GhastlyPassed;
}




// main loop--------------------------------------------------------------------
//------------------------------------------------------------------------------

public void start() {
   
        boolean clickEvent=false;
        float mouseXlast = 0, mouseX = 0, mouseYlast = 0, mouseY = 0;
        
 	// set colors in the array

	ColorMapArraySet();


        while(!moleculeGeometryLoaded){
            try{
            Thread.sleep(1);
            }catch(Exception e){
                
            }
		// Don't do anything until the geometry has been loaded
	}

        
        
        // code borrowed from ArcBallCameraDemo to use mouse to rotate the molecule
        // Remember the current time.
        long lastTime = System.nanoTime();
        long dragTime = System.nanoTime();
        long holdTimeStart = System.nanoTime();
        long holdTimeEnd = System.nanoTime();
        
       

        //cam.setAlpha((float) Math.toRadians(-20));
        //cam.setBeta((float) Math.toRadians(20));
        cam.setAlpha((float) Math.toRadians(0));
        cam.setBeta((float) Math.toRadians(0));
        
        
       
	// start up the display
	init();

        
        
        
        
        // everything set up
        // loop and determine if moving model or selecting model
        // if selecting, enter selection mode then render mode
        // example:
        // http://relativity.net.au/gaming/java/ObjectSelection.html
        // 
        boolean RightClickEvent = false;
        
	while (!Display.isCloseRequested()) {
            // wanted to only draw if the window is active this appears to not work'    
            //if(Display.isActive()){
            
                 //check for a resize event and do it
                if (Display.wasResized())doResize(Display.getWidth(), Display.getHeight());


                 // render OpenGL here
                GL11.glLoadIdentity();

                // OK, now read mouse input and decide if a rotaion is going to happen or a atom picking
                // picking an atom should be a mouse click less then 200 msec


                 // only select if mouse button was clicked
                 //mark the time from the very first mouse down event
                if(Mouse.isButtonDown(0) && clickEvent == false){
                    //get the initial time clicked
                    //only get the time if we haven't been through this loop before during the same click so the time isn't continually updating  
                    //during a mouse button down hold
                    holdTimeStart = System.nanoTime();
                    //set initial mouse coordinates on click
                    mouseXlast = Mouse.getX();
                    mouseYlast = Mouse.getY();
                    clickEvent=true;                                                         
                };  


                // mouse button released, check if less then 200 msec then execute, clear button clock state if >= 200 msec
                if(!Mouse.isButtonDown(0)){



                    holdTimeEnd = System.nanoTime();
                    //check if less than 200 msec, otherwise reset 
                    if((((holdTimeEnd - holdTimeStart) / 1e6) < 200.0) && clickEvent==true){ 
                        //check is selection is currently allowed
                        // this variable is set within the swing components

                            // only select if unlocked 
                            if(Ghastly.LockSelection == false){
                                SelectionInterface(Mouse.getX(), Mouse.getY(), false);
                            }
                            clickEvent=false;

                    //release time >= 200 msec, go to the drag interface or just reset the click    
                    }else if(((holdTimeEnd - holdTimeStart) / 1e6) >= 200.0){
                        clickEvent=false;

                    }
                }

                //look for a down mouse button >= 200 msec, must be a drag
                if(Mouse.isButtonDown(0)){
                    //check it it's been longer the 200 msec then calculate the move during this time slice
                    dragTime = System.nanoTime();
                    if(((dragTime - holdTimeStart) / 1e6) >= 200.0){


                        // get coordinates, calculate delta and modify camera alpha and beta
                        mouseX = Mouse.getX();
                        mouseY = Mouse.getY();
                        cam.setAlpha(cam.getAlpha() + Math.toRadians((mouseX - mouseXlast) * 0.3f));
                        cam.setBeta(cam.getBeta() + Math.toRadians((mouseYlast - mouseY) * 0.3f));
                        mouseXlast = mouseX;
                        mouseYlast = mouseY;

                    }
                }

                int dWheel = Mouse.getDWheel();
                if(dWheel < 0){
                    RenderSettings.zoom *= 1.1f;                 
                }else if(dWheel > 0){
                    RenderSettings.zoom /= 1.1f;                
                }
                // implement zoom 
                cam.zoom(RenderSettings.zoom);

                // see if the molecule is being dragged around in the XY plane
                // right mouse button?

                if(Mouse.isButtonDown(1)){

                    RightClickEvent = true;
                    SelectionInterface(Mouse.getX(), Mouse.getY(), true);

                }else if(!Mouse.isButtonDown(1)){
                    if(RightClickEvent == true){

                        RightClickEvent = false;
                        planeDefined = false;




                    }
                }

                //calculate time delta and update
                /* Compute delta time */
                long thisTime = System.nanoTime();
                float diff = (float) ((thisTime - lastTime) / 1E9);
                lastTime = thisTime;
                /* And let the camera make its update */
                cam.update(diff);



                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          

                GL11.glEnable(GL11.GL_COLOR_MATERIAL);


                // new code to render the molecule
                mat.setPerspective(RenderSettings.fieldOfView, windowWidth / windowHeight, RenderSettings.ZNear, RenderSettings.ZFar).get(fb);
                glMatrixMode(GL_PROJECTION);          
                GL11.glLoadMatrix(fb);
                 /*
                 * Obtain the camera's view matrix and render molecule.
                 */
                cam.viewMatrix(mat.identity()).get(fb);
                glMatrixMode(GL_MODELVIEW);
                GL11.glLoadMatrix(fb);                                    
               
                RenderMolecule();
                
                Display.update();

                // unselect all atoms if requested by swing interface
                synchronized(GhastlyLock){
                    if(Ghastly.UnselectAtoms){
                        UnselectAtoms();
                        Ghastly.UnselectAtoms = false;
                    }
                }
            //}
	}
 
	Display.destroy();
}
 

// rendering components---------------------------------------------------------
//------------------------------------------------------------------------------



private void RenderMolecule(){
                              
        GL11.glTranslatef(DragRayVector.x, DragRayVector.y, DragRayVector.z);
         // rendering order matters for transparency       
	RenderAtoms();        
	RenderBonds();
        AxisRender.RenderGlobalAxisMarker();
        RenderSelectedCursor();
        RenderGhostAtomsByNICSType();
        GL11.glTranslatef(-DragRayVector.x, -DragRayVector.y, -DragRayVector.z);
	return;
}
















// atom selection interface 
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

private void UnselectAtoms(){
    for(int itor = 1; itor < atomSelectedArray.length; itor++){
        if(atomSelectedArray[itor] != 0){
            // unselect the atom and remove from the current selection plane
            atomSelectedArray[itor] = 0;
            // unselected, remove from the list
            Ghastly.RemovePointFromPlane(itor);
        }
    }

}


// used ray tracing to determine the location of the mouse 
// depending if left or right mouse, dragging or picking will occur
private void SelectionInterface(int mouse_x, int mouse_y, boolean dragInterface){
   
    
    Vector2f PickerCoordinates = new Vector2f(mouse_x, mouse_y);
    int[] viewport = {0,0,(int)windowWidth, (int)windowHeight};
    
  
    // create a temporary matrix to put in both the projection and modelview matrices 
    // which are need to be multiplied together to backtrack the mouse location into 
    // the scene
        
    Matrix4f temp = new Matrix4f();
    
   
    // get the projection matrix
    temp.setPerspective(RenderSettings.fieldOfView, windowWidth / windowHeight, 0.1f, 100.0f);
    // take the projection matrix and multiply it by the modelview matrix 
    cam.viewMatrix(temp).translate(DragRayVector).unprojectRay(PickerCoordinates, viewport, RayOrigin, RayDirection);            
    RayDirection.normalize();
    
    if(dragInterface){
        //reset and recalculate
        temp.identity();
        temp.setPerspective(RenderSettings.fieldOfView, windowWidth / windowHeight, 0.1f, 100.0f);
        
        cam.viewMatrix(temp).unprojectRay(PickerCoordinates, viewport, MouseRayOrigin, MouseRayDirection);   
        MouseRayDirection.normalize();
    }
    
    //  calculate ray directly from the center of the screen, use to define the plane of the screen    
    PickerCoordinates.x = windowWidth / 2.0f;
    PickerCoordinates.y = windowHeight / 2.0f;
    //reset and recalculate
    temp.identity();
    temp.setPerspective(RenderSettings.fieldOfView, windowWidth / windowHeight, 0.1f, 100.0f);
    cam.viewMatrix(temp).unprojectRay(PickerCoordinates, viewport, RayOriginZero, RayDirectionZero);            
    RayDirectionZero.normalize();
    // now do atom collisions and drag calculations (if asked for)
    
   
    
    TestAtomCollision(dragInterface);
    
    return;
}

// find out which atoms intersect the picking ray and do some stuff depending on the current mode
private void TestAtomCollision(boolean dragInterface){
    
    // place to store all the atoms that intersect the mouse ray
    int numberHits = 0;
    
    float LastR = 1e6f, TestR; // impossibly large number to start 
    int currentAtom = 0;
    
    // get closest point between sphere center and line defined by RayOrigin and RayDirection
    //see if the distance is less than the radius of the sphere
    
    //itorate over all atoms, find the hits    
    for(int itor = 1; itor < atomSelectedArray.length; itor++){
       
        if(RayAtomCollides((float)internalAtomArray[itor][1], 
                           (float)internalAtomArray[itor][2],
                           (float)internalAtomArray[itor][3],
                           (float)RenderSettings.CovalentRadii[(int)internalAtomArray[itor][0]])){
            numberHits++;
            //test if the atom is any closer than the last atom entered, if so replace it
            TestR = SphereOriginDistance((float)internalAtomArray[itor][1], 
                                         (float)internalAtomArray[itor][2], 
                                         (float)internalAtomArray[itor][3]);
            
            //if closer, swap it out, else move on
            if(TestR <= LastR){
                currentAtom = itor;
                LastR = TestR;
            }
                                                                    
        };                                             
       
     }
   
  //Found the closest atom to the origin (and def. found one) add to plane if requested
  // only do this if for selection and not drag interface  
    if(!dragInterface){
        if(numberHits >= 1){
                                                            
            if(atomSelectedArray[currentAtom] == 0){
                // first make sure last atom is no longer highlighted (turn 2 into 1)
                for(int itor_refresh = 1; itor_refresh < atomSelectedArray.length; itor_refresh++){
                    if(atomSelectedArray[itor_refresh] == 2)atomSelectedArray[itor_refresh] = 1;
                }                                            
                
                // newly selected so give it a value of 2
                atomSelectedArray[currentAtom] = 2;
                
                // store coordinates
                HighlightedAtom = new Vector3f((float)internalAtomArray[currentAtom][1], (float)internalAtomArray[currentAtom][2], (float)internalAtomArray[currentAtom][3]);
                
                // add the atom to the selection plane
                // need to list whole name, clashes in the libraries
                org.joml.Vector4f NewPoint = new org.joml.Vector4f((float)internalAtomArray[currentAtom][1], (float)internalAtomArray[currentAtom][2], (float)internalAtomArray[currentAtom][3], (float)currentAtom);
                Ghastly.AddPointToPlane(NewPoint);                                                
                // set the new atom coordinates as the "X-axis" for plane calculations
                Ghastly.SetPlaneXAxisPoint(new Vector3f((float)internalAtomArray[currentAtom][1], 
                                           (float)internalAtomArray[currentAtom][2], 
                                           (float)internalAtomArray[currentAtom][3]));
            }else if(atomSelectedArray[currentAtom] == 2){
                // unselect the atom and remove from the current selection plane
                atomSelectedArray[currentAtom] = 0;
                // unselected, remove from the list
                Ghastly.RemovePointFromPlane(currentAtom);
                // as this was the most highlighted atom it must have been the set plane axis
                // atom so assume that we are removing it from the list and falling back one.
                // fall back to last "X-axis" point
                Ghastly.RemoveCurrentXAxisPoint();
                
            }else if(atomSelectedArray[currentAtom] == 1) {
                // unselect the currently extra-highlighted atom and choose this new one
                // no change to the plane selection but change the "X-Axis" to this one
                for(int itor_refresh = 1; itor_refresh < atomSelectedArray.length; itor_refresh++){
                    if(atomSelectedArray[itor_refresh] == 2)atomSelectedArray[itor_refresh] = 1;
                }
                atomSelectedArray[currentAtom] = 2;                
                // store coordinates
                HighlightedAtom = new Vector3f((float)internalAtomArray[currentAtom][1], (float)internalAtomArray[currentAtom][2], (float)internalAtomArray[currentAtom][3]);
                // reset the "X-Axis" point to this one
                Ghastly.SetPlaneXAxisPoint(HighlightedAtom);

            }
        }
          
    }else{
        // for dragging around the molecule on screen
        // first hit defines the drag plane, subsequent 
       // keeps the dragging on the plane, or it should anyways
        if((numberHits >= 1)&&(planeDefined == false)){

             
            // first rotate the atom  selected to match the screen location (not just the internal atom coordinates) 
            //Plus rotation!!!!


            Vector3f TempRotation = new Vector3f((float)internalAtomArray[currentAtom][1], (float)internalAtomArray[currentAtom][2], (float)internalAtomArray[currentAtom][3]);
            Vector3f TempAfterRotation = cam.RotateVector(TempRotation);
            
          
            AtomSelectionVector.x = TempAfterRotation.x;
            AtomSelectionVector.y = TempAfterRotation.y;
            AtomSelectionVector.z = TempAfterRotation.z;
                        
            
            // get the plane parallel to the screen that runs through the selected atom
            DefineScreenPlane();            
            planeDefined = true;
            
        }else if((numberHits >= 1)&&(planeDefined == true)){
            // find out where the screen intersect the current drag line
            
            SelectionVectorScreenIntersection();
            //update the drag line to match 
            DragRayVector.x = MousePlaneCoordinates.x - AtomSelectionVector.x;
            DragRayVector.y = MousePlaneCoordinates.y - AtomSelectionVector.y;
            DragRayVector.z = MousePlaneCoordinates.z - AtomSelectionVector.z;
          
        }
        
    }
}

private boolean RayAtomCollides(float AtomX, float AtomY, float AtomZ, float radius){
    Vector3f LineStart = new Vector3f();
    Vector3f LineEnd = new Vector3f();


    LineStart.x = RayDirection.x * RenderSettings.ParametricTValue + RayOrigin.x;
    LineStart.y = RayDirection.y * RenderSettings.ParametricTValue + RayOrigin.y;
    LineStart.z = RayDirection.z * RenderSettings.ParametricTValue + RayOrigin.z;
    LineEnd.x = RayDirection.x * (-RenderSettings.ParametricTValue) + RayOrigin.x;
    LineEnd.y = RayDirection.y * (-RenderSettings.ParametricTValue) + RayOrigin.y;
    LineEnd.z = RayDirection.z * (-RenderSettings.ParametricTValue) + RayOrigin.z;
                                         
        
   
    Vector3f AtomCoordinate = new Vector3f(AtomX, AtomY, AtomZ);
    
    Vector3f X0_X1 = new Vector3f();
    Vector3f X0_X2 = new Vector3f();
    Vector3f X2_X1 = new Vector3f();
    Vector3f CrossProduct = new Vector3f();
    float distance;
    
    //http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
    
    AtomCoordinate.sub(LineStart, X0_X1);
    AtomCoordinate.sub(LineEnd, X0_X2);
    LineEnd.sub(LineStart, X2_X1);
    X0_X1.cross(X0_X2, CrossProduct);
    
    distance = CrossProduct.length() / X2_X1.length();
    
    if(distance <= radius)return true;
    
    return false;
}

private float SphereOriginDistance(float AtomX, float AtomY, float AtomZ){
    
    return (float)Math.sqrt(Math.pow((AtomX - RayOrigin.x), 2) + Math.pow((AtomY - RayOrigin.y),2) + Math.pow((AtomZ - RayOrigin.z), 2));
    
}


void DefineScreenPlane(){
    PlaneCoefficients.x = RayDirectionZero.x;
    PlaneCoefficients.y = RayDirectionZero.y;
    PlaneCoefficients.z = RayDirectionZero.z;
    PlaneCoefficients.w =  -RayDirectionZero.x*(AtomSelectionVector.x + DragRayVector.x) - RayDirectionZero.y*(AtomSelectionVector.y + DragRayVector.y) - RayDirectionZero.z*(AtomSelectionVector.z + DragRayVector.z);
}

void SelectionVectorScreenIntersection(){
    
    float numerator;
    float denominator;
    float ParametricT;
    numerator = -(MouseRayOrigin.x * PlaneCoefficients.x + MouseRayOrigin.y * PlaneCoefficients.y + MouseRayOrigin.z * PlaneCoefficients.z + PlaneCoefficients.w);
    denominator = PlaneCoefficients.x * MouseRayDirection.x + PlaneCoefficients.y * MouseRayDirection.y + PlaneCoefficients.z * MouseRayDirection.z;
    ParametricT = numerator / denominator;
    MousePlaneCoordinates.x = MouseRayDirection.x * ParametricT + MouseRayOrigin.x;
    MousePlaneCoordinates.y = MouseRayDirection.y * ParametricT + MouseRayOrigin.y;
    MousePlaneCoordinates.z = MouseRayDirection.z * ParametricT + MouseRayOrigin.z;
}






// rendering functions
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

private void RenderSelectedCursor(){
	

    for(int itor = 1; itor < atomSelectedArray.length; itor++){
        if(atomSelectedArray[itor] > 0){


            // differenct colors depending on if a highlighted atom or a selected atom
            GL11.glTranslatef((float)internalAtomArray[itor][1], (float)internalAtomArray[itor][2],  (float)internalAtomArray[itor][3]);
            if(atomSelectedArray[itor] == 1){
                // modded this because of transparency issues but changing display order solved the problem, not needed but kept for flexibility
                if(Ghastly.RenderHighlightCursorSolid){
                    GL11.glColor4f(RenderSettings.HighlightedAtomColor.x, RenderSettings.HighlightedAtomColor.y, RenderSettings.SolidHighlightedAtomColor.z, RenderSettings.SolidHighlightedAtomColor.w);
                }else{
                    GL11.glColor4f(RenderSettings.HighlightedAtomColor.x, RenderSettings.HighlightedAtomColor.y, RenderSettings.HighlightedAtomColor.z, RenderSettings.HighlightedAtomColor.w);
                }
                GL11.glLoadName(itor);
                Sphere s = new Sphere();
                s.draw((float)(RenderSettings.atomSelectScaling * RenderSettings.CovalentRadii[(int)internalAtomArray[itor][0]]), 20, 20);
            }else if(atomSelectedArray[itor] == 2  ){
                // modded this because of transparency issues but changing display order solved the problem, not needed but kept for flexibility
                if(Ghastly.RenderSelectionCursorSolid){
                    GL11.glColor4f(RenderSettings.SolidSelectedAtomColor.x, RenderSettings.SolidSelectedAtomColor.y, RenderSettings.SolidSelectedAtomColor.z, RenderSettings.SolidSelectedAtomColor.w);
                }else{
                    GL11.glColor4f(RenderSettings.SelectedAtomColor.x, RenderSettings.SelectedAtomColor.y, RenderSettings.SelectedAtomColor.z, RenderSettings.SelectedAtomColor.w);
                }
                
                GL11.glLoadName(itor);
                Sphere s = new Sphere();
                s.draw((float)(RenderSettings.atomSelectScaling * RenderSettings.CovalentRadii[(int)internalAtomArray[itor][0]]), 20, 20);
            }
            GL11.glTranslatef(-1.0f * (float)internalAtomArray[itor][1],  -1.0f * (float)internalAtomArray[itor][2],  -1.0f * (float)internalAtomArray[itor][3]);

        }
    }

}




// render each type of ghost atom potentially by a different method
// rendering large number of grid atoms with everything else, at least 1000 is taking up too much 
// rendering time. Came up with a different solution to render them.
private void RenderGhostAtomsByNICSType(){
    ArrayList<GhostAtomType> GhostTypeSetList;
    ArrayList<GhostAtom> GhostAtomListToRender;
    GhostAtomType ProposedGhostTypeSet;
    
    // first see if displaying a proposed set
    if(Ghastly.DisplayProposed){        
        ProposedGhostTypeSet = Ghastly.GetProposedGhostAtomType();
        if(ProposedGhostTypeSet == null) return;
        // should be OK, render it depending on the type
        if(ProposedGhostTypeSet.GhostSetType() < 2){
            RenderGenericGhostAtomList.RenderSimpleProposedGhostAtoms(Ghastly.GetProposedAtoms(), GhastlyLock);
        }else if(ProposedGhostTypeSet.GhostSetType() == 2){
            RenderNICSScan.RenderScanProposedGhostAtoms((NICS_Scan)ProposedGhostTypeSet, GhastlyLock);            
        }else if(ProposedGhostTypeSet.GhostSetType() == 3){
            RenderNICSGrid.RenderGridProposedGhostAtoms((NICS_Grid)ProposedGhostTypeSet, GhastlyLock);
        }
        return;
        // not a proposed set, step through each ghost atom type and pick
        // render type that is appropriate
        // when originally rendering grids the total number of atoms can be *huge* ( > 1000 easily)
        // so the whole program slows when rendering everything the same way together
        // modify this so different types of rendering can be handled individually depending on the NICS type
        
    }else{       
        GhostTypeSetList = Ghastly.GetCurrentGhostAtomTypes();
        if(GhostTypeSetList == null) return;
        if(GhostTypeSetList.isEmpty()) return;
        for(int itor = 0; itor < GhostTypeSetList.size(); itor++){            
            GhostAtomListToRender = GhostTypeSetList.get(itor).GetAtomList();
            if(GhostTypeSetList.get(itor).GhostSetType() < 2){
                RenderGenericGhostAtomList.RenderSimpleGhostAtoms(GhostAtomListToRender, GhastlyLock);
            }else if(GhostTypeSetList.get(itor).GhostSetType() == 2){
                RenderNICSScan.RenderScanGhostAtoms((NICS_Scan)GhostTypeSetList.get(itor), GhastlyLock);
               
            }else if(GhostTypeSetList.get(itor).GhostSetType() == 3){
                RenderNICSGrid.RenderGridGhostAtoms((NICS_Grid)GhostTypeSetList.get(itor), GhastlyLock);
            }
        }
    }
    
}





private void RenderAtoms(){

	int itor, atomicNumber;
	
	for(itor = 1; itor < internalAtomArray.length; itor++){

		atomicNumber = (int)internalAtomArray[itor][0];
		
		
   		GL11.glTranslatef((float)internalAtomArray[itor][1], 
   					  	  (float)internalAtomArray[itor][2], 
   					  	  (float)internalAtomArray[itor][3]);

		GL11.glColor3f(atomColorMap[atomicNumber][0],
					   atomColorMap[atomicNumber][1],
					   atomColorMap[atomicNumber][2]);
		// associate the drawn sphere with the atom number
		GL11.glLoadName(itor);
	
		Sphere s = new Sphere();

   		s.draw((float)(RenderSettings.atomScaling * RenderSettings.CovalentRadii[atomicNumber]), 16, 16);

   		// go back to where you started for next atom addition
		GL11.glTranslatef(-1.0f * (float)internalAtomArray[itor][1], 
   					  	  -1.0f * (float)internalAtomArray[itor][2], 
   					  	  -1.0f * (float)internalAtomArray[itor][3]);

   	
	}

	return;
}

private void RenderBonds(){
	 RenderSingleBonds();
	return;
}


//glRotatef(xrot,1.0f,0.0f,0.0f);
//singleBondColorMap
// third [] represent (Origin X, Origin Y, Origin Z, R, ThetaX, ThetaY, Theta Z, crossZI, crossZJ, crossZK) in [0] ... [9]

private void RenderSingleBonds(){
	//  draw single bonds from array.  Itorate over symmetric bond array and if bond = SINGLE_BOND found, 
	//  calculate the geometry of the bond and draw it
	int A_itor, B_itor;

	

	for(A_itor = 1; A_itor < internalBondArray[0].length; A_itor++){
		for(B_itor = A_itor; B_itor < internalBondArray[0].length; B_itor++){
			// test to see if a single bond was formed	
			if(internalBondArray[A_itor][B_itor] == RenderSettings.SINGLE_BOND){
			
				

				
				
// third [] represent (Origin X, Origin Y, Origin Z, R, Theta, crossZI, crossZJ, crossZK, Xaxis)
				GL11.glTranslatef((float)internalBondGeometry[A_itor][B_itor][0], 
                                                    (float)internalBondGeometry[A_itor][B_itor][1], 
                                                    (float)internalBondGeometry[A_itor][B_itor][2]);
                                //vector is only in the XY-plane
				if(internalBondGeometry[A_itor][B_itor][8] < 0){                                    
                                        // initially rotate the along the Y-axis (Up) with the Z axis (out at user) rotated to the X axis (right)
                                        // (moving the cylinder into the XY-Plane)
			   		GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			   		// then rotate by theta calculated along the negative X-axis			
				   	GL11.glRotatef((float)internalBondGeometry[A_itor][B_itor][4], 
				   				   -1.0f, 0.0f, 0.0f);
                                        //GL11.glColor3f((float)1.0,(float)0.0,(float)0.0);
                                // vector exists in Z space
			   	}else{
			   		GL11.glRotatef((float)internalBondGeometry[A_itor][B_itor][4], 
                                                        (float)internalBondGeometry[A_itor][B_itor][5],
			   				(float)internalBondGeometry[A_itor][B_itor][6],
			   				(float)internalBondGeometry[A_itor][B_itor][7]);
                                        //GL11.glColor3f((float)0.0,(float)1.0,(float)0.0);
			   	}
				GL11.glColor3f(RenderSettings.singleBondColorMap[0],
                                                RenderSettings.singleBondColorMap[1],
						RenderSettings.singleBondColorMap[2]);
				// load a default name one more than he number of atoms in molecule for all bonds
				GL11.glLoadName(internalAtomArray.length);
				Cylinder C = new Cylinder();
		   		
		   		// cylinder is scaled based on radius of hydrogen listed
                                //draw cylinder, startes at origin and length R
				C.draw((float)(RenderSettings.bondScaling * RenderSettings.CovalentRadii[1]), 
					(float)(RenderSettings.bondScaling * RenderSettings.CovalentRadii[1]), 
					internalBondGeometry[A_itor][B_itor][3], 16, 16);
		   		


				// step backwards to get to starting point

				if(internalBondGeometry[A_itor][B_itor][8] < 0){

					GL11.glRotatef(-1.0f * (float)internalBondGeometry[A_itor][B_itor][4], 
				   				   -1.0f, 0.0f, 0.0f);

			   		GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
			   					
				   
				   				  
			   	}else{
			   		GL11.glRotatef(-1.0f * (float)internalBondGeometry[A_itor][B_itor][4], 
			   					   (float)internalBondGeometry[A_itor][B_itor][5],
			   					   (float)internalBondGeometry[A_itor][B_itor][6],
			   					   (float)internalBondGeometry[A_itor][B_itor][7]);

			   	}

				GL11.glTranslatef(-1.0f * (float)internalBondGeometry[A_itor][B_itor][0], 
			   					  -1.0f * (float)internalBondGeometry[A_itor][B_itor][1], 
			   					  -1.0f * (float)internalBondGeometry[A_itor][B_itor][2]);


		   
			}
		}
	}

	

}











// setup functions
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------



void ColorMapArraySet(){
	int itor;
	

	// set standard colors for some atoms and something else for the rest

	for(itor = 0; itor < RenderSettings.colorMapArraySize; itor++){
		if((itor == 1) || (itor == 5) || (itor == 6) || 
		   (itor == 7) || (itor == 8) || (itor == 9) ||
		   (itor == 14) || (itor == 15) || (itor == 16) ||
		   (itor == 17) || (itor == 35) || (itor == 53)) continue;

			// cyan
		atomColorMap[itor][0] = (float)0.0;
		atomColorMap[itor][1] = (float)1.0;
		atomColorMap[itor][2] = (float)1.0;
	}
	// hydrogen light gray
	atomColorMap[1][0] = (float)0.827;
	atomColorMap[1][1] = (float)0.827;
	atomColorMap[1][2] = (float)0.827;

	// boron pink
	atomColorMap[5][0] = (float)1.0;
	atomColorMap[5][1] = (float)0.753;
	atomColorMap[5][2] = (float)0.796;
	
	// carbon dark gray
	atomColorMap[6][0] = (float)0.235;
	atomColorMap[6][1] = (float)0.235;
	atomColorMap[6][2] = (float)0.235;

	// nitrogen blue
	atomColorMap[7][0] = (float)0.0;
	atomColorMap[7][1] = (float)0.0;
	atomColorMap[7][2] = (float)1.0;

	// oxygen red
	atomColorMap[8][0] = (float)1.0;
	atomColorMap[8][1] = (float)0.0;
	atomColorMap[8][2] = (float)0.0;

	// fluorine violet
	atomColorMap[9][0] = (float)0.561;
	atomColorMap[9][1] = (float)0.0;
	atomColorMap[9][2] = (float)1.0;

	// silicon blue
	atomColorMap[14][0] = (float)0.0;
	atomColorMap[14][1] = (float)0.0;
	atomColorMap[14][2] = (float)1.0;

	// Phosphorus Orange
	atomColorMap[15][0] = (float)255.0;
	atomColorMap[15][1] = (float)140.0;
	atomColorMap[15][2] = (float)0.0;
 
	atomColorMap[17][2] = (float)0.196;

	// Bromine Firebrick
	atomColorMap[35][0] = (float)0.698;
	atomColorMap[35][1] = (float)0.133;
	atomColorMap[35][2] = (float)0.133;

	// Iodine Purple
	atomColorMap[53][0] = (float)0.627;
	atomColorMap[53][1] = (float)0.125;
	atomColorMap[53][2] = (float)0.941;
	
}

// openGL related functions not related to actual drawing-----------------------
//------------------------------------------------------------------------------


private void doResize(int newWidth, int newHeight){
float aspectRatio = (float)newWidth / (float)newHeight;
   
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glLoadIdentity();
  GL11.glViewport(0, 0, newWidth, newHeight);
  GLU.gluPerspective(45.0f, (float)aspectRatio, RenderSettings.ZNear, RenderSettings.ZFar); 
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  //reset global variables used for picking
  windowWidth = newWidth;
  windowHeight = newHeight;
}



 private void initLightArrays() {

    ambientLight = BufferUtils.createFloatBuffer(4);
    ambientLight.put(RenderSettings.ambientLightData[0]).put(RenderSettings.ambientLightData[1]).put(RenderSettings.ambientLightData[2]).put(RenderSettings.ambientLightData[3]).flip();

    

    diffuseLight = BufferUtils.createFloatBuffer(4);
    diffuseLight.put(RenderSettings.diffuseLightData[0]).put(RenderSettings.diffuseLightData[1]).put(RenderSettings.diffuseLightData[2]).put(RenderSettings.diffuseLightData[3]).flip();

    

    specularLight = BufferUtils.createFloatBuffer(4);
    specularLight.put(RenderSettings.specularLightData[0]).put(RenderSettings.specularLightData[1]).put(RenderSettings.specularLightData[2]).put(RenderSettings.specularLightData[3]).flip();

    

    positionLight = BufferUtils.createFloatBuffer(4);
    positionLight.put(RenderSettings.positionLightData[0]).put(RenderSettings.positionLightData[1]).put(RenderSettings.positionLightData[2]).put(RenderSettings.positionLightData[3]).flip();



    matSpecular = BufferUtils.createFloatBuffer(4);
    matSpecular.put(RenderSettings.matSpecularData[0]).put(RenderSettings.matSpecularData[1]).put(RenderSettings.matSpecularData[2]).put(RenderSettings.matSpecularData[3]).flip();

    return;
}




private void init(){
	createWindow();
	initOpenGl();
	return;
}


private void createWindow(){

	try {

        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            
            //System.out.println("Width: " + d[i].getWidth() + " Height: " + d[i].getHeight() + " BPP: " + d[i].getBitsPerPixel());
            
            if (d[i].getWidth() == 800
                && d[i].getHeight() == 600
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                //System.out.println("Hello");
                break;
                
            }
            
        }
        
        Display.setDisplayMode(displayMode);
        Display.setTitle("Molecule Render Window");
        Display.setResizable(true);
        Display.create();
        
	} catch (LWJGLException e) {
		e.printStackTrace();
		System.exit(0);
	}
 	

	return;
}

private void initOpenGl(){
	
	
    initLightArrays();

    GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
    GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
    GL11.glClearColor(RenderSettings.BackgroundColor.x, RenderSettings.BackgroundColor.y, RenderSettings.BackgroundColor.z, RenderSettings.BackgroundColor.w); //  Background COLOR
    GL11.glClearDepth(1.0); // Depth Buffer Setup
    GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
    GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do
    //GL11.glDepthRange(0, 10000);
    GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
    GL11.glLoadIdentity(); // Reset The Projection Matrix

    // Calculate The Aspect Ratio Of The Window

    windowWidth = displayMode.getWidth();
    windowHeight = displayMode.getHeight();
    //GLU.gluPerspective(45.0f,windowWidth / windowHeight, ZNear, ZFar);
    GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

    // Really Nice Perspective Calculations
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

    

    // adjust lighting
    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, matSpecular);
    GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, RenderSettings.shininess);       
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambientLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, specularLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, positionLight);


    // enable lighting
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_LIGHT0);


    // enable transparency blending
    GL11.glEnable(GL11.GL_BLEND); 
    // test of "order independant transparency"
    //GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_SRC_COLOR);  
    //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
  
    
    
    
    // original function call
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
}




















//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//----------------deprecated code, saved but unused for now---------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------


/*


private void RenderGhostAtoms(){
    ArrayList<GhostAtom> GhostAtomListToRender;
    GhostAtom SingleGhostAtom;
    synchronized(GhastlyLock){
        // in display proposed mode                                                      
            // only display proposed
        if(Ghastly.DisplayProposed){
            GhostAtomListToRender = Ghastly.GetProposedAtoms();
            if(GhostAtomListToRender == null) return;
            if(GhostAtomListToRender.isEmpty())return;       
            for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                SingleGhostAtom = GhostAtomListToRender.get(itor);
                GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                // need to higlight atoms if they are
                if(SingleGhostAtom.HighlightLevel == 0){
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    GL11.glLoadName(itor + 10000);

                    Sphere s = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        s.draw(0.2f, 20, 20);
                    }else{
                        s.draw(SingleGhostAtom.radius, 20, 20);
                    }
                }else if(SingleGhostAtom.HighlightLevel == 1){
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    } 
                    GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }else{
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    } 
                    GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }


                GL11.glTranslatef(-SingleGhostAtom.x, -SingleGhostAtom.y, -SingleGhostAtom.z);
            }

        // in normal mode, display all    
        }else{

            if(Ghastly.GetAtoms().isEmpty())return;   

            GhostAtomListToRender = Ghastly.GetAtoms();
            if(GhostAtomListToRender == null) return;
            if(GhostAtomListToRender.isEmpty())return;  

            for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                SingleGhostAtom = GhostAtomListToRender.get(itor);
                GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                // need to higlight atoms if they are
                if(SingleGhostAtom.HighlightLevel == 0){
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glLoadName(itor + 10000);
                    Sphere s = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        s.draw(0.2f, 20, 20);
                    }else{
                        s.draw(SingleGhostAtom.radius, 20, 20);
                    }
                }else if(SingleGhostAtom.HighlightLevel == 1){
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    }                                    
                    GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }else{
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    }
                    GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }
                GL11.glTranslatef(-SingleGhostAtom.x, -SingleGhostAtom.y, -SingleGhostAtom.z);
            }
        }                               
    }
    
}






private void RenderMouseInPlace(){
    
    GL11.glTranslatef(MousePlaneCoordinates.x, MousePlaneCoordinates.y,  MousePlaneCoordinates.z);

    GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
    
    Sphere s = new Sphere();

    s.draw(1.0f, 20, 20);

    GL11.glTranslatef(-MousePlaneCoordinates.x, -MousePlaneCoordinates.y,  -MousePlaneCoordinates.z);           
                               
}



private void RenderNormalPlane(){
    
    // http://mathworld.wolfram.com/Plane.html
    
    
    float X_1, X_2, X_3, X_4;
    float Y_1, Y_2, Y_3, Y_4;
    float Z_1, Z_2, Z_3, Z_4;
    float A_P, B_P, C_P, D_P;
    X_1 = -100.0f;
    X_2 = -100.0f;
    X_3 = 100.0f;
    X_4 = 100.0f;
    
    Y_1 = -100.0f;
    Y_2 = 100.0f;
    Y_3 = 100.0f;
    Y_4 = -100.0f;
    
    // variable defining the plane
    // the plance is parralell to the screen, normal vector direction discovered by deprojection from center of screen
    // the plane is specified to be the incident with the center of the selected atom
    
    A_P = RayDirectionZero.x;
    B_P = RayDirectionZero.y;
    C_P = RayDirectionZero.z;
    D_P = -RayDirectionZero.x*(AtomSelectionVector.x + DragRayVector.x) - RayDirectionZero.y*(AtomSelectionVector.y + DragRayVector.y) - RayDirectionZero.z*(AtomSelectionVector.z + DragRayVector.z);
    
    Z_1 = (-D_P - B_P * Y_1 - A_P * X_1) / C_P;
    Z_2 = (-D_P - B_P * Y_2 - A_P * X_2) / C_P;
    Z_3 = (-D_P - B_P * Y_3 - A_P * X_3) / C_P;
    Z_4 = (-D_P - B_P * Y_4 - A_P * X_4) / C_P;
    
    
    GL11.glBegin(GL11.GL_QUADS);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
    GL11.glVertex3f(X_1, Y_1, Z_1);
    GL11.glVertex3f(X_2, Y_2, Z_2);
    GL11.glVertex3f(X_3, Y_3, Z_3);
    GL11.glVertex3f(X_4, Y_4, Z_4);
    GL11.glDisable(GL11.GL_BLEND);
    
    
    GL11.glEnd();
   
}


private void RenderPCAPlane(){
    synchronized(GhastlyLock){ 
        if(!Ghastly.PlaneCalculated()){
            
            return;
        } 

        org.joml.Vector4f SelPlaneEq = Ghastly.GetPlaneEquation();
        // http://mathworld.wolfram.com/Plane.html
    
  

        float X_1, X_2, X_3, X_4;
        float Y_1, Y_2, Y_3, Y_4;
        float Z_1, Z_2, Z_3, Z_4;
        float A_P, B_P, C_P, D_P;
        X_1 = -100.0f;
        X_2 = -100.0f;
        X_3 = 100.0f;
        X_4 = 100.0f;

        Y_1 = -100.0f;
        Y_2 = 100.0f;
        Y_3 = 100.0f;
        Y_4 = -100.0f;

        // variable defining the plane
        // the plance is parralell to the screen, normal vector direction discovered by deprojection from center of screen
        // the plane is specified to be the incident with the center of the selected atom

        A_P = SelPlaneEq.x;
        B_P = SelPlaneEq.y;
        C_P = SelPlaneEq.z;
        D_P = SelPlaneEq.w;

        Z_1 = (-D_P - B_P * Y_1 - A_P * X_1) / C_P;
        Z_2 = (-D_P - B_P * Y_2 - A_P * X_2) / C_P;
        Z_3 = (-D_P - B_P * Y_3 - A_P * X_3) / C_P;
        Z_4 = (-D_P - B_P * Y_4 - A_P * X_4) / C_P;


        GL11.glBegin(GL11.GL_QUADS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
        GL11.glVertex3f(X_1, Y_1, Z_1);
        GL11.glVertex3f(X_2, Y_2, Z_2);
        GL11.glVertex3f(X_3, Y_3, Z_3);
        GL11.glVertex3f(X_4, Y_4, Z_4);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnd();
    
    
    }
    
}

void renderSelectionLine() {
    float start_x = 0, start_y = 0, start_z = 0;
    float end_x = 0, end_y = 0, end_z = 0;
   
    
    float start_x0 = 0, start_y0 = 0, start_z0 = 0;
    float end_x0 = 0, end_y0 = 0, end_z0 = 0;
    
    
    start_x = RayDirection.x * ParametricTValue + RayOrigin.x;
    start_y = RayDirection.y * ParametricTValue + RayOrigin.y;
    start_z = RayDirection.z * ParametricTValue + RayOrigin.z;

    end_x = RayDirection.x * (-ParametricTValue) + RayOrigin.x;
    end_y = RayDirection.y * (-ParametricTValue) + RayOrigin.y;
    end_z = RayDirection.z * (-ParametricTValue) + RayOrigin.z;
   
    start_x0 = RayDirectionZero.x * ParametricTValue + RayOriginZero.x;
    start_y0 = RayDirectionZero.y * ParametricTValue + RayOriginZero.y;
    start_z0 = RayDirectionZero.z * ParametricTValue + RayOriginZero.z;

    end_x0 = RayDirectionZero.x * (-ParametricTValue) + RayOriginZero.x;
    end_y0 = RayDirectionZero.y * (-ParametricTValue) + RayOriginZero.y;
    end_z0 = RayDirectionZero.z * (-ParametricTValue) + RayOriginZero.z;
   
    
   
    
    
    
    // draw lines
    GL11.glBegin(GL11.GL_LINES);
   // selection line    
    GL11.glColor3f(1.0f, 0.0f, 0.0f); 
    GL11.glVertex3f(start_x, start_y, start_z);
    GL11.glVertex3f(end_x, end_y, end_z);
    //camera center line
    GL11.glColor3f(0.0f, 1.0f, 0.0f); 
    GL11.glVertex3f(start_x0, start_y0, start_z0);
    GL11.glVertex3f(end_x0, end_y0, end_z0);
   
    GL11.glEnd();



}

*/
// EOF
}