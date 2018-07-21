/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenGLEngine;


import org.lwjgl.util.vector.Vector4f;

/**
 *
 * @author djosh
 */
public class RenderSettings {


    public final static float shininess = 50.0f;
    public final static float ambientLightData[] = { 0.2f, 0.2f, 0.2f, 1.0f };
    public final static float diffuseLightData[] = { 0.8f, 0.8f, 0.8f, 1.0f };
    public final static float specularLightData[] = { 0.30f, 0.30f, 0.30f, 1.0f };
    public final static float positionLightData[] = { 30.0f, 30.0f, 20.0f, 1.0f }; 
    public final static float matSpecularData[] = { 1.0f, 1.0f, 1.0f, 1.0f }; 

    public final static float ZNear = 0.01f;
    public final static float ZFar = 100000.0f;

    //------------------------------------------------------------------------------





    //window parameters and camera FOV
    //------------------------------------------------------------------------------

    // field of view (in radians) (~45 degrees)
    public final static float fieldOfView = 0.785f;
    //------------------------------------------------------------------------------






    // for picking algorithm based on ray tracing and 
    // atom selection / plane variables
    //------------------------------------------------------------------------------

    //maximum value for the parametric equation defining a line to get 
    //the end and beginning points for a picker ray
    // make large to ensure that it always crosses atom space (1000 angstroms here)
    public final static float ParametricTValue = 1000.0f;
    // variables for UI

    //------------------------------------------------------------------------------


// can't be final, modded a bit during run

    // for arc ball camera, rotation of the model + zoom
    //------------------------------------------------------------------------------

    public static float zoom = 20;
    //------------------------------------------------------------------------------






    // variables for the size of atoms, scaling, and colors
    //------------------------------------------------------------------------------
    public final static int colorMapArraySize = 97;
    // covalent radii used to draw spheres for each AN
    public final static double [] CovalentRadii = {0.00,0.32,0.28,1.29,0.96,0.84,0.76,0.71,0.66,0.57,0.58,
                                       1.67,1.42,1.21,1.11,1.07,1.05,1.02,1.06,2.03,1.76,
                                       1.71,1.61,1.54,1.40,1.40,1.32,1.26,1.24,1.32,1.22,
                                       1.22,1.20,1.19,1.20,1.20,1.16,2.21,1.95,1.91,1.76,
                                       1.65,1.55,1.48,1.47,1.43,1.40,1.46,1.45,1.43,1.39,
                                       1.40,1.38,1.39,1.41,2.44,2.15,2.08,2.05,2.04,2.02,
                                       1.99,1.99,1.99,1.97,1.95,1.93,1.93,1.90,1.90,1.88,
                                       1.88,1.75,1.71,1.63,1.52,1.44,1.42,1.37,1.37,1.33,
                                       1.46,1.47,1.48,1.40,1.50,1.50,2.60,2.21,2.15,2.07,
                                       2.00,1.97,1.90,1.87,1.81,1.69};


    public final static int SINGLE_BOND = 10;
    public final static float atomScaling = 0.5f; 
    public final static float atomSelectScaling = 0.7f;
    public final static float bondScaling = 0.3f;

   
    // color map for bonds
    public final static float [] singleBondColorMap = {0.6f, 0.6f, 0.6f};

    // color map for NICS grid------------------------------------------------------
    //------------------------------------------------------------------------------
    //Proposed
    public final static Vector4f GridCubeProposedColor = new Vector4f(1.0f, 1.0f, 1.0f, 0.5f);
    // outline color for the proposed grid
    public final static Vector4f GridOutlineProposedColor= new Vector4f(1.0f, 0.0f, 0.0f, 0.5f);
    // surface point color for the proposed
    public final static Vector4f GridSurfacePointProposedColor= new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);

    // Selected
    public final static Vector4f GridCubeSelectedColor = new Vector4f(1.0f, 1.0f, 0.0f, 0.5f);
    // outline color for the selected grid
    public final static Vector4f GridOutlineSelectedColor= new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    // Surface point for the selected grid
    public final static Vector4f GridSurfacePointSelectedColor= new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Non-selected 
    public final static Vector4f GridCubeColor = new Vector4f(1.0f, 1.0f, 1.0f, 0.5f);
    // outline color for the non-selected grid
    public final static Vector4f GridOutlineColor= new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    // Surface point for the non-selected grid
    public final static Vector4f GridSurfacePointColor= new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    // size of lines outlining NICS grid cube
    public final static float OutlineLineWidth = 2.0f;
    // size of points decorating NICS grid cube
    public final static float GridPointSize = 5.0f;

    // to display the points as a quad    
    public final static boolean RenderPointsAsQuad = true;
    
    // maximum points to display in any quad, larger than this and just don't display
    // too much brings the rendering engine to it's knees
    public final static int NO_DISPLAY = 100000;
    public final static float Z_fight_delta = 0.001f;
    
    // color map for proposed and accepted ghost atoms------------------------------
    //------------------------------------------------------------------------------
    public final static Vector4f ProposedGhostAtomColor = new Vector4f(1.0f, 1.0f, 1.0f, 0.5f);
    public final static Vector4f GhostAtomColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    // color map for highlighted atoms and highlighted + selected atoms-------------
    //------------------------------------------------------------------------------
    public final static Vector4f HighlightedAtomColor = new Vector4f(1.0f, 1.0f, 0.0f, 0.5f);
    public final static Vector4f SelectedAtomColor = new Vector4f(1.0f, 0.0f, 0.0f, 0.5f);
    // transparency in openGL is difficult, render the highlights behind
    // grid cubes as solid rather than semi-transparent
    public final static Vector4f HighlightedAtomInGridColor = new Vector4f(1.0f, 1.0f, 0.0f, 0.5f);
    public final static Vector4f SelectedAtomInGridColor = new Vector4f(1.0f, 0.0f, 0.0f, 0.5f);

    // Screen background color------------------------------------------------------
    //------------------------------------------------------------------------------
    public final static Vector4f BackgroundColor = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);

    //------------------------------------------------------------------------------




}
