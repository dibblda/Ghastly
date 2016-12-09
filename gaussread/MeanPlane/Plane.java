/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MeanPlane;

/**
 *
 * @author David Joshua Dibble
 * this class will implement the best fit plane calculation
 * AX + BY + CZ + D = 0
 * likely choice will be principal component analysis once
 * I find a suitable algorithm
 * least squares is a poor choice, which optimizes for
 * (X,Y, F(X,Y))
 * and
 * AX + BY + D = Z
 * 
 */

import org.lwjgl.util.vector.Vector3f;
import java.util.ArrayList;

public class Plane {
    private ArrayList VectorList = new ArrayList();

    // add points to the list for eventual plane calculation
    public void AddPoint(Vector3f NewPoint){
       VectorList.add(NewPoint);
    }

    
    
}


