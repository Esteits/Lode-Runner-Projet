package com.loderunner.project.entity;
import com.loderunner.project.entity.Character.Direction;

import com.loderunner.project.engine.Game;

public class EnemyIA extends Enemy{

    public EnemyIA(int x, int y) {
        super(x, y);
    }

    public EnemyIA(int x, int y, boolean free, int time, boolean state){
        super(x, y);
        setFree(free);
        setTimeToRespawn(time);
        setState(state);
    }
    
    @Override
    public Direction mouvement(Game game) {
        
        return Direction.NONE; 
    }
}