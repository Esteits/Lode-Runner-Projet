package com.loderunner.project.entity;

import com.loderunner.project.engine.Game;

public class EnemyPlayer extends Enemy{
    public EnemyPlayer(int x, int y){
        super(x, y);
        this.setState(true);
    }
    
    public EnemyPlayer(Enemy e){
        super(e.getX(), e.getY());
        this.setFree(e.getFree());
        this.setTimeToRespawn(e.getTimeToRespawn());
        this.setState(e.getState());
    }

    public EnemyPlayer(int x, int y, boolean free, int time, boolean state){
        super(x, y);
        setFree(free);
        setTimeToRespawn(time);
        setState(state);
    }

    public Direction mouvement(Game game){
        return Direction.NONE;
    }
}
