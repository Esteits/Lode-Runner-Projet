package com.loderunner.project.entity;

/**
 * Représente un joueur dans le jeu.
 * 
 * Un joueur possède :
 * - des points de vie (hp)
 * - un état d'invincibilité temporaire
 * - un nom
 * - une position (héritée de Character)
 */

public class Player extends Character{
    private int hp ;
    private boolean invincible;
    private int timeInv;
    private String name;

    public Player(int x, int y, String name){
        super(x, y);
        this.hp = 5;
        this.name = name;
    }

    public int getHp(){
        return this.hp;
    }
    public void setHp(int h){
        this.hp=h;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public boolean getInvin(){
        return this.invincible;
    }
    public void setInvin(boolean i){
        this.invincible = i;
    }

    public int getTimeInv(){
        return this.timeInv;
    }
    public void setTimeInve(int sti){
        this.timeInv = sti;
    }

    public boolean playerDead(){
        return this.hp<=0;
    }

    public void loseHp(){
        this.hp--;
    }

    public void respawn(int x, int y){
        this.x = x;
        this.y = y;
    }
}