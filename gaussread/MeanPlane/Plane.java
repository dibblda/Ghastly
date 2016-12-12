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

import org.joml.Vector3f;
import java.util.ArrayList;

public class Plane {
    private ArrayList<Vector3f> VectorList = new ArrayList();

    private Vector3f TranslationVector = new Vector3f();
    
    private float[][] CovarianceMatrix = new float[3][3];
    
    // add points to the list for eventual plane calculation
    public void AddPoint(Vector3f NewPoint){
       VectorList.add(NewPoint);
    }

    public void CalculatePCAMatrix(){
        
        CalculateTranslationVector();
        CenterData();
        CreateCovarianceMatrix();
        
        
        //DebugCovariance();
    }
    
    
    private void CalculateTranslationVector(){
        
        Vector3f temp;
                
        TranslationVector.x = 0;
        TranslationVector.y = 0;
        TranslationVector.z = 0;
        
        for(int itor = 0; itor < VectorList.size(); itor++){
             temp = (Vector3f)VectorList.get(itor);
             TranslationVector.x = temp.x + TranslationVector.x;
             TranslationVector.y = temp.y + TranslationVector.y;
             TranslationVector.z = temp.z + TranslationVector.z;
        }
        TranslationVector.x = TranslationVector.x / (float)VectorList.size();
        TranslationVector.y = TranslationVector.y / (float)VectorList.size();        
        TranslationVector.z = TranslationVector.z / (float)VectorList.size();
    }
    
    private void CenterData(){
        for(int itor = 0; itor < VectorList.size(); itor++){
             VectorList.get(itor).x = VectorList.get(itor).x - TranslationVector.x;
             VectorList.get(itor).y = VectorList.get(itor).y - TranslationVector.y;
             VectorList.get(itor).z = VectorList.get(itor).z - TranslationVector.z;
        }
    }
    
    
    // matrix[row][column]
    private void CreateCovarianceMatrix(){
        float[][] CoordinateMatrix = new float[3][VectorList.size()];
        float[][] TransposedCoordinateMatrix = new float[VectorList.size()][3];
        float sum;
        
        
        // fill in coordinate matrix and transposed coordinate matrix
        
        for(int itor = 0; itor < VectorList.size(); itor++){
            CoordinateMatrix[0][itor] = VectorList.get(itor).x;
            CoordinateMatrix[1][itor] = VectorList.get(itor).y;
            CoordinateMatrix[2][itor] = VectorList.get(itor).z;
            
            TransposedCoordinateMatrix[itor][0] = VectorList.get(itor).x;
            TransposedCoordinateMatrix[itor][1] = VectorList.get(itor).y;
            TransposedCoordinateMatrix[itor][2] = VectorList.get(itor).z;
        }
        /* debugging
        for(int RowItor = 0; RowItor < 3; RowItor++){
            System.out.println("[" +CoordinateMatrix[RowItor][0]+ "][" +CoordinateMatrix[RowItor][1]+"]["+CoordinateMatrix[RowItor][2]+ "]");
        }
        System.out.println();
        for(int RowItor = 0; RowItor < 3; RowItor++){
            System.out.println("[" +TransposedCoordinateMatrix[RowItor][0]+ "][" +TransposedCoordinateMatrix[RowItor][1]+"]["+TransposedCoordinateMatrix[RowItor][2]+ "]");
        }
        System.out.println();
        */
        
        // multiply CoordinateMatrix by TransposedCoordinateMatrix
        // to obtain the covariance matrix
        
        // first itoration over the product matrix
        for(int RowItor = 0; RowItor < 3; RowItor++){
            for(int ColItor = 0; ColItor < 3; ColItor++){
                sum = 0;
                // itoration over the row/column to sum it up
                for(int SumItor = 0; SumItor < VectorList.size(); SumItor++){
                    sum = CoordinateMatrix[RowItor][SumItor] * TransposedCoordinateMatrix[SumItor][ColItor] + sum;
                }                
                CovarianceMatrix[RowItor][ColItor] = sum;                                
            }
        }
                      
    }    
    
    
    private void DebugCovariance(){
        for(int RowItor = 0; RowItor < 3; RowItor++){
            System.out.println("[" +CovarianceMatrix[RowItor][0]+ "][" +CovarianceMatrix[RowItor][1]+"]["+CovarianceMatrix[RowItor][2]+ "]");
        }
    }
    
}


