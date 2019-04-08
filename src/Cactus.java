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
	private double maxSpriteSize=1; //Taille maximale du sprite
	private double minSpriteSize=0.2; //Taille minimale du sprite

	private int deathAge=1750; //Age maximal de l'objet
	
	private double pGrow=0.01; //Chance d'obtenir un objet qui grandit
	
	//A ne pas modifier
	private Image cactusSprite;
	private double spriteSize;
	private boolean alive; //Si l'objet est en vie
	private int age; //L'age de l'objet
	
	public Cactus() {
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize+minSpriteSize);
		try {
			double rand= Math.random();
			if (rand<0.25)cactusSprite = ImageIO.read(new File("pictures/cactus4.png"));
			else if (rand<0.50)cactusSprite = ImageIO.read(new File("pictures/cactus3.png"));
			else if (rand<0.75)cactusSprite = ImageIO.read(new File("pictures/cactus2.png"));
			else cactusSprite = ImageIO.read(new File("pictures/cactus1.png"));
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
		if (Math.random()<pGrow && spriteSize<=maxSpriteSize) spriteSize+=0.1; //Chance de grandir en taille
		if (age>=deathAge) alive=false; //L'objet a une duree de vie, il meurt quand l'age maximal est atteint
		if (alive) {
			age++; //L'age augmente avec le nombre d'iteration
		}
	}
}
