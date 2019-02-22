import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Grass implements Item{
	private Image grassSprite;
	
	public Grass() {
		try {
			grassSprite = ImageIO.read(new File("grass.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage(){
		return grassSprite;
	}
	
	public void update(){
	}
}
