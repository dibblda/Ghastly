/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;
import java.util.ArrayList;
import MeanPlane.*;
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
public class GhostAtomSet {
    
// public varialbe to store the current plane needed to generate ghost atoms
// in general coordinates    
    public Plane CurrentPlane = new Plane();
//variables to store information on ghost atom types and lists       
        
    int NumberOfSets = 0;
    int TypeIndex = 0;
    
    
    boolean SetProposed = false;
    GhostAtomType ProposedSet;
    
    ArrayList<GhostAtomType> GhostAtomSets = new ArrayList();
    
    ArrayList<GhostAtom> CurrentAtoms = new ArrayList();
    
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
    public void IncludeProposed(GhostAtomType GhostType){
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
    
// Atom list return Section    
//------------------------------------------------------------------------------    
//------------------------------------------------------------------------------    
    
    // return every atom stored in this class including the propsed atoms     
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

}
