import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Marguerite extends Flower {
	private Image spriteMarguerite;
	
	public Marguerite() {
		super();
		try {
			spriteMarguerite= ImageIO.read(new File("fleur2.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage() {
		return spriteMarguerite;
	}
	
	public void update() {
		
	}
	
}