package asteroids;

import org.lwjgl.opengl.GL11;

public class Ship {
		
	//Location of Ship
		
	private double centerX = 683,centerY = 384;
	private double rotationAngle = 180;
	
	private int livesLeft = 5;
	
	public void rotate(boolean leftOrRight){
		//true = right
		//false = left
		if(leftOrRight){
			rotationAngle += 3;
			rotationAngle %= 360;
		}
		else{
			rotationAngle -= 3;
			if(rotationAngle < 0){
				rotationAngle %= 360;
			}
		}
	}
	
	public void move(boolean forwardOrBackward){
		//true = forward
		//false = backward
		
		double speedX = toCartesian(90 + rotationAngle, 3)[0];
		double speedY = toCartesian(90 + rotationAngle, 3)[1];
		
		if(forwardOrBackward){
			centerX += speedX;
			centerY += speedY;
		}
		else if(!forwardOrBackward){
			centerX -= speedX;
			centerY -= speedY;
		}
		
		if(centerX < 0){
			centerX = 0;
		}
		if(centerX > 1366){
			centerX = 1366;
		}
		if(centerY < 0){
			centerY = 0;
		}
		if(centerY > 768){
			centerY = 768;
		}
	}
	
	//Some math for rotation
	
	private double pythag(double a, double b){
		return Math.sqrt(Math.pow(a,2) + Math.pow(b,2));
	}
	
	private double[] toCartesian(double theta, double r){
		//First number is x
		//Second is y
		theta = Math.toRadians(theta);
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		double[] cortesCoords = new double[2];
		cortesCoords[0] = x;
		cortesCoords[1] = y;
		return cortesCoords;
	}
	
	private double[] toPolar(double x, double y){
		//First number is theta (in degrees)
		//Second is r
		double theta = Math.toDegrees(Math.atan2(y, x));
		double r = pythag(x, y);
		double[] polarNumbers = new double[2];
		polarNumbers[0] = theta;
		polarNumbers[1] = r;
		return polarNumbers;
	}
	
	//Draw the ship into existence!
	
	public void draw(){
		GL11.glColor3d(0.3647, 0.3647, 0.87);
		double[] polarValues = toPolar(0, 0 - 50);
		double[] coords1 = toCartesian(90 + rotationAngle, polarValues[1]);
		polarValues = toPolar(0 + 20, 0 + 30);
		double[] coords2 = toCartesian(236.31 + rotationAngle, polarValues[1]);
		polarValues = toPolar(0 - 20, 0 + 30);
		double[] coords3 = toCartesian(-56.31 + rotationAngle, polarValues[1]);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glVertex2d(coords1[0] + centerX, coords1[1] + centerY);
		GL11.glVertex2d(coords2[0] + centerX, coords2[1] + centerY);
		GL11.glVertex2d(coords3[0] + centerX, coords3[1] + centerY);
		GL11.glEnd();
	}
	
	//Hit the ship
	
	public void hit(){
		livesLeft--;
	}
	
	//See if ship is dead or not
	
	public boolean dead(){
		return livesLeft <= 0 ? true : false;
	}
	
	//Accessing some private variables for use in other classes
	
	public double getRotation(){
		return rotationAngle;
	}
	
	public double[] getFrontCoords(){
		//first number = x
		//second = y
		
		double[] polar = toPolar(0, 0 - 50);
		
		double[] coords = toCartesian(90 + rotationAngle, polar[1]);
		
		coords[0] += centerX;
		coords[1] += centerY;
		
		return coords;
		
	}
	
	
	
	public int getLives(){
		return livesLeft;
	}
	
	public double[] getShipCoords(){
		double[] shipCoords = new double[6];
		double[] polarValues = toPolar(0, 0 - 50);
		double[] coords = toCartesian(90 + rotationAngle, polarValues[1]);
		shipCoords[0] = coords[0] + centerX;
		shipCoords[1] = coords[1] + centerY;
		polarValues = toPolar(0 + 20, 0 + 30);
		coords = toCartesian(236.31 + rotationAngle, polarValues[1]);
		shipCoords[2] = coords[0] + centerX;
		shipCoords[3] = coords[1] + centerY;
		polarValues = toPolar(0 - 20, 0 + 30);
		coords = toCartesian(-56.31 + rotationAngle, polarValues[1]);
		shipCoords[4] = coords[0] + centerX;
		shipCoords[5] = coords[1] + centerY;
		return shipCoords;
	}
	
	//Math!
	
	private boolean ptOnLineSide(double ptX, double ptY, double ptLine1X, double ptLine1Y, double ptLine2X, double ptLine2Y){
		double lineDeltaY = ptLine2Y - ptLine1Y;
		double lineDeltaX = ptLine2X - ptLine1X;
		if(lineDeltaX != 0){
			double slope = lineDeltaY/lineDeltaX;
			double yIntercept = slope * (0 - ptLine1X) + ptLine1Y;
			return (ptY > slope*ptX + yIntercept && centerY > slope*centerX + yIntercept) || (ptY < slope*ptX + yIntercept && centerY < slope*centerX + yIntercept);
		}
		else{
			return (ptX > ptLine1X && centerX > ptLine1X) || (ptX < ptLine1X && centerX < ptLine1X);
		}
	}
	
	//Find if point is inside ship
	
	public boolean ptInShip(double ptX, double ptY){
		//All in one line!
		return ptOnLineSide(ptX, ptY, getShipCoords()[0],getShipCoords()[1], getShipCoords()[2], getShipCoords()[3]) && ptOnLineSide(ptX, ptY, getShipCoords()[2],getShipCoords()[3], getShipCoords()[4], getShipCoords()[5]) && ptOnLineSide(ptX, ptY, getShipCoords()[4],getShipCoords()[5], getShipCoords()[0], getShipCoords()[1]);
	}

}
