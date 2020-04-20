/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenGLEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.Sphere;
import MeanPlane.*;

/**
 *
 * @author djosh
 */
public class AxisRender {
    
public static void RenderGlobalAxisMarker(){
    
    // draw a black sphere at the origin
    GL11.glColor3f((float)0.0,(float)0.0,(float)0.0);
    Sphere s = new Sphere();
    s.draw(0.1f, 16, 16);
    
    // first cylinder for axis marking
        //first red for Z
        GL11.glColor3f((float)0.0,(float)0.0,(float)1.0);
        Cylinder Z = new Cylinder();
        Cylinder Z_Arrowhead = new Cylinder();
        Disk Z_Arrowhead_Cap = new Disk();
        //draw cylinder, startes at origin and length R
        Z.draw(0.1f, 
               0.1f, 
               2.0f, 16, 16);
        // draw arrowhead on cylinder
        GL11.glTranslatef(0.0f, 0.0f, 2.0f);
        Z_Arrowhead.draw(0.2f, 0.0f, 0.4f, 16, 16);        
        Z_Arrowhead_Cap.draw(0.0f, 0.2f, 16, 16);
        GL11.glTranslatef(0.0f, 0.0f, -2.0f);   
        
        
        //now rotate 90 degrees to place a new one on the X-Axis
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glColor3f((float)1.0,(float)0.0,(float)0.0);
        Cylinder X = new Cylinder();
        Cylinder X_Arrowhead = new Cylinder();
        Disk X_Arrowhead_Cap = new Disk();
        
        //draw cylinder, startes at origin and length R
        X.draw(0.1f, 
               0.1f, 
               2.0f, 16, 16);        
        // draw arrowhead on cylinder
        GL11.glTranslatef(0.0f, 0.0f, 2.0f);
        X_Arrowhead.draw(0.2f, 0.0f, 0.4f, 16, 16);        
        X_Arrowhead_Cap.draw(0.0f, 0.2f, 16, 16);                
        GL11.glTranslatef(0.0f, 0.0f, -2.0f);   

        ///rotate back    
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);                
        // now rotate 90 degree to place a new one on the Y-axis
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        GL11.glColor3f((float)0.0,(float)1.0,(float)0.0);
        Cylinder Y = new Cylinder();
        Cylinder Y_Arrowhead = new Cylinder();
        Disk Y_Arrowhead_Cap = new Disk();
        
        //draw cylinder, startes at origin and length R
        Y.draw(0.1f, 
               0.1f, 
               2.0f, 16, 16);
        // draw arrowhead on cylinder
        GL11.glTranslatef(0.0f, 0.0f, 2.0f);
        Y_Arrowhead.draw(0.2f, 0.0f, 0.4f, 16, 16);        
        Y_Arrowhead_Cap.draw(0.0f, 0.2f, 16, 16);
        GL11.glTranslatef(0.0f, 0.0f, -2.0f); 
        GL11.glRotatef(-90.0f, -1.0f, 0.0f, 0.0f);
           
    }


    public static void RenderLocalAxisMarker(Plane LocalPlane){
        // to draw an axis on the plane created by the atom selection
        // 1st calculate cross product of normal vector with global Z-axis
        // the calculate cross product of normal vector with glo 
        
    }

}
