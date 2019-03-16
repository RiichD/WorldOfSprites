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
	private boolean alive;
	private boolean onFire;
	
	private int x, y;
	private int spriteX, spriteY; // position du Sprite
	private int pspriteX, pspriteY; // ancienne position du Sprite qui permet de deplacer fluidement
	
	private int sexe; //0 Male, 1 Female
	
	private int rayon = 3; //Rayon des predateurs
	
	private int nbItMax = 100; //Le choix de fuite est aleatoire mais toujours a l'endroit ou il n'y a pas de predateur. si nbItMax est depasse, l'agent ne bouge pas
	
	private int chasingPause = 5; //Duree avant chaque poursuite
	private int chasingTime = 15; //Nombre d'iterations de chasses maximale. Le predateur arrete ensuite de chasser.
	
	private double pN = 0.25; //Plus de probabilite pour aller a une certaine direction durant une fuite
	private double pS = 0.25;
	private double pW = 0.25;
	private double pE = 0.25;
	
	//A ne pas modifier
	private int currChasing = 0;
	
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
		
		//Deux tableaux de boolean indiquant si un agent est a une position, sinon c'est vide ( donc false )
		boolean[][] posPrey = new boolean[World.X][World.Y];
		boolean[][] posPred = new boolean[World.X][World.Y];
		
		if (! (this instanceof Human) ) { //L'humain fait sa vie et n'a pas de predateur ou de proie
			for (Agent b: agents) { //On compare l'agent actuel a un autre agent
				if (!(b instanceof Human) && !this.equals(b) && x==b.getX() && y==b.getY()) { //Verifie que l'agent a et b sont a la meme case
					if (this instanceof Chicken) {
						if (b instanceof Viper) { //La poule mange la vipere
							b.setAlive(false);
							currChasing = -chasingPause;
						} else if (b instanceof Fox) { //Le renard mange la poule
							setAlive(false);
						}
					} else if (this instanceof Fox) {
						if (b instanceof Chicken) { //Le renard mange la poule
							b.setAlive(false);
							currChasing = -chasingPause;
						} else if (b instanceof Viper) { //La vipere mange le renard
							setAlive(false);
						}
					} else if (this instanceof Viper) {
						if (b instanceof Fox) { //La vipere mange le renard
							b.setAlive(false);
							currChasing = -chasingPause;
						} else if (b instanceof Chicken) { //La poule mange la vipere
							setAlive(false);
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
		}
		
		if (!chase && !escape) {
			move(terrain,environnement); //Deplacement aleatoire
			currChasing = 0;
		} else if (!escape && chase){
			chasingPrey(terrain, environnement, posPrey); //Chasse
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
