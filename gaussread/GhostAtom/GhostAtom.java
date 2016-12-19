/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GhostAtom;

/**
 *
 * @author David Joshua Dibble
 */
public class GhostAtom {

    // atom set is to identify the ghost atom within as belonging particular collection
    // non-specific as the collection should be specified elsewhere
    public int AtomSet;
    // AtomIndex is to give each atom a unique identifier within a collection
    public int AtomIndex;
    //coordinates of the ghost atom
    public float x,y,z;
}
