
import OpenGLEngine.MoleculeDisplay;
import GhostAtom.GhostAtomSet;
import OpenGLEngine.MoleculeDisplay;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David Joshua Dibble
 */
public class Main {
    
      // classes and data for communicating between components
    public Object GhastlyLock = new Object();
    public static MoleculeDisplay molecule = new MoleculeDisplay();;
    public static GhostAtomSet Ghastly = new GhostAtomSet();    
    
    public static void main(String args[]) {
    final GhastlyGUI Watching = new GhastlyGUI(molecule, Ghastly);
       

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Start the program
                Watching.setVisible(true);
                
            }
        });
        
        Ghastly.addObserver(Watching);
        
        // Get the molecular display running                 
        molecule.start();
        
    }
}
