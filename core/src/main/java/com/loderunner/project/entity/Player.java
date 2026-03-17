package com.loderunner.project.entity;

public class Player extends Character {
    private int hp ;
    private boolean invincible;
    private int timeInv;

    public Player(int x, int y){
        super(x, y);
        this.hp = 5;
    }

    public int getHp(){
        return this.hp;
    }
    public void setHp(int h){
        this.hp=h;
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