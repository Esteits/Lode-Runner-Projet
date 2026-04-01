package com.loderunner.project.entity;
import java.io.Serializable;

import com.loderunner.project.engine.Game;

public abstract class Enemy extends Character implements Serializable{
    private boolean state; //true = joue false = desactiver
    private boolean free ;  //true = normal false = pieger
    private int timetorespawn;


    public Enemy(int x, int y){
        super(x, y);
        this.timetorespawn = 0;
        this.free = true;
        this.state = false;
    }

    public boolean getState(){
        return this.state;
    }
    public void setState(boolean s){
        this.state = s;
    }

    public int getTimeToRespawn(){
        return this.timetorespawn;
    }
    public void setTimeToRespawn(int time){
        this.timetorespawn = time ;
    }

     public boolean getFree(){
        return this.free;
    }
    public void setFree(boolean t){
        this.free = t;
    }

    public void respawn(int x, int y){
        this.x = x;
        this.y = y;
        this.timetorespawn = 0;
        this.free = true;
    }

    public abstract Direction mouvement(Game game);
}
