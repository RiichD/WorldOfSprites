import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class World extends JPanel{
	
	private JFrame frame;
	
	private int spriteLength = 32;
	
	private Item[][] world;
	
	public World(int x, int y){
		world = new Item[x][y];
		
		int i, j;
		
		// Terrain prédéfini
		
		for (i=0; i<x; i++) {
			for (j=0; j<y; j++){
				world[i][0] = new Water();
				world[0][j] = new Water();
				world[x-1][j] = new Water();
				world[i][y-1] = new Water();
				
				world[i][1] = new Water();
				world[1][j] = new Water();
				world[x-2][j] = new Water();
				world[i][y-2] = new Water();
			}
		}
		
		for (i=2; i<x-2; i++) {
			if (Math.random() < 0.5 ) world[i][2] = new Sand();
			else world[i][2] = new Water();
			if (Math.random() < 0.5 ) world[x-3][i] = new Sand();
			else world[x-3][i] = new Water();
			
			world[i][3] = new Sand();
			world[x-4][i] = new Sand();
		}
		
		for (i=2; i<y-2; i++) {
			if (Math.random() < 0.5 ) world[2][i] = new Sand();
			else world[2][i] = new Water();
			if (Math.random() < 0.5 ) world[i][y-3] = new Sand();
			else world[i][y-3] = new Water();
			
			world[3][i] = new Sand();
			world[i][y-4] = new Sand();
		}
		
		for (i=0; i<x; i++) 
			for (j=0; j<y; j++)
				if (world[i][j]==null) world[i][j] = new Grass();
		
		// Fin du terrain prédéfini
		
		
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize(978,1000);
		frame.setVisible(true);
	}
	
	public void add(Item i, int x, int y) {
		world[x][y] = i;
	}
	
	public void update() {
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ ) {
				if (world[i][j]!=null) { 
					world[i][j].update();
				}
			}
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ )
			{
				if (world[i][j] instanceof Grass) g2.drawImage(((Grass)world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				else if (world[i][j] instanceof Tree) g2.drawImage(((Tree)world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				else if (world[i][j] instanceof Water) g2.drawImage(((Water)world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				else if (world[i][j] instanceof Sand) g2.drawImage(((Sand)world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
			}
	}
	
	public static void main(String[] args) {
		World world = new World(30,30);
		//world.update();
	}
}


