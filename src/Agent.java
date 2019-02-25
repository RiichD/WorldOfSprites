import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")
public abstract class Agent{ //Agents sera abstract, avec différents types d'agents
	private boolean alive;
	private int age;
	private Image tmp; // A retirer quand il y aura des agents
	private int tailleX, tailleY;
	private int x, y;
	
	public Agent(int tailleX, int tailleY) {
		alive = true;
		age = 1;
		this.tailleX = tailleX;
		this.tailleY = tailleY;
		x = (int)(Math.random()*(tailleX));
		y = (int)(Math.random()*(tailleY));
		alive=true;
	}
	
	//Get
	public boolean getAlive() {
		return alive;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	//Set
	public void setAlive(boolean b) {
		alive = b;
	}
	
	//Déplacement aléatoire pour l'instant
	public void move(int[][] floor) {
		double rand = Math.random();
		if (rand < 0.75) {
			if (rand < 0.5) {
				if (rand < 0.25) {
					if (x-1>0 && floor[x-1][y]!=0 ) x--;
				} else {
					if (y+1<tailleY && floor[x][y+1]!=0) y++;
				}
			} else {
				if (y-1>0 && floor[x][y-1]!=0 ) y--;
			}
		} else {
			if (x+1<tailleX && floor[x+1][y]!=0) x++;
		}
	}
	
	public void move_right() {
		if (x<tailleX) x++;
	}
	
	public void move_left() {
		if (x>0) x--;
	}
	
	public void move_up() {
		if (y>0) y--;
	}
	
	public void move_down() {
		if (y<tailleY) y++;
	}
	
	public abstract Image getImage();
	
	public abstract void update();
}
