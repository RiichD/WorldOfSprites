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
public class Human extends Agent{
	private Image humanSprite;
	private Image fireSprite;
	private double spriteSize = 1; //Change la taille du sprite. 1 etant la taille normale
	
	private int drowning;
	private int drowningTime = 22;//Se noie s'il reste drowningtime dans l'eau
	
	private int fire;
	private int fireTime = 10;
	
	private int age;
	private int deathAge = 600; //age maximum
	
	private int health;
	private int maxHealth = 300;
	private int minHealth = 150;
	private double ploseHealth = 0.75; //probabilite de perdre de la vie
	
	private int stimeIni; //temps initial avant de pouvoir avoir un enfant, entre minStime et maxStime inclus
	private int stime; //possibilite d'avoir un enfant a partir de stime=0
	private int minStime = 15;
	private int maxStime = 30;
	
	public Human() {
		super();
		try {
			if (getSexe()==0)humanSprite = ImageIO.read(new File("pictures/human1.png"));
			else humanSprite = ImageIO.read(new File("pictures/human2.png"));
			fireSprite = ImageIO.read(new File("pictures/fire.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=1;
		drowning=0;
		fire=0;
		health=(int)(Math.random()*(maxHealth-minHealth+1)+minHealth);
		stimeIni=(int)(Math.random()*(maxStime-minStime+1)+minStime);
		stime=stimeIni;
	}
	
	public Human(int x, int y) {
		super(x,y);
		try {
			if (getSexe()==0)humanSprite = ImageIO.read(new File("pictures/human1.png"));
			else humanSprite = ImageIO.read(new File("pictures/human2.png"));
			fireSprite = ImageIO.read(new File("pictures/fire.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=1;
		drowning=0;
		fire=0;
		health=(int)(Math.random()*(maxHealth+1)+minHealth);
		stimeIni=(int)(Math.random()*(maxStime-minStime+1)+minStime);
		stime=stimeIni;
	}
	
	//Get
	public Image getImage() {
		return humanSprite;
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
	
	//Remove
	public void removeStime() {
		if (stime != 0) {
			stime--;
		}
	}
	
	public void update() {
		if (health <= 0) setAlive(false);
		if (age>=deathAge) setAlive(false);
		if (fire>=fireTime) setOnFire(false);
		if (getAlive()) {
			if (Math.random()<ploseHealth) health--;
			addAge();
			removeStime();
			if (getOnFire()) {
				fire++;
				health-=World.fireDamage;
			}
		}
	}
	
	public void draw(Graphics2D g, JFrame frame) {
		Graphics2D g2 = (Graphics2D) g;
		if (getSpriteX()<getPSpriteX()) {
			g2.drawImage(humanSprite, World.spriteLength + (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), -(int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		} else {
			g2.drawImage(humanSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
		if (getOnFire()) {
			g2.drawImage(fireSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
	}
}
