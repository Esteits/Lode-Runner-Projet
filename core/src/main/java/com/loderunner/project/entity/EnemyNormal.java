package com.loderunner.project.entity;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.engine.Game;

public class EnemyNormal extends Enemy {

    public EnemyNormal(int x, int y) {
        super(x, y);
    }

    @Override
    public Direction Mouvement(Game game) {
        Player p = game.getPlay().get(0);

        if (p.getX() > this.getX()) 
            {
            return Direction.RIGHT;
            } 
        else if (p.getX() < this.getX()) 
            {
            return Direction.LEFT;
            }
        
        return Direction.NONE; 
    }
}