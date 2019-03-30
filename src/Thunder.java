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
public class Thunder extends NaturalDisaster{
	private Image thunderSprite;
	private double spriteSize;
	private double maxSpriteSize=1;
	private double minSpriteSize=0.2;
	
	private boolean actif;
	
	private double pGrow=0.01;
	
	public Thunder() {
		super();
		actif=true;
		spriteSize=Math.random()*(maxSpriteSize-minSpriteSize+minSpriteSize);
		try {
			thunderSprite = ImageIO.read(new File("pictures/thunder1.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage() {
		return thunderSprite;
	}
	
	public boolean getAlive() {
		return actif;
	}
	
	public double getSpriteSize() {
		return spriteSize;
	}
	
	public void update() {
		try {
			thunderSprite = ImageIO.read(new File("pictures/thunder2.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		actif = false;
	}
}