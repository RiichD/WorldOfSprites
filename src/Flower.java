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
public abstract class Flower implements Item {
	private boolean fire;
	public Flower() {
		fire=false;
	}
	
	public abstract int getAge();
	
	public abstract boolean getAlive();
	
	public boolean getFire() {
		return fire;
	}

	public abstract Image getImage();
	
	public void setFire(boolean b) {
		fire=b;
	}
	
	public abstract void setAlive(boolean b);
	
	public abstract void update();
}
