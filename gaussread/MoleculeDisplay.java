import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Util;
// required for lighting
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
// required for mouse input
import org.lwjgl.input.Mouse;
// required for picking buffer
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.joml.camera.ArcBallCamera;




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

// variable for the opengl window width and height
float windowWidth, windowHeight;
float pickScale = 1.0f;


// for arc ball camera 

 ArcBallCamera cam = new ArcBallCamera();

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
private int DOUBLE_BOND = 20;
private int TRIPLE_BOND = 30;
private int DATIVE_BOND = 5;
private int DOUBLE_RESONANCE_BOND = 15;

private float atomScaling = 0.5f; 
private float atomSelectScaling = 0.7f;
private float originScaling = 0.3f;
private float bondScaling = 0.3f;


// default color map for each atom
private float [][] atomColorMap = new float[colorMapArraySize][3];
// color map for bonds
private float [] singleBondColorMap = {0.6f, 0.6f, 0.6f};


// variables for UI
private boolean lastMouseDown = false;
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
	float mult = 1.0f;
 	// set colors in the array

	ColorMapArraySet();


        while(!moleculeGeometryLoaded){
            try{
            Thread.sleep(1);
            }catch(Exception e){
                
            }
		// Don't do anything until the geometry has been loaded
	}

	
       
	// start up the display
	init();

	
 	


	while (!Display.isCloseRequested()) {
 	
            
            
            // render OpenGL here
            GL11.glLoadIdentity();
		
            SelectionInterface();

            
            // default look for now
            GLU.gluLookAt(0.0f, 0.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);  
					  
            

          
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);          

            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glPushMatrix();

            RenderMolecule();
		

            GL11.glPopMatrix();

  

            Display.update();
            //check for a resize event and do it
            if (Display.wasResized())doResize(Display.getWidth(), Display.getHeight());
            
            
	}
 
	Display.destroy();
}
 


    



// doesn't work, distorts the window
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
    GLU.gluPerspective(45.0f,
    				    windowWidth / windowHeight,
    				   0.1f,
    				   100.0f);
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


private void CameraLocation(){
	// default look for now
           GLU.gluLookAt(0.0f, 0.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);  
	return;
}















private void SelectionInterface(){
	int mouse_x = Mouse.getX(); 
	int mouse_y = Mouse.getY();
	int choice, depth;
	IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
	int buffer[] = new int[256];
		
	IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
	// The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
    int[] viewport = new int[4];
		
	// The number of "hits" (objects within the pick area).
	int hits;
		
	// only select if mouse button was clicked
	if(Mouse.isButtonDown(0) && !lastMouseDown){
		System.out.printf("Mouse Button Pressed\n");



		// Get the viewport info
        GL11.glGetInteger(GL11.GL_VIEWPORT, vpBuffer);
        vpBuffer.get(viewport);
		
		// Set the buffer that OpenGL uses for selection to our buffer
		GL11.glSelectBuffer(selBuffer);
		
		// Change to selection mode
		GL11.glRenderMode(GL11.GL_SELECT);
		
		// Initialize the name stack (used for identifying which object was selected)
		GL11.glInitNames();
		GL11.glPushName(0);

		
		


		GL11.glMatrixMode(GL11.GL_PROJECTION);


		

		GL11.glPushMatrix();
		
		GL11.glLoadIdentity();
		// picking window
		GLU.gluPickMatrix( (float) mouse_x, (float) mouse_y, pickScale * 5.0f, pickScale * 5.0f,IntBuffer.wrap(viewport));
		GLU.gluPerspective(45.0f,
    				       windowWidth / windowHeight,
    				   	   0.1f,
    				   	   100.0f);
		CameraLocation();

	


		RenderMolecule();
		
		GL11.glPopMatrix();

		

		// Exit selection mode and return to render mode, returns number selected
		hits = GL11.glRenderMode(GL11.GL_RENDER);
                
		
		
		selBuffer.get(buffer);
        // Objects Were Drawn Where The Mouse Was
        if (hits > 0) {
              // If There Were More Than 0 Hits
              choice = buffer[3]; // Make Our Selection The First Object
              depth = buffer[1]; // Store How Far Away It Is
              for (int i = 1; i < hits; i++) {
                    // Loop Through All The Detected Hits
			// If This Object Is Closer To Us Than The One We Have Selected
                    if (buffer[i * 4 + 1] < (int) depth) {
                          choice = buffer[i * 4 + 3]; // Select The Closer Object
                          depth = buffer[i * 4 + 1]; // Store How Far Away It Is
                    }
              }

              //System.out.println("Atom:" + choice);

              // just for bug fixing
              if(choice < atomSelectedArray.length){
              	if(atomSelectedArray[choice] == 1){
              		atomSelectedArray[choice] = 0;
              	}else{
              		atomSelectedArray[choice] = 1;
              	}

              }
              	
        }

	}
	GL11.glMatrixMode(GL11.GL_MODELVIEW);
	lastMouseDown = Mouse.isButtonDown(0);
	return;
}


















private void RenderMolecule(){
// Clear the screen and depth buffer

		

	
	RenderAtoms();
	RenderBonds();
	//MarkOrigin();

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


        // test angle in the X/Y plane
        /*
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(45.0f, -1.0f, 0.0f, 0.0f);
        
        Cylinder Test = new Cylinder();
        GL11.glColor3f((float)1.0,(float)1.0,(float)0.0);
        
        Test.draw(0.1f, 
                0.1f, 
                2.0f, 16, 16);
        
        GL11.glRotatef(-45.0f, -1.0f, 0.0f, 0.0f);
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        */
}




private void DrawSelectedCursor(){
	

	for(int itor = 1; itor < atomSelectedArray.length; itor++){
		if(atomSelectedArray[itor] == 1){



			GL11.glTranslatef((float)internalAtomArray[itor][1], 
		   					 (float)internalAtomArray[itor][2], 
		   					 (float)internalAtomArray[itor][3]);

			GL11.glColor4f(1.0f, 1.0f, 0.0f, 0.5f);
			GL11.glLoadName(itor);
			Sphere s = new Sphere();

   			s.draw((float)(atomSelectScaling * CovalentRadii[(int)internalAtomArray[itor][0]]), 20, 20);

	   		GL11.glTranslatef(-1.0f * (float)internalAtomArray[itor][1], 
			   			 	 -1.0f * (float)internalAtomArray[itor][2], 
			   				 -1.0f * (float)internalAtomArray[itor][3]);

		}
	}

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




// EOF
}