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

import java.util.ArrayList;

@SuppressWarnings({ "serial", "unused" })
public class World extends JPanel{
	
	public static final int X = 48, Y = 48;//taille de world
	
	private JFrame frame;
	
	private int spriteLength = 32;
	
	private Item[][] world; //Le terrain uniquement
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //environnement contient les arbres, le feu, volcan etc..
	
	public World(int x, int y){
		world = new Item[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		
		
		int i, j;
		
		// Terrain prédéfini
		
		for (i=0; i<x; i++) {
			for (j=0; j<y; j++){
				//Condition à ajouter pour éviter de créer un nouveau Water()
				if (world[i][0]==null)world[i][0] = new Water();
				if (world[0][j]==null)world[0][j] = new Water();
				if (world[x-1][j]==null)world[x-1][j] = new Water();
				if (world[i][y-1]==null)world[i][y-1] = new Water();
				
				if (world[i][1]==null)world[i][1] = new Water();
				if (world[1][j]==null)world[1][j] = new Water();
				if (world[x-2][j]==null)world[x-2][j] = new Water();
				if (world[i][y-2]==null)world[i][y-2] = new Water();
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
		
		//Création du frame et affichage de la fenêtre
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize(spriteLength*X+X,spriteLength*Y+Y);
		frame.setVisible(true);
	}
	
	public void add(Item i, int x, int y) {
		if (environnement[x][y]== null) {
			environnement[x][y] = null; //Programme plus rapide si on met null puis ajouter l'Item i
			environnement[x][y] = i;
		} else {
			System.out.println("Ajout impossible");
		}
	}
	
	public void addAgents(Agent a) {
		agents.add(a);
	}
	
	// Déplace les agents
	public void move() {
		for (Agent a : agents) {
			a.move();
		}
	}
	
	// Mise à jour de chaque Item et Agents
	public void update() {
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ ) {
				if (world[i][j]!=null) { 
					world[i][j].update();
				}
			}
		for (Agent a : agents) {
			a.update();
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ )
			{
				if (world[i][j] instanceof Item) g2.drawImage((world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
				if (environnement[i][j] instanceof Item) g2.drawImage((environnement[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);				
			}
		for (Agent a : agents) {
			g2.drawImage(a.getImage(),spriteLength*(a.getX()),spriteLength*(a.getY()),spriteLength,spriteLength, frame);
		}
	}
	
	public static void main(String[] args) {
		World world = new World(X,Y);
		world.add(new Tree(), 15, 15);
		world.add(new Rose(), 10, 10);
		world.add(new Marguerite(), 20, 20);
		world.add(new Tree(), 15, 15); //Simule l'ajout impossible
		world.addAgents(new Agent(10, 15, X, Y));
		int delai = 200;
		int nbpas = 0;
		while (nbpas < 10000) {
			
			world.move();
			nbpas++;
			try {
				Thread.sleep(delai);
			} catch ( InterruptedException e ) 
			{
			}
			world.repaint();
		}
	}
}


