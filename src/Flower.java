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
	private int age;
	
	public Flower() {
		age=0;
	}
	
	public int getAge() {
		return age;
	}
	
	public abstract Image getImage();
	
	public abstract void update();
}
