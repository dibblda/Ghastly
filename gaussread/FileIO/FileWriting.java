/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileIO;
import GhostAtom.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author djosh
 */
public class FileWriting {
    // write everything to file 
    // add on functionality as requested
    
    public static void WriteAtomsToFile(Path FilePath,
                                 FileReading AtomFileInfo, 
                                 GhostAtomSet GhostAtomFileInfo,
                                 String FileType,
                                 String GhostAtomLabel,
                                 boolean CombineGhostAndMolecule) throws IOException{
        int itor;
        
        
       
// if a .txt of .csv, or .gst, then no coordinates other than the ghost atoms are written
        // otherwise check to see if user wanted to add in both
        if(FileType.equals("txt")){
            WriteTextFile(FilePath, GhostAtomFileInfo, GhostAtomLabel);
        }else if(FileType.equals("csv")){
            WriteCSVFile(FilePath, GhostAtomFileInfo, GhostAtomLabel);
        }else if(FileType.equals("gst")){
            //WriteMol2File(FilePath, AtomFileInfo,  GhostAtomFileInfo, FileType,GhostAtomLabel, CombineGhostAndMolecule);
        }
        
        // now can add atoms together or not depending on the user, prepare for the following functions to read them in
        if(CombineGhostAndMolecule){
            
            // combine the relevant matrices to submit to each file writing function as a unit
            // molecular coordinates are in a matrix starting at one for atom one
            // ghost atoms are in a matrix starting at 0
            // final matrix starts at one for atom count from 1
            
            float [][] AtomicCoordinates = AtomFileInfo.AtomicCoordinates(false);
            float [][] GhostAtomCoordinates = GhostAtomFileInfo.GetGhostAtomArray();            
            String [] AtomicSymbol = AtomFileInfo.AtomicSymbol();
            float [] AtomicCharge =  AtomFileInfo.AtomicCharge();
            // get the size of the new matrices
            int MatrixLength = AtomicCoordinates.length + GhostAtomCoordinates.length;
            // create coordinate matrix
            float [][] OutputCoordinates = new float[MatrixLength][4];
            // create a new Atomic Symbol matrix
            String [] OutputAtomicSymbol = new String[MatrixLength];
            // create a new atomic charge matrix
            float [] OutputAtomicCharge = new float[MatrixLength];
            // *do not* creat a new bond matrix....with a lot of ghost atoms
            // this could be prohibitively large
            // run the bond matrix population seperately 
            int [][] OutputBonds = AtomFileInfo.BondArray();
            
            // populate the molecule first
            for(itor = 1; itor < AtomicCoordinates.length; itor++){
                OutputCoordinates[itor][0] = AtomicCoordinates[itor][0];
                OutputCoordinates[itor][1] = AtomicCoordinates[itor][1];
                OutputCoordinates[itor][2] = AtomicCoordinates[itor][2];
                OutputCoordinates[itor][3] = AtomicCoordinates[itor][3];
                OutputAtomicSymbol[itor] = AtomicSymbol[itor];
                // charge may not exist, set to zero if it doesnt 
                if(AtomicCharge != null){
                    OutputAtomicCharge[itor] = AtomicCharge[itor];
                }else{
                    OutputAtomicCharge[itor] = 0.0f;
                }
            }
            // populate the rest of the matrix with the ghost atoms
            for(itor = AtomicCoordinates.length; itor < MatrixLength; itor++){
                // set the ghost atom atomic number to 0, may not be used anyways?
                OutputCoordinates[itor][0] = 0.0f;
                OutputCoordinates[itor][1] = GhostAtomCoordinates[itor][1];
                OutputCoordinates[itor][2] = GhostAtomCoordinates[itor][2];
                OutputCoordinates[itor][3] = GhostAtomCoordinates[itor][3];
                OutputAtomicSymbol[itor] = GhostAtomLabel;
                OutputAtomicCharge[itor] = 0.0f;
            }
            
            System.out.println("<"+FileType+">");
            
            if(FileType.equals("mol2")){
                System.out.println("Writing mol2");
                WriteMol2File(FilePath,
                              OutputCoordinates,
                              OutputBonds,
                              AtomFileInfo.NumberOfBonds,
                              OutputAtomicSymbol,
                              OutputAtomicCharge,
                              AtomFileInfo.MoleculeType,
                              AtomFileInfo.ChargeType);
            }
            
            if(FileType.equals("mol")){
                System.out.println("Writing mol");
                WriteMolFile(FilePath,
                              OutputCoordinates,
                              OutputBonds,
                              AtomFileInfo.NumberOfBonds,
                              OutputAtomicSymbol,
                              OutputAtomicCharge,
                              AtomFileInfo.MoleculeType,
                              AtomFileInfo.ChargeType);
            }
            
        }else{ 
            System.out.println("Only Ghost Atoms Not Implemented Yet");
        }
        
        
    }
    
    
    
    private static void WriteMol2File(Path FilePath,
                                 float [][] AtomicCoordinates,
                                 int [][] BondMatrix,
                                 int NumberOfBonds,
                                 String [] AtomicSymbol,
                                 float [] AtomicCharge,
                                 String MoleculeType,
                                 String ChargeType) throws IOException{
        BufferedWriter OutputBuffer;
        Charset charset = Charset.forName("US-ASCII");
        
        int itor, itor_a, itor_b, index;        
        String AtomString = new String("");
        
        int number_atoms = AtomicCoordinates.length;
        try{
            OutputBuffer = Files.newBufferedWriter(FilePath, charset);
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        
        // write the header information
        
        try{            
            OutputBuffer.write("#Molecule and Ghost Atoms generated by \"Ghastly\"\n#Written by: Dr. David Joshua Dibble, djoshdibble@gmail.com\n");
            // molecule specification section flag            
            OutputBuffer.write("@<TRIPOS>MOLECULE\n");
            // molecule name - just use the path for now            
            OutputBuffer.write(FilePath.toString() + "\n");
            // number of atoms now            
            OutputBuffer.write(Integer.toString(number_atoms) + " " + NumberOfBonds + "\n");
            // assume its a "small" molecule unless otherwise specified
            if(MoleculeType == null){
                OutputBuffer.write("SMALL\n");                
            }else{
                if(MoleculeType.endsWith("\n")){OutputBuffer.write(MoleculeType);}
                else{OutputBuffer.write(MoleculeType + "\n");}
            }            
            // enter atomic charge (read from file reading portion sometimes)
            if(ChargeType != null){
                if(ChargeType.endsWith("\n")){OutputBuffer.write(ChargeType);}
                else{OutputBuffer.write(ChargeType + "\n");}                
            }else{
                OutputBuffer.write("NO_CHARGES\n");
            }
            //------------------------------------------------------------------
            // molecule specification done, now on to the atom section
            //------------------------------------------------------------------
            OutputBuffer.write("@<TRIPOS>ATOM\n");
            for(itor = 1; itor < AtomicCoordinates.length; itor++){
                // first, arbitrary index
                AtomString = AtomString.concat(Integer.toString(itor)+" ");
                // then atom name
                AtomString =  AtomString.concat(AtomicSymbol + " ");
                // then X, Y, and Z
                //x
                AtomString =  AtomString.concat((new BigDecimal(AtomicCoordinates[itor][1])).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
                //y
                AtomString =  AtomString.concat((new BigDecimal(AtomicCoordinates[itor][2])).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
                //z
                AtomString =  AtomString.concat((new BigDecimal(AtomicCoordinates[itor][3])).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + " ");
                // then atom type
                AtomString =  AtomString.concat(AtomicSymbol + " ");
                // then the ID number of the structure (just set to 1)
                AtomString =  AtomString.concat("1 ");
                // then the substructure (just set to "UNL1" unlabeled)
                AtomString =  AtomString.concat("UNL1 ");
                // then the charge of each atom 
                AtomString =  AtomString.concat((new BigDecimal(AtomicCharge[itor])).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString() + "\n");
                // write and reset  AtomString
                OutputBuffer.write(AtomString.toString());
                AtomString = "";
            }
            // now write the bond matrix only look at one side of the matrix before the diagonal
            OutputBuffer.write("@<TRIPOS>BOND\n");
            index = 1;
            AtomString = "";
            for(itor_a = 1; itor_a < BondMatrix.length; itor_a++){
                for(itor_b = 1; itor_b < itor_a; itor_b++){
                    // bond if the matrix is non-zero
                    if(BondMatrix[itor_a][itor_b]!=0){
                        // first the bond index
                        AtomString.concat(Integer.toString(index) + " ");
                        // then the first atom
                        AtomString.concat(Integer.toString(itor_a) + " ");
                        // then the second atom
                        AtomString.concat(Integer.toString(itor_b) + " ");
                        // then the bond order, but probably just one, never resolved multiple bonds in this code
                        AtomString.concat(Integer.toString(BondMatrix[itor_a][itor_b]) + "\n");
                        // write it
                        OutputBuffer.write(AtomString.toString());
                    }
                }
            }
            
            
            //all done!
        }catch(IOException e){
            System.out.println("In WriteMol2File, writing atom: Error: " + e.getMessage());
        }                                                       
        
        // close the file
        try{
            OutputBuffer.close();   
        }catch(IOException e){
            System.out.println("At end of WriteGhostAtomFile: " + e.getMessage());
        }
        
        
    }
    
    private static void WriteMolFile(Path FilePath,
                                 float [][] AtomicCoordinates,
                                 int [][] BondMatrix,
                                 int NumberOfBonds,
                                 String [] AtomicSymbol,
                                 float [] AtomicCharge,
                                 String MoleculeType,
                                 String ChargeType) throws IOException{
        if(NumberOfBonds > 999)throw new IOException("Number of bonds > 999 for a mol file, this is unsupported");
        if(AtomicCoordinates.length > 1000)throw new IOException("Number of atoms > 999 for a mol file, this is unsupported");
        
        BufferedWriter OutputBuffer;
        Charset charset = Charset.forName("US-ASCII");                       
        
        String LeftDecimal, RightDecimal, ValueString;
        String [] Token;
        BigDecimal TempValue;       
        // String builder with "append" is supposed to be faster than "concat"
        // or "+"
        StringBuilder AtomString;
        int itor, itor_a, itor_b;
        
        try{
            OutputBuffer = Files.newBufferedWriter(FilePath, charset);
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        
        
        
        
       
        
        try{
            
            // write the header line three spots, leave the first two blank and 
            // sign the last as a comment
            OutputBuffer.write("\n");
            OutputBuffer.write("\n");
            OutputBuffer.write("Molecule and Ghost Atoms generated by \"Ghastly\", Written by: Dr. David Joshua Dibble, djoshdibble@gmail.com\n");
            
            // write the counts line
            // atom or bond count can't be larger than 255 for many versions
            // we will assume 999 in our case and leave it up to other programs
            // to decide otherwise exception thrown at beginning of the function
            
            // right justified
            // initialize string builder with blank string
            AtomString = new StringBuilder();
            // first the number of atoms
            AtomString.append(String.format("%3", Integer.toString(AtomicCoordinates.length - 1)));
            // then the number of bonds
            AtomString.append(String.format("%3", NumberOfBonds));
            // various irrelevant flags, eight of them
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("  0");
            AtomString.append("000");
            AtomString.append(" v2000\n");
            OutputBuffer.write(AtomString.toString());
            
            // write the atom block
            for(itor = 1; itor < AtomicCoordinates.length; itor++){
                // initialize string builder with blank string
                AtomString = new StringBuilder();

                // format first number X            
                TempValue = new BigDecimal(AtomicCoordinates[itor][1]);             
                ValueString = TempValue.setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
                Token = ValueString.split(".");
                // the left side of the decimal can take up five character spaces and will be right justified and padded
                LeftDecimal = String.format("%5", Token[0]);
                //right side of the decimal is 4 spots by default
                RightDecimal = Token[1];
                // build the properly formatted number
                AtomString.append(LeftDecimal);
                AtomString.append(".");
                AtomString.append(RightDecimal);

                // format the second number, Y
                TempValue = new BigDecimal(AtomicCoordinates[itor][2]);             
                ValueString = TempValue.setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
                Token = ValueString.split(".");
                // the left side of the decimal can take up five character spaces and will be right justified and padded
                LeftDecimal = String.format("%5", Token[0]);
                //right side of the decimal is 4 spots by default
                RightDecimal = Token[1];
                // append to the previous number
                AtomString.append(LeftDecimal);
                AtomString.append(".");
                AtomString.append(RightDecimal);

                // format the third number, Z
                TempValue = new BigDecimal(AtomicCoordinates[itor][3]);             
                ValueString = TempValue.setScale(4, BigDecimal.ROUND_HALF_UP).toPlainString();
                Token = ValueString.split(".");
                // the left side of the decimal can take up five character spaces and will be right justified and padded
                LeftDecimal = String.format("%5", Token[0]);
                //right side of the decimal is 4 spots by default
                RightDecimal = Token[1];
                // append to the previous two numbers
                AtomString.append(LeftDecimal);
                AtomString.append(".");
                AtomString.append(RightDecimal);
                // append a space as required
                AtomString.append(" ");
                // append a left justified atom symbol
                AtomString.append(String.format("%-3", AtomicSymbol[itor]));
                // append right justified mass difference
                AtomString.append(" 0");
                // append rest of values as zeros
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0");
                AtomString.append("  0\n");

                //write to the output buffer
                OutputBuffer.write(AtomString.toString());
            }
            // write the bond block
            for(itor_a = 1; itor_a < BondMatrix.length; itor_a++){
                for(itor_b = 1; itor_b < itor_a; itor_b++){
                    // bond if the matrix is non-zero
                    if(BondMatrix[itor_a][itor_b]!=0){
                        // initialize string builder with blank string
                        AtomString = new StringBuilder();                       
                        // the the first atom
                        AtomString.append(String.format("%-3", Integer.toString(itor_a)));                        
                        // then the second atom
                        AtomString.append(String.format("%-3", Integer.toString(itor_b)));
                        // then the bond value
                        AtomString.append(String.format("%-3", Integer.toString(BondMatrix[itor_a][itor_b])));                                                
                        // then unused values
                        AtomString.append("  0");
                        AtomString.append("  0");
                        AtomString.append("  0");
                        AtomString.append("  0\n");
                        //write to the output buffer
                        OutputBuffer.write(AtomString.toString());
                    }
                }
            }
            // finally terminate the file
            OutputBuffer.write("M  END\n");
            
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        // close the file
        try{
            OutputBuffer.close();   
        }catch(IOException e){
            System.out.println("At end of WriteGhostAtomFile: " + e.getMessage());
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    private static void WriteTextFile(Path FilePath, GhostAtomSet GhostAtomFileInfo, String GhostAtomLabel) throws IOException{
        
        BufferedWriter OutputBuffer;
        Charset charset = Charset.forName("US-ASCII");
        ArrayList<GhostAtom> ToPrint = GhostAtomFileInfo.GetAtoms();
        String GhostAtomString = new String("");
        try{
            OutputBuffer = Files.newBufferedWriter(FilePath, charset);
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        
        
        for(int itor = 0; itor < ToPrint.size(); itor++){
                GhostAtomString = GhostAtomString.concat(GhostAtomLabel + " ");
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
        
        try{
            OutputBuffer.close();   
        }catch(IOException e){
            System.out.println("At end of WriteGhostAtomFile: " + e.getMessage());
        }
    }
    
    
    
    
    
    private static void WriteCSVFile(Path FilePath, GhostAtomSet GhostAtomFileInfo, String GhostAtomLabel) throws IOException{
        
        BufferedWriter OutputBuffer;
        Charset charset = Charset.forName("US-ASCII");
        ArrayList<GhostAtom> ToPrint = GhostAtomFileInfo.GetAtoms();
        String GhostAtomString = new String("");
        
        try{
            OutputBuffer = Files.newBufferedWriter(FilePath, charset);
        }catch(IOException e){
            System.out.println("In WriteGhostAtomFile " + e.getMessage());
            return;
        }
        
        
        for(int itor = 0; itor < ToPrint.size(); itor++){
                GhostAtomString = GhostAtomString.concat(GhostAtomLabel + ", ");
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

    
    
    
    
}
