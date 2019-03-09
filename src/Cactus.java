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
public class Cactus implements Item{
	private Image cactusSprite;
	private double spriteSize;
	private double maxSpriteSize=1;
	private double minSpriteSize=0.2;
	
	private boolean alive;
	
	private int age;
	private int deathAge=1750;
	private double pGrow=0.01;

	public Cactus() {
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize+minSpriteSize);
		try {
			double rand= Math.random();
			if (rand<0.25)cactusSprite = ImageIO.read(new File("cactus4.png"));
			else if (rand<0.50)cactusSprite = ImageIO.read(new File("cactus3.png"));
			else if (rand<0.75)cactusSprite = ImageIO.read(new File("cactus2.png"));
			else cactusSprite = ImageIO.read(new File("cactus1.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage(){
		return cactusSprite;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public int getAge() {
		return age;
	}

	public double getSpriteSize() {
		return spriteSize;
	}
	
	public void update(){
		if (Math.random()<pGrow && spriteSize<=maxSpriteSize) spriteSize+=0.1;
		if (age>=deathAge) alive=false;
		if (alive) {
			age++;
		}
	}
}
