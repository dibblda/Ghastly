import java.util.Arrays;

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




public class MoleculeDisplay {

private boolean moleculeGeometryLoaded = false;
private double [][] internalAtomArray = null;
private int [][] internalBondArray = null;
private float [][][] internalBondGeometry = null;
private DisplayMode displayMode;
private boolean fullscreen = false;
 
// lighting parameters
private float shininess = 50.0f;
private float ambientLightData[] = { 0.2f, 0.2f, 0.2f, 1.0f };
private float diffuseLightData[] = { 0.8f, 0.8f, 0.8f, 1.0f };
private float specularLightData[] = { 0.30f, 0.30f, 0.30f, 1.0f };
private float positionLightData[] = { 30.0f, 30.0f, 20.0f, 1.0f }; 
private float matSpecularData[] = { 1.0f, 1.0f, 1.0f, 1.0f }; 


private FloatBuffer ambientLight;
private FloatBuffer diffuseLight; 
private FloatBuffer specularLight; 
private FloatBuffer positionLight; 
private FloatBuffer matSpecular;

// variable for the opengl window width and height, field of view
float windowWidth, windowHeight;
// field of view (in radians) (~45 degrees)
float fieldOfView = 0.785f;
//float pickScale = 1.0f;

// for picking algorithm based on ray tracing
Vector3f RayOrigin = new Vector3f();
Vector3f RayDirection = new Vector3f();
//maximum value for the parametric equation defining a line to get 
//the end and beginning points for a picker ray
// make large to ensure that it always crosses atom space (1000 angstroms here)
float ParametricTValue = 1000.0f;
boolean debug = false;
// end of picking algorithm
float zoom = 20;

// for arc ball camera 
 Matrix4f mat = new Matrix4f();
 // FloatBuffer for transferring matrices to OpenGL
 FloatBuffer fb = BufferUtils.createFloatBuffer(16);
 //FloatBuffer Pickerfb = BufferUtils.createFloatBuffer(16);
 ArcBallCamera cam = new ArcBallCamera();
// end arcball camera
 
private int colorMapArraySize = 97;
// covalent radii used to draw spheres for each AN
private double [] CovalentRadii = {0.00,0.32,0.28,1.29,0.96,0.84,0.76,0.71,0.66,0.57,0.58,
						   1.67,1.42,1.21,1.11,1.07,1.05,1.02,1.06,2.03,1.76,
						   1.71,1.61,1.54,1.40,1.40,1.32,1.26,1.24,1.32,1.22,
						   1.22,1.20,1.19,1.20,1.20,1.16,2.21,1.95,1.91,1.76,
						   1.65,1.55,1.48,1.47,1.43,1.40,1.46,1.45,1.43,1.39,
						   1.40,1.38,1.39,1.41,2.44,2.15,2.08,2.05,2.04,2.02,
						   1.99,1.99,1.99,1.97,1.95,1.93,1.93,1.90,1.90,1.88,
						   1.88,1.75,1.71,1.63,1.52,1.44,1.42,1.37,1.37,1.33,
						   1.46,1.47,1.48,1.40,1.50,1.50,2.60,2.21,2.15,2.07,
						   2.00,1.97,1.90,1.87,1.81,1.69};


private int SINGLE_BOND = 10;


private float atomScaling = 0.5f; 
private float atomSelectScaling = 0.7f;
private float originScaling = 0.3f;
private float bondScaling = 0.3f;


// default color map for each atom
private float [][] atomColorMap = new float[colorMapArraySize][3];
// color map for bonds
private float [] singleBondColorMap = {0.6f, 0.6f, 0.6f};


// variables for UI
private int [] atomSelectedArray = null;
private int [] atomHighlightedArrary = null;

public void PassMoleculeGeometry(double [][] atomArray, int [][] bondArray, float [][][] bondGeometry){
	assert(atomArray != null);
	assert(bondArray != null);
	assert(bondGeometry != null);

	internalAtomArray = atomArray;
	internalBondArray = bondArray;
	internalBondGeometry = bondGeometry;

	atomSelectedArray = new int [internalAtomArray.length];
	atomHighlightedArrary = new int [internalAtomArray.length];
	for(int itor = 0; itor < internalAtomArray.length; itor++){
		atomSelectedArray[itor] = 0;
		atomHighlightedArrary[itor] = 0;
	}
	
        //System.out.println("Made it here2 Geometry");
	moleculeGeometryLoaded = true;
}







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
        
       

        cam.setAlpha((float) Math.toRadians(-20));
        cam.setBeta((float) Math.toRadians(20));
        
        
        
       
	// start up the display
	init();

	
            
        // everything set up
        // loop and determine if moving model or selecting model
        // if selecting, enter selection mode then render mode
        // example:
        // http://relativity.net.au/gaming/java/ObjectSelection.html
        // 

        boolean renderSelection = false;
	while (!Display.isCloseRequested()) {
 	
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
                System.out.println("Mouse Picking");
                    SelectionInterface(Mouse.getX(), Mouse.getY());
                    debug = true;
                    renderSelection = true;
                    clickEvent=false;
                //release time >= 200 msec, go to the drag interface or just reset the click    
                }else if(((holdTimeEnd - holdTimeStart) / 1e6) >= 200.0){
                    clickEvent=false;
                  
                }
            }
             
            //look for a down mouse button >= 200 msec, must ba a drag
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
                zoom *= 1.1f;
                 System.out.println("DOWN " + zoom);
            }else if(dWheel > 0){
                zoom /= 1.1f;
                 System.out.println("UP " + zoom);
            }
            // implement zoom eventually
            cam.zoom(zoom);
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
            mat.setPerspective(fieldOfView, windowWidth / windowHeight, 0.1f, 100.0f).get(fb);
            glMatrixMode(GL_PROJECTION);          
            GL11.glLoadMatrix(fb);
             /*
             * Obtain the camera's view matrix and render molecule.
             */
            cam.viewMatrix(mat.identity()).get(fb);
            glMatrixMode(GL_MODELVIEW);
            GL11.glLoadMatrix(fb);                                    
            // move to the center
            /* possibly not needed?
            mat.translate(cam.centerMover.target).get(fb);
            GL11.glLoadMatrix(fb);
            */
            //end of new code                                                                                                   
            
            if(renderSelection == true)renderSelectionLine();                              
            RenderMolecule();		          
            Display.update();
           
           
               
            
            
            
	}
 
	Display.destroy();
}
 




private void doResize(int newWidth, int newHeight){
float aspectRatio = (float)newWidth / (float)newHeight;

 
  
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glLoadIdentity();
  GL11.glViewport(0, 0, newWidth, newHeight);
  GLU.gluPerspective(40.0f, (float)aspectRatio, 0.1f, 100.0f); 
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  //reset global variables used for picking
  windowWidth = newWidth;
  windowHeight = newHeight;
}





		
















// selction mode is deprected, implement ray tracing if possible

private void SelectionInterface(int mouse_x, int mouse_y){
   
    
    Vector2f PickerCoordinates = new Vector2f(mouse_x, mouse_y);
    int[] viewport = {0,0,(int)windowWidth, (int)windowHeight};
    
  
    // create a temporary matrix to put in both the projection and modelview matrices 
    // which are need to be multiplied together to backtrack the mouse location into 
    // the scene
    
    Matrix4f temp = new Matrix4f();
    // get the projection matrix
    temp.setPerspective(fieldOfView, windowWidth / windowHeight, 0.1f, 100.0f);
    // take the projection matrix and multiply it by the modelview matrix 
    cam.viewMatrix(temp).unprojectRay(PickerCoordinates, viewport, RayOrigin, RayDirection);
    
    
    
    RayDirection.normalize();
  
   
    
    TestAtomCollision();
    
    return;
}


private void TestAtomCollision(){
    
    // place to store all the atoms that intersect the mouse ray
    int numberHits = 0;
    int[] atomlist = new int[0];
    float LastR = 1e6f, TestR; // impossibly large number to start 
    int currentAtom = 0;
    
    // get closest point between sphere center and line defined by RayOrigin and RayDirection
    //see if the distance is less than the radius of the sphere
    
    //itorate over all atoms, find the hits
    for(int itor = 1; itor < atomSelectedArray.length; itor++){
       
        if(RayAtomCollides((float)internalAtomArray[itor][1], 
                           (float)internalAtomArray[itor][2],
                           (float)internalAtomArray[itor][3],
                           (float)CovalentRadii[(int)internalAtomArray[itor][0]])){
            numberHits++;              
            //test if the atom is any closer than the last atom entered, if so replace it
            TestR = SphereOriginDistance((float)internalAtomArray[itor][1], (float)internalAtomArray[itor][2], (float)internalAtomArray[itor][3]);
            System.out.println(TestR);
            //if closer, swap it out, else move on
            if(TestR <= LastR){
                currentAtom = itor;
                LastR = TestR;
            }
                                                                    
        };                                             
       
     }
  //Found the closest atom to the origin (and def. found one), just highlight (or unhighlight) for now, in the future this will be a big part of the GUI
    if(numberHits >= 1){
        if(atomSelectedArray[currentAtom] == 0){
            atomSelectedArray[currentAtom] = 1;
        }else{
            atomSelectedArray[currentAtom] = 0;
        }
    }
}

private boolean RayAtomCollides(float AtomX, float AtomY, float AtomZ, float radius){
    Vector3f LineStart = new Vector3f(RayDirection.x * ParametricTValue + RayOrigin.x,
                                      RayDirection.y * ParametricTValue + RayOrigin.y,  
                                      RayDirection.z * ParametricTValue + RayOrigin.z);
    Vector3f LineEnd = new Vector3f(RayDirection.x * (-ParametricTValue) + RayOrigin.x,
                                    RayDirection.y * (-ParametricTValue) + RayOrigin.y,  
                                    RayDirection.z * (-ParametricTValue) + RayOrigin.z);
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


void renderSelectionLine() {
    float start_x = 0, start_y = 0, start_z = 0;
    float end_x = 0, end_y = 0, end_z = 0;
   
           
    start_x = RayDirection.x * ParametricTValue + RayOrigin.x;
    start_y = RayDirection.y * ParametricTValue + RayOrigin.y;
    start_z = RayDirection.z * ParametricTValue + RayOrigin.z;
    
    end_x = RayDirection.x * (-ParametricTValue) + RayOrigin.x;
    end_y = RayDirection.y * (-ParametricTValue) + RayOrigin.y;
    end_z = RayDirection.z * (-ParametricTValue) + RayOrigin.z;
    
    //if(debug)System.out.println("SX: "+start_x+" SY: "+start_y+" SZ: "+start_z+" EX: "+end_x+" EY: "+end_y+" EZ: "+end_z);
    debug = false;
    // draw lines
    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor3f(1.0f, 0.0f, 0.0f);
        
    GL11.glVertex3f(start_x, start_y, start_z);
    GL11.glVertex3f(end_x, end_y, end_z);
        
    GL11.glEnd();
}



private void DrawSelectedCursor(){
	

    for(int itor = 1; itor < atomSelectedArray.length; itor++){
        if(atomSelectedArray[itor] == 1){



            GL11.glTranslatef((float)internalAtomArray[itor][1], (float)internalAtomArray[itor][2],  (float)internalAtomArray[itor][3]);

            GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
            GL11.glLoadName(itor);
            Sphere s = new Sphere();

            s.draw((float)(atomSelectScaling * CovalentRadii[(int)internalAtomArray[itor][0]]), 20, 20);

            GL11.glTranslatef(-1.0f * (float)internalAtomArray[itor][1],  -1.0f * (float)internalAtomArray[itor][2],  -1.0f * (float)internalAtomArray[itor][3]);

        }
    }

}









private void RenderMolecule(){

	RenderAtoms();
	RenderBonds();

	DrawSelectedCursor();

	return;

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

   		s.draw((float)(atomScaling * CovalentRadii[atomicNumber]), 16, 16);

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

	int debug = 0;

	for(A_itor = 1; A_itor < internalBondArray[0].length; A_itor++){
		for(B_itor = A_itor; B_itor < internalBondArray[0].length; B_itor++){
			// test to see if a single bond was formed	
			if(internalBondArray[A_itor][B_itor] == SINGLE_BOND){
			
				

				
				
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
				GL11.glColor3f(singleBondColorMap[0],
                                                singleBondColorMap[1],
						singleBondColorMap[2]);
				// load a default name one more than he number of atoms in molecule for all bonds
				GL11.glLoadName(internalAtomArray.length);
				Cylinder C = new Cylinder();
		   		
		   		// cylinder is scaled based on radius of hydrogen listed
                                //draw cylinder, startes at origin and length R
				C.draw((float)(bondScaling * CovalentRadii[1]), 
					(float)(bondScaling * CovalentRadii[1]), 
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

	// dummy cyllinder for axis marking
        //first red for Z
        GL11.glColor3f((float)1.0,(float)0.0,(float)0.0);
        Cylinder Z = new Cylinder();
        
        //draw cylinder, startes at origin and length R
        Z.draw(0.1f, 
                0.1f, 
                2.0f, 16, 16);
        //now rotate 90 degrees to place a new one on teh X-Axis
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
         GL11.glColor3f((float)0.0,(float)1.0,(float)0.0);
        Cylinder X = new Cylinder();
        
        //draw cylinder, startes at origin and length R
        X.draw(0.1f, 
                0.1f, 
                2.0f, 16, 16);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        
        // now rotate 90 degree to place a new one on the Y-axis
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        GL11.glColor3f((float)0.0,(float)0.0,(float)1.0);
        Cylinder Y = new Cylinder();
        
        //draw cylinder, startes at origin and length R
        Y.draw(0.1f, 
                0.1f, 
                2.0f, 16, 16);
        GL11.glRotatef(-90.0f, -1.0f, 0.0f, 0.0f);

}










void ColorMapArraySet(){
	int itor;
	

	// set standard colors for some atoms and something else for the rest

	for(itor = 0; itor < colorMapArraySize; itor++){
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

	// nitrogen brown
	atomColorMap[7][0] = (float)0.545;
	atomColorMap[7][1] = (float)0.271;
	atomColorMap[7][2] = (float)0.075;

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

	// Sulfur Yellow
	atomColorMap[16][0] = (float)1.0;
	atomColorMap[16][1] = (float)1.0;
	atomColorMap[16][2] = (float)0.0;

	// Chlorine Green
	atomColorMap[17][0] = (float)0.196;
	atomColorMap[17][1] = (float)0.804;
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




 private void initLightArrays() {

    ambientLight = BufferUtils.createFloatBuffer(4);
    ambientLight.put(ambientLightData[0]).put(ambientLightData[1]).put(ambientLightData[2]).put(ambientLightData[3]).flip();

    

    diffuseLight = BufferUtils.createFloatBuffer(4);
    diffuseLight.put(diffuseLightData[0]).put(diffuseLightData[1]).put(diffuseLightData[2]).put(diffuseLightData[3]).flip();

    

    specularLight = BufferUtils.createFloatBuffer(4);
    specularLight.put(specularLightData[0]).put(specularLightData[1]).put(specularLightData[2]).put(specularLightData[3]).flip();

    

    positionLight = BufferUtils.createFloatBuffer(4);
    positionLight.put(positionLightData[0]).put(positionLightData[1]).put(positionLightData[2]).put(positionLightData[3]).flip();



    matSpecular = BufferUtils.createFloatBuffer(4);
    matSpecular.put(matSpecularData[0]).put(matSpecularData[1]).put(matSpecularData[2]).put(matSpecularData[3]).flip();

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
            if (d[i].getWidth() == 640
                && d[i].getHeight() == 480
                && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
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
    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //  Background COLOR
    GL11.glClearDepth(1.0); // Depth Buffer Setup
    GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
    GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

    GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
    GL11.glLoadIdentity(); // Reset The Projection Matrix

    // Calculate The Aspect Ratio Of The Window

    windowWidth = displayMode.getWidth();
    windowHeight = displayMode.getHeight();
    GLU.gluPerspective(45.0f,windowWidth / windowHeight, 0.1f, 100.0f);
    GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

    // Really Nice Perspective Calculations
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);



    // adjust lighting
    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, matSpecular);
    GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);       
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambientLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, specularLight);
    GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, positionLight);


    // enable lighting
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_LIGHT0);


    // enable transparency blending
    GL11.glEnable (GL11.GL_BLEND); 
    GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

}






// EOF
}