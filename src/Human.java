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
	private int drowning; //Se noie s'il reste 15 secondes dans l'eau
	
	public Human() {
		super();
		
		try {
			humanSprite = ImageIO.read(new File("human.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		drowning=0;
	}
	
	public Human(int x, int y) {
		super(x,y);
		
		try {
			humanSprite = ImageIO.read(new File("humain.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		drowning=0;
	}
	
	//Get
	public Image getImage() {
		return humanSprite;
	}
	
	//Add
	public void addDrowning() {
		drowning++;
		if (drowning == 15 ) {
			super.setAlive(false);
		}
	}
	
	//Set
	public void setDrowning(int x) {
		drowning=x;
	}
	
	public void update() {
	}
}
