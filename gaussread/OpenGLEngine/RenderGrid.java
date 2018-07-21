/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenGLEngine;

import GhostAtom.NICS_Grid;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author djosh
 *  Grid functions for plotting were getting out of hand in the main MoleculeDisplay class, 
 *  pulled them out to simplify it there and make it easier to find here
 */
public class RenderGrid {
    
    
    
    
    
    // render the grid as a semi-opaque outlined cube with grid points shown on the periphery
public static void RenderGridProposedGhostAtoms(NICS_Grid GridToRender, Object GhastlyLock){
    float X, Y, Z;
    synchronized(GhastlyLock){
        // should only render a grid
        assert(GridToRender.GhostSetType() == 3);
        
        //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
    // for only a z-plane: 8,7,3,4
    // for only a y plane: 1,5,8,4
    // for only an x-plane: 1,2,3,4 
   
        // render highlighted planes / cubes
        if(GridToRender.IsHighlighted()){
            if(GridToRender.X_Plane){                
                // Render Highlight of Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
                // render outline
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                 
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);            
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                                             
                // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                        
                     //ArrayList<Vector3f[]> QuadPoints   
            }else if(GridToRender.Y_Plane){
                // Render Highlight of Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);               
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glEnd();
                
                
                // render outline
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                 
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                                          
                // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
                
            }else if(GridToRender.Z_Plane){
                // Render Highlight of Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);               
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
                
                // render outline
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                 
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glEnd();
                
                
                                       
                // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
            }else{
                // Render Highlight of Bounding Box
                //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);                
                GL11.glBegin(GL11.GL_QUADS);                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z);  
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z);
                GL11.glEnd();
                
                // render the internal cube as well                                                  
                 
                GL11.glColor4f(RenderSettings.GridCubeProposedColor.x, RenderSettings.GridCubeProposedColor.y, RenderSettings.GridCubeProposedColor.z, RenderSettings.GridCubeProposedColor.w);
                GL11.glBegin(GL11.GL_QUADS);                                   
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z); 
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);  
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z); 
                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
               
                
                
                // render outline of the cube
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                 
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);              
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);  
                GL11.glEnd();
                
                
                
                               
                // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
                
            }
            // render 'non-highlighted' planes/cubes 
        }else{
            if(GridToRender.X_Plane){             
                // Render Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeProposedColor.x, RenderSettings.GridCubeProposedColor.y, RenderSettings.GridCubeProposedColor.z, RenderSettings.GridCubeProposedColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glEnd();
                
                
                 // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineProposedColor.x, RenderSettings.GridOutlineProposedColor.y, RenderSettings.GridOutlineProposedColor.z, RenderSettings.GridOutlineProposedColor.w);
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                            
                // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
            }else if(GridToRender.Y_Plane){
                // Render Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeProposedColor.x, RenderSettings.GridCubeProposedColor.y, RenderSettings.GridCubeProposedColor.z, RenderSettings.GridCubeProposedColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();    
                
                
                 // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineProposedColor.x, RenderSettings.GridOutlineProposedColor.y, RenderSettings.GridOutlineProposedColor.z, RenderSettings.GridOutlineProposedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                
                               // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
            }else if(GridToRender.Z_Plane){
                // Render Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeProposedColor.x, RenderSettings.GridCubeProposedColor.y, RenderSettings.GridCubeProposedColor.z, RenderSettings.GridCubeProposedColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);               
                GL11.glEnd();
                
                 // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineProposedColor.x, RenderSettings.GridOutlineProposedColor.y, RenderSettings.GridOutlineProposedColor.z, RenderSettings.GridOutlineProposedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glEnd();
                
                
                               // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
            }else{
                 // Render Bounding Box 
                //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
                 
                GL11.glColor4f(RenderSettings.GridCubeProposedColor.x, RenderSettings.GridCubeProposedColor.y, RenderSettings.GridCubeProposedColor.z, RenderSettings.GridCubeProposedColor.w);
                GL11.glBegin(GL11.GL_QUADS);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z); 
                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                 
                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z); 
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                                
                GL11.glEnd();
                
                
                // render outline of the cube
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineProposedColor.x, RenderSettings.GridOutlineProposedColor.y, RenderSettings.GridOutlineProposedColor.z, RenderSettings.GridOutlineProposedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                             
                GL11.glEnd();
                
                
                               // render the grid points
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointProposedColor.x, RenderSettings.GridSurfacePointProposedColor.y, RenderSettings.GridSurfacePointProposedColor.z, RenderSettings.GridOutlineProposedColor.w);                
                    GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
            }   
            
        }
        
    }
                        
}










public static void RenderGridGhostAtoms(NICS_Grid GridToRender, Object GhastlyLock){
    float X, Y, Z;
    synchronized(GhastlyLock){
        // should only render a grid
        assert(GridToRender.GhostSetType() == 3);
        
        //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
    // for only a z-plane: 8,7,3,4
    // for only a y plane: 1,5,8,4
    // for only an x-plane: 1,2,3,4 


    
        // render highlighted planes / cubes
        if(GridToRender.IsHighlighted()){
            if(GridToRender.X_Plane){                
                // Render Highlight of Bounding plane               
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                
                // render grid points
                 
                 if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
            }else if(GridToRender.Y_Plane){
                // Render Highlight of Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
     
                 if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                

            }else if(GridToRender.Z_Plane){
                // Render Highlight of Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glEnd();
                
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
       
            }else{
                 // Render Highlight of Bounding Box
                //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);                                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z);
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[1].x, GridToRender.CubeSelectedPoints[1].y, GridToRender.CubeSelectedPoints[1].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[2].x, GridToRender.CubeSelectedPoints[2].y, GridToRender.CubeSelectedPoints[2].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[6].x, GridToRender.CubeSelectedPoints[6].y, GridToRender.CubeSelectedPoints[6].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[5].x, GridToRender.CubeSelectedPoints[5].y, GridToRender.CubeSelectedPoints[5].z); 
                
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[8].x, GridToRender.CubeSelectedPoints[8].y, GridToRender.CubeSelectedPoints[8].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[7].x, GridToRender.CubeSelectedPoints[7].y, GridToRender.CubeSelectedPoints[7].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[3].x, GridToRender.CubeSelectedPoints[3].y, GridToRender.CubeSelectedPoints[3].z);
                GL11.glVertex3f(GridToRender.CubeSelectedPoints[4].x, GridToRender.CubeSelectedPoints[4].y, GridToRender.CubeSelectedPoints[4].z);
                GL11.glEnd();
                
                                                
                // render the internal cube as well                
                 
                GL11.glColor4f(RenderSettings.GridCubeSelectedColor.x, RenderSettings.GridCubeSelectedColor.y, RenderSettings.GridCubeSelectedColor.z, RenderSettings.GridCubeSelectedColor.w);
                GL11.glBegin(GL11.GL_QUADS);              
                GL11.glColor4f(RenderSettings.GridCubeColor.x, RenderSettings.GridCubeColor.y, RenderSettings.GridCubeColor.z, RenderSettings.GridCubeColor.w);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                
                GL11.glEnd();
                                                             
                // render outline of the cube
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineSelectedColor.x, RenderSettings.GridOutlineSelectedColor.y, RenderSettings.GridOutlineSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z); 
                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                             
                GL11.glEnd();
                
                
                
                 if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointSelectedColor.x, RenderSettings.GridSurfacePointSelectedColor.y, RenderSettings.GridSurfacePointSelectedColor.z, RenderSettings.GridOutlineSelectedColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
           
            }
            // render 'non-highlighted' planes/cubes 
        }else{
            if(GridToRender.X_Plane){             
                // Render Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeColor.x, RenderSettings.GridCubeColor.y, RenderSettings.GridCubeColor.z, RenderSettings.GridCubeColor.w);
                GL11.glBegin(GL11.GL_QUADS);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineColor.x, RenderSettings.GridOutlineColor.y, RenderSettings.GridOutlineColor.z, RenderSettings.GridOutlineColor.w);
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
            }else if(GridToRender.Y_Plane){
                // Render Bounding plane
                 
                GL11.glColor4f(RenderSettings.GridCubeColor.x, RenderSettings.GridCubeColor.y, RenderSettings.GridCubeColor.z, RenderSettings.GridCubeColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineColor.x, RenderSettings.GridOutlineColor.y, RenderSettings.GridOutlineColor.z, RenderSettings.GridOutlineColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glEnd();
                
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
                
                
                
            }else if(GridToRender.Z_Plane){
                // Render Bounding plane                
                 
                GL11.glColor4f(RenderSettings.GridCubeColor.x, RenderSettings.GridCubeColor.y, RenderSettings.GridCubeColor.z, RenderSettings.GridCubeColor.w);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glEnd();
                
                // render outline
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineColor.x, RenderSettings.GridOutlineColor.y, RenderSettings.GridOutlineColor.z, RenderSettings.GridOutlineColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glEnd();
                
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
            }else{
                 // Render Bounding Box 
                //faces points for a full cube: 1,2,3,4; 6,2,3,7; 5,6,7,8; 1,5,8,4; 1,2,6,5; 8,7,3,4; 

                 
                GL11.glColor4f(RenderSettings.GridCubeColor.x, RenderSettings.GridCubeColor.y, RenderSettings.GridCubeColor.z, RenderSettings.GridCubeColor.w);                
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                                
                GL11.glEnd();
                
                // render outline of the cube
                 
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);
                GL11.glColor4f(RenderSettings.GridOutlineColor.x, RenderSettings.GridOutlineColor.y, RenderSettings.GridOutlineColor.z, RenderSettings.GridOutlineColor.w);
                GL11.glBegin(GL11.GL_LINES);                
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[2].x, GridToRender.CubePoints[2].y, GridToRender.CubePoints[2].z);
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                              
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);
                GL11.glVertex3f(GridToRender.CubePoints[3].x, GridToRender.CubePoints[3].y, GridToRender.CubePoints[3].z);                
                GL11.glVertex3f(GridToRender.CubePoints[6].x, GridToRender.CubePoints[6].y, GridToRender.CubePoints[6].z);
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[7].x, GridToRender.CubePoints[7].y, GridToRender.CubePoints[7].z);                
                GL11.glVertex3f(GridToRender.CubePoints[5].x, GridToRender.CubePoints[5].y, GridToRender.CubePoints[5].z);
                GL11.glVertex3f(GridToRender.CubePoints[1].x, GridToRender.CubePoints[1].y, GridToRender.CubePoints[1].z);                
                GL11.glVertex3f(GridToRender.CubePoints[8].x, GridToRender.CubePoints[8].y, GridToRender.CubePoints[8].z);
                GL11.glVertex3f(GridToRender.CubePoints[4].x, GridToRender.CubePoints[4].y, GridToRender.CubePoints[4].z);                             
                GL11.glEnd();
                
                if(!RenderSettings.RenderPointsAsQuad){ 
                    GL11.glPointSize(RenderSettings.GridPointSize);
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                    GL11.glBegin(GL11.GL_POINTS);             
                    for(int itor = 0; itor < GridToRender.SurfaceAtomCoordinates.size(); itor++){




                        GL11.glVertex3f(GridToRender.SurfaceAtomCoordinates.get(itor).x,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).y,
                                        GridToRender.SurfaceAtomCoordinates.get(itor).z);
                    }
                    GL11.glEnd ();
                    // render the points as a quad
                }else if(GridToRender.QuadPoints.size() < RenderSettings.NO_DISPLAY){
                    GL11.glColor4f(RenderSettings.GridSurfacePointColor.x, RenderSettings.GridSurfacePointColor.y, RenderSettings.GridSurfacePointColor.z, RenderSettings.GridOutlineColor.w);                
                     GL11.glBegin(GL11.GL_TRIANGLES);                
                    for(int itor = 0; itor < GridToRender.QuadPoints.size(); itor++){   
                        // rendered as a triangle instead
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[1].x;
                        Y = GridToRender.QuadPoints.get(itor)[1].y;
                        Z = GridToRender.QuadPoints.get(itor)[1].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[0].x;
                        Y = GridToRender.QuadPoints.get(itor)[0].y;
                        Z = GridToRender.QuadPoints.get(itor)[0].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[2].x;
                        Y = GridToRender.QuadPoints.get(itor)[2].y;
                        Z = GridToRender.QuadPoints.get(itor)[2].z;
                        GL11.glVertex3f(X, Y, Z);
                        X = GridToRender.QuadPoints.get(itor)[3].x;
                        Y = GridToRender.QuadPoints.get(itor)[3].y;
                        Z = GridToRender.QuadPoints.get(itor)[3].z;
                        GL11.glVertex3f(X, Y, Z);
                    }
                    GL11.glEnd();

                }
                
                
            }   
            
        }
        
    }
  
}








    
    
    
}
