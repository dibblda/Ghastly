/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileIO;

/**
 *
 * @author djosh
 */
public class FileIOParameters {
    
// index is atomic number, value is maximum valency...this does not consider charge
// and is only a basic pass through for sanity check
// goes up to AN 103
//
// values from http://www.ptable.com based on max bonds to fluorine
// modifications (atom, original, new)
// (B, 3, 4), (N, 3, 4), (O, 2, 3), (Si, 4, 6)
public static int [] AtomicValenceMax = {0,1,0,1,2,4,4,4,3,1,0,
                           1,2,3,6,5,6,5,0,1,2,
                           3,4,5,6,4,3,4,4,2,2,
                           3,4,5,6,7,4,1,2,3,4,
                           5,6,7,6,6,4,4,2,3,4,
                           5,6,7,6,3,2,3,4,4,3,
                           3,3,3,3,4,3,3,3,3,3,
                           3,4,5,6,7,7,6,6,7,2,
                           3,4,5,6,7,6,3,2,3,4,
                           5,6,6,6,4,4,4,4,4,3,
                           3, 3, 3};

// atomic radii table indexed by atomic number.  many are unfilled. table size 97 
public static double [] AtomicRadii = {-100,0.35,-100,0.85,0.56,0.39,0.73,0.66,0.69,0.68,-100,
                                        1.10,0.81,0.64,1.11,1.04,0.96,0.95,-100,1.43,1.08,
                                        0.84,0.77,0.74,0.72,0.84,0.70,0.79,0.79,0.82,0.83,
                                        0.72,0.82,1.13,1.12,1.08,-100,-100,1.25,-100,-100,
                                        -100,-100,-100,0.78,0.76,0.95,1.02,1.03,-100,-100,
                                        -100,-100,1.26,-100,-100,1.41,-100,-100,-100,-100,
                                        -100,-100,-100,-100,-100,-100,-100,-100,-100,-100,
                                        -100,-100,-100,-100,-100,-100,-100,0.89,1.42,1.26,
                                        1.55,1.26,-100,-100,-100,-100,-100,-100,-100,-100,
                                        -100,-100,-100,-100,-100,-100};

// up to AN 96 bond radii based on statistical analysis of CSD as presented by wikipedia
// this isn't currently in use in the program but may be in the future

public static double [] CovalentRadii = {0.00,0.32,0.28,1.29,0.96,0.84,0.76,0.71,0.66,0.57,0.58,
                                        1.67,1.42,1.21,1.11,1.07,1.05,1.02,1.06,2.03,1.76,
                                        1.71,1.61,1.54,1.40,1.40,1.32,1.26,1.24,1.32,1.22,
                                        1.22,1.20,1.19,1.20,1.20,1.16,2.21,1.95,1.91,1.76,
                                        1.65,1.55,1.48,1.47,1.43,1.40,1.46,1.45,1.43,1.39,
                                        1.40,1.38,1.39,1.41,2.44,2.15,2.08,2.05,2.04,2.02,
                                        1.99,1.99,1.99,1.97,1.95,1.93,1.93,1.90,1.90,1.88,
                                        1.88,1.75,1.71,1.63,1.52,1.44,1.42,1.37,1.37,1.33,
                                        1.46,1.47,1.48,1.40,1.50,1.50,2.60,2.21,2.15,2.07,
                                        2.00,1.97,1.90,1.87,1.81,1.69};

 // atomic symbols listed by atomic number for lookup table size 97
static public String [] AtomicSymbol = {"Bq","H","He","Li","Be","B","C","N","O","F","Ne",
                                        "Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca",
                                        "Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn",
                                        "Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr",
                                        "Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn",
                                        "Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd",
                                        "Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb",
                                        "Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg",
                                        "Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th",
                                        "Pa","U","Np","Pu","Am","Cm"};



// non-single bond not implemented 
public static int SINGLE_BOND = 10;
public static int DOUBLE_BOND = 20;
public static int TRIPLE_BOND = 30;
public static int DATIVE_BOND = 5;
public static int DOUBLE_RESONANCE_BOND = 15;

// Minimum bond angle accepted for any new bond during sanity check
public static double BOND_ANGLE_MINIMUM = 59;
// Maximum bond angle for a molecule with valency > 2
public static double BOND_ANGLE_MAXIMUM_NON_LINEAR = 160;
// Maximum valance for atom center with BOND_ANGLE_MAXIMUM_NON_LINEAR bond
public static int VALENCE_MAXIMUM_NON_LINEAR = 3;



}
