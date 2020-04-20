/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import java.util.ArrayList;
import java.util.Observable;
import org.joml.Vector4f;
import org.joml.Vector3f;
import MeanPlane.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.io.*;
/**
 *
 * @author David Joshua Dibble
 * 
 * add ghost atom types (need to extend GhostAtomType
 * keep track of index if needed or search by atom coordinate
 * 
 * have a buffer for one proposed set of atoms that can be added to the full list
 * highlight or unhighlight a whole set (or atom, not oimplemented yeet)
 * get all atom including proposed, just the list of atoms, and just the highlighted atoms
 * 
 * need to implement file printing of the atom list
 * need to implement multiple atom types
 * 
 */
public class GhostAtomSet extends Observable{

// variable for the render image to know if any changes have occured in the list of atoms to be rendered    
   
//variables to store information on ghost atom types and lists       
        
    int NumberOfSets = 0;
    int TypeIndex = 0;        
    boolean SetProposed = false;
    GhostAtomType ProposedSet = null;
    // array list of all of the different ghost atom types with their atoms
    ArrayList<GhostAtomType> GhostAtomSets = new ArrayList();
    // array list of every single ghost atom irrespective of the type they belong to
    ArrayList<GhostAtom> CurrentAtoms = new ArrayList();
    
   
    
// booleans for GUI / display purposes      
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
    // to turn off mouse choosing after create has been set
    // mo selection until in the proper context
    public boolean LockSelection = true;
    // unselect every real atom that is currently selected
    public boolean UnselectAtoms = false;
    // display only the proposed set
    public boolean DisplayProposed = false;

    // needed to deal with some poorly behaved transparency issues
    //changing display order solved the problem, not needed but kept for flexibility 
    public boolean RenderSelectionCursorSolid = false;
    public boolean RenderHighlightCursorSolid = false;

    // plane is not calculated from atom positions it is a standard plane corresponding to the 
    // global coordinates
    public boolean GlobalCoordinatePlane = false;
    
// add Section    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------   
    
    public int AddType(GhostAtomType GhostType){
        NumberOfSets++;
        TypeIndex++;
        GhostType.Identifier = TypeIndex;
        GhostAtomSets.add(GhostType);
        return TypeIndex;
    }
    
    // add but don't include in the list yet
    public void NewProposed(GhostAtomType GhostType){
        
        try{
            ProposedSet = (GhostAtomType)GhostType.clone();
        }catch(CloneNotSupportedException e){        
        }
    }
    
    // add the proposed to the list
    public int AddProposed(){
        assert(ProposedSet != null);
        NumberOfSets++;
        TypeIndex++;
        ProposedSet.Identifier = TypeIndex;
        GhostAtomSets.add(ProposedSet);
        SetProposed = true;
        return TypeIndex;
    }
    
    
// remove Section    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    public void RemoveType(int TypeIndexValue){
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                GhostAtomSets.remove(itor);
                break;
            };
        }
        NumberOfSets--;
        return;
    }
    
    public void RemoveType(float x, float y, float z){
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).AtomMatches(x, y, z)){
                GhostAtomSets.remove(itor);            
            }
        }
        
        
    }
    
    // add but don't include in the list yet
    public void ExcludeProposed(){
        DisplayProposed = false;
        ProposedSet = null;
        SetProposed = false;        
    }
    
    
// highlight Section    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    // to set a highlight flag on a particular set of atoms
    public void HighlightType(float x, float y, float z){        
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).AtomMatches(x, y, z)){                
                GhostAtomSets.get(itor).Highlight();              
            }
        }
    
    }
    
    public void HighlightType(int TypeIndexValue){
      for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                GhostAtomSets.get(itor).Highlight(); 
            };
        }    
    }
    
    // to set a highlight flag on a particular set of atoms
    public void UnHighlightType(float x, float y, float z){        
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).AtomMatches(x, y, z)){
                GhostAtomSets.get(itor).UnHighlight();
            }
        }
    
    }
    
     // to set a highlight flag on a particular set of atoms
    public void UnHighlightType(int TypeIndexValue){        
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                GhostAtomSets.get(itor).UnHighlight(); 
            };
        }    
    }
    
    public boolean IsHighlighted(float x, float y, float z){
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).AtomMatches(x, y, z)){
               return GhostAtomSets.get(itor).IsHighlighted();
            }
        }
        // couldn't find, false by default
        return false;
    }
    
    public boolean IsHighlighted(int TypeIndexValue){        
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                return GhostAtomSets.get(itor).IsHighlighted();
            };
        }
        // couldn't find, false by default
        return false;
    }
    
    
    
// Atom list return Section    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    
    // return every atom stored in this class including the proposed atoms 
    
    public ArrayList<GhostAtomType> GetCurrentGhostAtomTypes(){
        return GhostAtomSets;
    }
     public GhostAtomType GetProposedGhostAtomType(){
        return ProposedSet;
    }
    public ArrayList<GhostAtom> GetAllAtoms(){                
        if(!CurrentAtoms.isEmpty())CurrentAtoms.clear();        
        //proposed
        CurrentAtoms.addAll(ProposedSet.GetAtomList());
        //currently listed
        for(int itor = 0; itor < GhostAtomSets.size(); itor++){
            CurrentAtoms.addAll(GhostAtomSets.get(itor).GetAtomList());
        }
        return CurrentAtoms;
    }
    
    // return the list of atoms, minus the proposed set
    public ArrayList<GhostAtom> GetAtoms(){
                
        if(!CurrentAtoms.isEmpty())CurrentAtoms.clear();
                
        //currently listed
        for(int itor = 0; itor < GhostAtomSets.size(); itor++){
            CurrentAtoms.addAll(GhostAtomSets.get(itor).GetAtomList());
        }
        return CurrentAtoms;
    }
    
    public ArrayList<GhostAtom> GetProposedAtoms(){
        if(ProposedSet == null){
            System.out.println("Proposed Atoms Already Deleted: GhostAtomSet.java");
            return null;
        };
        
        
        return ProposedSet.GetAtomList();
    }
    
    // return the list of highlighted atoms 
    public ArrayList<GhostAtom> GetHighlightedAtoms(){
    
        if(!CurrentAtoms.isEmpty())CurrentAtoms.clear();
        
         //currently listed
        for(int itor = 0; itor < GhostAtomSets.size(); itor++){
            // if the whole set is highlighted add it
            if(GhostAtomSets.get(itor).IsHighlighted){
               CurrentAtoms.addAll(GhostAtomSets.get(itor).GetAtomList());        
            // check for individual atoms in unhighlighted sets
            }else{
                CurrentAtoms.addAll(GhostAtomSets.get(itor).GetHighlightedAtoms());
            }
        }
        return CurrentAtoms;
        
    }
    
    
    
//  Section to detail with the plane obtained from the atom selection interface
// make it observable for the java swing interface    
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------    
// public variable to store the current plane needed to generate ghost atoms
// in general coordinates    
    private Plane CurrentPlane = new Plane();
    public void AddPointToPlane(Vector4f point){
        CurrentPlane.AddPoint(point);
        // if a plane now exists, tell everyone who's asked to know
        if(CurrentPlane.PlaneCalculated()){
            setChanged();
            notifyObservers();
        }
    }
    
    public void ClearPlanePoints(){
        CurrentPlane.ClearPlanePoints();
         setChanged();
         notifyObservers();
    }
    
    public void SetPlaneXAxisPoint(Vector3f Point){
        CurrentPlane.AddXProjectionAtom(Point);
        if(CurrentPlane.PlaneCalculated()){
            setChanged();
            notifyObservers();
        }
       
       
    }
    public void RemoveCurrentXAxisPoint(){
        CurrentPlane.RemoveCurrentXProjectionAtom();
    }
    public void RemovePointFromPlane(int AtomIndex){
        CurrentPlane.RemovePoint(AtomIndex);

        // actually always notify, even if plane still exists, changing the number
        // of points changes the plane equation
        setChanged();
        notifyObservers();
        
    }
    
    public boolean PlaneCalculated(){
        return CurrentPlane.PlaneCalculated();       
    }
    
    public Vector4f GetPlaneEquation(){
        return CurrentPlane.GetPlaneEquation();
    };
    public Plane GetPlane(){
        return CurrentPlane;
    }

    public String GetPlaneNormal(){
        String PlaneNormalString = new String("");
        PlaneNormalString =  PlaneNormalString.concat((new BigDecimal(CurrentPlane.GetPlaneNormal().x)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
        PlaneNormalString =  PlaneNormalString.concat((new BigDecimal(CurrentPlane.GetPlaneNormal().y)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
        PlaneNormalString =  PlaneNormalString.concat((new BigDecimal(CurrentPlane.GetPlaneNormal().z)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "\n");
        
        return PlaneNormalString;
    }
    
    public void GlobalCoordinatePlane(float X_Origin, float Y_Origin, float Z_Origin){
        CurrentPlane = new Plane();
        CurrentPlane.GlobalCoordinatePlane(new Vector3f(X_Origin, Y_Origin, Z_Origin));
    }
    
    
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
    public int GetAtomsInType(int TypeIndexValue){
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                return GhostAtomSets.get(itor).GetAtomList().size();                
            };
        }
        // shouldn't get here
        System.out.println("Error in GetAtomsInType, GhostAtomSet");
        System.out.flush();
        assert(false);
        return 0;
    }
//section to write the calculated data to file (or return a formatted string
// containing it
// or return an array filled with the coordinates of all of the ghost atoms
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
    
    
    // array is indexed from 0
    public float [][] GetGhostAtomArray(){
        ArrayList<GhostAtom> ToConvert =  GetAtoms();
        if(ToConvert != null){
            float [][] GhostAtomArray = new float[ToConvert.size()][4];

            for(int itor = 0; itor < ToConvert.size(); itor++){
                GhostAtomArray[itor][0] = 0;
                GhostAtomArray[itor][1] = ToConvert.get(itor).x;
                GhostAtomArray[itor][2] = ToConvert.get(itor).y;
                GhostAtomArray[itor][3] = ToConvert.get(itor).z;
            }
            
            return GhostAtomArray;
        }else{
            return null;
        }
    }
    
    
    // write all of the current atoms into a string in xyz format 
    public String GetGhostAtomString(){
        String GhostAtomString = new String("");
        ArrayList<GhostAtom> ToPrint = GetAtoms();
       //use "Big Decimal java formatting and toPlainString"???
        
        for(int itor = 0; itor < ToPrint.size(); itor++){
           GhostAtomString = GhostAtomString.concat("Bq ");
           //http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#setScale(int,%20java.math.RoundingMode)
          // http://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html
           GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).x)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
           GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).y)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
           GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).z)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "\n");
        }
        
        return GhostAtomString;
    }
/*
// write the atoms to file in a format specified by the user     
    public void WriteGhostAtomFile(Path FilePath, String FileType){
        
        String GhostAtomString = new String("");
        ArrayList<GhostAtom> ToPrint = GetAtoms();
        Charset charset = Charset.forName("US-ASCII");
        BufferedWriter OutputBuffer;
        
        try{
            OutputBuffer = Files.newBufferedWriter(FilePath, charset);
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        
        if(FileType.matches("txt")){
            for(int itor = 0; itor < ToPrint.size(); itor++){
                GhostAtomString = GhostAtomString.concat("Bq ");
                //http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#setScale(int,%20java.math.RoundingMode)
                // http://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).x)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).y)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).z)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "\n");
                try{
                    OutputBuffer.write(GhostAtomString);
                }catch(IOException e){
                    System.out.println("In WriteGhostAtomFile, writing atom: " + itor + " Error: " + e.getMessage());
                }               
                // empty the string and start again
                GhostAtomString = "";
               
            }
        }else if(FileType.matches("csv")){
            for(int itor = 0; itor < ToPrint.size(); itor++){
                GhostAtomString = GhostAtomString.concat("Bq,");
                //http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#setScale(int,%20java.math.RoundingMode)
                // http://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).x)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + ",");
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).y)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + ",");
                GhostAtomString =  GhostAtomString.concat((new BigDecimal(ToPrint.get(itor).z)).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "\n");



                try{
                    OutputBuffer.write(GhostAtomString);
                }catch(IOException e){
                    System.out.println("In WriteGhostAtomFile, writing atom: " + itor + " Error: " + e.getMessage());
                }
                // empty the string and start again              
                GhostAtomString = "";
            }
        }
        try{
            OutputBuffer.close();   
        }catch(IOException e){
            System.out.println("At end of WriteGhostAtomFile: " + e.getMessage());
        }
    }
    */
}
