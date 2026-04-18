package com.loderunner.project.entity;

/**
 * Classe abstraite représentant un personnage du jeu.
 * 
 * Regroupe les comportements communs aux entités mobiles :
 * - position (héritée de Entity)
 * - direction de déplacement
 * - déplacements basiques
 */

public abstract class Character extends Entity{
    protected Direction dir;

    public enum Direction {
        LEFT,
        RIGHT,
        UP,    
        DOWN,  
        NONE 
    }

    public Character(int x, int y){
        super(x, y);
        this.dir = Direction.RIGHT;
    }

    public Direction getDirection(){
        return this.dir;
    }
    public void setDirection(Direction d){
        this.dir = d;
    }

    public void right(){
        this.x++;
        this.dir = Direction.RIGHT;
    }

    public void left(){
        this.x--;
        this.dir = Direction.LEFT;
    }

    public void up(){
        this.y--;
        this.dir = Direction.UP; 
    }

    public void down(){
        this.y++;
        this.dir = Direction.DOWN; 
    }

    public abstract void respawn(int x, int y);
}
