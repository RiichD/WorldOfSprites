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
	
	public static final int X = 64, Y = 64; //taille de world
	
	private JFrame frame;
	
	public static final int spriteLength = 20; //taille de chaque sprite
	
	private int ite = 0; //Nombre d'iteration
	
	//Sprites
	private Image waterSprite;
	private Image grassSprite;
	private Image sandSprite;
	private Image fireSprite;
	private Image lavaSprite;
	private Image volcanoSprite;
	private Image obsidianSprite;
	private Image dirtSprite;
	
	/* valeurs attribuees au terrain :
	 * 0 : water
	 * 1 : grass
	 * 2 : sand
	 * 3 : volcano
	 * 4 : obsidian
	 * 5 : dirt
	 */
	
	public static final int water = 0;
	public static final int grass = 1;
	public static final int sand = 2;
	public static final int dirt = 3;
	public static final int volcano = -1;
	public static final int obsidian = -2;
	
	private int[][] terrain; //Le terrain uniquement : herbe, sable, terre, eau, volcan, etc...
	private ArrayList<Agent> agents; //Les agents
	private Item[][] environnement; //Environnement contient les arbres, les fleurs etc...
	private Item[][] evenement; //Foudre, etc...
	private int[][] altitude; //Altitude du monde
	private int[][] fire; ///Si 0 rien, si >0 feu, sinon lave
	
	//Vitesse d'execution
	private int delai = 10; //Delai pour la vitesse de deplacement d'agent
	private int delai2 = 0; //Delai pour la vitesse d'execution (d'affichage)
	public static final int mainDelai = 0; //Delai du main ( iteration )
	private int lavaDelai = 200; //Delai permettant d'afficher la propagation de la lave progressivement
	private int newCycleLSDelai = 5; //Delai lors du passage de la lave a la nouvelle terre
	
	//Attributs du monde
	private int perlinSize = 10; //Taille du bruit de perlin
	private double perlinFloor = Math.random()*(1-0.5)+0.5; //Entre 0 et 1
	
	private boolean forceAdd = true; //Programme plus lent au demarrage si la valeur est true. Force l'ajout du nombre exact d'agents et d'items.
	
	private int nbHumanDepart = 75; //A chaque debut de cycle du monde, on ajoute un nombre d'agent au depart
	private int nbChickenDepart = 75;
	private int nbFoxDepart = 75;
	private int nbViperDepart = 75;
	private int nbZombieDepart = 1;
	
	private int nbFlowerDepart = 60; //Nombre de fleurs au depart
	private int nbTreeDepart = 700;
	private int nbCactusDepart = 25;
	private int nbAgentsMaxPos = 2; //Variable uniquement pour les naissances d'enfants : nombre d'agents maximum a une meme position. 2 au minimum pour avoir un enfant.
	
	private int fireStop = 15; //fireStop iterations pour que le feu s'eteigne
	
	//La sante que recupere chaque agent lorsqu'ils se soignent
	private int addHumanHealth = 50; 
	private int addChickenHealth = 45;
	private int addFoxHealth = 56;
	
	//Attributs pour la reapparition de terrain, suite au volcan
	private int volcanoSpawn = 0; //Si le nombre d'herbe est inferieur a volcanoSpawn, un volcan apparait sur l'un des herbes, sinon au centre
	private int volcanoX, volcanoY; //Coordonnees du volcan
	private int volcanoRange = (int)(X/1.3); //Distance de propagation de la lave sur le terrain
	
	private int lavaDissipate = 5; //Nombre de laves maximum qui disparaissent chaque iteration
	private int dirtRejuvenate = 5; //Nombre de terres maximum qui apparaissent chaque iteration
	private int grassRejuvenate = 20; //Nombre d'herbes maximum qui apparaissent chaque iteration
	
	private int addSandFill = 1500; //Probabilite tres faible de base. Prevoir une grande valeur
	
	private int nbChangementTerrain = 20; //Augmente la chance de changer le type du terrain. 1 par defaut
	
	//Probabilite d'apparition des agents
	private double pEnfant = 1; //probabilite de la naissance d'un enfant lorsque 2 sexes differents sont a la meme case
	
	private double pZombie = 0.01; //Probabilite d'apparition d'un zombie
	
	//Probabilite d'apparation des items
	private double pFlower = 0.25; //Probabilite qu'une fleur apparaisse
	private double pTulipe = 0.05; //Si une fleur apparait, il y a une probabilite que la tulipe apparaisse
	private double pDaisy = 0.05; //Si une fleur apparait, il y a une probabilite que la Marguerite apparaisse
	private double pRose = 0.05; //Si une fleur apparait, il y a une probabilite que la Rose apparaisse
	
	private double pSand = 0.5; //Probabilite qu'une herbe devienne du sable, pSand doit avoir une probabilite strictement superieure a pWater
	private double pWater = 0.3; //Probabilite que du sable devienne de l'eau
	
	private double pTree = 0.01;
	private double pCactus = 0.1;
	
	private double pThunder = 0.01; //Probabilite qu'un tonnerre apparaisse
	
	private double pLavaNoise = 0.15; //Bruit affectant la propagation de la lave.
	
	//Variables en rapport aux degats sur chaque agents
	public static final int fireDamage = 10;
	public static final int thunderDamage = 50;
	
	//A ne pas modifier
	private int currentRange = 0; // Variable permettant de creer un effet de propagation de la lave
	private int currentSandFill = 0; //Assure une apparition de sables de maniere progressive
	private boolean newCycle = false; //Nouveau cycle du monde, avec un terrain qui se recree avec l'aide d'un volcan
	private boolean newCycleLastStep = false; //Dernier etape du nouveau cycle avant que tout reprenne normalement
	private int nbWater=0; //Compte le nombre d'eau
	private int nbSand=0; //Compte le nombre de sable
	private int nbGrass=0; //Compte le nombre d'herbe
	private boolean perlinReady = false; //Verifie que la lave peut s'ecouler
	private int[][] perlinTable;
	
	public World(int x, int y){
		
		terrain = new int[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		evenement = new Item[x][y];
		altitude = new int[x][y];
		fire = new int[x][y];
		perlinTable = new int[x][y];
		int i, j;
		
		try {
			waterSprite = ImageIO.read(new File("pictures/water1.png"));
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
		
		//Terrain aleatoire
		for (i=0; i<X; i++) {
			for (j=0; j<Y; j++) {
				terrain[i][j]=((int)((Get2DPerlinNoiseValue(i, j, perlinSize)+1)*2*perlinFloor))%2;
				perlinTable[i][j] = 0;
			}
		}
		
		//Boucle permettant d'ajouter du sable a l'extremite des zones terrestres
		for (i=0; i<X; i++) {
			for (j=0; j<Y; j++) {
				if (terrain[i][j] == grass) {
					boolean foundWater = false;
					boolean foundGrass = false;
					for (int n = -1; n <= 1 ; n++) //Voisinage de Moore
						for (int m = -1; m <= 1 ; m++) {
							if (i+n>=0 && i+n<X && j+m>=0 && j+m<Y) {
								if (terrain[i+n][j+m] == water) foundWater = true;
								if (terrain[i+n][j+m] == grass) {
									foundGrass = true;
								}
							}
						}
					if (foundWater && foundGrass) {
						terrain[i][j] = sand;
					}
				}
			}
		}
		
		//Initialisation des agents et des items
		addInitiate();
		
		//Creation du frame et affichage de la fenetre
		frame = new JFrame("World Of Sprites");
		frame.add(this);
		frame.setSize((spriteLength+1)*(X),(spriteLength+1)*(Y));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void addInitiate() {
		//Compte le nombre de sables et d'herbes present sur le monde
		for (int i = 0 ; i < X ; i++)
			for (int j = 0 ; j < Y ; j++){
				if (terrain[i][j] == grass ) nbGrass++;
				if (terrain[i][j] == sand ) nbSand++;
			}
		
		//Agents de depart
		//Il est possible d'utiliser addAgent mais il n'y aura pas forcement le nombre d'agent souhaite
		for (int n=0;n<nbHumanDepart;n++) {
			if (forceAdd && nbHumanDepart<= nbGrass + nbSand)
				forceAddAgent(new Human());
			else 
				addAgent(new Human());
		}

		for (int n=0;n<nbChickenDepart;n++) {
			if (forceAdd && nbChickenDepart<= nbGrass + nbSand)
				forceAddAgent(new Chicken());
			else
				addAgent(new Chicken());
		}

		for (int n=0;n<nbFoxDepart;n++) {
			if (forceAdd && nbFoxDepart<= nbGrass + nbSand)
				forceAddAgent(new Fox());
			else
				addAgent(new Fox());
		}
		
		for (int n=0;n<nbViperDepart;n++) {
			if (forceAdd && nbViperDepart<= nbGrass + nbSand)
				forceAddAgent(new Viper());
			else
				addAgent(new Viper());
		}
		
		for (int n=0;n<nbZombieDepart;n++) {
			if (forceAdd && nbZombieDepart<= nbGrass + nbSand)
				forceAddAgent(new Zombie());
			else
				addAgent(new Zombie());
		}
		
		//Environnement de depart
		for (int n=0;n<nbFlowerDepart;n++) {
			if (forceAdd && nbFlowerDepart<= nbGrass) {
				forceAddItem(new Rose());
				forceAddItem(new Tulip());
				forceAddItem(new Daisy());
			} else {
				addItem(new Rose());
				addItem(new Tulip());
			}
		}
		
		for (int n=0;n<nbTreeDepart;n++) {
			if (forceAdd && nbTreeDepart<= nbGrass) {
				forceAddItem(new Tree());
			} else 
				addItem(new Tree());
		}
		
		for (int n=0;n<nbCactusDepart;n++) {
			if (forceAdd && nbCactusDepart<= nbSand)
				forceAddItem(new Cactus());
			else 
				addItem(new Cactus());
		}
		//Reinitialise les variables
		nbGrass = 0;
		nbSand = 0;
	}
	
	//add
	public void addItem(Item i, int x, int y) {
		if (environnement[x][y]== null && !(i instanceof Thunder)) { //Thunder doit se trouver dans evenement et non dans environnement
			environnement[x][y] = null; //Programme plus rapide si on met null avant d'ajouter l'Item i
			if ( ! (terrain[x][y]==water )) {
				if (terrain[x][y]==sand) {
					if (i instanceof Cactus) {
						environnement[x][y] = i;
					}
				} else if (terrain[x][y]==grass) {
					if (i instanceof Tree || i instanceof Flower)
						environnement[x][y] = i;
				}
			} else {
			}
		}
	}
	
	public void addItem(Item i) {
		addItem(i, (int)(Math.random()*X), (int)(Math.random()*Y));
	}
	
	public void forceAddItem(Item i) {
		boolean ajout = false;
		while (!ajout) {
			int x = (int)(Math.random()*X);
			int y = (int)(Math.random()*Y);
			if (environnement[x][y] == null ) {
				if (terrain[x][y] != water) {
					if (terrain[x][y] == sand) {
						if (i instanceof Cactus) {
							if (nbSand < nbCactusDepart) ajout = true; //S'il y a plus de cactus que de sable, on n'ajoute rien sinon il y a une boucle infinie
							ajout = true;
							environnement[x][y] = i;
						}
					} else if (terrain[x][y]==grass) {
						if (i instanceof Tree || i instanceof Flower) {
							ajout = true;
							environnement[x][y] = i;
						}
					}
				}
			}
		}
	}
	
	public void addAgent(Agent a) {
		if (!(terrain[a.getX()][a.getY()]==water))
			if (a instanceof Human || a instanceof Chicken || a instanceof Fox || a instanceof Viper || a instanceof Zombie)
				agents.add(a);
		else
			System.out.println("Ajout d'agent impossible");
	}
	
	public void forceAddAgent(Agent a) {
		if (a instanceof Human || a instanceof Chicken || a instanceof Fox || a instanceof Viper || a instanceof Zombie) {
			int tailleInit = agents.size()+1;
			while(agents.size() != tailleInit) {
				if (a instanceof Human) {
					Agent a2 = new Human();
					if (terrain[a2.getX()][a2.getY()]!=water) agents.add(a2);
				} else if (a instanceof Chicken) {
					Agent a2 = new Chicken();
					if (terrain[a2.getX()][a2.getY()]!=water) agents.add(a2);
				} else if (a instanceof Fox) {
					Agent a2 = new Fox();
					if (terrain[a2.getX()][a2.getY()]!=water) agents.add(a2);
				} else if (a instanceof Viper) {
					Agent a2 = new Viper();
					if (terrain[a2.getX()][a2.getY()]!=water) agents.add(a2);
				} else if (a instanceof Zombie) {
					Agent a2 = new Zombie();
					if (terrain[a2.getX()][a2.getY()]!=water) agents.add(a2);
				}
			}
		} else System.out.println("Ajout forcee d'agent impossible");
	}
	
	//Perlin noise. Source : http://sdz.tdct.org/sdz/bruit-de-perlin.html
	private double Get2DPerlinNoiseValue(float x, float y, float res)
	{
	    double tempX,tempY;
	    int x0,y0,ii,jj,gi0,gi1,gi2,gi3;
	    double unit = 1.0f/Math.sqrt(2);
	    double tmp,s,t,u,v,Cx,Cy,Li1,Li2;
	    double gradient2[][] = {{unit,unit},{-unit,unit},{unit,-unit},{-unit,-unit},{1,0},{-1,0},{0,1},{0,-1}};

	    int perm[] =
	       {151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,
	        142,8,99,37,240,21,10,23,190,6,148,247,120,234,75,0,26,197,62,94,252,219,
	        203,117,35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168,68,175,
	        74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,
	        105,92,41,55,46,245,40,244,102,143,54,65,25,63,161,1,216,80,73,209,76,132,
	        187,208,89,18,169,200,196,135,130,116,188,159,86,164,100,109,198,173,186,3,
	        64,52,217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,
	        47,16,58,17,182,189,28,42,223,183,170,213,119,248,152,2,44,154,163,70,221,
	        153,101,155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,
	        112,104,218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,
	        235,249,14,239,107,49,192,214,31,181,199,106,157,184,84,204,176,115,121,50,45,
	        127,4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,
	        156,180};
	    
	    //Adapter pour la r�solution
	    x /= res;
	    y /= res;

	    //On r�cup�re les positions de la grille associ�e � (x,y)
	    x0 = (int)(x);
	    y0 = (int)(y);

	    //Masquage
	    ii = x0 & 255;
	    jj = y0 & 255;

	    //Pour r�cup�rer les vecteurs
	    gi0 = perm[ii + perm[jj]] % 8;
	    gi1 = perm[ii + 1 + perm[jj]] % 8;
	    gi2 = perm[ii + perm[jj + 1]] % 8;
	    gi3 = perm[ii + 1 + perm[jj + 1]] % 8;

	    //on r�cup�re les vecteurs et on pond�re
	    tempX = x-x0;
	    tempY = y-y0;
	    s = gradient2[gi0][0]*tempX + gradient2[gi0][1]*tempY;

	    tempX = x-(x0+1);
	    tempY = y-y0;
	    t = gradient2[gi1][0]*tempX + gradient2[gi1][1]*tempY;

	    tempX = x-x0;
	    tempY = y-(y0+1);
	    u = gradient2[gi2][0]*tempX + gradient2[gi2][1]*tempY;

	    tempX = x-(x0+1);
	    tempY = y-(y0+1);
	    v = gradient2[gi3][0]*tempX + gradient2[gi3][1]*tempY;


	    //Lissage
	    tmp = x-x0;
	    Cx = 3 * tmp * tmp - 2 * tmp * tmp * tmp;

	    Li1 = s + Cx*(t-s);
	    Li2 = u + Cx*(v-u);

	    tmp = y - y0;
	    Cy = 3 * tmp * tmp - 2 * tmp * tmp * tmp;

	    return Li1 + Cy*(Li2-Li1);
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
	
	private void newPerlinTable() { //Creer un tableau de Perlin, entre 0 et 1
		double perlinRandom = Math.random()*(1-0.5)+0.5;
		for (int i = 0 ; i < X ; i++) {
			for (int j = 0 ; j < Y ;j++) {
				perlinTable[i][j] = ((int)((Get2DPerlinNoiseValue(i, j, perlinSize)+1)*2*perlinRandom))%2;
			}
		}
	}
	
	private void removePerlinTable() { //Met le tableau de Perlin avec les valeurs par defaut, soit 0
		for (int i = 0 ; i < X ; i++)
			for (int j = 0 ; j < Y ; j++)
				perlinTable[i][j] = 0;
	}
	
	//Nouveau cycle de vie
	private void newLifeCycle() {
		//S'il n'y a plus d'eau, un flac d'eau apparait
		if (nbWater==0 && !newCycle) {
			int x=(int)(Math.random()*X);
			int y=(int)(Math.random()*Y);
			terrain[x][y] = water;
			double rand = Math.random();
			if (rand<0.75) {
				if (rand<0.5) {
					if (rand<0.25) {
						if (x+1 < X && terrain[x+1][y]==grass)
							terrain[x+1][y] = sand;
					} else {
						if (x-1 >=0 && terrain[x-1][y]==grass)
							terrain[x-1][y] = sand;
					}
				} else {
					if (y+1 < Y && terrain[x][y+1]==grass)
						terrain[x][y+1] = sand;
				}
			} else {
				if (y-1 >=0 && terrain[x][y-1]==grass)
					terrain[x][y-1] = sand;
			}
		} else if ( nbSand==0 && nbGrass<=volcanoSpawn && !newCycle) { //S'il n'y a plus de sable et que le nombre d'herbe est inferieur a volcanoSpawn, un volcan apparait au centre du terrain
			newCycle = true;
			volcanoX = X/2;
			volcanoY = Y/2;
			terrain[volcanoX][volcanoY] = volcano;
		} else if (nbSand == 0 || newCycle) {//Le nouveau cycle commence si le nombre de sable vaut 0
			if (agents.size()>0) {
				ArrayList<Agent> copy = new ArrayList<Agent>(agents);
				for (Agent a : copy) agents.remove(a);
			}

			if (!newCycle) {
				for (int i = 0 ; i < X ; i++)
					for (int j = 0 ; j < Y ; j++) {
						environnement[i][j] = null; //Retire tous les items du monde car c'est un nouveau monde qui va commencer
					}
			}
			while (!newCycle) {
				int x = (int)(Math.random()*(X));
				int y = (int)(Math.random()*(Y));
				if (terrain[x][y]==grass) { //Enregistre les coordonnees du volcan pour la suite du nouveau cycle
					terrain[x][y] = volcano;
					newCycle = true;
					volcanoX = x;
					volcanoY = y;
				}
			}
			
			while (!perlinReady) { //On verifie que la lave peut bien s'ecouler, sinon on genere une autre tableau de Perlin
				newPerlinTable();
				if (volcanoX+1<X && perlinTable[volcanoX+1][volcanoY]!=0) perlinReady = true;
				else if (volcanoX-1>=0 && perlinTable[volcanoX-1][volcanoY]!=0) perlinReady = true;
				else if (volcanoY+1<Y && perlinTable[volcanoX][volcanoY+1]!=0) perlinReady = true;
				else if (volcanoY-1>=0 && perlinTable[volcanoX][volcanoY-1]!=0) perlinReady = true;
			}
			
			if (currentRange < volcanoRange) { //Tant que le rayon de propagation de la lave n'atteint pas volcanoRange, on continue dans cette condition
				//On copie le tableau fire pour eviter d'ajouter de la lave en meme temps que de modifier le tableau, cela remplirait le tableau entierement de lave
				int[][] copyFire = new int[X][Y]; 
				//Boucle copiant le contenu de fire dans copyFire pour permettre de faire un ecoulement de lave
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ )
						copyFire[i][j] = fire[i][j];
				
				//Boucle permettant la propagation de la lave
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ ) {
						if (fire[i][j]!=-1 && terrain[i][j]!=volcano) { //Si ce n'est pas de la lave, on cherche a le remplacer par de la lave s'il y a de la lave autour. Recherche sous forme de + (Voisinage de Von Neumann)
							//Remplacement de la lave par de l'obsdienne
							if (i+1<X && perlinTable[i][j]==1 && (copyFire[i+1][j]==-1 || terrain[i+1][j]==volcano) ) { //S'il y a de la lave ou un volcan a la position indiquee, alors il y a de la lave a la position actuelle
								fire[i][j] = -1;
								terrain[i][j] = obsidian; //La lave et de l'eau en contact forment de l'obsidienne
							}
							if (i-1>=0 && perlinTable[i][j]==1 && (copyFire[i-1][j]==-1 || terrain[i-1][j]==volcano) ) {
								fire[i][j] = -1;
								terrain[i][j] = obsidian;
							}
							if (j+1<Y && perlinTable[i][j]==1 && (copyFire[i][j+1]==-1 || terrain[i][j+1]==volcano) ) {
								fire[i][j] = -1;
								terrain[i][j] = obsidian;
							}
							if (j-1>=0 && perlinTable[i][j]==1 && (copyFire[i][j-1]==-1 || terrain[i][j-1]==volcano) ) {
								fire[i][j] = -1;
								terrain[i][j] = obsidian;
							}
							if (Math.random()<pLavaNoise) { //Ajoute du bruit pour eviter un ecoulement trop "lineaire"
								int x = (int)(Math.random()*(X));
								int y = (int)(Math.random()*(Y));
								boolean found = false; //On autorise seulement une modification pour chaque cote, et non des quatres cotes possibles
								if (terrain[x][y]!=volcano) {
									if (x+1<X && fire[x+1][y]==-1) {
										fire[x][y] = -1;
										found = true;
									}
									if (!found && x-1>=0 && fire[x-1][y]==-1) {
										fire[x][y] = -1;
										found = true;
									}
									if (!found && y+1<Y && fire[x][y+1]==-1) {
										fire[x][y] = -1;
										found = true;
									}
									if (!found && y-1>=0 && fire[x][y-1]==-1){
										fire[x][y] = -1;
										found = true;
									}
									if (found)terrain[x][y] = obsidian;
								}
							}
						}
					}
				currentRange++;
				try {
					Thread.sleep(lavaDelai);
				} catch ( Exception e ) {};
			} else { //Retrait de la lave car l'ecoulement de lave est terminee
				int cptLava = 0;
				
				//Verifie qu'il y a assez de lave pour eviter une boucle infinie dans la boucle while suivant
				for ( int i = 0 ; i < fire[0].length && cptLava<lavaDissipate; i++ )
					for ( int j = 0 ; j < fire.length && cptLava<lavaDissipate; j++ )
						if (fire[i][j]==-1) cptLava++; //Compte le nombre de lava restant dans le monde
				
				boolean fireDone = false; //des qu'un certains nombres de lave a ete retiree, fireDone devient vrai. (lavaDissipate est le nombre en question)
				int nbTerr=0; //Le nombre de modification faites a chaque iteration
				while (!fireDone && cptLava>0) {
					int x = (int)(Math.random()*(X));
					int y = (int)(Math.random()*(Y));
						if (fire[x][y]==-1) { //Si c'est de la lave, elle disparait et on pourra observer l'obsidienne formee
							fire[x][y] = 0;
							nbTerr++;
							if (cptLava<lavaDissipate) fireDone = true; //Si cptLava ne peut plus depasser lavaDissipate, on sort de la boucle pour eviter une boucle infinie
						}
						if (nbTerr>=lavaDissipate) fireDone = true; //Le nombre de laves qui disparaissent a chaque iteration a ete atteint, on sort de la boucle
				}
				
				//Derniere etape du nouveau cycle
				if (!newCycleLastStep && cptLava==0) {
					terrain[volcanoX][volcanoY] = obsidian; //Le volcan devient de l'obsidienne lorsqu'il n'y a plus de lave
					volcanoX = 0;
					volcanoY = 0;
					newCycleLastStep = true; //Actionne la derniere etape du nouveau cycle
				}
				
				if (newCycleLastStep && cptLava==0) {
					int cptObsidian = 0;
					int cptDirt = 0;
					int cptGrass = 0;
					//Boucle permettant de compter le nombre de chaque type de sol restant sur terre
					for ( int i = 0 ; i < terrain[0].length; i++ )
						for ( int j = 0 ; j < terrain.length ; j++ ) {
							if (terrain[i][j]==obsidian) cptObsidian++;
							if (cptObsidian == 0 && terrain[i][j]==dirt) cptDirt++;
							if (cptDirt == 0 && cptObsidian == 0  && terrain[i][j]==grass) cptGrass++;
						}
					
					boolean fillTerr = false; //Remplissage du terrain
					nbTerr = 0;
					while (!fillTerr && (cptObsidian>0 || cptDirt>0 || cptGrass>0) && currentSandFill<addSandFill) {
						int x = (int)(Math.random()*(X));
						int y = (int)(Math.random()*(Y));
							if (cptObsidian>0) { //Tant qu'il y a de l'obsidienne, on cherche a le remplacer par de la terre(dirt)
								if (terrain[x][y]==obsidian) {
									terrain[x][y] = dirt;
									nbTerr++;
									if (cptObsidian<dirtRejuvenate) fillTerr = true;
								}
								if (nbTerr>=dirtRejuvenate) fillTerr = true;
							} else {
								//Si terrain[x][y] est de la terre(dirt), elle se transforme en herbe
								if (terrain[x][y]==dirt) {
									terrain[x][y] = grass;
									nbTerr++;
									if (cptDirt<grassRejuvenate) fillTerr = true;
								}
								if (nbTerr>=grassRejuvenate) fillTerr = true;
								
								if (cptDirt==0) { //S'il n'y a plus de terre(dirt), on cherche a ajouter du sable aux bordures des zones terrestres
									x = (int)(Math.random()*(X));
									y = (int)(Math.random()*(Y));
									if (terrain[x][y]==grass) { //Le sable se forme lorsqu'il y a de l'eau et de l'herbe autour d'une case. (Voisinage de Von Neumann)
										if (x+1<X && (terrain[x+1][y]==sand || terrain[x+1][y]==water) ) {
											terrain[x][y] = sand;
										}
										if (x-1>=0 && (terrain[x-1][y]==sand || terrain[x-1][y]==water) ) {
											terrain[x][y] = sand;
										}
										if (y+1<Y && (terrain[x][y+1]==sand || terrain[x][y+1]==water) ) {
											terrain[x][y] = sand;
										}
										if (y-1>=0 && (terrain[x][y-1]==sand || terrain[x][y-1]==water) ) {
											terrain[x][y] = sand;
										}
										currentSandFill++;
									}
								}
							}
						}
					
					//Lorsque tout est termine, on reinitialise tout et le nouveau cycle peut commencer
					if (!fillTerr && cptObsidian<=0 && cptDirt<=0 && currentSandFill>=addSandFill) {
						currentSandFill = 0;
						currentRange = 0;
						newCycle = false;
						newCycleLastStep = false;
						addInitiate();
						perlinReady = false;
						removePerlinTable();
					}
				}
				try {
					Thread.sleep(newCycleLSDelai);
				} catch ( Exception e ) {};
			}
		}
	}
	
	private void updateEnvironnement() {
		int[][] cpFire = new int[X][Y];
		for (int i = 0 ; i < X ; i++)
			for (int j = 0 ; j < Y ; j++)
				cpFire[i][j] = fire[i][j]; 
		
		//Mise a jour des donnees de l'environnement, des evenements et du feu
		for (int i = 0 ; i < X ; i++ )
			for (int j = 0 ; j < Y ; j++ ) {
				if (environnement[i][j] instanceof Flower) {
					if (terrain[i][j]!=grass || !environnement[i][j].getAlive() || fire[i][j]>=fireStop) environnement[i][j] = null; //Si le terrain ne correspond pas a de l'herbe, ou que la fleur est morte, ou que le feu se s'arrete, la fleur meurt
					else if (fire[i][j]>0) ((Flower)environnement[i][j]).setFire(true); //S'il y a du feu et une fleur, la fleur est en feu
					else environnement[i][j].update(); //Met a jour la fleur
				} else if (environnement[i][j] instanceof Tree) {
					//L'arbre disparait si le sol(terrain) n'est plus de l'herbe, ou qu'elle est morte, ou que l'arbre est deja brulee et en feu
					if (terrain[i][j]!=grass || !environnement[i][j].getAlive() || ( fire[i][j]>=fireStop && ((Tree)environnement[i][j]).getFire())) environnement[i][j] = null;
					else if (!((Tree)environnement[i][j]).getFire() && fire[i][j]>=fireStop) ((Tree)environnement[i][j]).setBurned(); //L'arbre est en feu, il change de forme
					else if (fire[i][j]== 0 && !((Tree)environnement[i][j]).getFire()) { //Feu de foret (Voisinage de Von Neumann)
						if (i+1<X && cpFire[i+1][j]!=0 && environnement[i+1][j] instanceof Tree) fire[i][j] = 1;
						else if (i-1>=0 && cpFire[i-1][j]!=0 && environnement[i-1][j] instanceof Tree) fire[i][j] = 1;
						else if (j+1<Y && cpFire[i][j+1]!=0 && environnement[i][j+1] instanceof Tree) fire[i][j] = 1;
						else if (j-1>=0 && cpFire[i][j-1]!=0 && environnement[i][j-1] instanceof Tree) fire[i][j] = 1;
					}
					else environnement[i][j].update();
				} else if (environnement[i][j] instanceof Cactus) {
					//Si le terrain n'est plus du sable, ou que le cactus est morte, ou que le feu s'est eteint, le cactus disparait
					if (terrain[i][j]!=sand || !environnement[i][j].getAlive() || fire[i][j]>=fireStop) environnement[i][j] = null;
					else environnement[i][j].update();
				}
				
				if (evenement[i][j] instanceof Thunder) {
					if (!evenement[i][j].getAlive()) evenement[i][j] = null;
					else evenement[i][j].update();
				}
				
				if (fire[i][j]>0) {
					if (fire[i][j]>=fireStop) { //Le feu s'eteint
						fire[i][j] = 0;
					} else { //Sinon le feu continue
						fire[i][j]++;
					}
				}
				
				if (fire[i][j]<0) {
					terrain[i][j] = obsidian; //Le terrain devient de l'obsidienne
				}
				
				if (terrain[i][j]==grass) nbGrass++;
				else if (terrain[i][j]==sand) nbSand++;
				else if (terrain[i][j]==water) nbWater++;
			}
		//Environnement aleatoire
		if (Math.random()<pFlower) {
			if (Math.random()<pRose) addItem(new Rose());
			if (Math.random()<pTulipe) addItem(new Tulip());
			if (Math.random()<pDaisy) addItem(new Daisy());
		}
		
		//Variables pour le changement de terrain aleatoire
		for (int a=0; a < nbChangementTerrain ; a++) {
			int p=(int)(Math.random()*X);
			int q=(int)(Math.random()*Y);
			boolean presenceSand = false;
			
			if (Math.random()<pSand) {
				if (terrain[p][q]==grass) {
					for (int i = -1; i < 2 && !presenceSand;i++ ) //Rayon de 3x3 (Voisinage de Moore)
						for (int j = -1; j < 2 && !presenceSand; j++) {
							if (p+i>=0 && p+i<X && q+j>=0 && q+j<Y && terrain[p+i][q+j]==sand && !(p+i==p && q+j==q)) {
								presenceSand = true;
							}
						}
					if (presenceSand) { //On ne remplace que grass
						terrain[p][q] = sand;
					}
				}
			}
			
			if (Math.random()<pWater) {
				if (terrain[p][q]==sand) { //Remplace uniquement le sable
					boolean presenceWater = false;
					//Recherche d'eau sous forme de + (Voisinage de Von Neumann)
					if (p+1<X) {
						if (terrain[p+1][q]==water)
							presenceWater = true;
					}
					if (!false && p-1 >= 0) {
						if (terrain[p-1][q]==water)
							presenceWater = true;
					}
					if (!false && q+1 < Y) {
						if (terrain[p][q+1]==water)
							presenceWater = true;
					}
					if (!false && q-1 >= 0) {
						if (terrain[p][q-1]==water)
							presenceWater = true;
					}
					if (presenceWater) { //Uniquement s'il y a de l'eau a cote (les angles ne sont pas pris en compte)
						terrain[p][q] = water;
					}
				}
			}
			
			//Variables aleatoires
			p=(int)(Math.random()*X);
			q=(int)(Math.random()*Y);
			
			if (Math.random()<pTree) { //Probabilite d'ajouter un arbre aux coordonnees (p,q)
				if (terrain[p][q]==grass) addItem(new Tree());
			}
			
			if (Math.random()<pCactus) {
				if (terrain[p][q]==sand) addItem(new Cactus());
			}
			
			if (Math.random()<pThunder) {
				evenement[p][q] = new Thunder();
				if (terrain[p][q]!=water) fire[p][q] = 1;
			}
			repaint();
		}
	}
	
	private void updateAgents() {
		//Liste permettant d'ajouter les nouveaux enfants
		ArrayList<Agent> nEnfant = new ArrayList<Agent>();
		//Met a jour les agents
		for (Agent a : agents) {
			if (a.getAlive()) { //Verifie si l'agent est en vie, et le met a jour
				a.move(terrain, environnement, agents);
				if (a.getAlive()) { //Verifie si l'agent est toujours en vie
					a.update();
				}
				if (evenement[a.getX()][a.getY()] instanceof Thunder) a.addHealth(-thunderDamage);
			}
			
			//Quelques regles du monde pour les agents
			if (a instanceof Human && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==water) ((Human)a).addDrowning(); //si le terrain est de l'eau, l'agent incremente la noyade
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
				if (terrain[a.getX()][a.getY()]==water) ((Chicken)a).addDrowning();
				else ((Chicken)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Tulip && !((Flower)environnement[a.getX()][a.getY()]).getFire()) { //Les poules ne mangent que les tulipes
					((Chicken)a).addHealth(addChickenHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Chicken)a).setFire(0);
				}
			} else if (a instanceof Fox && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==water) ((Fox)a).addDrowning();
				else ((Fox)a).setDrowning(0);
				if (environnement[a.getX()][a.getY()] instanceof Daisy && !((Flower)environnement[a.getX()][a.getY()]).getFire()) { //Les renards ne mangent que les marguerites
					((Fox)a).addHealth(addFoxHealth);
					removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
				}
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Fox)a).setFire(0);
				}
			} else if (a instanceof Viper && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==water) ((Viper)a).addDrowning();
				else ((Viper)a).setDrowning(0);
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Viper)a).setFire(0);
				}
			} else if (a instanceof Zombie && a.getAlive()) {
				if (terrain[a.getX()][a.getY()]==water) ((Zombie)a).addDrowning();
				else ((Zombie)a).setDrowning(0);
				if (fire[a.getX()][a.getY()]!=0) {
					a.setOnFire(true);
					((Zombie)a).setFire(0);
				}
			}
			
			//Boucle permettant la naissance des enfants
			for (Agent a2 : agents) {
				if ( (a.getClass()).equals(a2.getClass()) && Math.random()<pEnfant) { //Verifie si les deux agents sont de meme espece
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
		
		if (Math.random()<pZombie) {
			forceAddAgent(new Zombie());
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
			else if (agents.get(i).getAlive() == true && ( environnement[agents.get(i).getX()][agents.get(i).getY()] instanceof Tree || environnement[agents.get(i).getX()][agents.get(i).getY()] instanceof Cactus ) ) removeAgent(agents.get(i));
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
		
		ite++;
		//Creation d'animations
		if (ite%4==0) {
			try {
				waterSprite = ImageIO.read(new File("pictures/water1.png"));
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else if (ite%4==2) {
			try {
				waterSprite = ImageIO.read(new File("pictures/water2.png"));
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		for ( int i = 0 ; i < terrain[0].length ; i++ )
			for ( int j = 0 ; j < terrain.length ; j++ ) {
				if (terrain[i][j]==water) {
					g2.drawImage(waterSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==grass) {
					g2.drawImage(grassSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==sand) {
					g2.drawImage(sandSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==volcano) {
					g2.drawImage(volcanoSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==obsidian) {
					g2.drawImage(obsidianSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				} else if (terrain[i][j]==dirt) {
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
				
				if (evenement[i][j] instanceof Item) {
					try {
						g2.drawImage((evenement[i][j]).getImage(),(int)(spriteLength*i+(1-evenement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*j+(1-evenement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*evenement[i][j].getSpriteSize()),(int)(spriteLength*evenement[i][j].getSpriteSize()), frame);
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
				
				if (terrain[i][j]==volcano) { //Evite d'afficher la lave au dessus du volcan
					g2.drawImage(volcanoSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
				}
			}
	}
	
	//Main
	public static void main(String[] args) {
		World world = new World(X,Y);
		while (true) {
			world.update();
			try {
				Thread.sleep(mainDelai);
			} catch ( Exception e ) {};
		}
	}
}


