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
	private int x, y;
	
	public Agent() {
		alive = true;
		age = 1;
		x = (int)(Math.random()*(World.X));
		y = (int)(Math.random()*(World.Y));
	}
	
	public Agent(int x, int y) {
		alive=true;
		age=1;
		this.x=x;
		this.y=y;
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
	
	public void setX(int n) {
		x=n;
	}
	
	public void setY(int n) {
		y=n;
	}
	//Déplacement aléatoire pour l'instant
	public void move(int[][] floor, Item[][] environnement) {
		double rand = Math.random();
		if (rand < 0.75) {
			if (rand < 0.5) {
				if (rand < 0.25) {
					if (x-1>0 && floor[x-1][y]>0 ) {
						if ( !( environnement[x-1][y] instanceof Cactus) && !(environnement[x-1][y] instanceof Tree) )
							x--;
					}
				} else {
					if (y+1<World.X && floor[x][y+1]>0) {
						if ( !( environnement[x][y+1] instanceof Cactus) && !(environnement[x][y+1] instanceof Tree) )
							y++;
					}
				}
			} else {
				if (y-1>0 && floor[x][y-1]>0 ) {
					if ( !( environnement[x][y-1] instanceof Cactus) && !(environnement[x][y-1] instanceof Tree) )
						y--;
				}
			}
		} else {
			if (x+1<World.Y && floor[x+1][y]>0) {
				if ( !( environnement[x+1][y] instanceof Cactus) && !(environnement[x+1][y] instanceof Tree) )
					x++;
			}
		}
	}
	
	/*
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
	*/
	public abstract Image getImage();
	
	public abstract void update();
}
