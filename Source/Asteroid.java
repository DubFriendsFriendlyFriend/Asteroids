package asteroids;

import org.lwjgl.opengl.GL11;

public class Asteroid {
	/*
	 * Size of asteroid:
	 * > 8 = Unknown (Unsupported)
	 * 8 = Mega (Not available in the game itself, you have to manually spawn it)
	 * 4 = Large
	 * 2 = Medium
	 * 1 = Small
	 */
	private int size;	
	private double x;
	private double y;	
	private int health;	
	private double speedX;
	private double speedY;
	private boolean pastBoundaries;
	
	public Asteroid(int newSize, double newX, double newY, double newSpeedX, double newSpeedY){
		size = newSize;
		x = newX;
		y = newY;
		speedX = newSpeedX;
		speedY = newSpeedY;
		switch(size){
		case 8: 
			health = 16;
			break;
		case 4: 
			health = 8;
			break;
		case 2: 
			health = 4;
			break;
		case 1: 
			health = 2;
			break;
		default: 
			health = 0;
			break;
		}
		pastBoundaries = false;
	}
	public void draw(){
		GL11.glColor3d(1,1,0);
		GL11.glRectd(x - size*8, y - size*8, x + size*8, y + size*8);
	}
	
	//Hit the asteroid
	
	public void hit(){
		health--;
	}
	
	//Check if asteroid is destroyed
	
	public boolean dead(){
		return health <= 0 ? true : false;
	}
	
	//Accessing private variables for use outside of class
	
	public int getSize(){
		return size;
	}
	
	public double[] getPos(){
		//First number = x
		//Second = y
		double[] coords = new double[2];
		coords[0] = x;
		coords[1] = y;
		return coords;
	}
	
	public double[] getSpeed(){
		//First number = speedx
		//Second = speedy
		double[] speed = new double[2];
		speed[0] = speedX;
		speed[1] = speedY;
		return speed;
	}
	
	//To fix a minor bug
	
	public void autoInBounds(){
		pastBoundaries = true;
	}
	
	//Move the asteroid
	
	public void move(){
		x += speedX;
		y += speedY;
		if(!pastBoundaries){
			if(x > 1 && x < 1365 && y > 1 && x < 767){
				pastBoundaries = true;
			}
		}
	}
	
	//Bounce off walls!
	
	public void ricochet(){
		if(pastBoundaries){
			if(x < 0 && speedX < 0){
				speedX *= -1;
			}
			if(x > 1366 && speedX > 0){
				speedX *= -1;
			}
			if(y < 0 && speedY < 0){
				speedY *= -1;
			}
			if(y > 768 && speedY > 0){
				speedY *= -1;
			}
		}
	}
	
	public boolean ptInAsteroid(double ptx, double pty){
		return ptx > x - size*8 && ptx < x + size*8 && pty > y - size*8 && pty < y + size*8? true : false;
	}
}
