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
	
	public Rose() {
		super();
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
	
	public void update() {
		
	}
	
}
