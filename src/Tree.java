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
	private double maxSpriteSize=1; //Taille maximale du sprite
	private double minSpriteSize=0.2; //Taille minimale du sprite
	
	private int deathAge=1500; //Age maximal de l'objet
	
	private double pGrow=0.01; //Chance de gagner en taille
	
	private int agingSpeed = 20; //Lorsque l'arbre est brule, il vieillit plus vite
	//A ne pas modifier
	private Image treeSprite; //Image du sprite
	private double spriteSize; //Taille du sprite
	private boolean fire; //Si l'objet est en feu
	private boolean alive; //Si l'objet est en vie
	private int age; //L'age de l'objet
	
	public Tree() {
		age=1;
		alive=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize)+minSpriteSize;
		fire=false;
		try {
			if (Math.random()<0.5)
				treeSprite = ImageIO.read(new File("pictures/tree1.png"));
			else
				treeSprite = ImageIO.read(new File("pictures/tree2.png"));
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
		//Changement d'image si l'arbre est brule
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
		if (age>=deathAge) alive=false; //L'objet a une duree de vie, il meurt quand l'age maximal est atteint
		if (alive) {
			if (fire) age+=agingSpeed; //Vieillit plus vite
			else age++; //L'objet vieillit toutes les iterations
		}
	}
}
