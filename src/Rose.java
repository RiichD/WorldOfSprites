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
public class Rose extends Flower {
	private Image spriteRose;
	private double spriteSize;
	private double maxSpriteSize=1;
	private double minSpriteSize=0.2;
	
	private boolean alive;
	
	private int age;
	private int deathAge=500;
	private double pGrow=0.01;

	public Rose() {
		super();
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize+minSpriteSize);
		try {
			spriteRose = ImageIO.read(new File("fleur1.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage() {
		return spriteRose;
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
	
	public void update() {
		if (Math.random()<pGrow && spriteSize<=maxSpriteSize) spriteSize+=0.1;
		if (age>deathAge) alive=false;
		age++;
	}
	
}
