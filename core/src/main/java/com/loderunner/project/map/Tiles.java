package com.loderunner.project.map;
import java.io.Serializable;

/**
 * Représente une tuile de la carte du jeu.
 * 
 * Une tuile possède :
 * - un type (mur, échelle, vide, etc.)
 * - une position (x, y)
 * - un état (visible ou non)
 * - un temps de respawn
 */

public class Tiles implements Serializable{
    private int type; // 0 = vide 1 = mur 2 = echelle 3 = bedrock
    private int x;
    private int y;
    private boolean state; //true =  visible false = invisible
    private int respawn ;

    public Tiles(int t, int x, int y){
        this.type = t;
        this.x = x;
        this.y = y;
        this.state = true;
        this.respawn = 0 ;
    }

    public int getType(){
        return this.type;
    }
    public void setType(int t){
        this.type = t;
    }

    public int getX(){
        return this.x;
    }
    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }
    public void setY(int y){
        this.y = y;
    }

    public boolean getState(){
        return this.state;
    }
    public void setState(boolean s){
        if (this.type==1){
            this.state = s;
        }
    }

    public int getRespawn(){
        return this.respawn;
    }
    public void setRespawn(int time){
        this.respawn = time;
    }
}