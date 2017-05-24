/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import java.util.ArrayList;
import java.util.Observable;
import org.joml.Vector4f;
import MeanPlane.*;
import java.math.BigDecimal;
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
    

//variables to store information on ghost atom types and lists       
        
    int NumberOfSets = 0;
    int TypeIndex = 0;
    
    
    boolean SetProposed = false;
    GhostAtomType ProposedSet = null;
    
    ArrayList<GhostAtomType> GhostAtomSets = new ArrayList();
    
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
        ProposedSet = GhostType;        
    }
    
    // add teh proposed to the list
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
        return ProposedSet.GetAtomList();
    }
    
    // return the list of highlited atoms 
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
// public varialbe to store the current plane needed to generate ghost atoms
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
    
    public void RemovePointFromPlane(int AtomIndex){
        CurrentPlane.RemovePoint(AtomIndex);
        // if a plane now exists, tell everyone who's asked to know
        if(!CurrentPlane.PlaneCalculated()){
            setChanged();
            notifyObservers();
        }
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
    
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
    public int GetAtomsInType(int TypeIndexValue){
        for (int itor = 0; itor < GhostAtomSets.size(); itor++){
            if(GhostAtomSets.get(itor).Identifier == TypeIndexValue){
                return GhostAtomSets.get(itor).GetAtomList().size();                
            };
        }
        // shouldn't get here
        return 0;
    }
//section to write the calculated data to file (or return a formatted string
// containing it
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
    
    // write all of the current atoms to a string in xyz format 
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
    
  
    
}
