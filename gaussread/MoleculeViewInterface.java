import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class MoleculeViewInterface extends JPanel implements ActionListener{
	final cameraDisplay internalCameraCoordinates;
	GridBagConstraints constraints = new GridBagConstraints();



	JTextField data;
	JButton atomPickButton, anglePickButton, dihedralPickButton;
	JButton upAngle, rightAngle, downAngle, leftAngle, resetAngle, clockwiseAngle, counterclockwiseAngle, resetRotate;
	JButton mxTranslate, pxTranslate, myTranslate, pyTranslate, reset2dTranslate, mzTranslate, pzTranslate, reset3dTranslate;  
	JSeparator divider, divider1;
	int buttonSpacing = 5;
	int dividerSpacing = 2;
	int labelSpacing = 5;
	// seperation between two axis and single axis interfaces
	int verticalSelectionSpacing = 20;


	long buttonDelay = 50;
	long repeatPeriod = 10;

	// Default move coordinates

	private float deltaX = 0.1f;
	private float deltaY = 0.1f;
	private float deltaZ = 0.1f;
	private float deltaTheta = 0.5f;
	private float deltaPhi = 0.5f;
	private float deltaRX = 0.5f;
	private float deltaRY = 0.5f;
	private float deltaRZ = 0.5f;	


	// these are used to detect button presses and to repeat the action when the button is held down
	// this set is the translate set
static TimerTask xmTask, xpTask, ymTask, ypTask, zmTask, zpTask; 
static Timer xmTimer, xpTimer, ymTimer, ypTimer, zmTimer, zpTimer;

	// this set is the rotate set
static TimerTask tmTask, tpTask, pmTask, ppTask, emTask, epTask; 
static Timer tmTimer, tpTimer, pmTimer, ppTimer, emTimer, epTimer;



	public MoleculeViewInterface(cameraDisplay cameraCoordinates){
		assert(cameraCoordinates != null);

		internalCameraCoordinates = cameraCoordinates;


		setLayout(new GridBagLayout());

		// first part is the data on top and buttons to choose picking style
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Atom Picking Method"), constraints);
	

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		data = new JTextField();
		data.setText("No Atoms Selected");
		data.setEditable(false);
		add(data, constraints);
	


		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		atomPickButton = new JButton("Atom Pick");
		atomPickButton.setPreferredSize(new Dimension(110, 15)); 
		atomPickButton.addActionListener(this);
		add(atomPickButton, constraints);
		

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		anglePickButton = new JButton("Angle Pick");
		anglePickButton.setPreferredSize(new Dimension(110, 15)); 
		anglePickButton.addActionListener(this);
		add(anglePickButton, constraints);
	

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		dihedralPickButton = new JButton("Dihedral Pick");
		dihedralPickButton.setPreferredSize(new Dimension(110, 15)); 
		dihedralPickButton.addActionListener(this);
		add(dihedralPickButton, constraints);
	
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 5;
		constraints.insets = new Insets(5,5,5,5);
		constraints.fill = GridBagConstraints.VERTICAL; 
		divider = new JSeparator(JSeparator.VERTICAL);
		divider.setPreferredSize(new Dimension(5,1));
		add(divider, constraints);
		// reset padding and height
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE; 

		// second part is the angular rotation section
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Molecule Rotation"), constraints);
		constraints.gridwidth = 1;

		constraints.gridx = 2;
		constraints.gridy = 3;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Theta"), constraints);

		constraints.gridx = 4;
		constraints.gridy = 1;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Phi"), constraints);

		constraints.gridx = 6;
		constraints.gridy = 1;
		constraints.insets = new Insets(labelSpacing,verticalSelectionSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Eps."), constraints);











		constraints.gridx = 3;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		Icon leftArrow = new ImageIcon("leftarrowS.gif");
		leftAngle = new JButton(leftArrow);
		leftAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener tmMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 tmTask = new tmTask();
														 tmTimer = new Timer();
														 tmTimer.scheduleAtFixedRate(tmTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(tmTask != null){
															tmTask.cancel();
														 	tmTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(tmTask != null){
															tmTask.cancel();
														 	tmTimer = new Timer();
														}
													}
												}; 
		leftAngle.addMouseListener(tmMouse);
		add(leftAngle, constraints);













		constraints.gridx = 4;
		constraints.gridy = 2;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		Icon upArrow = new ImageIcon("uparrowS.gif");
		upAngle = new JButton(upArrow);
		upAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener ppMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 ppTask = new ppTask();
														 ppTimer = new Timer();
														 ppTimer.scheduleAtFixedRate(ppTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(ppTask != null){
															ppTask.cancel();
														 	ppTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(ppTask != null){
															ppTask.cancel();
														 	ppTimer = new Timer();
														}
													}
												}; 





		upAngle.addMouseListener(ppMouse);
		add(upAngle, constraints);


		constraints.gridx = 5;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		Icon rightArrow = new ImageIcon("rightarrowS.gif");
		rightAngle = new JButton(rightArrow);
		rightAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener tpMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 tpTask = new tpTask();
														 tpTimer = new Timer();
														 tpTimer.scheduleAtFixedRate(tpTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(tpTask != null){
															tpTask.cancel();
														 	tpTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(tpTask != null){
															tpTask.cancel();
														 	tpTimer = new Timer();
														}
													}
												}; 
		rightAngle.addMouseListener(tpMouse);
		add(rightAngle, constraints);



		constraints.gridx = 4;
		constraints.gridy = 4;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		Icon downArrow = new ImageIcon("downarrowS.gif");
		downAngle = new JButton(downArrow);
		downAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener pmMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 pmTask = new pmTask();
														 pmTimer = new Timer();
														 pmTimer.scheduleAtFixedRate(pmTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(pmTask != null){
															pmTask.cancel();
														 	pmTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(pmTask != null){
															pmTask.cancel();
														 	pmTimer = new Timer();
														}
													}
												}; 
		downAngle.addMouseListener(pmMouse);
		add(downAngle, constraints);




		constraints.gridx = 4;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		Icon resetArrow = new ImageIcon("startoverS.gif");
		resetAngle = new JButton(resetArrow);
		resetAngle.setPreferredSize(new Dimension(34, 34)); 
		resetAngle.addActionListener(new ActionListener(){
											public void actionPerformed(ActionEvent ae){
												if(internalCameraCoordinates.isSphericalCoordinates()){
													internalCameraCoordinates.defaultPolar();
												}else{
													internalCameraCoordinates.defaultAxisRotationPosition();
												}
											}
									  	   });
		add(resetAngle, constraints);


		constraints.gridx = 6;
		constraints.gridy = 2;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);
		Icon clockwise = new ImageIcon("clockwiseRotateS.gif");
		clockwiseAngle = new JButton(clockwise);
		clockwiseAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener emMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 emTask = new emTask();
														 emTimer = new Timer();
														 emTimer.scheduleAtFixedRate(emTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(emTask != null){
															emTask.cancel();
														 	emTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(emTask != null){
															emTask.cancel();
														 	emTimer = new Timer();
														}
													}
												}; 


		clockwiseAngle.addMouseListener(emMouse);
		add(clockwiseAngle, constraints);

		constraints.gridx = 6;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);
		//Icon resetArrow = new ImageIcon("startover.gif"); already defined
		resetRotate = new JButton(resetArrow);
		resetRotate.setPreferredSize(new Dimension(34, 34)); 
		resetRotate.addActionListener(this);
		add(resetRotate, constraints);

		constraints.gridx = 6;
		constraints.gridy = 4;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);
		Icon counterclockwise = new ImageIcon("counterclockwiseRotateS.gif");
		counterclockwiseAngle = new JButton(counterclockwise);
		counterclockwiseAngle.setPreferredSize(new Dimension(34, 34)); 
		MouseListener epMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 epTask = new epTask();
														 epTimer = new Timer();
														 epTimer.scheduleAtFixedRate(epTask, buttonDelay, repeatPeriod);
														 // debugging	
														 System.out.printf("Polar: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeR(),
														 											  internalCameraCoordinates.eyeTheta(),
														 											  internalCameraCoordinates.eyePhi());
														 System.out.printf("Cartesian: %.2f, %.2f, %.2f\n", internalCameraCoordinates.eyeX(),
														 											  internalCameraCoordinates.eyeY(),
														 											  internalCameraCoordinates.eyeZ());

													}

													public void mouseReleased(MouseEvent e){
														if(epTask != null){
															epTask.cancel();
														 	epTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(epTask != null){
															epTask.cancel();
														 	epTimer = new Timer();
														}
													}
												}; 
		counterclockwiseAngle.addMouseListener(epMouse);
		add(counterclockwiseAngle, constraints);















		constraints.gridx = 7;
		constraints.gridy = 0;
		constraints.gridheight = 5;
		constraints.insets = new Insets(5,5,5,5);
		constraints.fill = GridBagConstraints.VERTICAL; 
		divider1 = new JSeparator(JSeparator.VERTICAL);
		divider1.setPreferredSize(new Dimension(5,1));
		add(divider1, constraints);
		// reset padding and height
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE; 

		// third part is translation

		constraints.gridx = 8;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Molecule Translation"), constraints);
		constraints.gridwidth = 1;

		constraints.gridx = 8;
		constraints.gridy = 3;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("X"), constraints);

		constraints.gridx = 10;
		constraints.gridy = 1;
		constraints.insets = new Insets(labelSpacing,labelSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Y"), constraints);

		constraints.gridx = 12;
		constraints.gridy = 1;
		constraints.insets = new Insets(labelSpacing,verticalSelectionSpacing,labelSpacing,labelSpacing);
		add(new JLabel("Z"), constraints);

		// 2d translation coordinates

		constraints.gridx = 9;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		mxTranslate = new JButton(leftArrow);
		mxTranslate.setPreferredSize(new Dimension(34, 34));
		MouseListener xmMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 xmTask = new xmTask();
														 xmTimer = new Timer();
														 xmTimer.scheduleAtFixedRate(xmTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(xmTask != null){
															xmTask.cancel();
														 	xmTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(xmTask != null){
															xmTask.cancel();
														 	xmTimer = new Timer();
														}
													}
												}; 
		mxTranslate.addMouseListener(xmMouse);
		add(mxTranslate, constraints);


		constraints.gridx = 10;
		constraints.gridy = 2;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		pyTranslate = new JButton(upArrow);
		pyTranslate.setPreferredSize(new Dimension(34, 34)); 
		MouseListener ypMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 ypTask = new ypTask();
														 ypTimer = new Timer();
														 ypTimer.scheduleAtFixedRate(ypTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(ypTask != null){
															ypTask.cancel();
														 	ypTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(ypTask != null){
															ypTask.cancel();
														 	ypTimer = new Timer();
														}
													}
												};
		pyTranslate.addMouseListener(ypMouse);
		add(pyTranslate, constraints);


		constraints.gridx = 11;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		pxTranslate = new JButton(rightArrow);
		pxTranslate.setPreferredSize(new Dimension(34, 34)); 
		MouseListener xpMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 xpTask = new xpTask();
														 xpTimer = new Timer();
														 xpTimer.scheduleAtFixedRate(xpTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(xpTask != null){
															xpTask.cancel();
														 	xpTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(xpTask != null){
															xpTask.cancel();
														 	xpTimer = new Timer();
														}
													}
												};
		pxTranslate.addMouseListener(xpMouse);
		add(pxTranslate, constraints);

		constraints.gridx = 10;
		constraints.gridy = 4;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		myTranslate = new JButton(downArrow);
		myTranslate.setPreferredSize(new Dimension(34, 34)); 
		MouseListener ymMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 ymTask = new ymTask();
														 ymTimer = new Timer();
														 ymTimer.scheduleAtFixedRate(ymTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(ymTask != null){
															ymTask.cancel();
														 	ymTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(ymTask != null){
															ymTask.cancel();
														 	ymTimer = new Timer();
														}
													}
												};


		myTranslate.addMouseListener(ymMouse);
		add(myTranslate, constraints);

		constraints.gridx = 10;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,buttonSpacing,buttonSpacing,buttonSpacing);
		reset2dTranslate = new JButton(resetArrow);
		reset2dTranslate.setPreferredSize(new Dimension(34, 34)); 
		reset2dTranslate.addActionListener(new ActionListener(){
											public void actionPerformed(ActionEvent ae){
												internalCameraCoordinates.defaultEyeXY();
												internalCameraCoordinates.defaultOriginXY();
											}
									  	   });
		add(reset2dTranslate, constraints);

		// 3d translation coordinate

		constraints.gridx = 12;
		constraints.gridy = 2;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);	
		pzTranslate = new JButton(upArrow);
		pzTranslate.setPreferredSize(new Dimension(34, 34)); 
		MouseListener zmMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 zmTask = new zmTask();
														 zmTimer = new Timer();
														 zmTimer.scheduleAtFixedRate(zmTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(zmTask != null){
															zmTask.cancel();
														 	zmTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(zmTask != null){
															zmTask.cancel();
														 	zmTimer = new Timer();
														}
													}
												};

		pzTranslate.addMouseListener(zmMouse);
		add(pzTranslate, constraints);




		constraints.gridx = 12;
		constraints.gridy = 3;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);		
		reset3dTranslate = new JButton(resetArrow);
		reset3dTranslate.setPreferredSize(new Dimension(34, 34)); 
		reset3dTranslate.addActionListener(new ActionListener(){
										public void actionPerformed(ActionEvent ae){
											internalCameraCoordinates.defaultEyeZ();
										}
									  });
		add(reset3dTranslate, constraints);

		constraints.gridx = 12;
		constraints.gridy = 4;
		constraints.insets = new Insets(buttonSpacing,verticalSelectionSpacing,buttonSpacing,buttonSpacing);	
		mzTranslate = new JButton(downArrow);
		mzTranslate.setPreferredSize(new Dimension(34, 34)); 
		MouseListener zpMouse = new MouseAdapter(){
													public void mousePressed(MouseEvent e){
														 zpTask = new zpTask();
														 zpTimer = new Timer();
														 zpTimer.scheduleAtFixedRate(zpTask, buttonDelay, repeatPeriod);
													}

													public void mouseReleased(MouseEvent e){
														if(zpTask != null){
															zpTask.cancel();
														 	zpTimer = new Timer();
														};
													}

													public void mouseExited(MouseEvent e) {
														if(zpTask != null){
															zpTask.cancel();
														 	zpTimer = new Timer();
														}
													}
												};

		mzTranslate.addMouseListener(zpMouse);
		add(mzTranslate, constraints);


	}


	public void actionPerformed(ActionEvent e){
		return;
	}





//deltaPolarPosition(float pr, float theta, float phi);
// polar coordinate transformations
private class tmTask extends TimerTask {
    public void run() {
    	if(internalCameraCoordinates.isSphericalCoordinates()){
			internalCameraCoordinates.deltaPolarPosition(0.0f, -1.0f * deltaTheta, 0.0f);
		}else{
			internalCameraCoordinates.deltaAxisRotationPosition(-1.0f * deltaRX, 0.0f, 0.0f);
		}
    }
}

private class tpTask extends TimerTask {
    public void run() {
    	if(internalCameraCoordinates.isSphericalCoordinates()){
        	internalCameraCoordinates.deltaPolarPosition(0.0f, deltaTheta, 0.0f);
    	}else{
    		internalCameraCoordinates.deltaAxisRotationPosition(deltaRX, 0.0f, 0.0f);
    	}
    }
}

private class pmTask extends TimerTask {
    public void run() {
    	if(internalCameraCoordinates.isSphericalCoordinates()){
        	internalCameraCoordinates.deltaPolarPosition(0.0f, 0.0f, -1.0f * deltaPhi);
    	}else{
    		internalCameraCoordinates.deltaAxisRotationPosition(0.0f, -1.0f * deltaRY, 0.0f);
    	}
    }
}

private class ppTask extends TimerTask {
    public void run() {
    	if(internalCameraCoordinates.isSphericalCoordinates()){
    		internalCameraCoordinates.deltaPolarPosition(0.0f, 0.0f, deltaPhi);
    	}else{
    		internalCameraCoordinates.deltaAxisRotationPosition(0.0f,  deltaRY, 0.0f);
    	}
    }	
}

private class emTask extends TimerTask {
    public void run() {
    	
    		internalCameraCoordinates.deltaAxisRotationPosition(0.0f, 0.0f, -1.0f * deltaRZ);
    	
    }
}

private class epTask extends TimerTask {
    public void run() {
    	
    		internalCameraCoordinates.deltaAxisRotationPosition(0.0f,  0.0f, deltaRZ);
    	
    }	
}
















// cartesian coordinate transformations
private class xmTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(-1.0f * deltaX, 0, 0);
        internalCameraCoordinates.deltaOriginPostion(-1.0f * deltaX, 0, 0);
    }
}

private class xpTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(deltaX, 0, 0);
        internalCameraCoordinates.deltaOriginPostion(deltaX, 0, 0);
    }
}

private class ymTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(0, -1.0f * deltaY, 0);
        internalCameraCoordinates.deltaOriginPostion(0, -1.0f * deltaY, 0);
    }
}

private class ypTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(0, deltaY, 0);
        internalCameraCoordinates.deltaOriginPostion(0, deltaY, 0);
    }
}

private class zmTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(0, 0, -1.0f * deltaZ);
    }
}

private class zpTask extends TimerTask {
    public void run() {
        internalCameraCoordinates.deltaEyePostion(0, 0, deltaZ);
    }
}

// end of file and class
}











