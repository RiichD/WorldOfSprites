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
	
	public static final int spriteLength = 24;
	
	private Item[][] world; //Le terrain uniquement : terre, mer, volcan
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //environnement contient les arbres, le feu etc..
	private int[][] floor; //permet de savoir si on peut se déplacer a la case
	
	public World(int x, int y){
		
		world = new Item[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		floor = new int[x][y];
		int i, j;
		
		// Terrain prédéfini
		
		for (i=0; i<x; i++) {
			for (j=0; j<y; j++){
				//Condition a ajouter pour éviter de creer un nouveau Water()
				if (world[i][0]==null)world[i][0] = new Water();
				if (world[0][j]==null)world[0][j] = new Water();
				if (world[x-1][j]==null)world[x-1][j] = new Water();
				if (world[i][y-1]==null)world[i][y-1] = new Water();
				
				if (world[i][1]==null)world[i][1] = new Water();
				if (world[1][j]==null)world[1][j] = new Water();
				if (world[x-2][j]==null)world[x-2][j] = new Water();
				if (world[i][y-2]==null)world[i][y-2] = new Water();
				floor[i][j]=0;
			}
		}
		
		for (i=2; i<x-2; i++) {
			if (Math.random() < 0.5 ) {
				world[i][2] = new Sand();
			} else {
				world[i][2] = new Water();
			} if (Math.random() < 0.5 ) {
				world[x-3][i] = new Sand();
			} else {
				world[x-3][i] = new Water();
			}
			
			world[i][3] = new Sand();
			world[x-4][i] = new Sand();
		}
		
		for (i=2; i<y-2; i++) {
			if (Math.random() < 0.5 ) {
				world[2][i] = new Sand();
			} else {
				world[2][i] = new Water();
			} if (Math.random() < 0.5 ) {
				world[i][y-3] = new Sand();
			} else {
				world[i][y-3] = new Water();
			}
			
			world[3][i] = new Sand();
			world[i][y-4] = new Sand();
		}
		
		for (i=0; i<x; i++) 
			for (j=0; j<y; j++)
				if (world[i][j]==null) {
					world[i][j] = new Grass();
				}
		
		
		for (int n=0;n<X;n++) {
			for (int m=0;m<Y;m++) {
				if (world[n][m] instanceof Sand || world[n][m] instanceof Grass)
					floor[n][m]=1;
			}
		}
		
		// Fin du terrain prédéfini
		
		//Création du frame et affichage de la fenêtre
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize(spriteLength*X+X,spriteLength*Y+Y);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//Get
	public Item getWorld(int x, int y) {
		return world[x][y];
	}
	
	public int[][] getFloor(){
		return floor;
	}
	
	//add
	public void addItem(Item i, int x, int y) {
		if (environnement[x][y]== null) {
			environnement[x][y] = null; //Programme plus rapide si on met null puis ajouter l'Item i
			if ( ! (world[x][y] instanceof Water) ) {
				if (world[x][y] instanceof Sand) {
					if (i instanceof Cactus) {
						environnement[x][y] = i;
						floor[x][y] = -1;
					}
				} else if (world[x][y] instanceof Grass) {
					if (i instanceof Tree || i instanceof Flower)
						environnement[x][y] = i;
				} else {
					System.out.println("Pas d'ajout possible");
				}
			} else
				System.out.println("Ajout dans l'eau impossible");
		} else {
			System.out.println("Ajout impossible");
		}
	}
	
	public void addAgent(Agent a) {
		if (!(world[a.getX()][a.getY()] instanceof Water))
			if (a instanceof Human)
				agents.add(a);
		else
			System.out.println("Ajout d'agent impossible dans l'eau");
	}
	
	//remove ou replace
	public void removeAgent(Agent a) {
		agents.remove(a);
	}
	
	public void removeAgent(ArrayList<Agent> rmagents) {
		for (Agent a : rmagents) {
			agents.remove(a);
		}
	}
	
	public void replaceItemTerrain(Item i, int x, int y) { //Pour world
		world[x][y] = i;
	}
	
	public void replaceItem(Item i, int x, int y) { //Pour environnement
		environnement[x][y] = i;
	}
	
	//Affichage du floor pour debuger
		public void showFloor() {
			for ( int i = 0 ; i < X ; i++ ) {
				for ( int j = 0 ; j < Y ; j++ ) {
					if (floor[j][i]>=0)
						System.out.print("  "+ floor[j][i]);
					else
						System.out.print(" " + floor[j][i]);
				}
				System.out.println();
			}
		}
	
	// Mise a jour de chaque Item et Agents
	public void update() {
		
		//Deplacement des agents
		for (Agent a : agents) {
			a.move(floor, environnement);
		}
		
		//Mise a jour des donnees
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ ) {
				if (world[i][j]!=null) { 
					world[i][j].update();
				}
			}
		
		//Creation d'une liste pour ajouter les agents decedes
		ArrayList<Agent> agentsmort = new ArrayList<Agent>();
		for (Agent a : agents) {
			a.update();
			
			if (a instanceof Human ) { //Si humain dans l'eau, incremente la noyade
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
		
		//Boucle permettant d'afficher les agents fluidement
		for (int i = 0; i < spriteLength; i++) {
			try {
				Thread.sleep(10);
			} catch ( Exception e ) {};
			for (Agent a : agents ) {
				a.smoothMove();
				repaint();
			}
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < world.length ; i++ )
			for ( int j = 0 ; j < world[0].length ; j++ )
			{
				if (world[i][j] instanceof Item) 
					g2.drawImage((world[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				if (environnement[i][j] instanceof Item) 
					g2.drawImage((environnement[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
			}
		
		//Le clone permet d'eviter les problemes rencontres lors d'affichage des agents et des modifications qui ont lieu en meme temps
		ArrayList<Agent> clone = new ArrayList<Agent>(agents);
		for (Agent a : clone) {
			a.draw(g2, frame, spriteLength);
		}
	}
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		for (int i=0;i<50;i++) {
			world.addAgent(new Human());
		}
		
		for (int i=0;i<10;i++) {
			world.addItem(new Tree(), (int)(Math.random()*X), (int)(Math.random()*Y));
			world.addItem(new Rose(), (int)(Math.random()*X), (int)(Math.random()*Y));
			world.addItem(new Tulipe(), (int)(Math.random()*X), (int)(Math.random()*Y));
			world.addItem(new Marguerite(), (int)(Math.random()*X), (int)(Math.random()*Y));
		}
		
		for (int i=0;i<25;i++)
			world.addItem(new Cactus(), (int)(Math.random()*X), (int)(Math.random()*Y));
		
		//world.showFloor(); //A utiliser en cas de probleme pour afficher les deplacements possibles avec les entiers
		while (true) {
			world.update();
		}
	}
}


