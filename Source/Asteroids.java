package asteroids;

import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.Date;
import org.lwjgl.opengl.*;
import org.lwjgl.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class Asteroids {
	
	//See how long the player survived for!
	
	public double compareTimes(Date beginningTime){
		return (((double)(new Date()).getTime() - beginningTime.getTime())/1000);
	}
	
	public Asteroids(){
		
		//Init
		
		try{
			
			Display.setDisplayMode(new DisplayMode(1366, 768));
			Display.setTitle("Time Alive: 0; Asteroids Destroyed: 0; Lives Left: 5");
			Display.create();
			
		}
		
		catch (LWJGLException e){
			
			e.printStackTrace();
			
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 1366, 768, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		boolean aDown;
		boolean dDown;
		boolean wDown;
		boolean sDown;
		boolean mouseDown;
		Date beginningTime = new Date();
		int asteroidsDestroyed = 0;
		int bulletCooldown = 21;
		ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
		ArrayList<Bullet> bullets = new ArrayList<Bullet>();
		double newAsteroidCooldown = 300 + Math.round(Math.random() * 300);
		Ship ship = new Ship();
				
		//Game loop
		
		gameloop: while (!Display.isCloseRequested()){			
			aDown = Keyboard.isKeyDown(Keyboard.KEY_A);
			dDown = Keyboard.isKeyDown(Keyboard.KEY_D);
			wDown = Keyboard.isKeyDown(Keyboard.KEY_W);
			sDown = Keyboard.isKeyDown(Keyboard.KEY_S);
			mouseDown = Mouse.isButtonDown(0);
			
			//Moving ship
			
			if(wDown && !sDown){
				ship.move(true);
			}
			if(sDown && !wDown){
				ship.move(false);
			}
			if(dDown && !aDown){
				ship.rotate(true);
			}
			if(aDown && !dDown){
				ship.rotate(false);
			}
			
			//Gun Firing
			
			if(bulletCooldown > 0){
				bulletCooldown--;
			}
			else if(bulletCooldown == 0 && mouseDown){
			
				Bullet bullet = new Bullet(ship.getRotation(),ship.getFrontCoords()[0],ship.getFrontCoords()[1]);
				bullets.add(bullet);
				
				bulletCooldown = 10;
			}
			else{
				bulletCooldown = 10;
			}
			
			glClear(GL_COLOR_BUFFER_BIT);
			
			//Move all bullets, destroy bullet if necessary
			
			for(int i = 0; i < bullets.size(); i++){
				bullets.get(i).move();
				if(bullets.get(i).wallDetector()){
					bullets.remove(i);
					i--;
				}
			}
			
			//Move all asteroids
			
			for(int i = 0; i < asteroids.size(); i++){
				asteroids.get(i).move();
				asteroids.get(i).ricochet();
			}
			
			//Detect if a bullet has hit an asteroid
			
			for(int i = 0; i < bullets.size(); i++){
				double bulletX = bullets.get(i).getPos()[0];
				double bulletY = bullets.get(i).getPos()[1];
				for(int i2 = 0; i2 < asteroids.size(); i2++){
					double x = asteroids.get(i2).getPos()[0];
					double y = asteroids.get(i2).getPos()[1];
					int size = asteroids.get(i2).getSize();
					if(asteroids.get(i2).ptInAsteroid(bulletX, bulletY)){
						bullets.remove(i);
						i--;
						asteroids.get(i2).hit();
						if(asteroids.get(i2).dead()){
							if(size > 1){
								double origTheta = Math.toDegrees(Math.atan2(asteroids.get(i2).getSpeed()[1], asteroids.get(i2).getSpeed()[0]));
								double speed;
								switch(size/2){
								case 4: speed = 2;
								break;
								case 2: speed = 3;
								break;
								case 1: speed = 4;
								break;
								default: speed = 0;
								break;
								}
								double speedx1 = speed * Math.cos(Math.toRadians(origTheta + 10));
								double speedy1 = speed * Math.sin(Math.toRadians(origTheta + 10));
								double speedx2 = speed * Math.cos(Math.toRadians(origTheta - 10));
								double speedy2 = speed * Math.sin(Math.toRadians(origTheta - 10));
								Asteroid asteroid1 = new Asteroid(size/2,x,y,speedx1,speedy1);
								Asteroid asteroid2 = new Asteroid(size/2,x,y,speedx2,speedy2);
								asteroid1.autoInBounds();
								asteroid2.autoInBounds();
								asteroids.add(asteroid1);
								asteroids.add(asteroid2);
							}
							asteroids.remove(i2);
							asteroidsDestroyed++;
						}
						break;
					}
				}
			}
			
			//Detect if an asteroid has hit a ship
			
			for(int i = 0; i < asteroids.size(); i++){
				if(ship.ptInShip(asteroids.get(i).getPos()[0] - (asteroids.get(i).getSize() * 8), asteroids.get(i).getPos()[1] - (asteroids.get(i).getSize() * 8)) || ship.ptInShip(asteroids.get(i).getPos()[0] + (asteroids.get(i).getSize() * 8), asteroids.get(i).getPos()[1] - (asteroids.get(i).getSize() * 8)) || ship.ptInShip(asteroids.get(i).getPos()[0] - (asteroids.get(i).getSize() * 8), asteroids.get(i).getPos()[1] + (asteroids.get(i).getSize() * 8)) || ship.ptInShip(asteroids.get(i).getPos()[0] + (asteroids.get(i).getSize() * 8), asteroids.get(i).getPos()[1] + (asteroids.get(i).getSize() * 8)) || asteroids.get(i).ptInAsteroid(ship.getShipCoords()[0], ship.getShipCoords()[1]) || asteroids.get(i).ptInAsteroid(ship.getShipCoords()[2], ship.getShipCoords()[3]) || asteroids.get(i).ptInAsteroid(ship.getShipCoords()[4], ship.getShipCoords()[5])){
					switch(asteroids.get(i).getSize()){
					case 1: ship.hit();
					if(ship.dead()){
						System.out.println("Your final score: " + (Math.round(compareTimes(beginningTime) + asteroidsDestroyed)));
						break gameloop;
					}
					break;
					case 2: ship.hit();
					ship.hit();
					if(ship.dead()){
						System.out.println("Your final score: " + (Math.round(compareTimes(beginningTime) + asteroidsDestroyed)));
						break gameloop;
					}
					break;
					case 4: ship.hit();
					ship.hit();
					ship.hit();
					if(ship.dead()){
						System.out.println("Your final score: " + (Math.round(compareTimes(beginningTime) + asteroidsDestroyed)));
						break gameloop;
					}
					break;
					case 8: ship.hit();
					ship.hit();
					ship.hit();
					ship.hit();
					if(ship.dead()){
						System.out.println("Your final score: " + (Math.round(compareTimes(beginningTime) + asteroidsDestroyed)));
						break gameloop;
					}
					break;
					}
					asteroids.remove(i);
					i--;
				}
			}
			
			//Draw all bullets
			
			for(int i = 0; i < bullets.size(); i++){
				
				bullets.get(i).draw();
				
			}
			
			//Draw all asteroids
			
			for(int i = 0; i < asteroids.size(); i++){
				asteroids.get(i).draw();
			}
			
			//Draw the ship
			
			ship.draw();
			
			//Asteroid factory!
			
			if(newAsteroidCooldown > 0){
				newAsteroidCooldown--;
			}
			else if(newAsteroidCooldown <= 0){
				double posX,posY,speedX,speedY;
				int size;
				if(Math.round(Math.random()) == 0){
					posX = (Math.random() * 1566) - 100;
					if(posX > 0 && posX < 1366){
						if(Math.round(Math.random()) == 0){
							posY = Math.random() * -100;
						}
						else{
							posY = (Math.random() * 100) + 768;
						}
					}
					else{
						posY = (Math.random() * 968) - 100;
					}
				}
				else{
					posY = (Math.random() * 968) - 100;
					if(posY > 0 && posY < 768){
						if(Math.round(Math.random()) == 0){
							posX = Math.random() * -100;
						}
						else{
							posX = (Math.random() * 100) + 1366;
						}
					}
					else{
						posX = (Math.random() * 1566) - 100;
					}
				}
				
				//Random asteroid size
				
				double randomSize = Math.round(Math.random() * 2);
				
				if(randomSize == 0){
					size = 1;
				}
				else if(randomSize == 1){
					size = 2;
				}
				else{
					size = 4;
				}
				
				//All asteroids head towards center
				
				double theta = Math.toDegrees(Math.atan2(384 - posY, 683 - posX));
				double speed;
				switch(size){
				case 1: speed = 4;
				break;
				case 2: speed = 3;
				break;
				case 4: speed = 2;
				break;
				case 8: speed = 1;
				break;
				default: speed = 0;
				break;
				}
				speedX = speed * Math.cos(Math.toRadians(theta));
				speedY = speed * Math.sin(Math.toRadians(theta));
				
				//Asteroid shipping!
				
				Asteroid asteroid = new Asteroid(size, posX, posY, speedX, speedY);
				asteroids.add(asteroid);
				newAsteroidCooldown = 300 + Math.round(Math.random() * 300);
			}
			
			Display.setTitle("Time Alive: " + compareTimes(beginningTime) + "; Asteroids Destroyed: " + asteroidsDestroyed + "; Lives Left: " + ship.getLives());
			Display.update();
			Display.sync(60);
			
		}
		
		Display.destroy();
		
	}

	public static void main(String[] args) {
		
		new Asteroids();

	}

}
