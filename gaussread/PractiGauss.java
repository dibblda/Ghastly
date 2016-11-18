import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class PractiGauss extends JFrame implements ActionListener{


JFrame moleculeControlFrame;

static MoleculeDisplay molecule;

GaussFile moleculeData = null;
boolean moleculeDataLoaded = false;



public PractiGauss(){
	super("PractiGauss v1.0");
	Container content = getContentPane();  // unnecessary in 5.0+

	JMenu menu = new JMenu("File");
	menu.add(makeMenuItem("Open"));	
	menu.add(makeMenuItem("Save"));	
	menu.add(makeMenuItem("Quit"));	
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(menu);
	setJMenuBar(menuBar);
	setSize(300,300);
	setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
}

public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals("Quit")){ 
		System.exit(0);
	}else if (command.equals("Open")){
		loadFile();
		if(moleculeDataLoaded){
			JLabel label = new JLabel("SCF Energy: " + String.valueOf(moleculeData.OptEnergyValue()) + " (Hartrees)", JLabel.CENTER);
			getContentPane().add(label);
			revalidate();
		};
		
	}else if (command.equals("Save")){ 
		saveFile();
	}
}

private void loadFile () {
	JFileChooser chooser = new JFileChooser();
        // set this version to read gaussian com files only for now
         FileNameExtensionFilter filter = new FileNameExtensionFilter("Gaussian Input File", "com");
        chooser.setFileFilter(filter);
        //now open the file dialog
	int result = chooser.showOpenDialog(this);
	if(result == JFileChooser.CANCEL_OPTION) return;
	try {
		File file = chooser.getSelectedFile();
		java.net.URL url = file.toURI().toURL();
		moleculeData = new GaussFile(file, chooser.getTypeDescription(file));
		if(moleculeData != null){
			moleculeDataLoaded = true;
			
			// temporarily here
			
			molecule.PassMoleculeGeometry(moleculeData.AtomicCoordinates(), moleculeData.BondArray(), moleculeData.BondGeometry());
			
                        //molecule.start();
                        
                       

		}
 		return;
		
	}
	catch (Exception e) {
		
		return;
	}
}

private void saveFile() {
JFileChooser chooser = new JFileChooser();
chooser.showSaveDialog(this);
// Save File Data....
}

private JMenuItem makeMenuItem( String name ) {
JMenuItem m = new JMenuItem( name );
m.addActionListener(this);
return m;
}

public static void main(String[] s) {
	System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
	new PractiGauss().setVisible(true);
//	RC = new RemoteControl();
//	RC.StartGeometryPrintout();


    molecule = new MoleculeDisplay();
	molecule.start();
	
}

}