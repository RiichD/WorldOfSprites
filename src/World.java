import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
	public static final int delai3=0; //delai du main ( iteration )
	
	//Attributs du monde
	private int nbHumanDepart = 25;
	private int nbChickenDepart = 25;
	private int nbEnvDepart = 10; //Arbres, Fleurs ...
	private int nbCactusDepart = 25;
	private int nbAgentsMaxPos = 2; //Variable uniquement pour les naissances d'enfants : nombre d'agents maximum a une meme position. 2 au minimum pour avoir un enfant.
	private int addHumanHealth = 50;
	
	private int nbChangementTerrain = 20; //Augmente la chance d'avoir des modifications du terrain. 1 par defaut
	//Probabilite d'ajout
	private double pHuman = 0.55; //probabilite d'apparation d'un humain aleatoirement
	private double pEnfant = 1; //probabilite d'apparation de la naissance d'un enfant lorsque 2 sexes differents sont a la meme case
	
	private double pFlower = 0.25; //probabilite d'apparation d'une fleur
	private double pTulipe = 0.05;
	private double pMarguerite = 0.05;
	private double pRose = 0.05;
	
	private double pGrass = 0.2;
	private double pSand = 0.8;
	private double pWater = 0.5;
	
	private double pTree = 0.01;
	private double pCactus = 0.1;
	
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
		for (int n=0;n<nbHumanDepart;n++) {
			addAgent(new Human());
		}
		
		for (int n=0;n<nbChickenDepart;n++) {
			addAgent(new Chicken());
		}
		
		for (int n=0;n<nbEnvDepart;n++) {
			addItem(new Tree());
			addItem(new Rose());
			addItem(new Tulipe());
			addItem(new Marguerite());
			addItem(new Tsunami());
		}
		
		for (int n=0;n<nbCactusDepart;n++)
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
				}
			} else {
				if (i instanceof Tsunami) {
					environnement[x][y]=i;
				}
			}
		}
	}
	
	public void addItem(Item i) {
		addItem(i, (int)(Math.random()*X), (int)(Math.random()*Y));
	}
	
	public void addAgent(Agent a) {
		if (!(terrain[a.getX()][a.getY()]==0))
			if (a instanceof Human || a instanceof Chicken)
				agents.add(a);
		else
			System.out.println("Ajout d'agent impossible");
	}
	
	//remove ou replace
	public void removeAgent(Agent a) {
		ArrayList<Agent> copie = new ArrayList<Agent>(agents);
		copie.remove(a);
		agents=copie;
	}
	
	public void removeItem(Item i, int x, int y) { //Pour environnement
		environnement[x][y] = null;
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
		for (int i = 0 ; i< X ; i++ )
			for (int j = 0 ; j<Y ; j++ ) {
				if (environnement[i][j] instanceof Flower || environnement[i][j] instanceof Tree) {
					if (terrain[i][j]!=1) environnement[i][j]=null;
				} else if (environnement[i][j] instanceof Cactus ) {
					if (terrain[i][j]!=2) environnement[i][j]=null;
				}
			}
		//Liste permettant d'ajouter les nouveaux enfants
		ArrayList<Agent> nEnfant = new ArrayList<Agent>();
		//Met a jour les agents
		for (Agent a : agents) {
			if (a.getAlive()) {
				a.move(terrain, environnement);
				a.update();
			}
			if (a instanceof Human && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Human)a).addDrowning();
				else ((Human)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Rose) { //Les humains ne mangent que les roses
					((Human)a).addHealth(addHumanHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
			} else if ( a instanceof Chicken && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Chicken)a).addDrowning();
				else ((Chicken)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Tulipe) { //Les poules ne mangent que les tulipes
					((Chicken)a).addHealth(addHumanHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
			}
			
			for (Agent a2 : agents) {
				if (!(a.equals(a2)) && Math.random()<pEnfant) {
					if (a.getSexe()!=a2.getSexe() && a.getX()==a2.getX() && a.getY()==a2.getY()) {
						if (a.getStime()==0 && a2.getStime()==0 ) { //Naissance d'un enfant
							int cptNbAgents = 0;
							for (Agent a3 : agents) {
								if (a.getX()==a3.getX() && a3.getY()==a3.getY()) cptNbAgents++;
								if (cptNbAgents>nbAgentsMaxPos) {
									break; //Permet de sortir de la boucle for et eviter de faire des boucles inutilement
								}
							}
							if (cptNbAgents <= nbAgentsMaxPos) { //S'il y a nbAgentsMaxPos agents a la meme position, il n'y a pas naissance d'enfant
								if (a instanceof Human) nEnfant.add(new Human(a.getX(), a.getY())); //nouveau ne 
								else if (a instanceof Chicken) nEnfant.add(new Chicken(a.getX(), a.getY()));
								a.setStime(); //Reinitialise le stime des deux agents
								a2.setStime();
							}
						}
					}
				}
			}
		}
		
		//Boucle permettant d'afficher les agents fluidement
		for (int nb=0; nb < spriteLength; nb++) {
			try {
				Thread.sleep(delai);
			} catch ( Exception e ) {}
			for (Agent a : agents) {
				if (a.getAlive()) a.smoothMove();
				repaint();
			}
		}
		
		//S'il y a des agents qui sont morts, alors on le retire de la liste
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).getAlive() == false) removeAgent(agents.get(i));
		}
		
		//Ajout les enfants dans la liste d'agent
		for ( Agent a : nEnfant ) {
			addAgent(a);
		}
		
		//Environnement aleatoire
		if (Math.random()<pFlower) {
			if (Math.random()<pRose) addItem(new Rose());
			if (Math.random()<pTulipe) addItem(new Tulipe());
			if (Math.random()<pMarguerite) addItem(new Marguerite());
		}
		
		//Variables pour le changement de terrain aleatoire
		for (int a=0; a < nbChangementTerrain ; a++) {
			int p=(int)(Math.random()*X);
			int q=(int)(Math.random()*Y);
			boolean presenceSand = false;
			
			if (Math.random()<pSand) {
				if (terrain[p][q]==1) {
					for (int i = -1; i < 2 && !false;i++ ) //Rayon de 3x3 
						for (int j = -1; j < 2 && !false; j++) {
							if (p+i>=0 && p+i<X && q+j>=0 && q+j<Y && terrain[p+i][q+j]==2) {
								presenceSand=true;
							}
						}
					if (presenceSand) { //On ne remplace que grass
						terrain[p][q]=2;
					}
				}
			}
			
			if (Math.random()<pWater) {
				if (terrain[p][q]==2) { //Remplace uniquement le sable
					boolean presenceWater = false;
					//Recherche d'eau sous forme de +
					if (p+1<X ) {
						if (terrain[p+1][q]==0)
							presenceWater = true;
					}
					if (p-1 >= 0) {
						if (terrain[p-1][q]==0)
							presenceWater = true;
					}
					if (q+1 < Y) {
						if (terrain[p][q+1]==0)
							presenceWater = true;
					}
					if (q-1 >= 0) {
						if (terrain[p][q-1]==0)
							presenceWater = true;
					}
					if (presenceWater) { //Uniquement s'il y a de l'eau a cote (les angles ne sont pas pris en compte)
						terrain[p][q] = 0;
					}
				}
			}
			
			p=(int)(Math.random()*X);
			q=(int)(Math.random()*Y);
			
			if (Math.random()<pTree) {
				if (terrain[p][q]==1) addItem(new Tree());
			}
			
			if (Math.random()<pTree) {
				if (terrain[p][q]==2) addItem(new Cactus());
			}
			repaint();
		}
		
		repaint();
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
					if (environnement[i][j] != null ) g2.drawImage((environnement[i][j]).getImage(),spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);	
			}
		
		//Le clone permet d'eviter les problemes rencontres lors d'affichage des agents et des modifications qui ont lieu en meme temps
		ArrayList<Agent> clone = new ArrayList<Agent>(agents);
		for (Agent a : clone) {
			try {
				if (a.getAlive() && a!=null) a.draw(g2, frame);
			} catch ( Exception e ) {
				System.out.println(a.getX() + " " + a.getY());
			}
		}
	}
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		int i=0;
		//world.showAltitude(); //A utiliser en cas de probleme avec l'affichage des deplacements possibles avec les entiers
		while (true) {
			world.update();
			try {
				Thread.sleep(delai3);
			} catch ( Exception e ) {};
			//System.out.println("it : " + i);
			i++;
		}
	}
}


