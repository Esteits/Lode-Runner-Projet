package com.loderunner.project.entity;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character.Direction;

public class EnemyPatrouilleur extends Enemy{

    public enum Etat {
        PATROUILLE, 
        POURSUIT   
    }

    private Etat etat;
    private Direction direction;  // Je te cache pas je sais pas pk on doit mettre ca 

    public EnemyPatrouilleur(int x, int y) {
        super(x, y);
        this.etat = Etat.PATROUILLE;
        this.direction = Direction.RIGHT;
    }

    @Override
    public Direction mouvement(Game game) {        
        return Direction.NONE; 
    }

    public Etat getPEtat() 
    {
        return etat; 
    }

    public void setPEtat(Etat etat) 
    { 
        this.etat = etat; 
    }

    public Direction getPDirection() 
    { 
        return direction; 
    }

    public void setPDirection(Direction direction) 
    { 
        this.direction = direction; 
    }
}