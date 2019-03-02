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
public abstract class Agent{ //Agents sera abstract, avec differents types d'agents
	private boolean alive;
	private int age;
	private double deathAge=200; //age maximum
	
	private int x, y;
	private int spriteX, spriteY; // position du Sprite
	private int pspriteX, pspriteY; // ancienne position du Sprite qui permet de deplacer fluidement
	
	private int sexe; //0 Homme, 1 Femme
	private int stime; //possibilite d'avoir un enfant a partir de stime=0
	private int stimeIni = 10; //temps initial avant de pouvoir avoir un enfant
	
	public Agent() {
		this((int)(Math.random()*(World.X)), (int)(Math.random()*(World.Y)));
	}
	
	public Agent(int x, int y) {
		alive = true;
		age = 1;
		this.x=x;
		this.y=y;
		spriteX=x*World.spriteLength;
		spriteY=y*World.spriteLength;
		pspriteX=spriteX;
		pspriteY=spriteY;
		sexe = (int)(Math.random()*2);
		stime=stimeIni;
	}
	
	//Get
	public boolean getAlive() {
		return alive;
	}
	
	public int getAge() {
		return age;
	}
	
	public double getDeathAge() {
		return deathAge;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getSpriteX() {
		return spriteX;
	}
	
	public int getSpriteY() {
		return spriteY;
	}
	
	public int getPSpriteX() {
		return pspriteX;
	}
	
	public int getPSpriteY() {
		return pspriteY;
	}
	
	public int getSexe() {
		return sexe;
	}
	
	public int getStime() {
		return stime;
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
	
	public void setStime() {
		stime=stimeIni;
	}
	//Add
	public void addAge() {
		age++;
	}
	
	//Remove
	public void removeStime() {
		if (stime != 0) {
			stime--;
		}
	}
	
	//Deplacement aleatoire
	public void move(int[][] terrain, Item[][] environnement) {
		if (pspriteX==spriteX && pspriteY==spriteY) {
			double rand = Math.random();
			if (rand < 0.75) {
				if (rand < 0.5) {
					if (rand < 0.25) {
						if (x-1>=0 && terrain[x-1][y]>0 ) {
							if ( !( environnement[x-1][y] instanceof Cactus) && !(environnement[x-1][y] instanceof Tree) )
								x--;
						}
					} else {
						if (y+1<World.X && terrain[x][y+1]>0) {
							if ( !( environnement[x][y+1] instanceof Cactus) && !(environnement[x][y+1] instanceof Tree) )
								y++;
						}
					}
				} else {
					if (y-1>=0 && terrain[x][y-1]>0 ) {
						if ( !( environnement[x][y-1] instanceof Cactus) && !(environnement[x][y-1] instanceof Tree) )
							y--;
					}
				}
			} else {
				if (x+1<World.Y && terrain[x+1][y]>0) {
					if ( !( environnement[x+1][y] instanceof Cactus) && !(environnement[x+1][y] instanceof Tree) )
						x++;
				}
			}
		}
		spriteX= x*World.spriteLength;
		spriteY= y*World.spriteLength;
	}
	
	//Fluidite des deplacements
	public void smoothMove() {
		//Si les sprites de chaque coordonnee est differente, il y a un deplacement, alors on incremente l'ancienne sprite de maniere progressive pour l'affichage d'un deplacement fluide
		if ( pspriteX!=spriteX || pspriteY!=spriteY) {
			if ( pspriteX < spriteX ) {
				pspriteX++;
			}
			if ( pspriteX > spriteX ) {
				pspriteX--;
			}
			if ( pspriteY < spriteY  ) {
				pspriteY++;
			}
			if ( pspriteY > spriteY ){
				pspriteY--;
			}
		} else {
			pspriteX = spriteX;
			pspriteY = spriteY;
		}
	}
	
	public abstract Image getImage();
	
	public abstract void update();
	
	public abstract void draw(Graphics2D g, JFrame frame);
}
