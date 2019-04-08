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
public class Viper extends Agent{
	private double spriteSize = 1; //Change la taille du sprite. 1 etant la taille normale et maximale.
	
	private int drowningTime = 70; //Se noie s'il reste drowningtime dans l'eau
	
	private int fireTime = 16; //Duree de l'agent en feu
	
	private int deathAge = 470; //age maximum
	
	private int maxHealth = 500; //Sante maximale
	private int minHealth = 300; //Sante minimale
	private double ploseHealth = 0.4; //probabilite de perdre de la vie
	
	private int minStime = 80; //Duree minimale avant de pouvoir avoir un enfant
	private int maxStime = 105; //Duree maximale avant de pouvoir avoir un enfant
	
	//A ne pas modifier
	private Image viperSprite;
	private Image fireSprite;
	private int drowning; //Duree courant de l'agent en train de se noyer
	private int fire; //Duree courant de l'agent en feu
	private int age; //Age de l'agent
	private int health; //Sante de l'agent
	private int stimeIni; //temps initial avant de pouvoir avoir un enfant, entre minStime et maxStime inclus
	private int stime; //possibilite d'avoir un enfant a partir de stime=0
	
	public Viper() {
		super();
		try {
			viperSprite = ImageIO.read(new File("pictures/viper.png"));
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
	
	public Viper(int x, int y) {
		super(x,y);
		try {
			viperSprite = ImageIO.read(new File("pictures/viper.png"));
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
	
	//Get
	public Image getImage() {
		return viperSprite;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getDeathAge() {
		return deathAge;
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
		if (drowning == drowningTime ) {
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
		if (health <= 0) setAlive(false); //Si la sante est inferieure ou egale a 0, l'agent meurt
		if (age>=deathAge) setAlive(false); //Si l'age est superieur ou egal a l'age maximal, l'agent meurt
		if (fire>=fireTime) setOnFire(false); //Si la duree courant du feu est egale a la duree de fireTime, l'agent n'est plus en feu
		if (getAlive()) {
			if (Math.random()<ploseHealth) health--; //Chance de perdre de la sante
			addAge(); //L'agent vieillit toutes les iterations
			removeStime(); //Il faut que stime vaille 0 pour pouvoir avoir un enfant
			if (getOnFire()) {
				fire++;
				health-=World.fireDamage; //Retirer fireDamage sante a l'agent pour etre en feu
			}
		}
	}
	
	public void draw(Graphics2D g, JFrame frame) {
		Graphics2D g2 = (Graphics2D) g;
		if (getPSpriteX()!=getSpriteX() && getSpriteX()<getPSpriteX()) {
			g2.drawImage(viperSprite, World.spriteLength + (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)),(int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), -(int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		} else {
			g2.drawImage(viperSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)),(int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
		if (getOnFire()) {
			g2.drawImage(fireSprite, (int)(getPSpriteX()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(getPSpriteY()+(1-spriteSize)*(World.spriteLength/2+1)), (int)(World.spriteLength*spriteSize), (int)(World.spriteLength*spriteSize), frame);
		}
	}
}
