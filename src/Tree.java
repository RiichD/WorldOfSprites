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
public class Tree implements Item{
	private Image treeSprite;
	private double spriteSize;
	private double maxSpriteSize=2; //Taille maximale du sprite
	private double minSpriteSize=0.2; //Taille minimale du sprite
	private boolean fire; //Si l'arbre est en feu
	private boolean alive; //Si l'arbre est en vie
	
	private int age; //L'age de l'arbre
	private int deathAge=1500; //L'age de deces
	private double pGrow=0.01; //Chance de gagner en taille
	
	public Tree() {
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize)+minSpriteSize;
		fire=false;
		try {
			treeSprite = ImageIO.read(new File("pictures/tree.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage(){
		return treeSprite;
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
	
	public boolean getFire() {
		return fire;
	}
	
	public void setAge(int age) {
		this.age=age;
	}
	
	public void setAlive(boolean b) {
		alive=b;
	}
	
	public void setBurned() {
		try {
			treeSprite = ImageIO.read(new File("pictures/burnedtree.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		fire=true;
	}
	
	public void update(){
		if (!fire && Math.random()<pGrow && spriteSize<=maxSpriteSize) spriteSize+=0.1;
		if (age>=deathAge) alive=false;
		if (alive) {
			age++;
		}
	}
}
