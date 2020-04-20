package FileIO;

import java.io.*;

public class FileReading{

// Class Variables

// File Parameters and buffers

RandomAccessFile InputFile = null;
long randomAccessBeginning = 0;
long randomAccessCheckpoint = 0;

// Job Parameters 


boolean ZMatrixRead = false;
int numberAtoms = 0;
float moleculeCharge = 0;
float multiplicity = 0;
boolean AtomCountFinished = false;


// determine which atoms are bonded to which atoms.  
// array index is atom number.  0 is unused. 
// Start initially as 1 or 0 to indicate bond and fill in lengths as needed
int [][] BondMatrix = null;
boolean BondMatrixComplete = false;
String [] AtomLabel = null;
// index is atom number, value is the number of bonds 
int [] AtomValence = null;




// first dimension is atom number, second dimension is atomic number and coordinates [atom #][Atomic #/x/y/z]
boolean InputOrientationRead = false;
float [][] InputAtomicCoordinates = null;
float [][] interatomicDistanceArray = null;
// for normalized (centered) display of molecule. Store the displacement from the original for later coordinate calculations
float [] normalizationVector = null;

// associated with mol2 files
//float AtomicCharge = 0;
String MoleculeType; // assumption unless proved otherwise
String ChargeType;
float [] AtomicCharge;
int NumberOfBonds = 0;

//Creator
public FileReading (File input_file, String filetype) throws IOException {
	
 
        //System.out.println("File name: " + filetype + "\n");
        // check if the file type is a gaussian com file, then start reading in
        if(input_file.getName().endsWith(".com")){
            
            try{
                    //open file, and get beggining pointer
                    InputFile = new RandomAccessFile(input_file, "r");

                    randomAccessBeginning = InputFile.getFilePointer();

                    // reset file to beginning 
                    InputFile.seek(randomAccessBeginning);
                    // read the file coordinates
                     RecordComFileCoordinates();
               
                    // calculate bonding based on the XYX coordinates
                    CoordinateBondDetermination();
                   
            }

            catch(FileNotFoundException e){
                        System.out.println("Whoopsie, where's the file? " + e);
            }
        }
        // check if the file type is a xyz file, then start reading in
        if(input_file.getName().endsWith(".xyz")){
           
            
            try{
                    //open file, and get beginning pointer
                    InputFile = new RandomAccessFile(input_file, "r");

                    randomAccessBeginning = InputFile.getFilePointer();

                    // reset file to beginning 
                    InputFile.seek(randomAccessBeginning);
                  
                    
                    // read the file coordinates
                    RecordXYZFileCoordinates();
               
                    System.out.println("XYZ File coordinates read");

                    
                    // calculate bonding based on the XYX coordinates
                    CoordinateBondDetermination();
                    
                   
                   
            }

            catch(FileNotFoundException e){
                        System.out.println("Whoopsie, where's the file? " + e);
            }
        }
        
        if(input_file.getName().endsWith(".mol")){
           
            try{
                    //open file, and get beggining pointer
                    InputFile = new RandomAccessFile(input_file, "r");

                    randomAccessBeginning = InputFile.getFilePointer();

                    // reset file to beginning 
                    InputFile.seek(randomAccessBeginning);
                    // read the file coordinates
                     RecordMOLFileCoordinates();
               
                    // calculate bonding based on the XYX coordinates
                    CoordinateBondDetermination();
                   
            }

            catch(FileNotFoundException e){
                        System.out.println("Whoopsie, where's the file? " + e);
            }
        }
        
        if(input_file.getName().endsWith(".mol2")){
           
            try{
                    //open file, and get beggining pointer
                    InputFile = new RandomAccessFile(input_file, "r");

                    randomAccessBeginning = InputFile.getFilePointer();

                    // reset file to beginning 
                    InputFile.seek(randomAccessBeginning);
                    // read the file coordinates
                     RecordMOL2FileCoordinates();
               
                    // calculate bonding based on the XYX coordinates
                    CoordinateBondDetermination();
                   
            }

            catch(FileNotFoundException e){
                        System.out.println("Whoopsie, where's the file? " + e);
            }
        }
         if(input_file.getName().endsWith(".pdb")){
           
            try{
                    //open file, and get beggining pointer
                    InputFile = new RandomAccessFile(input_file, "r");

                    randomAccessBeginning = InputFile.getFilePointer();

                    // reset file to beginning 
                    InputFile.seek(randomAccessBeginning);
                    // read the file coordinates
                     RecordPDBFileCoordinates();
                     
                     System.out.println("PDB File coordinates read");
               
                    // calculate bonding based on the XYX coordinates
                    CoordinateBondDetermination();
                   
            }

            catch(FileNotFoundException e){
                        System.out.println("Whoopsie, where's the file? " + e);
            }
        }
        
        
       System.out.println("Done reading File");
}


private void RecordPDBFileCoordinates() throws IOException {
    String line = "This line is blank";
    
    int A_itor, B_itor, itor;
    boolean breakloop = false;
    
    // fast forward to the atom specification section 
    while(!(line.substring(0,6).trim().contentEquals("ATOM")||line.substring(0,6).trim().contentEquals("HETATM"))){
        line = InputFile.readLine();        
    };
    //you've hit the first atom specification
    numberAtoms++;
    //count the remaining
    line = InputFile.readLine(); 
    if(line.length() < 6)breakloop = true;
    while((line.substring(0,6).trim().contentEquals("ATOM")||line.substring(0,6).trim().contentEquals("HETATM"))&&!breakloop){
              
        numberAtoms++;
        line = InputFile.readLine();  
        if(line.length() < 6)break;
        if(line == null)break;
    };
    

    // allocate memory for storing the atom information 

     //allocate memory to store bond connectivity and label
    BondMatrix = new int [numberAtoms + 1][numberAtoms + 1];
    for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
        for(B_itor = 1; B_itor < numberAtoms + 1; B_itor++){
                BondMatrix[A_itor][B_itor] = 0;
        }
    }
    // allocate memory for input coordinate matrix

    InputAtomicCoordinates = new float [numberAtoms + 1][4];

    // allocate memory to store the atom labels
    AtomLabel = new String[numberAtoms + 1];
    // allocate space  to store the nume of bonds to each atom in the matrix
    AtomValence = new int [numberAtoms + 1];
    
    
    // return to file beginning and then forward to atom specification section
    // read in the first atom
    InputFile.seek(randomAccessBeginning);
    line = InputFile.readLine();
    while(!(line.substring(0,6).trim().contentEquals("ATOM")||line.substring(0,6).trim().contentEquals("HETATM"))){
        line = InputFile.readLine();                  
    };    
    
    AtomLabel[1] =  new String(line.substring(76,78).trim());
    //enter atomic number and coordinates            
    InputAtomicCoordinates[1][0] = (float)AtomicNumberLookup(line.substring(76,78).trim());
    InputAtomicCoordinates[1][1] = Float.parseFloat(line.substring(30,38).trim());
    InputAtomicCoordinates[1][2] = Float.parseFloat(line.substring(38,47).trim());
    InputAtomicCoordinates[1][3] = Float.parseFloat(line.substring(46,55).trim());
    
    
    //now loop though and read in the subsequent atoms
    for(itor = 2; itor <= numberAtoms; itor++){      
        //line containing an atom specification should have (atom #) (atom) X Y Z      
        line = InputFile.readLine();                
        AtomLabel[itor] =  new String(line.substring(76,78).trim());
        //enter atomic number and coordinates            
        InputAtomicCoordinates[itor][0] = (float)AtomicNumberLookup(line.substring(76,78).trim());
        InputAtomicCoordinates[itor][1] = Float.parseFloat(line.substring(30,38).trim());
        InputAtomicCoordinates[itor][2] = Float.parseFloat(line.substring(38,47).trim());
        InputAtomicCoordinates[itor][3] = Float.parseFloat(line.substring(46,55).trim());            
    };

    
    // flip a boolean letting rest of code know we are all good
    InputOrientationRead = true;
    
}






private void RecordMOL2FileCoordinates() throws IOException {
    String line = null;
    int A_itor, B_itor, itor;
    
        
    // fast forward to the molecule specification section 
    while(!InputFile.readLine().contentEquals("@<TRIPOS>MOLECULE"));
    // read the number of atoms in the molecule in the molecule specification section
    InputFile.readLine();
    line = InputFile.readLine();
    line.replace("\\t","\\s");
    // break up into the individual components
    String[] tokens = line.trim().split("\\s+");
    // read the number of atoms
    numberAtoms = Integer.parseInt(tokens[0]);
    // next line is molecule type
    MoleculeType = InputFile.readLine();
    // next line is the atomic charge type
    ChargeType = InputFile.readLine();
    
    // allocate memory for storing the atom information 

     //allocate memory to store bond connectivity and label
    BondMatrix = new int [numberAtoms + 1][numberAtoms + 1];
    for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
        for(B_itor = 1; B_itor < numberAtoms + 1; B_itor++){
                BondMatrix[A_itor][B_itor] = 0;
        }
    }
    // allocate memory for input coordinate matrix and atomic charges

    InputAtomicCoordinates = new float [numberAtoms + 1][4];
   
    AtomicCharge = new float [numberAtoms + 1];
    // set charge to zero by default in case it isn't present in the file
    for(itor = 0; itor < numberAtoms + 1; itor++){
        AtomicCharge[itor] = 0;
    }
   
    
// allocate memory to store the atom labels
    AtomLabel = new String[numberAtoms + 1];
    // allocate space  to store the nume of bonds to each atom in the matrix
    AtomValence = new int [numberAtoms + 1];
    
    
    // return to file beginning and then forward to atom specification section
    InputFile.seek(randomAccessBeginning);
    while(!InputFile.readLine().contentEquals("@<TRIPOS>ATOM"));
    
    // read in the atoms
        for(itor = 1; itor <= numberAtoms; itor++){      
            //line containing an atom specification should have (atom #) (atom) X Y Z      
            line = InputFile.readLine();
             
                
            // swap out tabs with spaces if they exist
            line.replace("\\t","\\s");
            // break up into the individual components
            tokens = line.trim().split("\\s+");
 
            AtomLabel[itor] =  new String(tokens[1]);
            //enter atomic number and coordinates            
            InputAtomicCoordinates[itor][0] = (float)AtomicNumberLookup(tokens[1]);
            InputAtomicCoordinates[itor][1] = Float.parseFloat(tokens[2]);
            InputAtomicCoordinates[itor][2] = Float.parseFloat(tokens[3]);
            InputAtomicCoordinates[itor][3] = Float.parseFloat(tokens[4]);
            // input charge, if it seems to exist
            if(tokens.length >= 9){
                AtomicCharge[itor] = Float.parseFloat(tokens[8]);
            }
        }
  
        // flip a boolean letting rest of code know we are all good
        InputOrientationRead = true;
        //
        // Although bonding is available in a mol2 file we are ignoring it. my code handles the bonding guesses well enough.
        //
    
    
    
}


private void RecordMOLFileCoordinates() throws IOException {
    String line = null;
    int A_itor, B_itor, itor;
        
        // first three lines are irrelevent
        InputFile.readLine();
        InputFile.readLine();
        InputFile.readLine();
        //InputFile.readLine();
        // now you should be at the header portion, the first number is the number of atoms
        line = InputFile.readLine();
        // swap out tabs with spaces if they exist
        line.replace("\\t","\\s");
        // break up into the individual components
        String[] tokens = line.trim().split("\\s+");
        // read the number of atoms
        numberAtoms = Integer.parseInt(tokens[0]);
      
        
        
        // allocate memory for storing the atom information 
                 
         //allocate memory to store bond connectivity and label
        BondMatrix = new int [numberAtoms + 1][numberAtoms + 1];
        for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
                for(B_itor = 1; B_itor < numberAtoms + 1; B_itor++){
                        BondMatrix[A_itor][B_itor] = 0;
                }
        }
        // allocate memory for input coordinate matrix
        
        InputAtomicCoordinates = new float [numberAtoms + 1][4];
        
        // allocate memory to store the atom labels
        AtomLabel = new String[numberAtoms + 1];
        // allocate space  to store the nume of bonds to each atom in the matrix
        AtomValence = new int [numberAtoms + 1];
    
        
        // now loop though from first atom to the last, entering in the atom label, number, and coordinates 

        for(itor = 1; itor <= numberAtoms; itor++){      
            //line containing an atom specification should have X Y Z (atom)             
            line = InputFile.readLine();
             
                
            // swap out tabs with spaces if they exist
            line.replace("\\t","\\s");
            // break up into the individual components
            tokens = line.trim().split("\\s+");
 
            AtomLabel[itor] =  new String(tokens[3]);
            //enter atomic number and coordinates            
            InputAtomicCoordinates[itor][0] = (float)AtomicNumberLookup(tokens[3]);
            InputAtomicCoordinates[itor][1] = Float.parseFloat(tokens[0]);
            InputAtomicCoordinates[itor][2] = Float.parseFloat(tokens[1]);
            InputAtomicCoordinates[itor][3] = Float.parseFloat(tokens[2]);                                                                                              
           
        }
  
        // flip a boolean letting rest of code know we are all good
        InputOrientationRead = true;
        //
        // Although bonding is available in a mol file we are ignoring it. my code handles the bonding guesses well enough.
        //
}



// read in xyz file format from the file pointer
private void RecordXYZFileCoordinates() throws IOException {
    String line = null;
    boolean AtomCountFinished = false;
    float atomic_number;
    int A_itor, B_itor, itor;
    

         
        // assume first two lines are probably number of atoms and then a comment, throw them out
        // not neccesarily true in all cases
        InputFile.readLine();
        InputFile.readLine();
        // now you should be just before the first line
        randomAccessCheckpoint = InputFile.getFilePointer();
        
        // count the number of atoms in the file past this
         while(!AtomCountFinished){
            line = InputFile.readLine();
            if(!line.trim().isEmpty()){
                numberAtoms += 1;                
                
            }else{
                AtomCountFinished = true;
            }
            //System.out.println("Made it here" + numberAtoms);
         }
         
         
         
        // allocate memory for storing the atom information 
                 
         //allocate memory to store bond connectivity and label
        BondMatrix = new int [numberAtoms + 1][numberAtoms + 1];
        for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
                for(B_itor = 1; B_itor < numberAtoms + 1; B_itor++){
                        BondMatrix[A_itor][B_itor] = 0;
                }
        }
        // allocate memory for input coordinate matrix
        
        InputAtomicCoordinates = new float [numberAtoms + 1][4];
        
        // allocate memory to store the atom labels
        AtomLabel = new String[numberAtoms + 1];
        // allocate space  to store the nume of bonds to each atom in the matrix
        AtomValence = new int [numberAtoms + 1];
  

        // now loop though from first atom to the last, entering in the atom label, number, and coordinates 
        // go back to first line
       
        InputFile.seek(randomAccessCheckpoint);
        
      
        
        
        //reenter the first line string
    
        for(itor = 1; itor <= numberAtoms; itor++){      
            //line containing an atom specification should have (atom) X Y Z so four columns             
            line = InputFile.readLine();
             
                
            // swap out tabs with spaces if they exist
            line.replace("\\t","\\s");
            // break up into the individual components
            String[] tokens = line.trim().split("\\s+");
            // there should only be four per line
            if(tokens.length == 4){
                 
                // if it's an tomic symbol read it otherwise it is an atomic number?
                if(IsAtomicSymbol(tokens[0])){
                    
                    AtomLabel[itor] =  new String(tokens[0]);
                    //enter atomic number and coordinates            
                    InputAtomicCoordinates[itor][0] = (float)AtomicNumberLookup(AtomLabel[itor]);
                    InputAtomicCoordinates[itor][1] = Float.parseFloat(tokens[1]);
                    InputAtomicCoordinates[itor][2] = Float.parseFloat(tokens[2]);
                    InputAtomicCoordinates[itor][3] = Float.parseFloat(tokens[3]);                                                                                              
                }else{
                    atomic_number = Integer.getInteger(tokens[0]);
                    if(atomic_number > 96) throw new IOException("File contains atom with atomic number > 96, probably an error");
                    InputAtomicCoordinates[itor][0] = (float)atomic_number;
                    InputAtomicCoordinates[itor][1] = Float.parseFloat(tokens[1]);
                    InputAtomicCoordinates[itor][2] = Float.parseFloat(tokens[2]);
                    InputAtomicCoordinates[itor][3] = Float.parseFloat(tokens[3]); 
                }
        
            
            }else{
                throw new IOException("File is improperly formatted. It does not contain a complete atom coordinate description");
            }
            
        }
        
        
        
        
        // flip a boolean letting rest of code know we are all good
        InputOrientationRead = true;
        
      System.out.println("Done Reading xyz coordinates:RecordXYZFileCoordinates"); 
      
}



/*
coordinate input scan from a com file is below.
first, second the file and determine the number of atoms, then allocate memory based on th is (also get the molecular charge)
second, rescan, recording the atoms and coordinates this time
*/

private void RecordComFileCoordinates(){
    String line = null;
    String firstline = null;
    boolean AtomCountFinished = false;
    float atomic_number;
    int A_itor, B_itor, itor;
  
    try{

        
        //advance to the atom coordinate matrix
        //reading lined until it is ready to be read in (Three past teh job specification lione
        // the charge and multuiplicity starts then on to the atom coordinate line
        line = InputFile.readLine();
        //get past the headers and route section
        while(line.startsWith("$")||line.startsWith("%")||line.startsWith("#"))line = InputFile.readLine();
        //read three more lines to get to the charge / multiplicity
        InputFile.readLine();
        InputFile.readLine();
        line = InputFile.readLine();
        //store the charge and multiplicity               
        String[] tokens = line.split("\\s+");
        moleculeCharge = Float.parseFloat(tokens[0]);
        multiplicity = Float.parseFloat(tokens[1]);	

        
        
        
        //first line of the atom and coordinate section
        line = InputFile.readLine();
        //mark its location
        randomAccessCheckpoint = InputFile.getFilePointer();
        //duplicate the data
        firstline = line;
        //up atom count if not blank
        if(!line.trim().isEmpty()){
            numberAtoms += 1;
        }else{
            AtomCountFinished = true;
        }
        //now loop the the rest until a blank line is encountered
        while(!AtomCountFinished){
            line = InputFile.readLine();
            if(!line.trim().isEmpty()){
                numberAtoms += 1;
            }else{
                AtomCountFinished = true;
            }
        }
        // atom count complete, now return, allocate memory for the atoms

        //System.out.println("Atom Count:" + numberAtoms + "\n"); 


        //allocate memory to store bond connectivity and label
        BondMatrix = new int [numberAtoms + 1][numberAtoms + 1];
        for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
                for(B_itor = 1; B_itor < numberAtoms + 1; B_itor++){
                        BondMatrix[A_itor][B_itor] = 0;
                }
        }
        // allocate memory for input coordinate matrix
        
        InputAtomicCoordinates = new float [numberAtoms + 1][4];
        
        // allocate memory to store the atom labels
        AtomLabel = new String[numberAtoms + 1];
        // allocate space  to store the nume of bonds to each atom in the matrix
        AtomValence = new int [numberAtoms + 1];


        // now loop though from first atom to the last, entering in the atom label, number, and coordinates 
        // go back to first line
        InputFile.seek(randomAccessCheckpoint);
        //reenter the first line string
        line = firstline; 
        for(itor = 1; itor <= numberAtoms; itor++){                      
            tokens = line.split("\\s+");           
            // enter atom label
            AtomLabel[itor] =  new String(tokens[1]);
            //enter atomic number and coordinates            
            InputAtomicCoordinates[itor][0] = (float)AtomicNumberLookup(AtomLabel[itor]);
            InputAtomicCoordinates[itor][1] = Float.parseFloat(tokens[2]);
            InputAtomicCoordinates[itor][2] = Float.parseFloat(tokens[3]);
            InputAtomicCoordinates[itor][3] = Float.parseFloat(tokens[4]);
            //get next line
            line = InputFile.readLine();
        }
        
        // flip a boolean letting rest of code know we are all good
        InputOrientationRead = true;
            
    }
    catch(IOException e){
            System.out.println("Duh Duh Dummmmmm " + e);
    }

  
    
}


// get the atomic number of the symbol
private float AtomicNumberLookup(String Label){
    float atomic_number = 0;
    int itor = 0;
    for(itor = 0; itor < FileIOParameters.AtomicSymbol.length; itor++){
        if(FileIOParameters.AtomicSymbol[itor].compareTo(Label) == 0){
            atomic_number = (float)itor;
            break;
        }
    }
    return atomic_number;       
}

//determine if the string is an atomic number
private boolean IsAtomicSymbol(String Label){
    int itor;
    for(itor = 0; itor < FileIOParameters.AtomicSymbol.length; itor++){
        if(FileIOParameters.AtomicSymbol[itor].equals(Label) == true){            
            return true;
        }
    }
    return false;
}


private void CoordinateBondDetermination(){
	float offsetValue = 0;
	float xSquare = 0, ySquare = 0, zSquare = 0, sumSquare = 0;
	float interatomicDistance = 0;
	float predictedBondLength = 0;
	float offsetMultiplier = 0;
	int A_itor, B_itor;

	if(InputOrientationRead == false){
		System.out.printf("Error: Bond determination was called before input orientation was read\n");
		return;
	}


	interatomicDistanceArray = new float [numberAtoms + 1][numberAtoms + 1];

	for(A_itor = 1; A_itor < numberAtoms + 1; A_itor++){
		//only need to start with the current atom for efficiency
		for (B_itor = A_itor; B_itor < numberAtoms + 1; B_itor++) {
			predictedBondLength = (float)(FileIOParameters.AtomicRadii[(int)InputAtomicCoordinates[A_itor][0]] + FileIOParameters.AtomicRadii[(int)InputAtomicCoordinates[B_itor][0]]);

			//determine the interatomic distance, square root of sum of squares
			xSquare = (float)Math.pow(InputAtomicCoordinates[A_itor][1] - InputAtomicCoordinates[B_itor][1], 2);
			ySquare = (float)Math.pow(InputAtomicCoordinates[A_itor][2] - InputAtomicCoordinates[B_itor][2], 2);
			zSquare = (float)Math.pow(InputAtomicCoordinates[A_itor][3] - InputAtomicCoordinates[B_itor][3], 2);
			sumSquare = xSquare + ySquare + zSquare;
			interatomicDistance = (float)Math.sqrt(sumSquare);

			// parameters for range of acceptable bonding values
			if(interatomicDistance <= 1.5)
				offsetMultiplier = 0.15f;
			if((interatomicDistance > 1.5) && (interatomicDistance <= 1.9))
				offsetMultiplier = 0.11f;
			if((interatomicDistance > 1.9) && (interatomicDistance <= 2.05))
				offsetMultiplier = 0.09f;
			if(interatomicDistance > 2.05)
				offsetMultiplier = 0.08f;
			offsetValue = interatomicDistance * offsetMultiplier;

			// all distances stored for bug checking purposes
			interatomicDistanceArray[A_itor][B_itor] = (float)interatomicDistance;
			interatomicDistanceArray[B_itor][A_itor] = (float)interatomicDistance;

			//determine if the bond is possible, and if so, check in matrix to see if it's there.  if not add it symmetrically

			//	Original algorithm had a minimum distance, missed some bonds. moved to no minimum but no self bonding
			//if((interatomicDistance > (predictedBondLength / 2.00)) && (interatomicDistance < (predictedBondLength + offsetValue))){
			if(interatomicDistance < (predictedBondLength + offsetValue)){
				//predicted to be a bond based on distance, 
				// Do a valency sanity check on the atoms connected by the new atomic bond.  If too high, dont form a bond 
				// Do an angle sanity check.  if the bond formed would lead to bond angles that are unrealistically low, don't form the bond
				//check the array to see if bond already exists first	
				if((BondMatrix[A_itor][B_itor] == 0) && (A_itor != B_itor)){
					if(GaussValencyGood(A_itor, B_itor) && GaussBondAngleGood(A_itor, B_itor)){											
						BondMatrix[A_itor][B_itor] = FileIOParameters.SINGLE_BOND;
						BondMatrix[B_itor][A_itor] = FileIOParameters.SINGLE_BOND;
						// increase the stored valency by one for each atom
						AtomValence[A_itor] += 1;
						AtomValence[B_itor] += 1;
                                                // total number of bonds goes up by one
                                                NumberOfBonds++;
					}
				}
			}

		}

	}
	
	BondMatrixComplete = true;
	

}

// Determine if the valency after addition of a new bond is physically possible
private boolean GaussValencyGood(int Atom_A, int Atom_B){

	if(InputOrientationRead == false){
		System.out.printf("Error: Sanity check of bond valency was called before input orientation was read\n");
		return false;
	}

	if((((int)InputAtomicCoordinates[Atom_A][0]) > (FileIOParameters.AtomicValenceMax.length - 1))||(((int)InputAtomicCoordinates[Atom_B][0]) > (FileIOParameters.AtomicValenceMax.length - 1))){
		System.out.printf("Warning: Atomic number of element in logfile is greater than value in AtomicValenceMax table, continuing with additonal bonding\n");
		return true;
	}

	if((AtomValence[Atom_A] + 1) > FileIOParameters.AtomicValenceMax[(int)InputAtomicCoordinates[Atom_A][0]])
		return false;
	if((AtomValence[Atom_B] + 1) > FileIOParameters.AtomicValenceMax[(int)InputAtomicCoordinates[Atom_B][0]])
		return false;

	return true;
}


// Determine if the bond angle of the additional bond is physically possible or at least reasonable
private boolean GaussBondAngleGood(int Atom_A, int Atom_B){
	// arrays to store the bond vector in including the proposed bond
	// don't need a 4 length, just matches the atomic coordinates array 
	float [][] bondVectorTestA = new float [AtomValence[Atom_A] + 1][4];  
	float [][] bondVectorTestB = new float [AtomValence[Atom_B] + 1][4];
	int Itor, Bond_itor;
	float dotProduct, magnitudeA, magnitudeB;
	float angleMeasurement = 0;

	if(InputOrientationRead == false){
		System.out.printf("Error: Sanity check of bond valency was called before input orientation was read\n");
		return false;
	}
	
	// if there are no other bonds, return true (nothing to measure an angle of)
	if((AtomValence[Atom_A] == 0) && (AtomValence[Atom_B] == 0)){
		return true;
	}
	// Testing for atom A
	// add the bound atom coordinates into the bond vector array normalized by origin atom
	Bond_itor = 0;
	for(Itor = 1; Itor < numberAtoms + 1; Itor++){
		if(BondMatrix[Atom_A][Itor] == FileIOParameters.SINGLE_BOND){
			bondVectorTestA[Bond_itor][1] = InputAtomicCoordinates[Itor][1] - InputAtomicCoordinates[Atom_A][1];
			bondVectorTestA[Bond_itor][2] = InputAtomicCoordinates[Itor][2] - InputAtomicCoordinates[Atom_A][2];
			bondVectorTestA[Bond_itor][3] = InputAtomicCoordinates[Itor][3] - InputAtomicCoordinates[Atom_A][3];
			Bond_itor += 1;
		};
	}
	// input coordinates for proposed bond normalized by origin atom
	bondVectorTestA[Bond_itor][1] = InputAtomicCoordinates[Atom_B][1] - InputAtomicCoordinates[Atom_A][1];
	bondVectorTestA[Bond_itor][2] = InputAtomicCoordinates[Atom_B][2] - InputAtomicCoordinates[Atom_A][2];
	bondVectorTestA[Bond_itor][3] = InputAtomicCoordinates[Atom_B][3] - InputAtomicCoordinates[Atom_A][3];

	// loop through bonds, comparing angles to proposed bond indexed at Bond_itor if a bond angle less than BOND_ANGLE_MINIMUM is found return false
	for(Itor = 0; Itor < Bond_itor; Itor++){
		// inverse cosine of magnitude adjusted dot product gives the angle between vectors

		dotProduct = bondVectorTestA[Bond_itor][1] * bondVectorTestA[Itor][1] + 
					 bondVectorTestA[Bond_itor][2] * bondVectorTestA[Itor][2] + 
					 bondVectorTestA[Bond_itor][3] * bondVectorTestA[Itor][3];

		magnitudeA = (float)Math.sqrt(Math.pow(bondVectorTestA[Bond_itor][1], 2) + 
							   Math.pow(bondVectorTestA[Bond_itor][2], 2) + 
							   Math.pow(bondVectorTestA[Bond_itor][3], 2));

		magnitudeB = (float)Math.sqrt(Math.pow(bondVectorTestA[Itor][1], 2) + 
						  	   Math.pow(bondVectorTestA[Itor][2], 2) + 
							   Math.pow(bondVectorTestA[Itor][3], 2));

		angleMeasurement = (float)Math.acos( dotProduct / (magnitudeA * magnitudeB));
							
		// convert to degrees
		angleMeasurement = (float)(angleMeasurement * 180 / Math.PI);
		if(angleMeasurement < FileIOParameters.BOND_ANGLE_MINIMUM){
			return false;
		}

		// if the bond is greater than BOND_ANGLE_MAXIMUM_NON_LINEAR and has at least a valecy of 2 not a real bond
		if(angleMeasurement > FileIOParameters.BOND_ANGLE_MAXIMUM_NON_LINEAR && (AtomValence[Atom_A] + 1) > FileIOParameters.VALENCE_MAXIMUM_NON_LINEAR){
			return false;
		}
	}

        // Testing for atom B
        // add the bound atom coordinates into the bond vector array normalized by origin atom
	Bond_itor = 0;
	for(Itor = 1; Itor < numberAtoms + 1; Itor++){
		if(BondMatrix[Atom_B][Itor] == FileIOParameters.SINGLE_BOND){
			bondVectorTestB[Bond_itor][1] = InputAtomicCoordinates[Itor][1] - InputAtomicCoordinates[Atom_B][1];
			bondVectorTestB[Bond_itor][2] = InputAtomicCoordinates[Itor][2] - InputAtomicCoordinates[Atom_B][2];
			bondVectorTestB[Bond_itor][3] = InputAtomicCoordinates[Itor][3] - InputAtomicCoordinates[Atom_B][3];
			Bond_itor += 1;
		};
	}
	// input coordinates for proposed bond normalized by origin atom
	bondVectorTestB[Bond_itor][1] = InputAtomicCoordinates[Atom_A][1] - InputAtomicCoordinates[Atom_B][1];
	bondVectorTestB[Bond_itor][2] = InputAtomicCoordinates[Atom_A][2] - InputAtomicCoordinates[Atom_B][2];
	bondVectorTestB[Bond_itor][3] = InputAtomicCoordinates[Atom_A][3] - InputAtomicCoordinates[Atom_B][3];

	// loop through bonds, comparing angles to proposed bond indexed at Bond_itor if a bond angle less than BOND_ANGLE_MINIMUM is found return false
	for(Itor = 0; Itor < Bond_itor; Itor++){
		// inverse cosine of magnitude adjusted dot product gives the angle between vectors
		dotProduct = bondVectorTestB[Bond_itor][1] * bondVectorTestB[Itor][1] + 
					 bondVectorTestB[Bond_itor][2] * bondVectorTestB[Itor][2] + 
					 bondVectorTestB[Bond_itor][3] * bondVectorTestB[Itor][3];

		magnitudeA = (float)Math.sqrt(Math.pow(bondVectorTestB[Bond_itor][1], 2) + 
							   Math.pow(bondVectorTestB[Bond_itor][2], 2) + 
							   Math.pow(bondVectorTestB[Bond_itor][3], 2));

		magnitudeB = (float)Math.sqrt(Math.pow(bondVectorTestB[Itor][1], 2) + 
						  	   Math.pow(bondVectorTestB[Itor][2], 2) + 
							   Math.pow(bondVectorTestB[Itor][3], 2));

		angleMeasurement = (float)Math.acos( dotProduct / (magnitudeA * magnitudeB));

		// convert to degrees
		angleMeasurement = (float)(angleMeasurement * 180 / Math.PI);

		if(angleMeasurement < FileIOParameters.BOND_ANGLE_MINIMUM){
			return false;
		}
		if(angleMeasurement > FileIOParameters.BOND_ANGLE_MAXIMUM_NON_LINEAR && (AtomValence[Atom_B] + 1) > FileIOParameters.VALENCE_MAXIMUM_NON_LINEAR){
			return false;
		}
	}

	// all the way through with no bad angles, therefore true
	return true;
}







// Return Public Information About the Structure

// Structure Energy
float OptEnergyValue(){
	return 0;
}

// may return null if charges don't exist, this is not entirely unlikely
public float [] AtomicCharge(){
    if(AtomicCharge != null){
        float [] SafeAtomicCharge = new float[AtomicCharge.length];
        for(int itor = 1; itor < AtomicCharge.length; itor++){
            SafeAtomicCharge[itor] =  AtomicCharge[itor];
        }
        return SafeAtomicCharge;
    }else{
        return null;
    }
}

public String [] AtomicSymbol(){
    String [] SafeAtomicLabel = new String[AtomLabel.length];
    for(int itor = 1; itor < AtomLabel.length; itor++){
        SafeAtomicLabel[itor] =  new String(AtomLabel[itor]);
    }
    
    return SafeAtomicLabel;
}


// first dimension is atom number, second dimension is atomic number and coordinates [0/1/2/3] is [A#/x/y/z]
// determine average position and normalize coordinates so that they appear around the average position
//boolean InputOrientationRead = false;
//double [][] InputAtomicCoordinates = null;

public float [][] AtomicCoordinates(boolean ReturnNormalized){
	float [][] safeAtomicCoordinates = null;
	int A_itor, B_itor;
	double averageX, sumX, averageY, sumY, averageZ, sumZ;
	


	assert(InputOrientationRead);

	// Pass a copy of the array, not the array itself to be on the safe side

	// just the input orientation read as you might find on single point job
	if(InputOrientationRead){
		// determine average position
		sumX = 0;
		sumY = 0;
		sumZ = 0;
		for(A_itor = 0; A_itor < InputAtomicCoordinates.length; A_itor++){
			sumX = InputAtomicCoordinates[A_itor][1] + sumX;
			sumY = InputAtomicCoordinates[A_itor][2] + sumY;
			sumZ = InputAtomicCoordinates[A_itor][3] + sumZ;	
		}
		averageX = sumX / numberAtoms;
		averageY = sumY / numberAtoms;
		averageZ = sumZ / numberAtoms;


		safeAtomicCoordinates = new float [InputAtomicCoordinates.length][InputAtomicCoordinates[0].length];
		for(A_itor = 0; A_itor < InputAtomicCoordinates.length; A_itor++){
			safeAtomicCoordinates[A_itor][0] = InputAtomicCoordinates[A_itor][0];
                        if(ReturnNormalized){
                            safeAtomicCoordinates[A_itor][1] = (float)(InputAtomicCoordinates[A_itor][1] - averageX);
                            safeAtomicCoordinates[A_itor][2] = (float)(InputAtomicCoordinates[A_itor][2] - averageY);
                            safeAtomicCoordinates[A_itor][3] = (float)(InputAtomicCoordinates[A_itor][3] - averageZ);
                        }else{
                            safeAtomicCoordinates[A_itor][1] = InputAtomicCoordinates[A_itor][1];
                            safeAtomicCoordinates[A_itor][2] = InputAtomicCoordinates[A_itor][2];
                            safeAtomicCoordinates[A_itor][3] = InputAtomicCoordinates[A_itor][3];
                        }                    
		}
		
	}else{
            return null;
        }





	return safeAtomicCoordinates;		
}


// Bonding array 
public int [][] BondArray(){
	int [][] safeBondArray = null;
	int A_itor, B_itor;

	assert(BondMatrixComplete);

	// Pass a copy of the array, not the array itself to be on the safe side
	safeBondArray = new int[numberAtoms + 1][numberAtoms + 1];
	for(A_itor = 1; A_itor < (numberAtoms + 1); A_itor++){
		for(B_itor = 1; B_itor < (numberAtoms + 1); B_itor++){
			safeBondArray[A_itor][B_itor] = BondMatrix[A_itor][B_itor];
		}
	}

	return safeBondArray;

}


// to take the load off of the rendering engine, pre calculate all bond angles relative to the (X,Y,Z)
// coordinate axis and determine the overall length of each bond
// may include linear equation to determine new XYZ coordinates for any R along each bond vector but not yet

// determine if the vector defining the bond is in the XY plane or extends into the Z plane
// calculate the angle with respect to the X axis or the Z axis



// first and second [] represent atoms
// third [] represent (Origin X, Origin Y, Origin Z, R, Theta, crossZI, crossZJ, crossZK, Xaxis) in [0] ... [9]
// units are degrees
public float[][][] BondGeometry(boolean ReturnNormalized){
	int A_itor, B_itor;
	float dx, dy, dz, interatomicDistance,  angleConversion;
	float theta;
	float averageX, sumX, averageY, sumY, averageZ, sumZ;

	float [][][] BondGeometryArray = null;

	assert(BondMatrixComplete);

	angleConversion = 180.0f / (float)Math.PI;
	
	// determine what geometry to pass
	if(InputOrientationRead){
		// make the BondGeometryArray
		BondGeometryArray = new float[numberAtoms + 1][numberAtoms + 1][9];

		// if normalized coordinates used 
		sumX = 0;
		sumY = 0;
		sumZ = 0;
		for(A_itor = 0; A_itor < InputAtomicCoordinates.length; A_itor++){
			sumX = (float)InputAtomicCoordinates[A_itor][1] + sumX;
			sumY = (float)InputAtomicCoordinates[A_itor][2] + sumY;
			sumZ = (float)InputAtomicCoordinates[A_itor][3] + sumZ;	
		}
		averageX = sumX / numberAtoms;
		averageY = sumY / numberAtoms;
		averageZ = sumZ / numberAtoms;



		// scan array
		for(A_itor = 1; A_itor < (numberAtoms + 1); A_itor++){
			for(B_itor = 1; B_itor < (numberAtoms + 1); B_itor++){
				//calculate needed geometry if a bond was found
				if(BondMatrix[A_itor][B_itor] != 0){
									
					dx = (float)(InputAtomicCoordinates[A_itor][1] - InputAtomicCoordinates[B_itor][1]);
					dy = (float)(InputAtomicCoordinates[A_itor][2] - InputAtomicCoordinates[B_itor][2]);
					dz = (float)(InputAtomicCoordinates[A_itor][3] - InputAtomicCoordinates[B_itor][3]);
					interatomicDistance = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                                        
                                        // if the rotation is only in the X/Y plane, calculate the angle from the x-axis
					if(Math.abs(dz) < 0.001f){
						theta = angleConversion * (float)Math.acos(dx / interatomicDistance);
                                                
                                                // flip angle depending on the quadrant that the X/Y vector is pointing in	
                                                
                                                
                                                // first look at the quadrants
                                                //+X+Y
                                                if(dx > 0.0f && dy > 0.0f){
                                                    //do nothing
                                                    //-X+Y
                                                }else if(dx < 0.0f && dy > 0.0f){
                                                    //do nothing  
                                                     //-X-Y
                                                }else if(dx < 0.0f && dy < 0.0f){
                                                    theta = 360.0f -  theta;                                                
                                                //+X-Y
                                                }else if(dx > 0.0f && dy < 0.0){
                                                    theta = 360.0f -  theta;
                                                }
                                                                                                                                             
                                                //then each exis
                                                //+X axis
                                                if(dx > 0.0f && dy == 0.0f){
                                                    theta = 0.0f;
                                                //+Y axis    
                                                }else if(dx == 0.0f && dy > 0.0f){
                                                    theta = 90.0f;
                                                //-X axis
                                                }else if(dx < 0.0f && dy == 0.0f){
                                                    theta = 180.0f;                                                
                                                //-Y Axis
                                                }else if(dx == 0.0f && dy < 0.0){
                                                    theta = 270.0f;
                                                }
                                                                                                                                                                                             
                                                
                                        // if the rotation is in the not in the X/Y plane, calculate the ang from the Z-axis
                                        }else{
						theta = angleConversion * (float)Math.acos(dz / interatomicDistance);
                                                // flip angle sign if in the negative Z direction
						if(dz <= 0.0) theta = -1.0f * theta;

					}
                                        // origin shifted to match the overall shift in the geometry to the geometric center
                                        if(ReturnNormalized){
                                            BondGeometryArray[A_itor][B_itor][0] = (float)InputAtomicCoordinates[B_itor][1] - averageX;
                                            BondGeometryArray[A_itor][B_itor][1] = (float)InputAtomicCoordinates[B_itor][2] - averageY;
                                            BondGeometryArray[A_itor][B_itor][2] = (float)InputAtomicCoordinates[B_itor][3] - averageZ;
                                        }else{
                                            BondGeometryArray[A_itor][B_itor][0] = (float)InputAtomicCoordinates[B_itor][1];
                                            BondGeometryArray[A_itor][B_itor][1] = (float)InputAtomicCoordinates[B_itor][2];
                                            BondGeometryArray[A_itor][B_itor][2] = (float)InputAtomicCoordinates[B_itor][3];
                                        }
					BondGeometryArray[A_itor][B_itor][3] = interatomicDistance;
                                        //angle of the rotation about the vector
					BondGeometryArray[A_itor][B_itor][4] = theta;
                                        // cross product?
                                        //
					BondGeometryArray[A_itor][B_itor][5] = -1.0f * dy * dz;
					BondGeometryArray[A_itor][B_itor][6] = dx * dz;
					BondGeometryArray[A_itor][B_itor][7] = 0;
					if(Math.abs(dz) < 0.001f){
						BondGeometryArray[A_itor][B_itor][8] = -1.0f;
					}else{
						BondGeometryArray[A_itor][B_itor][8] = 1.0f;
					}
					
				}
			}
		}
	}



	return BondGeometryArray;
}



} // end of class file