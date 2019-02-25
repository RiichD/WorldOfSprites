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
public class Tree implements Item{
	private int age;
	private Image treeSprite;
	
	public Tree() {
		age=0;
		try {
			treeSprite = ImageIO.read(new File("tree.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getImage(){
		return treeSprite;
	}
	
	public void setAge(int age) {
		this.age=age;
	}
	
	public void update(){
		if (age == 0 ) {
			try {
				treeSprite = ImageIO.read(new File("tree.png"));
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			try{
				treeSprite = ImageIO.read(new File("tronc.png"));
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
