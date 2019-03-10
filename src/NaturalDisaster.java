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
public abstract class NaturalDisaster implements Item{
	
	public NaturalDisaster() {
	}
	
	public abstract Image getImage();
	
	public abstract void update();
}