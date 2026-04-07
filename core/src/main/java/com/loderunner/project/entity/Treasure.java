package com.loderunner.project.entity;

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
