#
#
#	PractiGauss Makefile
#
# 	Compiler is javac
#
COMPILER = javac
OPS = -cp ".;lwjgl-2.8.3\jar\lwjgl.jar;lwjgl-2.8.3\jar\slick-util.jar;lwjgl-2.8.3\jar\lwjgl_util.jar;"
OPSWII = -cp ".;wiigee\wiigee-lib-1.5.6.jar;wiigee\wiigee-plugin-wiimote-1.5.6.jar;wiigee\avetanaBluetooth.jar;"

DEPENDENCY = GaussFile.class MoleculeDisplay.class cameraDisplay.class MoleculeViewInterface.class RemoteControl.class
# below options are needed for the java interpreter to run the byte code on the command line
#LWJGLOPS = -cp ".;lwjgl-2.8.3\jar\lwjgl.jar" -Djava.library.path=lwjgl-2.8.3\native\windows

all : PractiGauss.class

PractiGauss.class : PractiGauss.java $(DEPENDENCY)
	$(COMPILER) $(OPS) PractiGauss.java 

GaussFile.class : GaussFile.java
	$(COMPILER) GaussFile.java

MoleculeDisplay.class : MoleculeDisplay.java
	$(COMPILER) $(OPS) MoleculeDisplay.java

cameraDisplay.class : cameraDisplay.java
	$(COMPILER) cameraDisplay.java

MoleculeViewInterface.class : MoleculeViewInterface.java
	$(COMPILER) MoleculeViewInterface.java

RemoteControl.class : RemoteControl.java
	$(COMPILER) $(OPSWII) RemoteControl.java

