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
public class Tsunami extends NaturalDisaster{
	private Image tsunamiSprite;
	
	public Tsunami() {
		super();
		try {
			tsunamiSprite = ImageIO.read(new File("tsunami.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage() {
		return tsunamiSprite;
	}
	
	public void update() {
	}
}