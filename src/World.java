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
	
	//Sprites
	private Image waterSprite;
	private Image grassSprite;
	private Image sandSprite;
	
	/* world :
	 * 0 : water
	 * 1 : grass
	 * 2 : sand
	 */
	private int[][] terrain; //Le terrain uniquement : terre, mer, volcan
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //environnement contient les arbres, le feu etc..
	private int[][] altitude; //altitude du world
	
	//Vitesse d'execution
	private int delai=5; //delai pour la vitesse de deplacement d'agent
	private int delai2=0; //delai pour la vitesse d'execution (d'affichage)
	
	//Probabilite d'ajout
	private double pHuman = 0.8; //Ajout d'un humain aleatoirement
	private double pEnfant = 1; //Ajout d'un enfant lorsque 2 sexe sont a la meme case
	
	public World(int x, int y){
		
		terrain = new int[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		altitude = new int[x][y];
		int i, j;
		
		try {
			waterSprite = ImageIO.read(new File("water.png"));
			grassSprite = ImageIO.read(new File("grass.png"));
			sandSprite = ImageIO.read(new File("sand.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Terrain predefini
		
		for (i=0; i<x; i++) {
			for (j=0; j<y; j++){
				altitude[i][j]=0;
			}
		}
		
		for (i=2; i<x-2; i++) {
			if (Math.random() < 0.5 ) {
				terrain[i][2] = 2;
			} else {
				terrain[i][2] = 0;
			} if (Math.random() < 0.5 ) {
				terrain[x-3][i] =2;
			} else {
				terrain[x-3][i] = 0;
			}
			
			terrain[i][3] = 2;
			terrain[x-4][i] = 2;
		}
		
		for (i=2; i<y-2; i++) {
			if (Math.random() < 0.5 ) {
				terrain[2][i] = 2;
			}
			if (Math.random() < 0.5 ) {
				terrain[i][y-3] = 2;
			}
			
			terrain[3][i] = 2;
			terrain[i][y-4] = 2;
		}
		
		for (i=4; i<x-4; i++) 
			for (j=4; j<y-4; j++)
				if (terrain[i][j]==0) {
					terrain[i][j] = 1;
				}
		
		// Fin du terrain predefini
		
		//Agents de depart
		for (int n=0;n<50;n++) {
			addAgent(new Human());
		}
		
		for (int n=0;n<10;n++) {
			addItem(new Tree(), (int)(Math.random()*X), (int)(Math.random()*Y));
			addItem(new Rose(), (int)(Math.random()*X), (int)(Math.random()*Y));
			addItem(new Tulipe(), (int)(Math.random()*X), (int)(Math.random()*Y));
			addItem(new Marguerite(), (int)(Math.random()*X), (int)(Math.random()*Y));
			addItem(new Tsunami(), (int)(Math.random()*X), (int)(Math.random()*Y));
		}
		
		for (int n=0;n<25;n++)
			addItem(new Cactus(), (int)(Math.random()*X), (int)(Math.random()*Y));
		
		
		//Creation du frame et affichage de la fenetre
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize(spriteLength*X+X,spriteLength*Y+Y);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//Get
	public int[][] getTerrain() {
		return terrain;
	}
	
	public int[][] getAltitude(){
		return altitude;
	}
	
	public Item[][] getEnvironnement(){
		return environnement;
	}
	
	//add
	public void addItem(Item i, int x, int y) {
		if (environnement[x][y]== null) {
			environnement[x][y] = null; //Programme plus rapide si on met null avant d'ajouter l'Item i
			if ( ! (terrain[x][y]==0 )) {
				if (terrain[x][y]==2) {
					if (i instanceof Cactus) {
						environnement[x][y] = i;
					}
				} else if (terrain[x][y]==1) {
					if (i instanceof Tree || i instanceof Flower)
						environnement[x][y] = i;
				} else {
					System.out.println("Pas d'ajout possible");
				}
			} else {
				if (i instanceof Tsunami) {
					environnement[x][y]=i;
				}
			}
		} else {
			System.out.println("Un item se trouve a cet emplacement");
		}
	}
	
	public void addAgent(Agent a) {
		if (!(terrain[a.getX()][a.getY()]==0))
			if (a instanceof Human)
				agents.add(a);
		else
			System.out.println("Ajout d'agent impossible");
	}
	
	//remove ou replace
	public void removeAgent(Agent a) {
		agents.remove(a);
	}
	
	public void replaceItem(Item i, int x, int y) { //Pour environnement
		environnement[x][y] = i;
	}
	
	//Affichage du altitude pour debuger
		public void showAltitude() {
			for ( int i = 0 ; i < X ; i++ ) {
				for ( int j = 0 ; j < Y ; j++ ) {
					if (altitude[j][i]>=0)
						System.out.print("  "+ altitude[j][i]);
					else
						System.out.print(" " + altitude[j][i]);
				}
				System.out.println();
			}
		}
	
	// Mise a jour de chaque Item et Agents
	public void update() {
		
		//Mise a jour des donnees de l'environnement
		for ( int i = 0 ; i < terrain.length ; i++ )
			for ( int j = 0 ; j < terrain[0].length ; j++ ) {
				if (environnement[i][j]!=null ) {
					environnement[i][j].update();
				}
				/*
				if (environnement[i][j] instanceof Tsunami) {
					boolean trouve = false;
					if (environnement[i][j] instanceof Tsunami ) {
						if (i+1 < X && !(environnement[i+1][j] instanceof Tsunami)) {
							environnement[i+1][j] = (new Tsunami()).clone((Tsunami)environnement[i+1][j]);
							trouve=true;
						}
						if (i-1 >=0 && !(environnement[i-1][j] instanceof Tsunami)) {
							environnement[i-1][j] = (new Tsunami()).clone((Tsunami)environnement[i-1][j]);
							trouve=true;
						}
						if (j+1 < X && !(environnement[i][j+1] instanceof Tsunami)) {
							environnement[i][j+1] = (new Tsunami()).clone((Tsunami)environnement[i][j+1]);
							trouve=true;
						}
						if (j-1 >=0 && !(environnement[i][j-1] instanceof Tsunami)) {
							environnement[i][j-1] = (new Tsunami()).clone((Tsunami)environnement[i][j-1]);
							trouve=true;
						}
					}
				}*/
			}
		
		//Liste permettant d'ajouter les nouveaux enfants
		ArrayList<Agent> nEnfant = new ArrayList<Agent>();
		//Met a jour les agents
		for (Agent a : agents) {
			if (a.getAlive()) {
				a.move(terrain, environnement);
				a.update();
			}
			if (a instanceof Human && terrain[a.getX()][a.getY()]==0) ((Human)a).addDrowning();
			else ((Human)a).setDrowning(0);
			
			for (Agent a2 : agents) {
				if (!(a.equals(a2)) && Math.random()<pEnfant) {
					if (a.getSexe()!=a2.getSexe() && a.getX()==a2.getX() && a.getY()==a2.getY()) {
						if (a.getStime()==0 && a2.getStime()==0 ) { //Naissance d'un enfant
							int cptNbPos = 0;
							for (Agent a3 : agents) {
								if (a.getX()==a3.getX() && a3.getY()==a3.getY()) cptNbPos++;
							}
							
							if (cptNbPos < 4) { //S'il y a 3 agents a la meme position, il n'y a pas naissance d'enfant
								nEnfant.add(new Human(a.getX(), a.getY())); //nouveau ne 
								a.setStime(); //Reinitialise le stime
								a2.setStime();
							}
						}
					}
				}
			}
		}
		
		//Boucle permettant d'afficher les agents fluidement
		for (int i = 0; i < spriteLength; i++) {
			try {
				Thread.sleep(delai);
			} catch ( Exception e ) {};
			for (Agent a : agents ) {
				a.smoothMove();
				repaint();
			}
		}
		
		//S'il y a des agents qui sont morts, alors on le retire de la liste
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getAlive() == false) agents.remove(agents.get(i));
		}
		
		//Ajout les enfants dans la liste d'agent
		for ( Agent a : nEnfant ) {
			addAgent(a);
		}
		
		try {
			Thread.sleep(delai2);
		} catch ( Exception e ) {};
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < terrain.length ; i++ )
			for ( int j = 0 ; j < terrain[0].length ; j++ )
			{
				if (terrain[i][j]==0) {
					g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
				} else if (terrain[i][j]==1) {
					g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
				} else if (terrain[i][j]==2) {
					g2.drawImage(sandSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	

				}
				if (environnement[i][j] instanceof Item) 
					g2.drawImage((environnement[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
			}
		
		//Le clone permet d'eviter les problemes rencontres lors d'affichage des agents et des modifications qui ont lieu en meme temps
		ArrayList<Agent> clone = new ArrayList<Agent>(agents);
		for (Agent a : clone) {
			if (a.getAlive()) a.draw(g2, frame);
		}
	}
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		//world.showAltitude(); //A utiliser en cas de probleme avec l'affichage des deplacements possibles avec les entiers
		while (true) {
			world.update();
		}
	}
}


