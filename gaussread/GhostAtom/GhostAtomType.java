/**
 *
 * @author David Joshua Dibble
 */

package GhostAtom;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Vector4f;

// this will store the different types of ghost atom types
// including NICS[zz] NICS XY planes, NICS scan, NICS Grid etc etc to be implemented as 
// they come up.
// this will be storded under 
public class GhostAtomType implements Cloneable{
    int SetType; // 0 = NICS(0) 1 = NICS(1) 2 = NICS(Scan) 3 = NICS(GRID)
    int Identifier;
    
    
    ArrayList<GhostAtom> Atoms;
    
    // usefull for giving out information about the plane the atoms are on
    Vector3f NormalToPlane;
    Vector4f PlaneEquation;
    
    // function to allow cloning of the data
    public Object clone() throws
                   CloneNotSupportedException
    {
        return super.clone();
    }
    
    // UI to keep track of if the user has highlighted this particular set for
    //further examination
    boolean IsHighlighted = false;
    int HighlightLevel = 0;
    
    public ArrayList<GhostAtom> GetAtomList(){
        return Atoms;
    }
    
    public int GhostSetType(){
        return SetType;
    } 
    
    public boolean AtomMatches(float x, float y, float z){
        
        for(int itor = 0; itor < Atoms.size(); itor++){
            if((x == Atoms.get(itor).x) && (y == Atoms.get(itor).y) && (z == Atoms.get(itor).z)){
                return true;
            }
        }
        
        
        return false;
    }
    
    public void Highlight(){
        IsHighlighted = true;
         // for different levels of selection highlighting
        if(HighlightLevel < 2){
            HighlightLevel++;
        }
        for(int itor = 0; itor < Atoms.size(); itor++){
            Atoms.get(itor).Highlight();                   
        }
        
        
    }
    
    
    
    
     public void UnHighlight(){
        IsHighlighted = false;
         // for different levels of selection highlighting
        if(HighlightLevel > 0){
            HighlightLevel--;
        }
        for(int itor = 0; itor < Atoms.size(); itor++){
            Atoms.get(itor).UnHighlight();                   
        }
    }         
     
    public boolean IsHighlighted(){
        return IsHighlighted;
    }
     
    public ArrayList<GhostAtom> GetHighlightedAtoms(){
        ArrayList<GhostAtom> TemporaryList = new ArrayList();
        for(int itor = 0; itor < Atoms.size(); itor++){
            if(Atoms.get(itor).IsHighlighted){
               TemporaryList.add(Atoms.get(itor));
            }
        }
        
        return TemporaryList;
        
    }
    
   
    
}
