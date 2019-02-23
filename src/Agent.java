import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Agent{ //Agents sera abstract, avec différents types d'agents
	private boolean alive;
	private int age;
	private Image tmp; // A retirer quand il y aura des agents
	private int tailleX, tailleY;
	private int x, y;
	public Agent(int x, int y, int tailleX, int tailleY) {
		alive = true;
		age = 1;
		this.x = x;
		this.y = y;
		try { //A retirer quand il y aura des agents
			tmp = ImageIO.read(new File("tronc.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
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
	
	public void move() { //Déplacement aléatoire pour l'instant
		double rand = Math.random();
		if (rand < 0.75) {
			if (rand < 0.5) {
				if (rand < 0.25) move_left();
				else move_down();
			} else {
				move_up();
			}
		} else {
			move_right();
		}
	}
	
	public void move_right() {
		x++;
	}
	
	public void move_left() {
		x--;
	}
	
	public void move_up() {
		y--;
	}
	
	public void move_down() {
		y++;
	}
	
	public Image getImage() {
		return tmp;
	}
	
	public void update() {
	}
	
}
