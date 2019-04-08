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

	private int chasingPause = 10; //Duree avant chaque poursuite
	private int chasingTime = 15; //Nombre d'iterations de chasses maximale. Le predateur arrete ensuite de chasser.
	
	private double pN = 0.25; //Probabilite pour aller a une certaine direction durant une fuite, si plusieurs chemins sont valides
	private double pS = 0.25;
	private double pW = 0.25;
	private double pE = 0.25;
	
	//Sante que recupere chaque agent pour avoir manger leur proie
	private int addChickenHealth = 65; 
	private int addFoxHealth = 81;
	private int addViperHealth = 178; 
	
	//A ne pas modifier
	private boolean alive; //Agent en vie
	private boolean onFire; //Agent en feu
	
	private int x, y; //Position de l'agent
	private int spriteX, spriteY; //Position du Sprite
	private int pspriteX, pspriteY; //Ancienne position du Sprite qui permet de deplacer fluidement

	private int currChasing = 0; //Nombre d'iteration durant lequel l'agent est en train de chasser sa proie
	
	private int nbItMax = 100; //Le choix de fuite est aleatoire mais toujours a l'endroit lorsqu'il n'y a pas de predateur. si nbItMax est depasse, l'agent ne bouge pas
	
	public Agent() { //Constructeur sans argument qui permet d'ajouter l'agent a des positions aleatoires
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
		if ( (pspriteX==spriteX && pspriteY==spriteY) || this instanceof Zombie) { //On peut changer la position de l'agent uniquement si la position du sprite courant et du precedent est identique. Sauf pour le zombie qui peut se deplacer plusieurs fois
			double rand = Math.random();
			if (rand < 0.75) {
				if (rand < 0.5) {
					if (rand < 0.25) {
						if (x-1>=0 && terrain[x-1][y]>0) { //ou (terrain[x-1][y]==World.grass || terrain[x-1][y]==World.sand || terrain[x-1][y]==World.dirt) ), car toutes valeur superieures a 0 sont des cases ou l'agent peut marcher
							if (environnement[x-1][y] instanceof Flower || !(environnement[x-1][y] instanceof Item)) //Verifie qu'il y a soit une fleur, soit aucun objet a la position
								x--;
						}
					} else {
						if (y+1<World.X && ( terrain[x][y+1]>0) ) {
							if (environnement[x][y+1] instanceof Flower || !(environnement[x][y+1] instanceof Item))
								y++;
						}
					}
				} else {
					if (y-1>=0 && ( terrain[x][y-1]>0) ) {
						if (environnement[x][y-1] instanceof Flower || !(environnement[x][y-1] instanceof Item))
							y--;
					}
				}
			} else {
				if (x+1<World.Y && ( terrain[x+1][y]>0) ) {
					if (environnement[x+1][y] instanceof Flower || !(environnement[x+1][y] instanceof Item))
						x++;
				}
			}
		}
		
		//Mise a jour du sprite courant la taille du sprite et la position de l'agent
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
		
		if ( !(this instanceof Human) && !(this instanceof Zombie) ) { //L'humain fait sa vie et n'a pas de predateur ou de proie
			for (Agent b: agents) { //On compare l'agent actuel a un autre agent
				if (!(b instanceof Human) && !this.equals(b) && x==b.getX() && y==b.getY()) { //Verifie que l'agent a et b sont a la meme case et qu'on ait pas selectionne le meme agent que a
					if (this instanceof Chicken) {
						if (b instanceof Viper) { //La poule mange la vipere
							b.setAlive(false);
							addHealth(addChickenHealth); //L'agent a gagne en sante
							currChasing = -chasingPause; //Pause pour la chasse apres avoir tue sa proie
						} else if (b instanceof Fox) { //Le renard mange la poule
							setAlive(false);
							b.addHealth(addFoxHealth); //L'agent b gagne en sante
						}
					} else if (this instanceof Fox) {
						if (b instanceof Chicken) { //Le renard mange la poule
							b.setAlive(false);
							addHealth(addFoxHealth);
							currChasing = -chasingPause; //Pause pour la chasse apres avoir tue sa proie
						} else if (b instanceof Viper) { //La vipere mange le renard
							setAlive(false);
							b.addHealth(addViperHealth);
						}
					} else if (this instanceof Viper) {
						if (b instanceof Fox) { //La vipere mange le renard
							b.setAlive(false);
							addHealth(addViperHealth);
							currChasing = -chasingPause; //Pause pour la chasse apres avoir tue sa proie
						} else if (b instanceof Chicken) { //La poule mange la vipere
							setAlive(false);
							b.addHealth(addChickenHealth);
						}
					}
				}
				
				//Recherche d'une proie autour de l'agent
				if (!escape && Math.abs(x-b.getX())<=rayon && Math.abs(y-b.getY())<=rayon) { //Recherche si une proie est dans le rayon du predateur
					if (this instanceof Chicken && b instanceof Viper) {
						chase = true; //Activation de la chasse
						posPrey[b.getX()][b.getY()] = true; //Sauvegarde la position de la proie
					}else if (this instanceof Fox && b instanceof Chicken) {
						chase = true;
						posPrey[b.getX()][b.getY()] = true;
					}else if (this instanceof Viper && b instanceof Fox) {
						chase = true;
						posPrey[b.getX()][b.getY()] = true;
					}
				}
				
				//Recherche d'un predateur autour de l'agent
				if (Math.abs(x-b.getX()) + Math.abs(y-b.getY())<=1) { //Recherche un predateur avec un voisinage de Von Neumann
					if (this instanceof Viper && b instanceof Chicken) {
						chase = false; //La fuite etant prioritaire, la chasse est desactivee
						escape = true; //Activation de la fuite
						posPred[b.getX()][b.getY()] = true; //Sauvegarde la position du predateur
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
			if (this instanceof Human) { //Si l'agent est un humain, on cherche si un zombie est a la meme case. Si oui, l'humain meurt
				for (Agent b: agents) {
					if (b instanceof Zombie && x==b.getX() && y==b.getY()) {
						this.setAlive(false);
					}
					
					if (Math.abs(x-b.getX()) + Math.abs(y-b.getY())<=1) {
						if (this instanceof Human && b instanceof Zombie) {
							escape = true; //Activation de la fuite
							posPred[b.getX()][b.getY()] = true; //Sauvegarde la position du predateur
						}
					}
				}
			}
		}
		
		if (!chase && !escape) { //S'il n'y a ni chasse et ni fuite, l'agent a un deplacement aleatoire
			if (this instanceof Zombie) { //Le zombie se deplace plus vite, donc on creer une boucle et on le fait deplacer plusieurs fois
				for (int i = 0 ; i < ((Zombie)this).getZombieIte() ; i++) {
					this.move(terrain,environnement);
				}
			} else {
				move(terrain,environnement); //Deplacement aleatoire
			}
			currChasing = 0; //Reinitialise le temps de chasse
		} else if (!escape && chase){ //Si l'agent ne fuit pas mais chasse, il va chasser sa proie
			chasingPrey(terrain, environnement, posPrey); //Chasse
		} else {
			escapingPredator(terrain, environnement, posPred); //Fuite
		}
	}
	
	//Methode privee car elle sert uniquement a la methode ci-dessous, 
	private void move(int[][] terrain, Item[][] environnement, boolean[][] position, int i, int j) {
		//On verifie les 8 cases autour de l'agent, donc on applique le voisinage de Moore. Le predateur cherche a se rapprocher de sa proie
		if (x-i==0) {
			if (y<j && y+1<World.Y && (terrain[x][y+1]>0) && (environnement[x][y+1] instanceof Flower || environnement[x][y+1]==null)) {
				y++;
			}
			else if (y>j && y-1>=0 && (terrain[x][y-1]>0) && (environnement[x][y-1] instanceof Flower || environnement[x][y-1]==null)) {
				y--;
			}
		} else if (y-j==0){
			if (x<i && x+1<World.X && (terrain[x+1][y]>0) && (environnement[x+1][y] instanceof Flower || environnement[x+1][y]==null)) { 
				x++;
			}
			else if (x>i && x-1>=0 && (terrain[x-1][y]>0) && (environnement[x-1][y] instanceof Flower || environnement[x-1][y]==null)) {
				x--;
			}
		} else {
			if (Math.random()<0.5) { //Meme distance de chemin pour atteindre la position, alors on choisit au hasard l'un des deux chemins possibles
				if (y<j && y+1<World.Y && (terrain[x][y+1]>0) && (environnement[x][y+1] instanceof Flower || environnement[x][y+1]==null)) {
					y++;
				}
				else if (y>j && y-1>=0 && (terrain[x][y-1]>0) && (environnement[x][y-1] instanceof Flower || environnement[x][y-1]==null)) {
					y--;
				}
			} else {
				if (x<i && x+1<World.X && (terrain[x+1][y]>0) && (environnement[x+1][y] instanceof Flower || environnement[x+1][y]==null)) {
					x++;
				}
				else if (x>i && x-1>=0 && (terrain[x-1][y]>0) && (environnement[x-1][y] instanceof Flower || environnement[x-1][y]==null)) {
					x--;
				}
			}
		}
		
		//Mise a jour du sprite
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	//Methode privee car on ne l'utilise uniquement pour la chasse aux proies
	private void chasingPrey(int[][] terrain, Item[][] environnement, boolean[][] position) {
		boolean found = false; //Proie trouvee
		
		//Coordonnees de la proie
		int n=0;
		int m=0;
		
		//Si la chasse actuelle vaut le temps de chasse maximal, le predateur fait une pause
		if (currChasing == chasingTime) {
			currChasing = -chasingPause;
		}
		
		//La chasse reprend uniquement si la valeur de currChasing se retrouve a 0, d'ou la valeur negatif appliquee. Sinon l'agent se deplace aleatoirement
		if (currChasing>=0) {
			for (int i = -rayon; i <= rayon; i++)
				for (int j = -rayon ; j <= rayon ; j++) {
					if (x+i>=0 && x+i<World.X && y+j>=0 && y+j<World.Y && position[x+i][y+j] && (terrain[x+i][y+j]>0) && (environnement[x+i][y+j] instanceof Flower || environnement[x+i][y+j]==null)) {
						if (!found) { //Premiere proie trouvee
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
			if (found) move(terrain, environnement, position, n, m); //Utilisation de la methode privee pour se rapprocher de la proie
			else move(terrain, environnement); //Cas si aucun chemin ne permet d'atteindre la cible, il y a un deplacement aleatoire
		} else {
			move(terrain, environnement);
		}
		
		currChasing++;
		
		//Mise a jour du sprite
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	private void escapingPredator(int[][] terrain, Item[][] environnement, boolean[][] position) {
		boolean found = false; //Predateur trouve
		boolean N = false, S = false, W = false, E = false; //Position du predateur par rapport a la proie. La proie cherche toujours le chemin oppose au predateur pour fuir
		
		//Recherche de la position d'un predateur avec un voisinage de Von Neumann, ensuite de Moore
		if (x+1<World.X && !position[x+1][y] && ( terrain[x+1][y]>0 ) && ( environnement[x+1][y]==null || environnement[x+1][y] instanceof Flower) )
			E = true;
		if (x-1>=0 && !position[x-1][y] && ( terrain[x-1][y]>0 ) && ( environnement[x-1][y]==null || environnement[x-1][y] instanceof Flower) )
			W = true;
		if (y+1<World.Y && !position[x][y+1] && ( terrain[x][y+1]>0 ) && ( environnement[x][y+1]==null || environnement[x][y+1] instanceof Flower) )
			S = true;
		if (y-1>=0 && !position[x][y-1] && ( terrain[x][y-1]>0 ) && ( environnement[x][y-1]==null || environnement[x][y-1] instanceof Flower) )
			N = true;
		
		if (N || S || E || W) { //Si l'une des positions est vraie, donc un predateur dans l'une des positions, la proie va activer la fuite
			found = false;
			int n=0; //n permet d'eviter une boucle infinie, apres nbItMax.
			while (!found) {
				if (!found && N && Math.random()<pN) {
					if (x+1<World.X && y-1>=0 && x-1>=0 && !position[x+1][y-1] && !position[x-1][y-1]) { //Verifie en haut a gauche et en haut a droite avec un voisinage de Moore
						y--; //Deplacement oppose au predateur
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
				if (n>nbItMax && !found) found = true; //Sortie de boucle, found est true mais uniquement pour mettre fin a la boucle
			}
		} else {
			move(terrain,environnement); //Deplacement aleatoire
		}
		
		//Mise a jour du sprite
		spriteX = x*World.spriteLength;
		spriteY = y*World.spriteLength;
	}
	
	//Fluidite des deplacements
	public void smoothMove() {
		//Si les sprites courant et precedent de chaque coordonnee sont differentes, il y a un deplacement, alors on incremente l'ancien sprite de maniere progressif pour obtenir un deplacement fluide
		if ( pspriteX!=spriteX || pspriteY!=spriteY) {
			//pspriteX se rapproche de spriteX pixel par pixel
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
