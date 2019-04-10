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
public class Zombie extends Agent{
	private double spriteSize = 1; //Change la taille du sprite. 1 etant la taille normale
	
	private int drowningTime = 10; //Se noie s'il reste drowningtime dans l'eau
	
	//L'age represente le temps d'apparition du zombie
	private int maxAppearenceTime = 500; //Le temps d'apparition maximal du zombie
	private int minAppearenceTime = 200; //Le temps d'apparition minimal du zombie
	
	private int zombieIte = 2; //Nombre de changements de position que fait l'agent en une iteration
	
	//A ne pas modifier
	private Image zombieSprite;
	private Image fireSprite;
	private int drowning; //Duree courant de l'agent en train de se noyer
	private int fire; //Duree courant de l'agent en feu
	private int age; //Age de l'agent
	private int deathAge; //Age maximum
	//Variables non necessaires
	private int health; //Sante de l'agent
	private int stimeIni; //temps initial avant de pouvoir avoir un enfant, entre minStime et maxStime inclus
	private int stime; //possibilite d'avoir un enfant a partir de stime=0
	
	public Zombie() {
		super();
		try {
			zombieSprite = ImageIO.read(new File("pictures/zombie.png"));
			fireSprite = ImageIO.read(new File("pictures/fire.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=0;
		drowning=0;
		fire=0;
		health=-1;
		stimeIni=-1;
		stime=stimeIni;
		deathAge = (int)(Math.random()*(maxAppearenceTime-minAppearenceTime+1)+minAppearenceTime);
	}
	
	public Zombie(int x, int y) {
		super(x,y);
		try {
			zombieSprite = ImageIO.read(new File("pictures/zombie.png"));
			fireSprite = ImageIO.read(new File("pictures/fire.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=0;
		drowning=0;
		fire=0;
		health=-1;
		stimeIni=-1;
		stime=stimeIni;
		deathAge = (int)(Math.random()*(maxAppearenceTime-minAppearenceTime+1)+minAppearenceTime);
	}
	
	//Get
	public Image getImage() {
		return zombieSprite;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getDeathAge() {
		return deathAge;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getStime() {
		return stime;
	}
	
	public int getStimeIni() {
		return stimeIni;
	}
	
	public int getZombieIte() {
		return zombieIte;
	}
	
	//Set
	public void setDrowning(int x) {
		drowning=x;
	}
	
	public void setFire(int x) {
		fire = x;
	}
	
	public void setStime() {
		stime=stimeIni;
	}
	
	//Add
	public void addDrowning() {
		drowning++;
		if (drowning == drowningTime) {
			super.setAlive(false);
		}
	}
	
	public void addAge() {
		age++;
	}
	
	public void addHealth(int n) {
		health+=n;
	}
	
	public void update() {
		if (age>=deathAge) setAlive(false); //Si l'age est superieur ou egal a l'age maximal, l'agent meurt
		if (getAlive()) {
			addAge(); //L'agent vieillit toutes les iterations
			if (getOnFire()) {
				setAlive(false); //Meurt instantanement s'il est en feu
			}
		}
	}
	
	public void draw(Graphics2D g, JFrame frame) {
		Graphics2D g2 = (Graphics2D) g;
		if (getSpriteX()<getPSpriteX()) {
			g2.drawImage(zombieSprite, World.spriteLength + (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), -(int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		} else {
			g2.drawImage(zombieSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
		if (getOnFire()) {
			g2.drawImage(fireSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
	}
}
