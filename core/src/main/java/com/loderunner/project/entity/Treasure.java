package com.loderunner.project.entity;
import java.io.Serializable;

public class Treasure extends Entity implements Serializable{
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
