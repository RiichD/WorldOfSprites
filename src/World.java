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
	
	public static final int X = 48, Y = 48; //taille de world
	
	private JFrame frame;
	
	public static final int spriteLength = 28; //taille de chaque sprite
	
	//Sprites
	private Image waterSprite;
	private Image grassSprite;
	private Image sandSprite;
	private Image fireSprite;
	private Image lavaSprite;
	private Image volcanoSprite;
	private Image obsidianSprite;
	private Image dirtSprite;
	
	/* terrain :
	 * 0 : water
	 * 1 : grass
	 * 2 : sand
	 * 3 : volcano
	 * 4 : obsidian
	 * 5 : dirt
	 */
	
	private int[][] terrain; //Le terrain uniquement : terre, mer, volcan
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //Environnement contient les arbres, le feu etc..
	private int[][] altitude; //Altitude du monde
	private int[][] fire; ///Si 0 rien, si >0 feu, sinon lave
	
	//Vitesse d'execution
	private int delai=5; //Delai pour la vitesse de deplacement d'agent
	private int delai2=0; //Delai pour la vitesse d'execution (d'affichage)
	public static final int delai3=0; //Delai du main ( iteration )
	private int lavaDelai=200; //Delai permettant d'afficher la propagation de la lave progressivement
	private int newCycleLSDelai=5; //Delai lors du passage de la lave a la nouvelle terre
	
	//Attributs du monde
	private int nbHumanDepart = 25; //A chaque debut de cycle du monde, on ajoute un nombre d'agent au depart
	private int nbChickenDepart = 10;
	private int nbFoxDepart = 10;
	private int nbViperDepart = 10;
	
	private int nbEnvDepart = 20; //Arbres, Fleurs ...
	private int nbCactusDepart = 25;
	private int nbAgentsMaxPos = 2; //Variable uniquement pour les naissances d'enfants : nombre d'agents maximum a une meme position. 2 au minimum pour avoir un enfant.
	
	//La sante que recupere chaque agent lorsqu'ils se soignent
	private int addHumanHealth = 50; 
	private int addChickenHealth = 45;
	private int addFoxHealth = 56;
	private int addViperHealth = 105;
	
	private int volcanoSpawn = 0; //Si le nombre d'herbe est inferieur a volcanoSpawn, un volcan apparait sur l'un des herbes, sinon au centre
	private int volcanoX, volcanoY; //Coordonnees du volcan
	private int volcanoRange = (int)(X/1.3); //Distance de propagation de la lave sur le terrain
	
	private int lavaDissipate = 5; //Nombre de laves maximum qui disparaissent chaque iteration
	private int dirtRejuvenate = 5; //Nombre de terres maximum qui apparaissant chaque iteration
	private int grassRejuvenate = 5; //Nombre d'herbes maximum qui apparaissant chaque iteration
	
	private int addSandFill = 1500; //Probabilite tres faible de base. Prevoir une grande valeur
	
	private int nbChangementTerrain = 20; //Augmente la chance de changer le type du terrain. 1 par defaut
	
	//Probabilite d'apparition des agents
	private double pEnfant = 1; //probabilite de la naissance d'un enfant lorsque 2 sexes differents sont a la meme case
	
	//Probabilite d'apparation des items
	private double pFlower = 0.25;
	private double pTulipe = 0.05;
	private double pMarguerite = 0.05;
	private double pRose = 0.05;
	
	private double pGrass = 0.2;
	private double pSand = 0.6; //probabilite qu'une herbe devienne du sable
	private double pWater = 0.4; //probabilite que du sable devienne de l'eau
	
	private double pTree = 0.01;
	private double pCactus = 0.1;
	
	private double pFire = 0.01; //Probabilite qu'un feu apparaisse
	private int fireStop = 15; //fireStop iterations pour que le feu s'eteigne
	
	public double pLavaNoise = 0.15; //Bruit affectant la propagation de la lave.
	
	//Variables en rapport aux degats sur chaque agents
	public static final int fireDamage = 10;
	
	//A ne pas modifier
	private int currentRange = 0; // Variable permettant de creer un effet de propagation de la lave
	private int currentSandFill = 0; //Assure une apparition de sables de maniere progressive
	private boolean newCycle = false; //Nouveau cycle du monde, avec un terrain qui se recree avec l'aide d'un volcan
	private boolean newCycleLastStep = false; //Dernier etape du nouveau cycle avant que tout reprenne normalement
	private int nbWater=0; //Compte le nombre d'eau
	private int nbSand=0; //Compte le nombre de sable
	private int nbGrass=0; //Compte le nombre d'herbe
	
	public World(int x, int y){
		
		terrain = new int[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		altitude = new int[x][y];
		fire = new int[x][y];
		int i, j;
		
		try {
			waterSprite = ImageIO.read(new File("pictures/water.png"));
			grassSprite = ImageIO.read(new File("pictures/grass.png"));
			sandSprite = ImageIO.read(new File("pictures/sand.png"));
			fireSprite = ImageIO.read(new File("pictures/fire.png"));
			lavaSprite = ImageIO.read(new File("pictures/lava.png"));
			volcanoSprite = ImageIO.read(new File("pictures/volcano.png"));
			obsidianSprite = ImageIO.read(new File("pictures/obsidian.png"));
			dirtSprite = ImageIO.read(new File("pictures/dirt.png"));
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Terrain predefini
		for (i=0; i<x; i++) {
			for (j=0; j<y; j++){
				altitude[i][j]=0;
				fire[i][j]=0;
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
		
		addInitiate();
		
		//Creation du frame et affichage de la fenetre
		frame = new JFrame("World Of Sprite");
		frame.add(this);
		frame.setSize((spriteLength+1)*(X),(spriteLength+1)*(Y));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void addInitiate() {
		//Agents de depart
		for (int n=0;n<nbHumanDepart;n++) {
			addAgent(new Human());
		}
		
		for (int n=0;n<nbChickenDepart;n++) {
			addAgent(new Chicken());
		}
		
		for (int n=0;n<nbFoxDepart;n++) {
			addAgent(new Fox());
		}
		
		for (int n=0;n<nbViperDepart;n++) {
			addAgent(new Viper());
		}
		
		//Environnement de depart
		for (int n=0;n<nbEnvDepart;n++) {
			addItem(new Tree());
			addItem(new Rose());
			addItem(new Tulip());
			addItem(new Daisy());
			addItem(new Tsunami());
		}
		
		for (int n=0;n<nbCactusDepart;n++)
			addItem(new Cactus(), (int)(Math.random()*X), (int)(Math.random()*Y));
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
			if (a instanceof Human || a instanceof Chicken || a instanceof Fox || a instanceof Viper)
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
	
	//Nouveau cycle de vie
	private void newLifeCycle() {
		//S'il n'y a plus d'eau, un flac d'eau apparait
		if (nbWater==0 && !newCycle) {
			int x=(int)(Math.random()*X);
			int y=(int)(Math.random()*Y);
			terrain[x][y]=0;
			if (Math.random()<0.75) {
				if (Math.random()<0.5) {
					if (Math.random()<0.25) {
						if (x+1 < X && terrain[x+1][y]==1)
							terrain[x+1][y]=2;
					} else {
						if (x-1 >=0 && terrain[x-1][y]==1)
							terrain[x-1][y]=2;
					}
				} else {
					if (y+1 < Y && terrain[x][y+1]==1)
						terrain[x][y+1]=2;
				}
			} else {
				if (y-1 >=0 && terrain[x][y-1]==1)
					terrain[x][y-1]=2;
			}
		} else if (nbSand==0 && nbGrass<=volcanoSpawn && !newCycle) { //S'il n'y a plus de sable et que le nombre d'herbe est inferieur a volcanoSpawn, un volcan apparait au centre
			newCycle = true;
			volcanoX = X/2;
			volcanoY = Y/2;
			terrain[volcanoX][volcanoY]=3;
		} else if (nbSand == 0 || newCycle) {//Le nouveau cycle commence si le nombre de sable vaut 0
			if (agents.size()>0) {
				ArrayList<Agent> copy = new ArrayList<Agent>(agents);
				for (Agent a : copy) agents.remove(a);
			}
			while (!newCycle) {
				int x = (int)(Math.random()*(X));
				int y = (int)(Math.random()*(Y));
				if (terrain[x][y]==1) { //Enregistre les coordonnees du volcan pour la suite
					terrain[x][y] = 3;
					newCycle = true;
					volcanoX = x;
					volcanoY = y;
				}
			}
			
			if (currentRange < volcanoRange) { //Tant que le rayon de propagation de la lave n'atteint pas volcanoRange, on continue dans cette condition
				//On copie le tableau fire pour eviter d'ajouter de la lave en meme temps que de modifier le tableau, cela remplirait le tableau entierement de lave
				int[][] copyFire = new int[X][Y]; 
				//Boucle copiant le contenu de fire dans copyFire
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ )
						copyFire[i][j] = fire[i][j];
				
				//Boucle 
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ ) {
						if (fire[i][j]!=-1 && terrain[i][j]!=3) { //Si ce n'est pas de la lave, on cherche a le remplacer par de la lave s'il y a de la lave autour. Recherche sous forme de + (Voisinage de Von Neumann)
							//Remplacement de la lave par de l'obsdienne
							if (i+1<X && (copyFire[i+1][j]==-1 || terrain[i+1][j]==3) ) { //S'il y a de la lave ou un volcan a la position indiquee, alors il y a de la lave a la position actuelle
								fire[i][j]=-1;
								terrain[i][j]=4;
							}
							if (i-1>=0 && (copyFire[i-1][j]==-1 || terrain[i-1][j]==3) ) {
								fire[i][j]=-1;
								terrain[i][j]=4;
							}
							if (j+1<Y && (copyFire[i][j+1]==-1 || terrain[i][j+1]==3) ) {
								fire[i][j]=-1;
								terrain[i][j]=4;
							}
							if (j-1>=0 && (copyFire[i][j-1]==-1 || terrain[i][j-1]==3) ) {
								fire[i][j]=-1;
								terrain[i][j]=4;
							}
							if (Math.random()<pLavaNoise) {
								int x = (int)(Math.random()*(X));
								int y = (int)(Math.random()*(Y));
								boolean found=false;
								if (terrain[x][y]!=3) {
									if (x+1<X && fire[x][y]==-1) {
										fire[x][y]=-1;
										found=true;
									}
									if (!found && x-1>=0 && fire[x-1][y]==-1) {
										fire[x][y]=-1;
										found=true;
									}
									if (!found && y+1<Y && fire[x][y+1]==-1) {
										fire[x][y]=-1;
										found=true;
									}
									if (!found && y-1>=0 && fire[x][y-1]==-1){
										fire[x][y]=-1;
										found=true;
									}
									if (found)terrain[x][y]=4;
								}
							}
						}
					}
				currentRange++;
				try {
					Thread.sleep(lavaDelai);
				} catch ( Exception e ) {};
			} else { //Retrait de la lave
				int cptLava=0;
				
				//Verifie qu'il y a assez de lave pour eviter une boucle infinie dans la boucle while suivant
				for ( int i = 0 ; i < fire[0].length && cptLava<lavaDissipate; i++ )
					for ( int j = 0 ; j < fire.length && cptLava<lavaDissipate; j++ )
						if (fire[i][j]==-1) cptLava++; //Compte le nombre de lava restant dans le monde
				
				boolean fireDone = false;
				int nbTerr=0; //Le nombre de modification faites a chaque iteration
				while (!fireDone && cptLava>0) {
					int x = (int)(Math.random()*(X));
					int y = (int)(Math.random()*(Y));
						if (fire[x][y]==-1) { //Si c'est de la lave, elle s'eteint pour laisser place a l'obsidienne
							fire[x][y]=0;
							nbTerr++;
							if (cptLava<lavaDissipate) fireDone=true; //Si cptLava ne peut plus depasser lavaDissipate, on sort de la boucle pour eviter une boucle infinie
						}
						if (nbTerr>=lavaDissipate) fireDone=true; //Le nombre de laves qui disparaissent a chaque iteration a ete atteint, on sort de la boucle
				}
				//Dernier etape du nouveau cycle
				if (!newCycleLastStep && cptLava==0) {
					terrain[volcanoX][volcanoY]=4; //Le volcan devient de l'obsidienne lorsqu'il n'y a plus de lave
					volcanoX=0;
					volcanoY=0;
					newCycleLastStep=true; //Actionne la derniere etape du nouveau cycle
				}
				
				if (newCycleLastStep && cptLava==0) {
					int cptObsidian=0;
					int cptDirt=0;
					int cptGrass=0;
					//Boucle permettant de compter le nombre de chaque type de sol restant sur terre
					for ( int i = 0 ; i < terrain[0].length; i++ )
						for ( int j = 0 ; j < terrain.length ; j++ ) {
							if (terrain[i][j]==4) cptObsidian++;
							if (cptObsidian == 0 && terrain[i][j]==5) cptDirt++;
							if (cptDirt == 0 && cptObsidian == 0  && terrain[i][j]==1) cptGrass++;
						}
					
					boolean fillTerr=false; //Remplissage du terrain
					nbTerr=0;
					while (!fillTerr && (cptObsidian>0 || cptDirt>0 || cptGrass>0) && currentSandFill<addSandFill) {
						int x = (int)(Math.random()*(X));
						int y = (int)(Math.random()*(Y));
							if (cptObsidian>0) { //Tant qu'il y a de l'obsidienne, on cherche a le remplacer par de la terre(dirt)
								if (terrain[x][y]==4) {
									terrain[x][y]=5;
									nbTerr++;
									if (cptObsidian<dirtRejuvenate) fillTerr = true;
								}
								if (nbTerr>=dirtRejuvenate) fillTerr = true;
							} else {
								//Si terrain[x][y] est de la terre(dirt), elle se transforme en herbe
								if (terrain[x][y]==5) {
									terrain[x][y]=1;
									nbTerr++;
									if (cptDirt<grassRejuvenate) fillTerr = true;
								}
								if (nbTerr>=grassRejuvenate) fillTerr = true;
								
								if (cptDirt==0) { //S'il n'y a plus de terre(dirt), on cherche a ajouter du sable aux bordures de la nouvelle zone terrestre
									x = (int)(Math.random()*(X));
									y = (int)(Math.random()*(Y));
									if (terrain[x][y]==1) {
										if (x+1<X && (terrain[x+1][y]==2 || terrain[x+1][y]==0) ) {
											terrain[x][y]=2;
										}
										if (x-1>=0 && (terrain[x-1][y]==2 || terrain[x-1][y]==0) ) {
											terrain[x][y]=2;
										}
										if (y+1<Y && (terrain[x][y+1]==2 || terrain[x][y+1]==0) ) {
											terrain[x][y]=2;
										}
										if (y-1>=0 && (terrain[x][y-1]==2 || terrain[x][y-1]==0) ) {
											terrain[x][y]=2;
										}
										currentSandFill++;
									}
								}
							}
						}
					
					//Condition permettant de tout reinitialiser et de debuter ce nouveau cycle
					if (!fillTerr && cptObsidian<=0 && cptDirt<=0 && currentSandFill>=addSandFill) {
						currentSandFill=0;
						currentRange=0;
						newCycle=false;
						newCycleLastStep=false;
						addInitiate();
					}
				}
				try {
					Thread.sleep(newCycleLSDelai);
				} catch ( Exception e ) {};
			}
		}
	}
	
	private void updateEnvironnement() {
		//Mise a jour des donnees de l'environnement
		for (int i = 0 ; i < Y ; i++ )
			for (int j = 0 ; j < X ; j++ ) {
				if (environnement[i][j] instanceof Flower) {
					if (terrain[i][j]!=1 || !environnement[i][j].getAlive() || fire[i][j]>=fireStop) environnement[i][j]=null; //Si le terrain ne correspond pas a de l'herbe, ou que la fleur est morte, ou que le feu se s'arrete, la fleur meurt
					else if (fire[i][j]>0) ((Flower)environnement[i][j]).setFire(true); //S'il y a du feu et une fleur, la fleur est en feu
					else environnement[i][j].update(); //Met a jour la fleur
				} else if (environnement[i][j] instanceof Tree) {
					if (terrain[i][j]!=1 || !environnement[i][j].getAlive() || ( fire[i][j]>=fireStop && ((Tree)environnement[i][j]).getFire())) environnement[i][j]=null;
					else if (fire[i][j]>=fireStop) ((Tree)environnement[i][j]).setBurned(); //L'arbre est en feu, il change de forme
					else environnement[i][j].update();
				} else if (environnement[i][j] instanceof Cactus) {
					if (terrain[i][j]!=2 || !environnement[i][j].getAlive() || fire[i][j]>=fireStop) environnement[i][j]=null;
					else environnement[i][j].update();
				} else if (environnement[i][j] instanceof Tsunami) {
					if (!environnement[i][j].getAlive()) environnement[i][j]=null;
					else environnement[i][j].update();
				}
				
				if (fire[i][j]>0) {
					if (fire[i][j]>=fireStop) { //Le feu s'eteint
						fire[i][j]=0;
					} else { //Sinon le feu continue
						fire[i][j]++;
					}
				}
				
				if (fire[i][j]<0) {
					terrain[i][j] = 4; //Le terrain devient de l'obsidienne
				}
				
				if (terrain[i][j]==1) nbGrass++;
				else if (terrain[i][j]==2) nbSand++;
				else if (terrain[i][j]==0) nbWater++;
			}
		//Environnement aleatoire
		if (Math.random()<pFlower) {
			if (Math.random()<pRose) addItem(new Rose());
			if (Math.random()<pTulipe) addItem(new Tulip());
			if (Math.random()<pMarguerite) addItem(new Daisy());
		}
		
		//Variables pour le changement de terrain aleatoire
		for (int a=0; a < nbChangementTerrain ; a++) {
			int p=(int)(Math.random()*X);
			int q=(int)(Math.random()*Y);
			boolean presenceSand = false;
			
			if (Math.random()<pSand) {
				if (terrain[p][q]==1) {
					for (int i = -1; i < 2 && !presenceSand;i++ ) //Rayon de 3x3 (Voisinage de Moore)
						for (int j = -1; j < 2 && !presenceSand; j++) {
							if (p+i>=0 && p+i<X && q+j>=0 && q+j<Y && terrain[p+i][q+j]==2 && !(p+i==p && q+j==q)) {
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
					//Recherche d'eau sous forme de + (Voisinage de Von Neumann)
					if (p+1<X ) {
						if (terrain[p+1][q]==0)
							presenceWater = true;
					}
					if (!false && p-1 >= 0) {
						if (terrain[p-1][q]==0)
							presenceWater = true;
					}
					if (!false && q+1 < Y) {
						if (terrain[p][q+1]==0)
							presenceWater = true;
					}
					if (!false && q-1 >= 0) {
						if (terrain[p][q-1]==0)
							presenceWater = true;
					}
					if (presenceWater) { //Uniquement s'il y a de l'eau a cote (les angles ne sont pas pris en compte)
						terrain[p][q] = 0;
					}
				}
			}
			
			//Variables aleatoires
			p=(int)(Math.random()*X);
			q=(int)(Math.random()*Y);
			
			if (Math.random()<pTree) { //Probabilite d'ajouter un arbre aux coordonnees (p,q)
				if (terrain[p][q]==1) addItem(new Tree());
			}
			
			if (Math.random()<pTree) {
				if (terrain[p][q]==2) addItem(new Cactus());
			}
			
			if (Math.random()<pFire) {
				if (terrain[p][q]!=0) fire[p][q]=1;
			}
			repaint();
		}
	}
	
	private void updateAgents() {
		//Liste permettant d'ajouter les nouveaux enfants
		ArrayList<Agent> nEnfant = new ArrayList<Agent>();
		//Met a jour les agents
		for (Agent a : agents) {
			boolean chasingMode = false;
			boolean escapingMode = false;
			if (a.getAlive()) { //Verifie si l'agent est en vie, et le met a jour
				if (! (a instanceof Human) ) { //L'humain fait sa vie et n'a pas de predateur ou de proie
					for (Agent b: agents) {
						if (!(b instanceof Human) &&!a.equals(b) && a.getX()==b.getX() && a.getY()==b.getY()) { //Verifie que l'agent a et b sont a la meme case
							if (a instanceof Chicken) {
								if (b instanceof Viper) { //La poule mange la vipere
									b.setAlive(false);
								} else if (b instanceof Fox) { //Le renard mange la poule
									a.setAlive(false);
								}
							} else if (a instanceof Fox) {
								if (b instanceof Chicken) { //Le renard mange la poule
									b.setAlive(false);
								} else if (b instanceof Viper) { //La vipere mange le renard
									a.setAlive(false);
								}
							} else if (a instanceof Viper) {
								if (b instanceof Fox) { //La vipere mange le renard
									b.setAlive(false);
								} else if (b instanceof Chicken) { //La poule mange la vipere
									a.setAlive(false);
								}
							}
							
						}
					}
				}
				if (a.getAlive()) {
					if (chasingMode) {
						a.chasingPrey(terrain, environnement, agents);
					} else if (escapingMode){
					
					} else {
						a.move(terrain, environnement);
					}
					a.update();
				}
			}
			
			//Quelques regles du monde pour les agents
			if (a instanceof Human && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Human)a).addDrowning(); //si le terrain est de l'eau, l'agent incremente la noyade
				else ((Human)a).setDrowning(0); //Reinitialise la noyade de l'agent
				if (environnement[a.getX()][a.getY()] instanceof Rose && !((Flower)environnement[a.getX()][a.getY()]).getFire()) { //Les humains ne mangent que les roses, et si celle-ci n'est pas en feu
					((Human)a).addHealth(addHumanHealth); //Soigne l'agent
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY()); //Retire la fleur
				}
				if (fire[a.getX()][a.getY()]!=0) { //L'agent est en feu s'il marche sur du feu ou de la lave
					a.setOnFire(true);
					((Human)a).setFire(0);
				}
			} else if (a instanceof Chicken && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Chicken)a).addDrowning();
				else ((Chicken)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Tulip && !((Flower)environnement[a.getX()][a.getY()]).getFire()) { //Les poules ne mangent que les tulipes
					((Chicken)a).addHealth(addChickenHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Chicken)a).setFire(0);
				}
			}  else if (a instanceof Fox && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Fox)a).addDrowning();
				else ((Fox)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Daisy && !((Flower)environnement[a.getX()][a.getY()]).getFire()) { //Les renards ne mangent que les marguerites
					((Fox)a).addHealth(addFoxHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Fox)a).setFire(0);
				}
			}  else if (a instanceof Viper && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==0) ((Viper)a).addDrowning();
				else ((Viper)a).setDrowning(0);
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Viper)a).setFire(0);
				}
			}
			
			//Boucle permettant la naissance des enfants
			for (Agent a2 : agents) {
				if (!(a.equals(a2)) && Math.random()<pEnfant) { //Verifie si les deux agents sont de meme espece
					if (a.getSexe()!=a2.getSexe() && a.getX()==a2.getX() && a.getY()==a2.getY()) { //Sexe different et a la meme position
						if (a.getStime()==0 && a2.getStime()==0 ) { //Naissance d'un enfant
							int cptNbAgents = 0;
							for (Agent a3 : agents) { //Verifie que le nombre d'agent a la meme case ne depasse pas nbAgentsMaxPos
								if (a.getX()==a3.getX() && a3.getY()==a3.getY()) cptNbAgents++;
								if (cptNbAgents>nbAgentsMaxPos) {
									break; //Permet de sortir de la boucle for et eviter de faire des boucles inutilement car la limite est depassee
								}
							}
							if (cptNbAgents <= nbAgentsMaxPos) { //S'il y a nbAgentsMaxPos agents a la meme position, il n'y a pas naissance d'enfant. Limite du nombre d'agent a la meme case
								if (a instanceof Human) nEnfant.add(new Human(a.getX(), a.getY())); //nouveau ne 
								else if (a instanceof Chicken) nEnfant.add(new Chicken(a.getX(), a.getY()));
								else if (a instanceof Fox) nEnfant.add(new Fox(a.getX(), a.getY()));
								else if (a instanceof Viper) nEnfant.add(new Viper(a.getX(), a.getY()));
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
	}
	
	// Mise a jour de chaque Item et Agents
	public void update() {
		nbGrass = 0;
		nbSand = 0;
		nbWater = 0;
		//Si newCycle est vrai, le nouveau cycle commence donc on n'a plus besoin de modifier le monde
		if (!newCycle) {
			updateEnvironnement();
			updateAgents();
		}
		newLifeCycle();
		repaint();
		try {
			Thread.sleep(delai2);
		} catch ( Exception e ) {};
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < terrain[0].length ; i++ )
			for ( int j = 0 ; j < terrain.length ; j++ ) {
				if (terrain[i][j]==0) {
					g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==1) {
					g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==2) {
					g2.drawImage(sandSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==3) {
					g2.drawImage(volcanoSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==4) {
					g2.drawImage(obsidianSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==5) {
					g2.drawImage(dirtSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
			}
		
		for ( int i = 0 ; i < environnement[0].length ; i++ )
			for ( int j = 0 ; j < environnement.length ; j++ ) {
				if (environnement[i][j] instanceof Item) {
					/* Pour centrer l'image en fonction de la taille, avec 1 la taille maximale d'un sprite,
					 * il faut faire : ( 1-SpriteSize ) * ( (spriteLength/2) + 1)
					 */
					try {
						g2.drawImage((environnement[i][j]).getImage(),(int)(spriteLength*i+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*j+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*environnement[i][j].getSpriteSize()),(int)(spriteLength*environnement[i][j].getSpriteSize()), frame);
					} catch ( Exception e ) {}
				}
			}
		
		//Le clone permet d'eviter les problemes rencontres lors d'affichage des agents et des modifications qui ont lieu en meme temps
		ArrayList<Agent> clone = new ArrayList<Agent>(agents);
		for (Agent a : clone) {
			if (a.getAlive() && a!=null) {
				try {
					a.draw(g2, frame);
				} catch (Exception e ) {
					System.out.println(a.getAlive());
					System.out.println(a.getX() + " " + a.getY() + " " + a.getPSpriteX() + " " + a.getPSpriteY() + " " + a.getSpriteX() + " " + a.getSpriteY());
				}
			}
		}
		
		for ( int i = 0 ; i < fire[0].length ; i++ )
			for ( int j = 0 ; j < fire.length ; j++ ) {
				if (fire[i][j]>0) {
					if (environnement[i][j] instanceof Tree) { //Affiche les arbres en feu
						g2.drawImage(fireSprite,(int)(spriteLength*i+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*j+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*environnement[i][j].getSpriteSize()),(int)(spriteLength*environnement[i][j].getSpriteSize()), frame);
					} else { //Sinon affiche le feu
						g2.drawImage(fireSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					}
				} else if (fire[i][j]<0) { //Affiche la lave
					g2.drawImage(lavaSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
				
				if (terrain[i][j]==3) { //Evite d'afficher la lave au dessus du volcan
					g2.drawImage(volcanoSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
			}
	}
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		int i=0;
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


