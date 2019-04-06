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
public class Tulip extends Flower {
	private double maxSpriteSize=1;
	private double minSpriteSize=0.2;
	
	private int deathAge=500;
	
	private double pGrow=0.01;
	
	//A ne pas modifier
	private Image spriteTulip;
	private double spriteSize;
	private boolean alive;
	private int age;
	
	public Tulip() {
		super();
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize+minSpriteSize);
		try {
			if (Math.random()<0.5)
				spriteTulip = ImageIO.read(new File("pictures/tulip1.png"));
			else
				spriteTulip = ImageIO.read(new File("pictures/tulip2.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage() {
		return spriteTulip;
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
	
	public void setAlive(boolean b) {
		alive=b;
	}
	
	public void update() {
		if (Math.random()<pGrow && spriteSize<=maxSpriteSize) spriteSize+=0.1;
		if (age>=deathAge) alive=false;
		if (alive) {
			age++;
		}
	}
	
}
