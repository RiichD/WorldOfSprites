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
	
	private int drowningTime = 10;//Se noie s'il reste drowningtime dans l'eau

	private int maxAppearenceTime = 10000; //Le temps d'apparition du zombie. L'age represente le temps d'apparition du zombie
	private int minAppearenceTime = 5000;
	
	//A ne pas modifier
	private Image zombieSprite;
	private Image fireSprite;
	private int drowning;
	private int fire;
	private int age;
	private int deathAge; //age maximum.
	//Variables non necessaires
	private int health;
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
		if (age>=deathAge) setAlive(false);
		if (getAlive()) {
			addAge();
			if (getOnFire()) {
				setAlive(false);
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
