package com.loderunner.project.entity;
import java.io.Serializable;

/**
 * Classe de base représentant une entité dans le jeu.
 * 
 * Une entité possède une position (x, y) dans la grille.
 * Elle est sérialisable pour être envoyée.
 */

public class Entity implements Serializable {
    protected int x;
    protected int y;

    public Entity(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }
    public void setX(int x){
        this.x=x;
    }

    public int getY(){
        return this.y;
    }
    public void setY(int y){
        this.y=y;
    }
}
