package com.loderunner.project.map;
import java.io.Serializable;

/**
 * Représente le labyrinthe du jeu.
 * 
 * Contient :
 * - une grille de tuiles
 * - les dimensions
 * - la position de la sortie
 * - l'état d'ouverture de la sortie
 */

public class Maze implements Serializable{
    private int width;
    private int height;
    private int exit;
    private Tiles[][] map;
    private boolean escapeOpen;

    public Maze(int width, int height, int exit){
        this.width = width;
        this.height = height;
        this.exit = exit;
        map = new Tiles[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = new Tiles(0, j, i); // Initialisation de la grille avec des tuiles vides (type 0)
            }
        }
    }

    public Tiles getTile(int x, int y){
        return this.map[y][x];
    }
    
    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public int getExit(){
        return this.exit;
    }
    public void setExit(int a){
        this.exit=a;
    }
            
    /**
 * @return true si la sortie est accessible
    */
    public boolean getCanEscape(){
        return this.escapeOpen;
    }
    public void setCanEscape(boolean escape){
        this.escapeOpen=escape;
    }

    /**
    * Crée des bordures incassable autour du labyrinthe.
    */
    public void bord(){
        for (int x = 0 ; x < this.getWidth() ; x++){
            getTile(x, 0).setType(3);
            getTile(x, height-1).setType(3);
        }
        for (int y = 0; y < height; y++) {
            getTile(0, y).setType(3);
            getTile(width-1, y).setType(3);
        }
    }
    
    /**
     * Crée une plateforme horizontale de blocs destructibles.
     *
     * @param x position centrale
     * @param y hauteur
     * @param lon longueur de la plateforme
     * @return bornes gauche et droite de la plateforme
     */
    public int[] plat(int x,int y,int lon){
        int right = x;
        int left = x ;
        int compt = 0 ;
        while (compt <= lon){
            if (compt==0){
                this.getTile(x, y).setType(1);
            }else if (compt%2==0){
                if (left - 1 > 0){
                    left -= 1 ;
                    this.getTile(left, y).setType(1);
                }
            }
            else{
                if (right + 1 < this.getWidth()-1){
                    right += 1;
                    this.getTile(right, y).setType(1);
                }
            }
            compt+=1;
        }
        return new int[]{left,right};
    }

    /**
     * Crée une échelle verticale à partir d'une position donnée
     * jusqu'à rencontrer un obstacle.
     *
     * @param x position X
     * @param y position Y de départ
     */
    public void ladder(int x, int y){
        this.getTile(x, y).setType(2);
        y+=1;
        while(this.getTile(x, y).getType()!=1 && this.getTile(x, y).getType()!=3){
            this.getTile(x, y).setType(2);
            y+=1;
        }
    }

    /**
     * Génère un labyrinthe aléatoire.
     *
     * @return nouveau labyrinthe généré
     */
    public static Maze generation(){
        int size = (int)(Math.random()*10)+20;
        Maze mazegenerator = new Maze(size, size, 0);
        mazegenerator.bord();
        int y = mazegenerator.getHeight()-1 ;
        while (mazegenerator.getExit()==0){
            int x = (int) (Math.random()*(mazegenerator.getWidth()-2))+ 1;
            int h = (int) (Math.random()*2)+2;
            y=y-h;
            if (y <= 1){
                mazegenerator.setExit(x);
            }else {
                int l = (int)(Math.random()*(mazegenerator.getWidth()-4)) + 3 ;
                int[] borneplat=mazegenerator.plat(x, y, l);
                int xLadder = borneplat[0] + (int) (Math.random()*(borneplat[1]-borneplat[0]));
                mazegenerator.ladder(xLadder, y);
            }
        }
        mazegenerator.ladder(mazegenerator.getExit(), 0);
        return mazegenerator;
    }
}