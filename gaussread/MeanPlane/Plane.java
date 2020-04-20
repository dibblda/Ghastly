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
import org.joml.Vector4f;
import org.joml.Matrix3f;
import java.util.ArrayList;
import org.ejml.data.DenseMatrix64F;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.factory.DecompositionFactory;
public class Plane {
    // input coordinates list
    private ArrayList<Vector4f> VectorList = new ArrayList();
    // intermediate data fro PCA calculation
    private Vector3f TranslationVector = new Vector3f(0,0,0);
    private double[][] CovarianceMatrix = new double[3][3];
    // output data from PCA
    private Vector3f EigenvectorOne = new Vector3f();
    private Vector3f EigenvectorTwo = new Vector3f();
    private Vector3f EigenvectorThree = new Vector3f();
    private Vector3f EigenValues = new Vector3f();               
    // place to store information for the plane defined by PCA
     // in PCA, all vectors should be orthogonal, uless 2 eigenvalues equal
    // normal to plane in the case of atoms should be the vector with the lowest eigenvalue   
    private Vector3f NormalToPlane = new Vector3f();
    
    private Vector4f PlaneEquation = new Vector4f(); 
    
    // used to define the x-vector on the plane
    private Vector3f XVectorAtom = new Vector3f(0,0,0);
    private Vector3f XVectorAtomLast = new Vector3f(0,0,0);
    private boolean XVectorAtomDefined = false;
    
    
    
    
    // add points to the list for eventual plane calculation
    public void AddPoint(Vector4f NewPoint){
       VectorList.add(NewPoint);
       
       // more than or equal to three points, Do PCA by default
       if(VectorList.size() >= 3){
            CalculatePCAMatrix();
           
        }
       //DebugAtomList();
    }
    
    public void ClearPlanePoints(){
        VectorList.clear();
    }
    
    public void RemovePoint(int AtomIndex){
        // just check atom index, which is unique
        for(int itor = 0; itor < VectorList.size(); itor++){
            if(AtomIndex == (int)VectorList.get(itor).w){
                VectorList.remove(itor);
                break;
            }
        }
        // reculculate matrix if greater than or equall to three points
        if(VectorList.size() >= 3){
            CalculatePCAMatrix();                       
        }
        //DebugAtomList();
    }
    
    public boolean PlaneCalculated(){
        if(VectorList.size() >= 3){
            return true;                       
        }
        return false;
    }
    
    // functions to get calculated Data
    
    public Vector3f GetOrigin(){
        if(VectorList.size() <= 2){
            return null;
        }
        return new Vector3f(TranslationVector);
    }    
    public Vector3f GetEigenVectorOne(){
        if(VectorList.size() <= 2){
            return null;
        }
        return new Vector3f(EigenvectorOne);
    }
    public Vector3f GetEigenVectorTwo(){
        if(VectorList.size() <= 2){
            return null;
        }
        return new Vector3f(EigenvectorTwo);
    }
    public Vector3f GetEigenVectorThree(){
        if(VectorList.size() <= 2){
            return null;
        }
        return new Vector3f(EigenvectorThree);
    }
    public float GetEigenValueOne(){
        // return a really large negative number if not enough points
        if(VectorList.size() <= 2){
            return -1.0e6f;
        }
        return EigenValues.x;
    }
    public float GetEigenValueTwo(){
        // return a really large negative number if not enough points
        if(VectorList.size() <= 2){
            return -1.0e6f;
        }
        return EigenValues.y;
    }
    public float GetEigenValueThree(){
        // return a really large negative number if not enough points
        if(VectorList.size() <= 2){
            return -1.0e6f;
        }
        return EigenValues.z;
    }
    // return the plane equation for the PCA
    // vector4f.x = A
    // vector4f.y = B
    // vector4f.z = C
    // vector4f.w = D
    
    public Vector4f GetPlaneEquation(){
        if(VectorList.size() <= 2){
            return null;
        }
        return new Vector4f(PlaneEquation);
    }
    
    public Vector3f GetPlaneNormal(){
        if(VectorList.size() <= 2){
            return null;
        }        
        return new Vector3f(NormalToPlane);
    }
    
    public void GlobalCoordinatePlane(Vector3f Origin){
        // Translation vector is at the origin point for a global plane
        TranslationVector.x = Origin.x;
        TranslationVector.y = Origin.y;
        TranslationVector.z = Origin.z;
        
        // this function should only be called for a new plane but anyways start with
        // a new vecotr list to be sure we aren't working with old data
        VectorList.clear();
        // create three points in the global coordinate plane space translated by the origin
        // add the translation vector points to each
        // these points create a well-behaved plane along the x-y axis with the normal vector
        // pointing in the positive z-direction
        VectorList.add(new Vector4f(0.0f + TranslationVector.x, 1.0f + TranslationVector.y, 0.0f + TranslationVector.z, 0.0f));
        VectorList.add(new Vector4f(1.0f + TranslationVector.x, 0.0f + TranslationVector.y, 0.0f + TranslationVector.z, 0.0f));
        VectorList.add(new Vector4f(1.0f + TranslationVector.x, 0.0f + TranslationVector.y, 0.0f + TranslationVector.z, 0.0f));
        
        //the projection atom is along the X axis
         AddXProjectionAtom(new Vector3f(1.0f + TranslationVector.x, 0.0f + TranslationVector.y, 0.0f + TranslationVector.z));
                
        // now do standard calculations
        CreateCovarianceMatrix();
        CalculateEigenVectors();
        CalculatePlaneCoefficients();
        
        
        //DebugEigenvectors();
    }
    
    public void CalculatePCAMatrix(){
        
        CalculateTranslationVector();        
        CreateCovarianceMatrix();
        CalculateEigenVectors();
        CalculatePlaneCoefficients();
        
        //DebugCovariance();
        //DebugEigenvectors();
    }
    
    
    
    
    
    // translation vector = average center of atomic coordinates
    private void CalculateTranslationVector(){
        
        Vector4f temp;
                
        TranslationVector.x = 0;
        TranslationVector.y = 0;
        TranslationVector.z = 0;
        
        for(int itor = 0; itor < VectorList.size(); itor++){
             temp = (Vector4f)VectorList.get(itor);
             TranslationVector.x = temp.x + TranslationVector.x;
             TranslationVector.y = temp.y + TranslationVector.y;
             TranslationVector.z = temp.z + TranslationVector.z;
        }
        TranslationVector.x = TranslationVector.x / (float)VectorList.size();
        TranslationVector.y = TranslationVector.y / (float)VectorList.size();        
        TranslationVector.z = TranslationVector.z / (float)VectorList.size();
    }
    
    
    
    // matrix[row][column]
    private void CreateCovarianceMatrix(){
        float[][] CoordinateMatrix = new float[3][VectorList.size()];
        float[][] TransposedCoordinateMatrix = new float[VectorList.size()][3];
        float sum;
        
        
        // fill in coordinate matrix and transposed coordinate matrix
        
        for(int itor = 0; itor < VectorList.size(); itor++){
            CoordinateMatrix[0][itor] = VectorList.get(itor).x - TranslationVector.x;
            CoordinateMatrix[1][itor] = VectorList.get(itor).y - TranslationVector.y;
            CoordinateMatrix[2][itor] = VectorList.get(itor).z - TranslationVector.z;
            
            TransposedCoordinateMatrix[itor][0] = VectorList.get(itor).x - TranslationVector.x;
            TransposedCoordinateMatrix[itor][1] = VectorList.get(itor).y - TranslationVector.y;
            TransposedCoordinateMatrix[itor][2] = VectorList.get(itor).z - TranslationVector.z;
        }
       
        
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
    // use EJML library to calculate eigenvector and eigenvalues from the covariance matrix
     private void CalculateEigenVectors(){
        
         
         DenseMatrix64F EigenCalcMatrix = new DenseMatrix64F(CovarianceMatrix);
         
         EigenDecomposition<DenseMatrix64F> eig = DecompositionFactory.eig(3, true);
         // matrix is 3x3 symmetric, 3 eigenvectors
         // use EJML to calculate the three eigenvectors
         // they are normalized
         // the sign may be flipped (It doesn't matter?)
         // see example usage http://stackoverflow.com/questions/25996202/ejml-obtain-matrix-eigenvectors-real-value         
         eig.decompose(EigenCalcMatrix);
         DenseMatrix64F eigMat0 = eig.getEigenVector(0);
         DenseMatrix64F eigMat1 = eig.getEigenVector(1);
         DenseMatrix64F eigMat2 = eig.getEigenVector(2);
         
         
         
         // get eigenvalues for each eigenvector
         EigenValues.x = (float)eig.getEigenvalue(0).getReal();
         EigenValues.y = (float)eig.getEigenvalue(1).getReal();
         EigenValues.z = (float)eig.getEigenvalue(2).getReal();
         // convert EJML matrix into Vector3f formatted vectors
         EigenvectorOne.x = (float)eigMat0.get(0, 0);
         EigenvectorOne.y = (float)eigMat0.get(1, 0);
         EigenvectorOne.z = (float)eigMat0.get(2, 0);
         EigenvectorTwo.x = (float)eigMat1.get(0, 0);
         EigenvectorTwo.y = (float)eigMat1.get(1, 0);
         EigenvectorTwo.z = (float)eigMat1.get(2, 0);
         EigenvectorThree.x = (float)eigMat2.get(0, 0);
         EigenvectorThree.y = (float)eigMat2.get(1, 0);
         EigenvectorThree.z = (float)eigMat2.get(2, 0);                  
         
         
     }
    
     
    // Calculate the coeffiecients for a plane equation based on eigenvectors (assumed 
    // to be normal to one another
    // potential edge cases (shouldn't happen)
    // 2 have same value
    // 3 have same value
    // ignoring these for now 
    // equation:
    // A*x + B*y + C*z + D = 0
    private void CalculatePlaneCoefficients(){
        
        // first find smallest (absolute)eigenvalue, this should correspond to the 
        // plane normal vector
        
        if((Math.abs(EigenValues.x) < Math.abs(EigenValues.y))&&(Math.abs(EigenValues.x) < Math.abs(EigenValues.z))){
            // its the first eigenvector
            NormalToPlane.x = EigenvectorOne.x;
            NormalToPlane.y = EigenvectorOne.y;
            NormalToPlane.z = EigenvectorOne.z;
        }
        if((Math.abs(EigenValues.y) < Math.abs(EigenValues.x))&&(Math.abs(EigenValues.y) < Math.abs(EigenValues.z))){
            // its the second eigenvector
            NormalToPlane.x = EigenvectorTwo.x;
            NormalToPlane.y = EigenvectorTwo.y;
            NormalToPlane.z = EigenvectorTwo.z;
        }
        if((Math.abs(EigenValues.z) < Math.abs(EigenValues.x))&&(Math.abs(EigenValues.z) < Math.abs(EigenValues.y))){
            // its the third eigenvector
            NormalToPlane.x = EigenvectorThree.x;
            NormalToPlane.y = EigenvectorThree.y;
            NormalToPlane.z = EigenvectorThree.z;
        }                
        
        PlaneEquation.x =  NormalToPlane.x;
        PlaneEquation.y =  NormalToPlane.y;
        PlaneEquation.z =  NormalToPlane.z;
        PlaneEquation.w =  -(NormalToPlane.x*TranslationVector.x + NormalToPlane.y*TranslationVector.y + NormalToPlane.z*TranslationVector.z);
        

    }
   
    public void AddXProjectionAtom(Vector3f XProjectionAtom){
        XVectorAtomLast.x = XVectorAtom.x;
        XVectorAtomLast.y = XVectorAtom.y;
        XVectorAtomLast.z = XVectorAtom.z;
        
        XVectorAtom.x = XProjectionAtom.x;
        XVectorAtom.y = XProjectionAtom.y;
        XVectorAtom.z = XProjectionAtom.z;
        XVectorAtomDefined = true;
        
    }
    
    public void RemoveCurrentXProjectionAtom(){
        // first fall back to last selection
        XVectorAtom.x = XVectorAtomLast.x;
        XVectorAtom.y = XVectorAtomLast.y;
        XVectorAtom.z = XVectorAtomLast.z;
        // if list is smaller than two, fall back to (0,0,0)
        if(VectorList.size() <= 2){
            XVectorAtom.x = 0;
            XVectorAtom.y = 0;
            XVectorAtom.z = 0;
            XVectorAtomDefined = false;
        }
        
    }
     
   
    // determine the coordinate of a point using the plane as the coordinate space to the coordinate space of the molecule
    // XProjectionAtom is the coordinate that when projected onto the plane represents the X vector (Z is the normal)
    // Plane coordinate is the coordinate to be transformed
    // ZcrossX means calculate Z x X to find the Y vector
    // otherwise do X x Z (which will flip the axis)
    public Vector3f CalculateTransformedCoordinates(Vector3f XProjectionAtom, Vector3f PlaneCoordinate, boolean ZcrossX){
        float A, B, C, D, a, b, c, xo, yo, zo;
        
        float ParametricT;
        Vector3f XVector = new Vector3f(0,0,0);
        Vector3f YVector = new Vector3f(0,0,0);
        Vector3f ZVector = new Vector3f(0,0,0);
        Vector3f FinalValue = new Vector3f(0,0,0);
        
        A = PlaneEquation.x;
        B = PlaneEquation.y;
        C = PlaneEquation.z;
        D = PlaneEquation.w;
        a = NormalToPlane.x;
        b = NormalToPlane.y;
        c = NormalToPlane.z;
        xo = XProjectionAtom.x;
        yo = XProjectionAtom.y;
        zo = XProjectionAtom.z;
        // find the closest point at which the atom used to define the X-axis (which
        //isn't necessarily on the plane) meet the plane. This point defines the 
        // x-vector of the coordinate transformation
        // find the t value at which the plane meets the line
        // I know the a = A, etc. but hust for this purpose for clarity
        ParametricT = -(A * xo + B * yo + C * zo + D) / (a * A + b * B + c * C);
        // first get the point where the paramteric line meets the plane        
        XVector.x = xo + a * ParametricT;
        XVector.y = yo + b * ParametricT;
        XVector.z = zo + c * ParametricT;
        // the substract the normalization vector to get the X vecotr relative to the Z and normalize
        XVector.x = XVector.x - TranslationVector.x;
        XVector.y = XVector.y - TranslationVector.y;
        XVector.z = XVector.z - TranslationVector.z;
        XVector.normalize();
        // normalize the XVector
       
        //transer data into Z vector and normalize
        ZVector.x = a;
        ZVector.y = b;       
        ZVector.z = c;
        ZVector.normalize();
        
        
        // now calculate the cross product to find the Y Vector, X and Z are normal to each other so no additional sine
        if(ZcrossX){
            ZVector.cross(XVector, YVector);
        }else{
            XVector.cross(ZVector, YVector);
        }
     
       // https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=4&ved=0ahUKEwiC1tTNnv_QAhUGwFQKHX4QCYoQFggvMAM&url=http%3A%2F%2Fwww.math.tau.ac.il%2F~dcor%2FGraphics%2Fcg-slides%2Fgeom3d.pdf&usg=AFQjCNG0qhbk3E8jlw8ale28Wt9s_kUxUg&bvm=bv.142059868,d.cGw&cad=rja
        //transforming from XVector, YVector, ZVector defining plane coordinate system to global coordinate system
        
        /*
        [m00 m10 m20]
        [m01 m11 m21]
        [m02 m12 m22]
         */
         Matrix3f TempRotation = new Matrix3f();
         TempRotation.m00 = XVector.x;
         TempRotation.m01 = XVector.y;
         TempRotation.m02 = XVector.z;
         TempRotation.m10 = YVector.x;
         TempRotation.m11 = YVector.y;
         TempRotation.m12 = YVector.z;
         TempRotation.m20 = ZVector.x;
         TempRotation.m21 = ZVector.y;
         TempRotation.m22 = ZVector.z;
        /*
        [m00 m10 m20]
        [m01 m11 m21]
        [m02 m12 m22]
         */
         //TempRotation.transpose();
         TempRotation.transform(PlaneCoordinate, FinalValue);
        
        //Finally move to the geometric center
        FinalValue.x = FinalValue.x + TranslationVector.x;
        FinalValue.y = FinalValue.y + TranslationVector.y;      
        FinalValue.z = FinalValue.z + TranslationVector.z;
        
        return FinalValue;
    }     
    
    
    
    
     public Vector3f CalculateTransformedCoordinates(Vector3f PlaneCoordinate, boolean ZcrossX) throws ArithmeticException{
         
         // since we are assuming the x-vector is in, then crash if not
         //System.out.println("XVectorAtomDefined " + XVectorAtomDefined);
         assert XVectorAtomDefined;
         
        if(!XVectorAtomDefined){
            throw new ArithmeticException("CalculateTransformedCoordinates in Plane.java missing XVectorAtom");
        }
         
        float A, B, C, D, a, b, c, xo, yo, zo;
        
        float ParametricT;
        Vector3f XVector = new Vector3f(0,0,0);
        Vector3f YVector = new Vector3f(0,0,0);
        Vector3f ZVector = new Vector3f(0,0,0);
        Vector3f FinalValue = new Vector3f(0,0,0);
        
        
        //http://www.songho.ca/math/plane/plane.html
        
        // for the plane equation Ax + By + Cz + D = 0, D = A*(x1) + B*(Y1) + C*(Z1)        
                      
        
        A = PlaneEquation.x;
        B = PlaneEquation.y;
        C = PlaneEquation.z;
        D = PlaneEquation.w;
        a = NormalToPlane.x;
        b = NormalToPlane.y;
        c = NormalToPlane.z;
        xo = XVectorAtom.x;
        yo = XVectorAtom.y;
        zo = XVectorAtom.z;
        // find the t value at which the plane meets the line
        // I know the a = A, etc. but hust for this purpose for clarity
        ParametricT = -(A * xo + B * yo + C * zo + D) / (a * A + b * B + c * C);
        
      
        // first get the point where the parametric line meets the plane        
        XVector.x = xo + a * ParametricT;
        XVector.y = yo + b * ParametricT;
        XVector.z = zo + c * ParametricT;
        
        //System.out.println("X:x: " + XVector.x + "X:y: " + XVector.y+ "X:z: " + XVector.z);
        
        // the substract the normalization vector to get the X vector relative to the Z and normalize
        XVector.x = XVector.x - TranslationVector.x;
        XVector.y = XVector.y - TranslationVector.y;
        XVector.z = XVector.z - TranslationVector.z;
        
       // System.out.println("X:x: " + XVector.x + "X:y: " + XVector.y+ "X:z: " + XVector.z);
        
        XVector.normalize();
        // normalize the XVector
       
        //transer data into Z vector and normalize
        ZVector.x = a;
        ZVector.y = b;       
        ZVector.z = c;
        ZVector.normalize();
        
        
       // System.out.println("X, Y, Z Vector Pre Cross" + System.nanoTime());
      //  System.out.println("X:x: " + XVector.x + "X:y: " + XVector.y+ "X:z: " + XVector.z);
       // System.out.println("Y:x: " + YVector.x + "Y:y: " + YVector.y+ "Y:z: " + YVector.z);
      //  System.out.println("Z:x: " + ZVector.x + "Z:y: " + ZVector.y+ "Z:z: " + ZVector.z);
      //  DebugEigenvectors();
        
        
        // now calculate the cross product to find the YVector, X and Z are normal to each other so no additional sine
        if(ZcrossX){
            ZVector.cross(XVector, YVector);
        }else{
            XVector.cross(ZVector, YVector);
        }
     
       // https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=4&ved=0ahUKEwiC1tTNnv_QAhUGwFQKHX4QCYoQFggvMAM&url=http%3A%2F%2Fwww.math.tau.ac.il%2F~dcor%2FGraphics%2Fcg-slides%2Fgeom3d.pdf&usg=AFQjCNG0qhbk3E8jlw8ale28Wt9s_kUxUg&bvm=bv.142059868,d.cGw&cad=rja
        //transforming from XVector, YVector, ZVector defining plane coordinate system to global coordinate system
        
      //  System.out.println("X, Y, Z Vector Post Cross" + System.nanoTime());
     //   System.out.println("X:x: " + XVector.x + "X:y: " + XVector.y+ "X:z: " + XVector.z);
      //  System.out.println("Y:x: " + YVector.x + "Y:y: " + YVector.y+ "Y:z: " + YVector.z);
      //  System.out.println("Z:x: " + ZVector.x + "Z:y: " + ZVector.y+ "Z:z: " + ZVector.z);
      //  DebugEigenvectors();
      
      
        /*
        [m00 m10 m20]
        [m01 m11 m21]
        [m02 m12 m22]
         */
         Matrix3f TempRotation = new Matrix3f();
         TempRotation.m00 = XVector.x;
         TempRotation.m01 = XVector.y;
         TempRotation.m02 = XVector.z;
         TempRotation.m10 = YVector.x;
         TempRotation.m11 = YVector.y;
         TempRotation.m12 = YVector.z;
         TempRotation.m20 = ZVector.x;
         TempRotation.m21 = ZVector.y;
         TempRotation.m22 = ZVector.z;
                                                      
         //TempRotation.transpose();
         TempRotation.transform(PlaneCoordinate, FinalValue);
        
        //Finally move to the geometric center
        FinalValue.x = FinalValue.x + TranslationVector.x;
        FinalValue.y = FinalValue.y + TranslationVector.y;      
        FinalValue.z = FinalValue.z + TranslationVector.z;
        
        return FinalValue;
    }      
    
     
     
     
     
     
    private void DebugCovariance(){
        for(int RowItor = 0; RowItor < 3; RowItor++){
            System.out.println("[" +CovarianceMatrix[RowItor][0]+ "][" +CovarianceMatrix[RowItor][1]+"]["+CovarianceMatrix[RowItor][2]+ "]");
        }
    }
    
    private void DebugEigenvectors(){
        System.out.println("EigenValues");
        System.out.println("[" +EigenValues.x+ "][" +EigenValues.y+"]["+EigenValues.z+ "]");
        System.out.println("EigenVectors");
        System.out.println("[" +EigenvectorOne.x+ "][" +EigenvectorOne.y+"]["+EigenvectorOne.z+ "]");
        System.out.println("[" +EigenvectorTwo.x+ "][" +EigenvectorTwo.y+"]["+EigenvectorTwo.z+ "]");
        System.out.println("[" +EigenvectorThree.x+ "][" +EigenvectorThree.y+"]["+EigenvectorThree.z+ "]");        
        
    }
    
    
    
    private void DebugAtomList(){
        System.out.println("Atoms Defining Plane: " + VectorList.size());
        for(int itor = 0; itor < VectorList.size(); itor++){
            System.out.println("[" + VectorList.get(itor).w + "," + VectorList.get(itor).x + 
                    ","+ VectorList.get(itor).y + ","+ VectorList.get(itor).z +"]");
        }
        System.out.println("End Atoms Defining Plane:");
    }
    
}


