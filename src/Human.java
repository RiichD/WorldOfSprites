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
	private double spriteSize=1; //Change la taille du sprite. 1 etant la taille normale
	
	private int drowning;
	private int drowningtime=20;//Se noie s'il reste drowningtime dans l'eau

	private int age;
	private int deathAge=600; //age maximum
	
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
			humanSprite = ImageIO.read(new File("human1.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=1;
		drowning=0;
		health=(int)(Math.random()*(maxHealth-minHealth+1)+minHealth);
		stimeIni=(int)(Math.random()*(maxStime-minStime+1)+minStime);
		stime=stimeIni;
	}
	
	public Human(int x, int y) {
		super(x,y);
		try {
			humanSprite = ImageIO.read(new File("human1.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		age=1;
		drowning=0;
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
	
	public void setStime() {
		stime=stimeIni;
	}
	
	//Add
	public void addDrowning() {
		drowning++;
		if (drowning == drowningtime ) {
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
		if (Math.random()<ploseHealth) health--;
		if (health == 0 ) setAlive(false);
		addAge();
		if (getAge()>getDeathAge()) setAlive(false);
		removeStime();
	}
	
	public void draw(Graphics2D g, JFrame frame) {
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(humanSprite, getPSpriteX(), getPSpriteY(), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);

	}
}
