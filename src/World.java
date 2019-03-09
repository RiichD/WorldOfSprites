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
	
	public static final int spriteLength = 28;
	
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
	private Item[][] environnement; //environnement contient les arbres, le feu etc..
	private int[][] altitude; //altitude du monde
	private int[][] fire; ///si 0 rien, si >0 feu, sinon lave
	
	//Vitesse d'execution
	private int delai=0; //delai pour la vitesse de deplacement d'agent
	private int delai2=0; //delai pour la vitesse d'execution (d'affichage)
	public static final int delai3=0; //delai du main ( iteration )
	private int lavaDelai=0;
	
	//Attributs du monde
	private int nbHumanDepart = 25;
	private int nbChickenDepart = 10;
	private int nbFoxDepart = 10;
	private int nbSnakeDepart = 10;
	
	private int nbEnvDepart = 20; //Arbres, Fleurs ...
	private int nbCactusDepart = 25;
	private int nbAgentsMaxPos = 2; //Variable uniquement pour les naissances d'enfants : nombre d'agents maximum a une meme position. 2 au minimum pour avoir un enfant.
	
	private int addHumanHealth = 50; //La sante que recupere chaque agent lorsqu'ils se soignent
	private int addChickenHealth = 45;
	private int addFoxHealth = 56;
	private int addSnakeHealth = 105;
	
	private int nbChangementTerrain = 20; //Augmente la chance d'avoir des modifications du terrain. 1 par defaut
	private int volcanoSpawn = 10;
	private int volcanoX, volcanoY;
	private int volcanoRange = (int)(X/1.2);
	private int CurrentRange = 0;
	private int lavaDissipate = 5;
	private int dirtRejuvenate = 5;
	private int addSandFill = 2500; //probabilite tres faible
	private int currentSandFill = 0;
	private boolean newCycle = false; //Nouveau cycle du monde, avec un terrain qui se recree avec l'aide d'un volcan
	private boolean newCycleLastStep = false;
	
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
	
	private double pFire = 0.01; //Probabilite qu'un item soit en feu
	private int fireStop = 15; //fireStop iterations pour que le feu s'eteigne
	public static final int fireDamage = 10;
	
	public World(int x, int y){
		
		terrain = new int[x][y];
		agents = new ArrayList<Agent>();
		environnement = new Item[x][y];
		altitude = new int[x][y];
		fire = new int[x][y];
		int i, j;
		
		try {
			waterSprite = ImageIO.read(new File("water.png"));
			grassSprite = ImageIO.read(new File("grass.png"));
			sandSprite = ImageIO.read(new File("sand.png"));
			fireSprite = ImageIO.read(new File("fire.png"));
			lavaSprite = ImageIO.read(new File("lava.png"));
			volcanoSprite = ImageIO.read(new File("volcano.png"));
			obsidianSprite = ImageIO.read(new File("obsidian.png"));
			dirtSprite = ImageIO.read(new File("dirt.png"));
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
		frame.setSize(spriteLength*X+X,spriteLength*Y+Y);
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
		
		for (int n=0;n<nbSnakeDepart;n++) {
			addAgent(new Snake());
		}
		
		//Environnement de depart
		for (int n=0;n<nbEnvDepart;n++) {
			addItem(new Tree());
			addItem(new Rose());
			addItem(new Tulipe());
			addItem(new Marguerite());
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
			if (a instanceof Human || a instanceof Chicken || a instanceof Fox || a instanceof Snake)
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
		for ( int i = 0 ; i < Y ; i++ ) {
			for ( int j = 0 ; j < X ; j++ ) {
				if (altitude[i][j]>=0)
					System.out.print("  "+ altitude[i][j]);
				else
					System.out.print(" " + altitude[i][j]);
			}
			System.out.println();
		}
	}
	
	// Mise a jour de chaque Item et Agents
	public void update() {
		int nbGrass = 0;
		int nbSand = 0;
		
		if (!newCycle) {
			//Mise a jour des donnees de l'environnement
			for (int i = 0 ; i < Y ; i++ )
				for (int j = 0 ; j < X ; j++ ) {
					if (environnement[i][j] instanceof Flower) {
						if (terrain[i][j]!=1 || !environnement[i][j].getAlive()) environnement[i][j]=null;
						else environnement[i][j].update();
					} else if (environnement[i][j] instanceof Tree) {
						if (terrain[i][j]!=1 || !environnement[i][j].getAlive()) environnement[i][j]=null;
						else environnement[i][j].update();
					} else if (environnement[i][j] instanceof Cactus) {
						if (terrain[i][j]!=2 || !environnement[i][j].getAlive()) environnement[i][j]=null;
						else environnement[i][j].update();
					} else if (environnement[i][j] instanceof Tsunami) {
						if (!environnement[i][j].getAlive()) environnement[i][j]=null;
						else environnement[i][j].update();
					}
					
					if (fire[i][j]>0) {
						if (fire[i][j]>=fireStop) {
							fire[i][j]=0;
						} else {
							fire[i][j]++;
						}
					}
					if (fire[i][j]<0) {
						terrain[i][j] = 4;
					}
					if (terrain[i][j]==1) nbGrass++;
					else if (nbSand==0 && terrain[i][j]==2) nbSand++;
				}
			
			//Liste permettant d'ajouter les nouveaux enfants
			ArrayList<Agent> nEnfant = new ArrayList<Agent>();
			//Met a jour les agents
			for (Agent a : agents) {
				if (a.getAlive()) { //Verifie si l'agent est en vie, et le met a jour
					a.move(terrain, environnement);
					a.update();
				}
				
				//Quelques regles du monde pour les agents
				if (a instanceof Human && a.getAlive()) {
					if (terrain[a.getX()][a.getY()]==0) ((Human)a).addDrowning(); //si le terrain est de l'eau, l'agent incremente la noyade
					else ((Human)a).setDrowning(0);
					if (environnement[a.getX()][a.getY()] instanceof Rose) { //Les humains ne mangent que les roses
						((Human)a).addHealth(addHumanHealth);
						removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
					}
					if (fire[a.getX()][a.getY()]!=0) {
						a.setOnFire(true);
						((Human)a).setFire(0);
					}
				} else if ( a instanceof Chicken && a.getAlive()) {
					if (terrain[a.getX()][a.getY()]==0) ((Chicken)a).addDrowning();
					else ((Chicken)a).setDrowning(0);
					if (environnement[a.getX()][a.getY()] instanceof Tulipe) { //Les poules ne mangent que les tulipes
						((Chicken)a).addHealth(addChickenHealth);
						removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
					}
					if (fire[a.getX()][a.getY()]!=0) {
						a.setOnFire(true);
						((Chicken)a).setFire(0);
					}
				}  else if ( a instanceof Fox && a.getAlive()) {
					if (terrain[a.getX()][a.getY()]==0) ((Fox)a).addDrowning();
					else ((Fox)a).setDrowning(0);
					if (environnement[a.getX()][a.getY()] instanceof Marguerite) { //Les renards ne mangent que les marguerites
						((Fox)a).addHealth(addFoxHealth);
						removeItem(environnement[a.getX()][a.getY()], a.getX(), a.getY());
					}
					if (fire[a.getX()][a.getY()]!=0) {
						a.setOnFire(true);
						((Fox)a).setFire(0);
					}
				}  else if ( a instanceof Snake && a.getAlive()) {
					if (terrain[a.getX()][a.getY()]==0) ((Snake)a).addDrowning();
					else ((Snake)a).setDrowning(0);
					if (fire[a.getX()][a.getY()]!=0) {
						a.setOnFire(true);
						((Snake)a).setFire(0);
					}
				}
				
				//Boucle permettant la naissance des enfants
				for (Agent a2 : agents) {
					if (!(a.equals(a2)) && Math.random()<pEnfant) { //Verifie si les deux agents sont de meme espece
						if (a.getSexe()!=a2.getSexe() && a.getX()==a2.getX() && a.getY()==a2.getY()) {
							if (a.getStime()==0 && a2.getStime()==0 ) { //Naissance d'un enfant
								int cptNbAgents = 0;
								for (Agent a3 : agents) { //Verifie que le nombre d'agent a la meme case ne depasse pas nbAgentsMaxPos
									if (a.getX()==a3.getX() && a3.getY()==a3.getY()) cptNbAgents++;
									if (cptNbAgents>nbAgentsMaxPos) {
										break; //Permet de sortir de la boucle for et eviter de faire des boucles inutilement car la limite est depassee
									}
								}
								if (cptNbAgents <= nbAgentsMaxPos) { //S'il y a nbAgentsMaxPos agents a la meme position, il n'y a pas naissance d'enfant
									if (a instanceof Human) nEnfant.add(new Human(a.getX(), a.getY())); //nouveau ne 
									else if (a instanceof Chicken) nEnfant.add(new Chicken(a.getX(), a.getY()));
									else if (a instanceof Fox) nEnfant.add(new Fox(a.getX(), a.getY()));
									else if (a instanceof Snake) nEnfant.add(new Snake(a.getX(), a.getY()));
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
				
				p=(int)(Math.random()*X);
				q=(int)(Math.random()*Y);
				
				if (Math.random()<pTree) {
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
		
		if ((nbSand == 0 && nbGrass < volcanoSpawn) || (newCycle)) {
			if (agents.size()>0) {
				ArrayList<Agent> copy = new ArrayList<Agent>(agents);
				for (Agent a : copy) agents.remove(a);
			}
			while (!newCycle) {
				int x = (int)(Math.random()*(X));
				int y = (int)(Math.random()*(Y));
				if (terrain[x][y]==1) {
					terrain[x][y] = 3;
					newCycle = true;
					volcanoX = x;
					volcanoY = y;
					volcanoRange++;
				}
			}
			
			if (CurrentRange < volcanoRange) {
				int[][] copyFire = new int[X][Y];
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ )
						copyFire[i][j] = fire[i][j];
				
				for ( int i = 0 ; i < copyFire[0].length ; i++ )
					for ( int j = 0 ; j < copyFire.length ; j++ ) {
						if (fire[i][j]!=-1) {
							if (i+1<X && (copyFire[i+1][j]==-1 || terrain[i][j]==3) ) {
								fire[i][j]=-1;
								if (terrain[i][j]!=3)terrain[i][j]=4;
							}
							if (i-1>=0 && (copyFire[i-1][j]==-1 || terrain[i][j]==3) ) {
								fire[i][j]=-1;
								if (terrain[i][j]!=3)terrain[i][j]=4;
							}
							if (j+1<Y && (copyFire[i][j+1]==-1 || terrain[i][j]==3) ) {
								fire[i][j]=-1;
								if (terrain[i][j]!=3)terrain[i][j]=4;
							}
							if (j-1>=0 && (copyFire[i][j-1]==-1 || terrain[i][j]==3) ) {
								fire[i][j]=-1;
								if (terrain[i][j]!=3)terrain[i][j]=4;
							}
						}
					}
				CurrentRange++;
			} else { //Retrait de la lave
				int cptLava=0;
				
				//Verifie qu'il y a assez de lave pour eviter une boucle infinie dans le while suivant
				for ( int i = 0 ; i < fire[0].length && cptLava<lavaDissipate; i++ )
					for ( int j = 0 ; j < fire.length && cptLava<lavaDissipate; j++ )
						if (fire[i][j]==-1) cptLava++;
				
				boolean fireDone = false;
				int nbLava=0;
				while (!fireDone && cptLava>0) {
					int x = (int)(Math.random()*(X));
					int y = (int)(Math.random()*(Y));
						if (fire[x][y]==-1) {
							fire[x][y]=0;
							nbLava++;
							if  (cptLava<lavaDissipate) fireDone=true;
						}
						if (nbLava>=lavaDissipate) fireDone=true;
					}
				if (cptLava==0 && !newCycleLastStep) {
					terrain[volcanoX][volcanoY]=4;
					volcanoX=0;
					volcanoY=0;
					newCycleLastStep=true;
				}
				
				if (newCycleLastStep) {
					int cptObsidian=0;
					int cptDirt=0;
					int cptGrass=0;
					for ( int i = 0 ; i < terrain[0].length && cptObsidian<dirtRejuvenate; i++ )
						for ( int j = 0 ; j < terrain.length && cptObsidian<dirtRejuvenate; j++ ) {
							if (terrain[i][j]==4) cptObsidian++;
							if (cptObsidian == 0 && terrain[i][j]==5) cptDirt++;
							if (cptDirt == 0 && cptObsidian ==0  && terrain[i][j]==1) cptGrass++;
						}
					int nbDirt=0;
					boolean dirtFill=false;
					while (!dirtFill && (cptObsidian>0 || cptDirt>0 || cptGrass>0) && currentSandFill<addSandFill) {
						int x = (int)(Math.random()*(X));
						int y = (int)(Math.random()*(Y));
							if (terrain[x][y]==4) {
								terrain[x][y]=5;
								nbDirt++;
								if  (cptObsidian<dirtRejuvenate) dirtFill=true;
							}
							if (cptObsidian>=dirtRejuvenate) dirtFill=true;
							
							if (cptObsidian==0) {
								if (terrain[x][y]==5) {
									terrain[x][y]=1;
									dirtFill=true;
								}
								
								if (cptDirt==0) {
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
					
					if (!dirtFill && cptObsidian<=0 && cptDirt<=0 && currentSandFill>=addSandFill) {
						currentSandFill=0;
						newCycle=false;
						newCycleLastStep=false;
						addInitiate();
					}
				}
			}
			try {
				Thread.sleep(lavaDelai);
			} catch ( Exception e )  {};
		}
		
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
			if (environnement[i][j] instanceof Item)
				/* Pour centrer l'image en fonction de la taille, avec 1 la taille maximale d'un sprite,
				 * Il faut faire : ( 1-SpriteSize ) * ( (spriteLength/2) + 1)
				 */
				g2.drawImage((environnement[i][j]).getImage(),(int)(spriteLength*i+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*j+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*environnement[i][j].getSpriteSize()),(int)(spriteLength*environnement[i][j].getSpriteSize()), frame);
			}
		
		//Le clone permet d'eviter les problemes rencontres lors d'affichage des agents et des modifications qui ont lieu en meme temps
		ArrayList<Agent> clone = new ArrayList<Agent>(agents);
		for (Agent a : clone) {
			if (a.getAlive() && a!=null) {
				a.draw(g2, frame);
			}
		}
		
		for ( int i = 0 ; i < fire[0].length ; i++ )
			for ( int j = 0 ; j < fire.length ; j++ ) {
				if (fire[i][j]>0) {
					if (environnement[i][j] instanceof Tree) {
						g2.drawImage(fireSprite,(int)(spriteLength*i+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*j+(1-environnement[i][j].getSpriteSize())*(spriteLength/2+1)),(int)(spriteLength*environnement[i][j].getSpriteSize()),(int)(spriteLength*environnement[i][j].getSpriteSize()), frame);
					} else {
						g2.drawImage(fireSprite,spriteLength*i,spriteLength*j,spriteLength,spriteLength, frame);
					}
				} else if (fire[i][j]<0) {
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
		//world.showAltitude(); //A utiliser en cas de probleme avec l'affichage des deplacements possibles avec les entiers
		while (true) {
			world.update();
			try {
				Thread.sleep(delai3);
			} catch ( Exception e ) {};
			System.out.println("it : " + i);
			i++;
		}
	}
}


