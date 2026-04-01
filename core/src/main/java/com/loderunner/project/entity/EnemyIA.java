package com.loderunner.project.entity;
import com.loderunner.project.entity.Character.Direction;

import java.io.Serializable;

import com.loderunner.project.engine.Game;

public class EnemyIA extends Enemy implements Serializable {

    public EnemyIA(int x, int y) {
        super(x, y);
    }

    @Override
    public Direction mouvement(Game game) {
        
        return Direction.NONE; 
    }
}