package com.loderunner.project.entity;

import com.loderunner.project.engine.Game;

public class EnemyPlayer extends Enemy{
    public EnemyPlayer(int x, int y){
        super(x, y);
        this.setState(true);
    }
    
    public Direction mouvement(Game game){
        return Direction.NONE;
    }
}
