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
public abstract class Predator extends Agent{
	public Predator() {
		super();
	}
	
	public Predator(int x, int y) {
		super(x,y);
	}
	
	//Get
	public abstract int getStime();
	
	public abstract int getStimeIni();
	
	public abstract int getHealth();

	public abstract Image getImage();
	
	//Set
	public abstract void setStime();
	
	public abstract void update();
	
	public abstract void draw(Graphics2D g, JFrame frame);
}
