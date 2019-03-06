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
public class Chicken extends Agent{
	private Image chickenSprite;
	private double spriteSize=0.9; //Change la taille du sprite. 1 etant la taille normale
	
	private int health;
	private int maxHealth = 300;
	private int minHealth = 150;
	private double ploseHealth = 0.75; //probabilite de perdre de la vie
	
	private int stimeIni; //temps initial avant de pouvoir avoir un enfant, entre minStime et maxStime inclus
	private int stime; //possibilite d'avoir un enfant a partir de stime=0
	private int minStime = 15;
	private int maxStime = 30;
	
	public Chicken() {
		super();
		try {
			chickenSprite = ImageIO.read(new File("chicken.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		health=(int)(Math.random()*(maxHealth-minHealth+1)+minHealth);
		stimeIni=(int)(Math.random()*(maxStime-minStime+1)+minStime);
		stime=stimeIni;
	}
	
	public Chicken(int x, int y) {
		super(x,y);
		try {
			chickenSprite = ImageIO.read(new File("chicken.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		health=(int)(Math.random()*(maxHealth+1)+minHealth);
		stimeIni=(int)(Math.random()*(maxStime-minStime+1)+minStime);
		stime=stimeIni;
	}
	
	//Get
	public Image getImage() {
		return chickenSprite;
	}
	
	public int getStime() {
		return stime;
	}
	
	public int getStimeIni() {
		return stimeIni;
	}
	
	public int getHealth() {
		return health;
	}
	
	//Set
	
	public void setStime() {
		stime=stimeIni;
	}
	
	//Add
	
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
		
		g2.drawImage(chickenSprite, getPSpriteX(), getPSpriteY(), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);

	}
}
