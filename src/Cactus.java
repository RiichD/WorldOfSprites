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
	
	public Cactus() {
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
	
	public void update(){
	}
}
