import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")
public abstract class Agent{ //Agents sera abstract, avec differents types d'agents
	private int sexe; //0 Male, 1 Female
	
	private int rayon = 3; //Rayon de chasse des predateurs
	
	private int nbItMax = 100; //Le choix de fuite est aleatoire mais toujours a l'endroit ou il n'y a pas de predateur. si nbItMax est depasse, l'agent ne bouge pas
	
	private int chasingPause = 10; //Duree avant chaque poursuite
	private int chasingTime = 15; //Nombre d'iterations de chasses maximale. Le predateur arrete ensuite de chasser.
	
	private double pN = 0.25; //Plus de probabilite pour aller a une certaine direction durant une fuite
	private double pS = 0.25;
	private double pW = 0.25;
	private double pE = 0.25;
	
	private int addChickenHealth = 65; //Sante que recupere chaque agent pour avoir manger leur proie
	private int addFoxHealth = 81;
	private int addViperHealth = 178; 
	
	//A ne pas modifier
	private boolean alive;
	private boolean onFire;
	
	private int x, y;
	private int spriteX, spriteY; //Position du Sprite
	private int pspriteX, pspriteY; //Ancienne position du Sprite qui permet de deplacer fluidement

	private int currChasing = 0; //Nombre d'iteration que l'agent est en train de chasser
	
	//Orientation prioritaire des proies
	boolean N = false;
	boolean S = false;
	boolean W = false;
	boolean E = false;
	
	Agent target = null; //Cible du zombie
	boolean targeted = false; //Le zombie possede une cible
	
	public Agent() {
		this((int)(Math.random()*(World.X)), (int)(Math.random()*(World.Y)));
	}
	
	public Agent(int x, int y) {
		alive = true;
		onFire = false;
		this.x = x;
		this.y = y;
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
		pspriteX = spriteX;
		pspriteY = spriteY;
		sexe = (int)(Math.random()*2);
	}
	
	//Get
	public boolean getAlive() {
		return alive;
	}
	
	public boolean getOnFire() {
		return onFire;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getSpriteX() {
		return spriteX;
	}
	
	public int getSpriteY() {
		return spriteY;
	}
	
	public int getPSpriteX() {
		return pspriteX;
	}
	
	public int getPSpriteY() {
		return pspriteY;
	}
	
	public int getSexe() {
		return sexe;
	}
	
	public abstract int getAge();
	
	public abstract int getDeathAge();
	
	public abstract int getHealth();
	
	public abstract int getStime();
	
	public abstract int getStimeIni();

	public abstract Image getImage();
	
	//Set
	public void setAlive(boolean b) {
		alive = b;
	}
	
	public void setOnFire(boolean b) {
		onFire = b;
	}
	
	public void setX(int n) {
		x=n;
	}
	
	public void setY(int n) {
		y=n;
	}
	
	public abstract void setStime();
	
	//Add
	public abstract void addAge();
	
	public abstract void addHealth(int n);
	
	//Deplacement aleatoire
	public void move(int[][] terrain, Item[][] environnement) {
		if (pspriteX==spriteX && pspriteY==spriteY) {
			double rand = Math.random();
			if (rand < 0.75) {
				if (rand < 0.5) {
					if (rand < 0.25) {
						if (x-1>=0 && (terrain[x-1][y]==World.grass || terrain[x-1][y]==World.sand || terrain[x-1][y]==World.dirt) ) {
							if (environnement[x-1][y] instanceof Flower || !(environnement[x-1][y] instanceof Item))
								x--;
						}
					} else {
						if (y+1<World.X && ( terrain[x][y+1]==World.grass || terrain[x][y+1]==World.sand || terrain[x][y+1]==World.dirt) ) {
							if (environnement[x][y+1] instanceof Flower || !(environnement[x][y+1] instanceof Item))
								y++;
						}
					}
				} else {
					if (y-1>=0 && ( terrain[x][y-1]==World.grass || terrain[x][y-1]==World.sand || terrain[x][y-1]==World.dirt) ) {
						if (environnement[x][y-1] instanceof Flower || !(environnement[x][y-1] instanceof Item))
							y--;
					}
				}
			} else {
				if (x+1<World.Y && ( terrain[x+1][y]==World.grass || terrain[x+1][y]==World.sand || terrain[x+1][y]==World.dirt)) {
					if (environnement[x+1][y] instanceof Flower || !(environnement[x+1][y] instanceof Item))
						x++;
				}
			}
		}
		spriteX= x*World.spriteLength;
		spriteY= y*World.spriteLength;
	}
	
	//Deplacement lie a une recherche de proie ou a un deplacement aleatoire | Deplacement principal
	/*
	 * Une poule chasse en suivant la proie jusqu'a qu'elle soit bloquee
	 */
	public void move(int[][] terrain, Item[][] environnement, ArrayList<Agent> agents) {
		boolean chase = false; //Une proie est a proximite, le predateur va le chasser
		boolean escape = false; //Un predateur cible sa proie, qui doit fuir. escape est prioritaire a chase.
		int[][] walk = new int[World.X][World.Y];
		
		//Deux tableaux de boolean indiquant si un agent est a une position, sinon c'est vide ( donc false )
		boolean[][] posPrey = new boolean[World.X][World.Y];
		boolean[][] posPred = new boolean[World.X][World.Y];
		
		if ( !(this instanceof Human) && !(this instanceof Zombie) ) { //L'humain fait sa vie et n'a pas de predateur ou de proie
			for (Agent b: agents) { //On compare l'agent actuel a un autre agent
				if (!(b instanceof Human) && !this.equals(b) && x==b.getX() && y==b.getY()) { //Verifie que l'agent a et b sont a la meme case et qu'on ait pas selectionne le meme agent que a
					if (this instanceof Chicken) {
						if (b instanceof Viper) { //La poule mange la vipere
							b.setAlive(false);
							addHealth(addChickenHealth);
							currChasing = -chasingPause;
						} else if (b instanceof Fox) { //Le renard mange la poule
							setAlive(false);
							b.addHealth(addFoxHealth);
						}
					} else if (this instanceof Fox) {
						if (b instanceof Chicken) { //Le renard mange la poule
							b.setAlive(false);
							addHealth(addFoxHealth);
							currChasing = -chasingPause;
						} else if (b instanceof Viper) { //La vipere mange le renard
							setAlive(false);
							b.addHealth(addViperHealth);
						}
					} else if (this instanceof Viper) {
						if (b instanceof Fox) { //La vipere mange le renard
							b.setAlive(false);
							addHealth(addViperHealth);
							currChasing = -chasingPause;
						} else if (b instanceof Chicken) { //La poule mange la vipere
							setAlive(false);
							b.addHealth(addChickenHealth);
						}
					}
				}
				
				//Recherche d'une proie autour de l'agent
				if (!escape && Math.abs(x-b.getX())<=rayon && Math.abs(y-b.getY())<=rayon) {
					if (this instanceof Chicken && b instanceof Viper) {
						chase = true;
						posPrey[b.getX()][b.getY()] = true;
					}else if (this instanceof Fox && b instanceof Chicken) {
						chase = true;
						posPrey[b.getX()][b.getY()] = true;
					}else if (this instanceof Viper && b instanceof Fox) {
						chase = true;
						posPrey[b.getX()][b.getY()] = true;
					}
				}
				
				//Recherche d'un predateur autour de l'agent
				if (Math.abs(x-b.getX())<=1 && Math.abs(y-b.getY())<=1) {
					if (this instanceof Viper && b instanceof Chicken) {
						chase = false;
						escape = true;
						posPred[b.getX()][b.getY()] = true;
					}else if (this instanceof Chicken && b instanceof Fox) {
						chase = false;
						escape = true;
						posPred[b.getX()][b.getY()] = true;
					}else if (this instanceof Fox && b instanceof Viper) {
						chase = false;
						escape = true;
						posPred[b.getX()][b.getY()] = true;
					}
				}
			}
		} else {
			if (this instanceof Human) {
				for (Agent b: agents) {
					if (b instanceof Zombie && x==b.getX() && y==b.getY()) {
						this.setAlive(false);
					}
					
					if (Math.abs(x-b.getX())<=1 && Math.abs(y-b.getY())<=1) {
						if (this instanceof Human && b instanceof Zombie) {
							escape = true;
							posPred[b.getX()][b.getY()] = true;
						}
					}
				}
			} else if (this instanceof Zombie) {
				if (!targeted) {
					for (Agent a: agents) {
						if (a instanceof Human) {
							target = a;
							targeted = true;
							break;
						}
					}
				}
				walk[target.getX()][target.getY()] = 4;
				chase = true;
			}
		}
		
		if (!chase && !escape) {
			move(terrain,environnement); //Deplacement aleatoire
			currChasing = 0;
		} else if (!escape && chase){
			if (this instanceof Zombie) {
				//Reinitialise les priorites
				N = false;
				S = false;
				E = false;
				W = false;
				
				//Recherche les cases ou l'agent peut avancer
				for (int i = 0 ; i < World.X ; i++) {
					for (int j = 0 ; j < World.Y ; j++) {
						if (walk[i][j]!=4 && terrain[i][j] > 0 && (environnement[i][j] instanceof Flower || environnement[i][j]==null)) {
							walk[i][j] = 1;
						}
					}
				}
				
				//Tableau representant le resultat de la recherche de proie
				int[][] res = chasingPreyS(x, y, walk);
				
				//Boucle pour afficher les cibles dans la console
				/*for (int i = 0 ; i < World.X && res!=null; i++) {
					for (int j = 0 ; j < World.Y ; j++) {
						System.out.print(res[j][i] + " ");
					}System.out.println();
				}*/
				
				if (res!=null) { //Si res vaut null, alors il n'y aucun humain trouve
					if (x+1<World.X && E &&(res[x+1][y]==2 || res[x+1][y]==5) ) x++;
					else if (x-1>=0 && W && (res[x-1][y]==2 || res[x-1][y]==5) ) x--;
					else if (y+1<World.Y && S &&( res[x][y+1]==2 || res[x][y+1]==5) ) y++;
					else if (y-1>=0 && N && (res[x][y-1]==2 || res[x][y-1]==5) ) y--;
					else { //Si les priorites ne peuvent pas etre realisees, l'agent prend un chemin malgre tout
						if (x+1<World.X &&(res[x+1][y]==2 || res[x+1][y]==5) ) x++;
						else if (x-1>=0 && (res[x-1][y]==2 || res[x-1][y]==5) ) x--;
						else if (y+1<World.Y &&( res[x][y+1]==2 || res[x][y+1]==5) ) y++;
						else if (y-1>=0 && (res[x][y-1]==2 || res[x][y-1]==5) ) y--;
					}
					if (!target.getAlive()) { //La cible est morte, le zombie change de cible
						target = null;
						targeted = false;
					}
				} else {
					move(terrain,environnement); //La cible n'est pas atteignable, le zombie se deplace aleatoirement
				}
				spriteX = x*World.spriteLength;
				spriteY = y*World.spriteLength;
			} else chasingPrey(terrain, environnement, posPrey); //Chasse
		} else {
			escapingPredator(terrain, environnement, posPred); //Fuite
		}
	}
	
	private void move(int[][] terrain, Item[][] environnement, boolean[][] position, int i, int j) {
		boolean moved = false;
		if (x-i==0) {
			if (y<j && y+1<World.Y && (terrain[x][y+1]==World.grass || terrain[x][y+1]==World.sand || terrain[x][y+1]==World.dirt) && ( environnement[x][y+1] instanceof Flower || environnement[x][y+1]==null)) {
				y++;
			}
			else if (y>j && y-1>=0 && (terrain[x][y-1]==World.grass || terrain[x][y-1]==World.sand || terrain[x][y-1]==World.dirt) && ( environnement[x][y-1] instanceof Flower || environnement[x][y-1]==null)) {
				y--;
			}
		} else if (y-j==0){
			if (x<i && x+1<World.X && (terrain[x+1][y]==World.grass || terrain[x+1][y]==World.sand || terrain[x+1][y]==World.dirt) && ( environnement[x+1][y] instanceof Flower || environnement[x+1][y]==null)) { 
				x++;
			}
			else if (x>i && x-1>=0 && (terrain[x-1][y]==World.grass || terrain[x-1][y]==World.sand || terrain[x-1][y]==World.dirt) && ( environnement[x-1][y] instanceof Flower || environnement[x-1][y]==null)) {
				x--;
			}
		} else {
			if (Math.random()<0.5) { //Meme distance de chemin pour atteindre la position, alors on choisit au hasard l'un des deux chemins possibles
				if (y<j && y+1<World.Y && (terrain[x][y+1]==World.grass || terrain[x][y+1]==World.sand || terrain[x][y+1]==World.dirt) && ( environnement[x][y+1] instanceof Flower || environnement[x][y+1]==null)) {
					y++;
				}
				else if (y>j && y-1>=0 && (terrain[x][y-1]==World.grass || terrain[x][y-1]==World.sand || terrain[x][y-1]==World.dirt) && ( environnement[x][y-1] instanceof Flower || environnement[x][y-1]==null)) {
					y--;
				}
			} else {
				if (x<i && x+1<World.X && (terrain[x+1][y]==World.grass || terrain[x+1][y]==World.sand || terrain[x+1][y]==World.dirt) && ( environnement[x+1][y] instanceof Flower || environnement[x+1][y]==null)) {
					x++;
				}
				else if (x>i && x-1>=0 && (terrain[x-1][y]==World.grass || terrain[x-1][y]==World.sand || terrain[x-1][y]==World.dirt) && ( environnement[x-1][y] instanceof Flower || environnement[x-1][y]==null)) {
					x--;
				}
			}
		}
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	private void chasingPrey(int[][] terrain, Item[][] environnement, boolean[][] position) {
		boolean found = false;
		int n=0;
		int m=0;
		if (currChasing == chasingTime) {
			currChasing = -chasingPause;
		}
		if (currChasing>=0) {
			for (int i = -rayon; i <= rayon; i++)
				for (int j = -rayon ; j <= rayon ; j++) {
					if (x+i>=0 && x+i<World.X && y+j>=0 && y+j<World.Y && position[x+i][y+j] && (terrain[x+i][y+j]==World.grass || terrain[x+i][y+j]==World.sand || terrain[x+i][y+j]==World.dirt) && ( environnement[x+i][y+j] instanceof Flower || environnement[x+i][y+j]==null)) {
						if (!found) {
							n = x+i;
							m = y+j;
							found = true;
						} else {
							//Comparaison entre la distance de chaque proie. On prend la distance la plus faible
							if (Math.abs(x-n)+Math.abs(y-m)>Math.abs(x-x+i)+Math.abs(y-y+j)) {
								n = x+i;
								m = y+j;
							}
						}
					}
				}
			if (found) move(terrain, environnement, position, n, m);
			else move(terrain, environnement); //Cas si aucun chemin ne permet d'atteindre la cible
		} else {
			move(terrain, environnement);
		}
		currChasing++;
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	private void escapingPredator(int[][] terrain, Item[][] environnement, boolean[][] position) {
		boolean found = false;
		boolean N = false, S = false, W = false, E = false;
		
		//Recherche de la position d'un predateur avec un voisinage de Von Neumann, ensuite de Moore
		if (x+1<World.X && !position[x+1][y] && ( terrain[x+1][y]==World.grass || terrain[x+1][y]==World.sand || terrain[x+1][y]==World.dirt) && ( environnement[x+1][y]==null || environnement[x+1][y] instanceof Flower) )
			E = true;
		if (x-1>=0 && !position[x-1][y] && ( terrain[x-1][y]==World.grass || terrain[x-1][y]==World.sand || terrain[x-1][y]==World.dirt) && ( environnement[x-1][y]==null || environnement[x-1][y] instanceof Flower) )
			W = true;
		if (y+1<World.Y && !position[x][y+1] && ( terrain[x][y+1]==World.grass || terrain[x][y+1]==World.sand || terrain[x][y+1]==World.dirt) && ( environnement[x][y+1]==null || environnement[x][y+1] instanceof Flower) )
			S = true;
		if (y-1>=0 && !position[x][y-1] && ( terrain[x][y-1]==World.grass || terrain[x][y-1]==World.sand || terrain[x][y-1]==World.dirt) && ( environnement[x][y-1]==null || environnement[x][y-1] instanceof Flower) )
			N = true;
		
		if (N || S || E || W) {
			found = false;
			int n=0;
			while (!found) {
				if (!found && N && Math.random()<pN) {
					if (x+1<World.X && y-1>=0 && x-1>=0 && !position[x+1][y-1] && !position[x-1][y-1]) { //Verifie en haut a gauche et en haut a droite avec un voisinage de Moore
						y--;
						found = true;
					}
					n++;
				}
				if (!found && S && Math.random()<pS) {
					if (x+1<World.X && x-1>=0 && y+1<World.Y && !position[x+1][y+1] && !position[x-1][y+1]) { //En bas a droite et en bas a gauche
						y++;
						found = true;
					}
					n++;
				}
				if (!found && E && Math.random()<pE) {
					if (x+1<World.X && y-1>=0 && y+1<World.Y && !position[x+1][y+1] && !position[x+1][y-1]) { //En bas a droite et en haut a droite
						x++;
						found = true;
					}
					n++;
				}
				if (!found && W && Math.random()<pW) {
					if (x-1>=0 && y+1<World.Y && y-1>=0 && !position[x-1][y+1] && !position[x-1][y-1]) { //En bas a gauche et en haut a gauche
						x--;
						found = true;
					}
					n++;
				}
				if (n>nbItMax && !found) found = true;
			}
		} else {
			move(terrain,environnement);
		}
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	private int[][] chasingPreyS(int i, int j, int[][] walk) {
		boolean N = false; //Choix prioritaire de chemin
		boolean S = false;
		boolean W = false;
		boolean E = false;
		
		int px = 0, py = 0; //Position de la proie
		
		boolean notWalkable = false;
		for (int n = 0 ; n < World.X; n++) {
			for (int m = 0 ; m < World.Y; m++) {
				if (walk[n][m]==4) {
					px = n;
					py = m;
					notWalkable = true;
				}
				if (walk[n][m]==1) notWalkable = true;
			}
		}
		
		//Choix prioritaire en fonction de la position de la proie et du predateur
		if (px<i) W = true;
		else if (px>i) E = true;
		if (py<j) N = true;
		else if (py>j) S = true;
		
		if (!notWalkable) { //S'il n'y a aucun terrain de type sable ou herbe dans le monde, l'agent ne peut pas bouger
			return null;
		}
		
		//Proie trouvee
		if (i+1<World.X && walk[i+1][j]==4) {
			this.S = S;
			walk[i+1][j] = 5; //5 est le resultat de la recherche, donc proie trouvee
			return walk;
		}
		if (i-1>=0 && walk[i-1][j]==4) {
			this.W=W;
			walk[i-1][j] = 5; //5 est le resultat de la recherche, donc proie trouvee
			return walk;
		}
		if (j+1<World.Y && walk[i][j+1]==4) {
			this.S=S;
			walk[i][j+1] = 5; //5 est le resultat de la recherche, donc proie trouvee
			return walk;
		}
		if (j-1>=0 && walk[i][j-1]==4) {
			this.N=N;
			walk[i][j-1] = 5; //5 est le resultat de la recherche, donc proie trouvee
			return walk;
		}
		
		//Recherche de chemin
		if (i+1<World.X && walk[i+1][j]==1 && E) {
			walk[i+1][j] = 2; //2 En cours de recherche
			return chasingPreyS(i+1, j, walk);
		}
		if (i-1>=0 && walk[i-1][j]==1 && W) {
			walk[i-1][j] = 2; //2 En cours de recherche
			return chasingPreyS(i-1, j, walk);
		}
		if (j+1<World.Y && walk[i][j+1]==1 && S) {
			walk[i][j+1] = 2; //2 En cours de recherche
			return chasingPreyS(i, j+1, walk);
		}
		if (j-1>=0 && walk[i][j-1]==1 && N) {
			walk[i][j-1] = 2; //2 En cours de recherche
			return chasingPreyS(i, j-1, walk);
		}
		
		//2eme recherche dans le cas ou ca bloque
		if (i+1<World.X && walk[i+1][j]==1) {
			walk[i+1][j] = 2; //2 En cours de recherche
			return chasingPreyS(i+1, j, walk);
		}
		if (i-1>=0 && walk[i-1][j]==1) {
			walk[i-1][j] = 2; //2 En cours de recherche
			return chasingPreyS(i-1, j, walk);
		}
		if (j+1<World.Y && walk[i][j+1]==1) {
			walk[i][j+1] = 2; //2 En cours de recherche
			return chasingPreyS(i, j+1, walk);
		}
		if (j-1>=0 && walk[i][j-1]==1) {
			walk[i][j-1] = 2; //2 En cours de recherche
			return chasingPreyS(i, j-1, walk);
		}
		
		//Blocage
		if (i+1<World.X && walk[i+1][j]==2) {
			walk[i][j] = 3; //3 Chemin bloque
			return chasingPreyS(i+1, j, walk);
		}
		if (i-1>=0 && walk[i-1][j]==2) {
			walk[i][j] = 3; //3 Chemin bloque
			return chasingPreyS(i-1, j, walk);
		}
		if (j+1<World.Y && walk[i][j+1]==2) {
			walk[i][j] = 3; //3 Chemin bloque
			return chasingPreyS(i, j+1, walk);
		}
		if (j-1>=0 && walk[i][j-1]==2) {
			walk[i][j] = 3; //3 Chemin bloque
			return chasingPreyS(i, j-1, walk);
		}
		
		return null;
	}
	
	//Fluidite des deplacements
	public void smoothMove() {
		//Si les sprites de chaque coordonnee est differente, il y a un deplacement, alors on incremente l'ancienne sprite de maniere progressive pour l'affichage d'un deplacement fluide
		if ( pspriteX!=spriteX || pspriteY!=spriteY) {
			if ( pspriteX < spriteX ) {
				pspriteX++;
			}
			if ( pspriteX > spriteX ) {
				pspriteX--;
			}
			if ( pspriteY < spriteY  ) {
				pspriteY++;
			}
			if ( pspriteY > spriteY ){
				pspriteY--;
			}
		} else {
			pspriteX = spriteX;
			pspriteY = spriteY;
		}
	}
	
	public abstract void update();
	
	public abstract void draw(Graphics2D g, JFrame frame);
}
