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
	
	private int spriteLength = 16;
	
	private Item[][] world; //Le terrain uniquement : terre, mer, volcan
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //environnement contient les arbres, le feu etc..
	private int[][] floor; //permet de savoir si on peut se déplacer à la case
	
	public World(int x, int y){
		
		world = new Item[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		floor = new int[x][y];
		
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
				
				floor[i][j] = 0;
			}
		}
		
		for (i=2; i<x-2; i++) {
			if (Math.random() < 0.5 ) {
				world[i][2] = new Sand();
				floor[i][2] = 1;
			} else {
				world[i][2] = new Water();
			} if (Math.random() < 0.5 ) {
				world[x-3][i] = new Sand();
				floor[x-3][i] = 1;
			} else {
				world[x-3][i] = new Water();
			}
			
			world[i][3] = new Sand();
			world[x-4][i] = new Sand();
			floor[i][3] = 1;
			floor[x-4][i] = 1;
		}
		
		for (i=2; i<y-2; i++) {
			if (Math.random() < 0.5 ) {
				world[2][i] = new Sand();
				floor[2][i] = 1;
			} else {
				world[2][i] = new Water();
			} if (Math.random() < 0.5 ) {
				world[i][y-3] = new Sand();
				floor[i][y-3] = 1;
			} else {
				world[i][y-3] = new Water();
			}
			
			world[3][i] = new Sand();
			world[i][y-4] = new Sand();
			floor[i][y-4] = 1;
		}
		
		for (i=0; i<x; i++) 
			for (j=0; j<y; j++)
				if (world[i][j]==null) {
					world[i][j] = new Grass();
					floor[i][j] = 1;
				}
		
		// Fin du terrain prédéfini
		
		//Création du frame et affichage de la fenêtre
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize(spriteLength*X+X,spriteLength*Y+Y);
		frame.setVisible(true);
	}
	
	//Get
	public int[][] getFloor() {
		return floor;
	}
	
	public Item getWorld(int x, int y) {
		return world[x][y];
	}
	
	//add
	public void addItem(Item i, int x, int y) {
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
	
	//remove ou replace
	public void replaceItemTerrain(Item i, int x, int y) { //Pour world
		world[x][y] = i;
	}
	
	public void replaceItem(Item i, int x, int y) { //Pour environnement
		environnement[x][y] = i;
	}
	
	// Déplace les agents
	public void move(int[][] floor) {
		for (Agent a : agents) {
			a.move(floor);
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
		
		//Création d'une liste pour ajouter les agents décédés
		ArrayList<Agent> agentsmort = new ArrayList<Agent>();
		
		for (Agent a : agents) {
			a.update();
			
			if (a instanceof Human ) { //Si humain dans l'eau, incrémente la noyade
				if ( floor[a.getX()][a.getY()]==0 ) ((Human)a).addDrowning();
				else ((Human)a).setDrowning(0);
			}

			if (a.getAlive()==false) {
				agentsmort.add(a);
			}
		}
		if (agentsmort.size()!=0)
			for (Agent arm : agentsmort) {
				agents.remove(arm);
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
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		world.addItem(new Tree(), 15, 15);
		world.addItem(new Rose(), 10, 10);
		world.addItem(new Marguerite(), 20, 20);
		world.addItem(new Tree(), 15, 15); //Simule l'ajout impossible
		
		world.replaceItemTerrain(new Sand(), 10, 10);
		world.replaceItem(new Cactus(), 10, 10);
		
		for (int i=0;i<20;i++) {
			world.addAgents(new Human(X,Y));
		}
		
		int delai = 200;
		int nbpas = 0;
		while (nbpas < 10000) {
			
			world.move(world.getFloor());
			world.update();
			try {
				Thread.sleep(delai);
			} catch ( InterruptedException e ) 
			{
			}

			nbpas++;
			world.repaint();
		}
	}
}


