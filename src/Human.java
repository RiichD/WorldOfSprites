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
	
	private int drowning;
	private int drowningtime=2;//Se noie s'il reste 15 secondes dans l'eau
	
	private int health;
	private int maxHealth = 150;
	private int minHealth = 75;
	private double loseHealth = 0.75; //probabilite de perdre de la vie
	
	public Human() {
		super();
		try {
			humanSprite = ImageIO.read(new File("human.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		drowning=0;
		health=(int)(Math.random()*(maxHealth+1)+minHealth);
	}
	
	public Human(int x, int y) {
		super(x,y);
		try {
			humanSprite = ImageIO.read(new File("human.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		drowning=0;
		health=(int)(Math.random()*(maxHealth+1)+minHealth);
	}
	
	//Get
	public Image getImage() {
		return humanSprite;
	}
	
	//Add
	public void addDrowning() {
		drowning++;
		if (drowning == drowningtime ) {
			super.setAlive(false);
		}
	}
	
	//Set
	public void setDrowning(int x) {
		drowning=x;
	}
	
	public void update() {
		if (Math.random()<loseHealth) health--;
		if (health == 0 ) setAlive(false);
		addAge();
		if (getAge()>getDeathAge()) setAlive(false);
		removeStime();
	}
	
	public void draw(Graphics2D g, JFrame frame) {
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(humanSprite, getPSpriteX(), getPSpriteY(), World.spriteLength, World.spriteLength, frame);

	}
}
