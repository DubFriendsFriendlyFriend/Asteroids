package asteroids;

import org.lwjgl.opengl.GL11;

public class Bullet {
	
	private double speedX,speedY,positionX,positionY;
	
	public Bullet(double shipRotation, double frontX, double frontY){
		
		positionX = frontX;
		positionY = frontY;
		speedX = toCartesian(90 + shipRotation, 5)[0];
		speedY = toCartesian(90 + shipRotation, 5)[1];
		
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
	
	//Unless you want a bullet that defies physics...
	
	public void move(){
		positionX = positionX + speedX;
		positionY = positionY + speedY;
	}
	
	//Sees if the bullet has hit a wall or not.
	
	public boolean wallDetector(){
		
		return positionX < 0 || positionX > 1366 || positionY < 0 || positionY > 768 ? true : false;
		
	}
	
	//Return position for use outside of class
	
	public double[] getPos(){
		//First number = x
		//Second = y
		
		double[] coords = new double[2];
		coords[0] = positionX;
		coords[1] = positionY;
		return coords;
		
	}
	
	public void draw(){
		GL11.glColor3d(0,1,0);
		GL11.glRectd(positionX - 4, positionY - 4, positionX + 4, positionY + 4);
	}

}
