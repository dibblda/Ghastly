import java.lang.Math.*;



public class cameraDisplay{

	// Eye X Y Z
private	float [] eyePosition = new float[3];
private	float [] defaultEyePosition = new float[3];
	// Origin X Y Z
private	float [] originPosition = new float[3];
private	float [] defaultOriginPosition = new float[3];

	// define a new zero to avoid problems with the polar coordinate system
private float zeroValue = 1.0e-10f;

// polar coordinates 
// measured from Origin position
private float polarR;
private float defaultPolarR;


private float polarPhi;
private float defaultPolarPhi;

private float polarTheta;
private float defaultPolarTheta;
// not strictly a polar coordinate but a roation of the axis
// implement seperately
private float polarEpsl = 0;
private float defaultPolarEps = 0;

// non spherical rotation about each axis
private float xRot;
private float yRot;
private float zRot;

private float defaultxRot;
private float defaultyRot;
private float defaultzRot;



private float radToDegree = 180.0f / (float)Math.PI;
private float degreeToRad = (float)Math.PI / 180.0f;


private boolean origin = false;
private boolean sphericalCoordinates = false;












public void cameraDisplay(){
	for(int itor = 0; itor < 3; itor++){
		eyePosition[itor] = zeroValue;
		defaultEyePosition[itor] = zeroValue;
		originPosition[itor] = zeroValue;
		defaultOriginPosition[itor] = zeroValue;
	}
}

public void originMarker(boolean mark){
	origin = mark;
	return;
}

public boolean isOriginMarked(){
	return origin;
}

public void useSpherical(boolean sphere){
	sphericalCoordinates = sphere;
}

public boolean isSphericalCoordinates(){
	return sphericalCoordinates;
}


// set defaults and current locations all at the same time
// also update the polar coordinates
public void setCoordinates(float eyeX, float eyeY, float eyeZ, float originX, float originY, float originZ, float rX, float rY, float rZ){
	assert(rX <= 360.0f);
	assert(rX >= 0.0f);
	assert(rY <= 360.0f);
	assert(rY >= 0.0f);
	assert(rZ <= 360.0f);
	assert(rZ >= 0.0f);


	if(eyeX == 0.0f)eyeX = zeroValue;
	if(eyeY == 0.0f)eyeY = zeroValue;
	if(eyeZ == 0.0f)eyeZ = zeroValue;

	if(originX == 0.0f)originX = zeroValue;
	if(originY == 0.0f)originY = zeroValue;
	if(originZ == 0.0f)originZ = zeroValue;

	defaultEyePosition[0] = eyeX;
	defaultEyePosition[1] = eyeY;
	defaultEyePosition[2] = eyeZ;
	defaultOriginPosition[0] = originX;
	defaultOriginPosition[1] = originY;
	defaultOriginPosition[2] = originZ;

	eyePosition[0] = defaultEyePosition[0];
	eyePosition[1] = defaultEyePosition[1];
	eyePosition[2] = defaultEyePosition[2];

	originPosition[0] = defaultOriginPosition[0];
	originPosition[1] = defaultOriginPosition[1];
	originPosition[2] = defaultOriginPosition[2];
	if(sphericalCoordinates){
		updatePolarCoordinates();
		setDefaultPolarPosition(polarR, polarTheta, polarPhi);
	}else{
		xRot = rX;
		yRot = rY;
		zRot = rZ;
		defaultxRot = rX;
		defaultyRot = rY;
		defaultzRot = rZ;
	}
	return;
}

private void updateCartesianCoordinates(){
	eyePosition[0] = polarR * 
					 (float)Math.sin(polarTheta * degreeToRad) * 
					 (float)Math.cos(polarPhi * degreeToRad) + 
					 originPosition[0];
	eyePosition[1] = polarR * 
					 (float)Math.sin(polarTheta * degreeToRad) * 
					 (float)Math.sin(polarPhi * degreeToRad) + 
					 originPosition[1];
	eyePosition[2] = polarR * 
					 (float)Math.cos(polarTheta * degreeToRad) + 
					 originPosition[0];					
	return;
}

private void updatePolarCoordinates(){
	float px, py, pz;
	px = eyePosition[0] - originPosition[0];
	py = eyePosition[1] - originPosition[1];
	pz = eyePosition[2] - originPosition[2];

	if(px == 0.0f)px = zeroValue;
	if(py == 0.0f)py = zeroValue;
	if(pz == 0.0f)pz = zeroValue;

	polarR = (float)Math.sqrt(px * px + py * py + pz * pz);
	polarTheta = radToDegree * (float)Math.acos(pz / polarR);

	// special cases need for Phi to avoid divide by zero
	polarPhi = radToDegree * (float)Math.atan(py / px);

	return;
}















public void setDefaultAxisRotationPosition(float rX, float rY, float rZ){
	assert(rX <= 360.0f);
	assert(rX >= 0.0f);
	assert(rY <= 360.0f);
	assert(rY >= 0.0f);
	assert(rZ <= 360.0f);
	assert(rZ >= 0.0f);
	defaultxRot = rX;
	defaultyRot = rY;
	defaultzRot = rZ;
	return;
}


public void deltaAxisRotationPosition(float rX, float rY, float rZ){
	assert(rX <= 360.0f);
	assert(rX >= 0.0f);
	assert(rY <= 360.0f);
	assert(rY >= 0.0f);
	assert(rZ <= 360.0f);
	assert(rZ >= 0.0f);

	if((xRot + rX) > 360.0f){
		xRot = xRot + rX - 360.0f;
	}else if((xRot + rX) < 0.0f){
		xRot = xRot + rX + 360.0f;
	}else{
		xRot = xRot + rX;
	}

	if((yRot + rY) > 360.0f){
		yRot = yRot + rY - 360.0f;
	}else if((yRot + rY) < 0.0f){
		yRot = yRot + rY + 360.0f;
	}else{
		yRot = yRot + rY;
	}

	if((zRot + rZ) > 360.0f){
		zRot = zRot + rZ - 360.0f;
	}else if((zRot + rZ) < 0.0f){
		zRot = zRot + rZ + 360.0f;
	}else{
		zRot = zRot + rZ;
	}

	return;
}

public void defaultAxisRotationPosition(){
	xRot = defaultxRot;
	yRot = defaultyRot;
	zRot = defaultzRot;	
}

public float RotX(){
	return xRot;
}

public float RotY(){
	return yRot;
}

public float RotZ(){
	return zRot;
}













public void setDefaultPolarPosition(float pr, float theta, float phi){
	assert(theta >= 0.0f);
	assert(theta <= 360.0f);
	assert(phi >= 0.0f);
	assert(phi <= 360.0f);
	assert(pr > 0.0f);


	defaultPolarR = pr;
	defaultPolarPhi = phi;
	defaultPolarTheta = theta;
	return;
}

public void deltaPolarPosition(float pr, float theta, float phi){
	assert(theta >= 0.0f);
	assert(theta <= 360.0f);
	assert(phi >= 0.0f);
	assert(phi <= 360.0f);


	// finish this up adjust if end up greater than 360 or less than 0
	// adjust ir R goes negative
	if((pr + polarR) < 0.0f){
		polarR = (float)Math.abs(pr + polarR);
		polarTheta = polarTheta - 180.0f;
		if(polarTheta < 0.0f)polarTheta = polarTheta + 360.0f;
		polarPhi = polarPhi - 180.0f;
		if(polarPhi < 0.0f)polarPhi = polarPhi + 360.0f;
	}else if((pr + polarR) > 360.0f){
		polarR = polarR + pr - 360.0f;
	}else{
		polarR = polarR + pr;
	}

	if((polarTheta + theta) > 360.0f){
		polarTheta = polarTheta + theta - 360.0f;
	}else if((polarTheta + theta) < 0.0f){
		polarTheta = polarTheta + theta + 360.0f;
	}else{
		polarTheta = polarTheta + theta;
	}

	if((polarPhi + phi) > 360.0f){
		polarPhi = polarPhi + phi - 360.0f;
	}else if((polarPhi + phi) < 0.0f){
		polarPhi = polarPhi + phi + 360.0f;
	}else{
		polarPhi = polarPhi + phi;
	}
	updateCartesianCoordinates();
	return;
}


public void setPolarR(float pr){	

	if(pr < 0.0f){
		polarR = (float)Math.abs(pr);
		polarTheta = polarTheta - 180.0f;
		if(polarTheta < 0.0f)polarTheta = polarTheta + 360.0f;
		polarPhi = polarPhi - 180.0f;
		if(polarPhi < 0.0f)polarPhi = polarPhi + 360.0f;
	}else if(pr > 360.0f){
		polarR =  pr - 360.0f;
	}else{
		polarR = pr;
	}
	updateCartesianCoordinates();
	return;	
}

public void setPolarTheta(float theta){
	assert(theta >= 0.0f);
	assert(theta <= 360.0f);
	polarTheta = theta;
	updateCartesianCoordinates();
	return;	
}

public void setPolarPhi(float phi){
	assert(phi >= 0.0f);
	assert(phi <= 360.0f);
	polarPhi = phi;
	updateCartesianCoordinates();
	return;	
}

public void defaultPolar(){
	polarR = defaultPolarR;
	polarPhi = defaultPolarPhi;
	polarTheta = defaultPolarTheta;
	updateCartesianCoordinates();
	return;
}

public float eyeR(){
	return polarR;
}

public float eyeTheta(){
	return polarTheta;
}

public float eyePhi(){
	return polarPhi;
}







public void setEyePosition(float x, float y, float z){
	if(x == 0.0f)x = zeroValue;
	if(y == 0.0f)y = zeroValue;
	if(z == 0.0f)z = zeroValue;
	eyePosition[0] = x;
	eyePosition[1] = y;
	eyePosition[2] = z;
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}
public void setDefaultEyePosition(float x, float y, float z){
	if(x == 0.0f)x = zeroValue;
	if(y == 0.0f)y = zeroValue;
	if(z == 0.0f)z = zeroValue;
	defaultEyePosition[0] = x;
	defaultEyePosition[1] = y;
	defaultEyePosition[2] = z;
	return;
}

public void defaultEye(){
	eyePosition[0] = defaultEyePosition[0];
	eyePosition[1] = defaultEyePosition[1];
	eyePosition[2] = defaultEyePosition[2];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}

public void defaultEyeXY(){
	eyePosition[0] = defaultEyePosition[0];
	eyePosition[1] = defaultEyePosition[1];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}

public void defaultEyeZ(){
	eyePosition[2] = defaultEyePosition[2];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}


public void deltaEyePostion(float x, float y, float z){
	eyePosition[0] = x + eyePosition[0];
	eyePosition[1] = y + eyePosition[1];
	eyePosition[2] = z + eyePosition[2];
	if(eyePosition[0] == 0.0f)eyePosition[0] = zeroValue;
	if(eyePosition[1] == 0.0f)eyePosition[1] = zeroValue;
	if(eyePosition[2] == 0.0f)eyePosition[2] = zeroValue;
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}











public void setOriginPosition(float x, float y, float z){
	if(x == 0.0f)x = zeroValue;
	if(y == 0.0f)y = zeroValue;
	if(z == 0.0f)z = zeroValue;
	originPosition[0] = x;
	originPosition[1] = y;
	originPosition[2] = z;
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}
public void setDefaultOriginPosition(float x, float y, float z){
	if(x == 0.0f)x = zeroValue;
	if(y == 0.0f)y = zeroValue;
	if(z == 0.0f)z = zeroValue;
	defaultOriginPosition[0] = x;
	defaultOriginPosition[1] = y;
	defaultOriginPosition[2] = z;
	return;
}
public void defaultOrigin(){
	originPosition[0] = defaultOriginPosition[0];
	originPosition[1] = defaultOriginPosition[1];
	originPosition[2] = defaultOriginPosition[2];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}
public void defaultOriginXY(){
	originPosition[0] = defaultOriginPosition[0];
	originPosition[1] = defaultOriginPosition[1];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}
public void defaultOriginZ(){
	originPosition[2] = defaultOriginPosition[2];
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}
public void deltaOriginPostion(float x, float y, float z){
	originPosition[0] = x + originPosition[0];
	originPosition[1] = y + originPosition[1];
	originPosition[2] = z + originPosition[2];
	if(originPosition[0] == 0.0f)originPosition[0] = zeroValue;
	if(originPosition[1] == 0.0f)originPosition[1] = zeroValue;
	if(originPosition[2] == 0.0f)originPosition[2] = zeroValue;
	if(sphericalCoordinates){
		updatePolarCoordinates();
	}
	return;
}











//return postions


public float eyeX(){
	return eyePosition[0];
}

public float eyeY(){
	return eyePosition[1];
}

public float eyeZ(){
	return eyePosition[2];
}

public float originX(){
	return originPosition[0];
}

public float originY(){
	return originPosition[1];
}

public float originZ(){
	return originPosition[2];
}

}