package com.loderunner.project.entity;

/**
 * Représente un trésor dans le jeu.
 * 
 * Un trésor possède :
 * - une position (héritée de Entity)
 * - un état indiquant s'il a été collecté
 */
public class Treasure extends Entity{
    private boolean collect;

    public Treasure(int x, int y){
        super(x,y);
        this.collect = false; 
    }
    public boolean getCollect(){
        return this.collect;
    }
    public void setCollect(boolean co){
        this.collect = co;
    }
}   
