package com.loderunner.project.entity;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.engine.Game;

import java.lang.Math;

public class EnemyNormal extends Enemy{
    Direction d = Direction.NONE;

    public EnemyNormal(int x, int y) {
        super(x, y);
    }

    public Player playClose(Game game){
        Player target = game.getPlay().get(0);
        int xEne = this.getX();
        int yEne = this.getY();
        int distanceTar = Math.abs(xEne - target.getX()) + Math.abs(yEne - target.getY());
        for(int i = 1 ; i<game.getPlay().size() ; i++){
            Player p = game.getPlay().get(i);
            int newDist = Math.abs(xEne - p.getX()) + Math.abs(yEne - p.getY());
            if(newDist > distanceTar){
                distanceTar = newDist;
                target = p;
            }
        }
        return target;
    }

    @Override
    public Direction mouvement(Game game) {
        Player p = playClose(game);

        if(p.getY() > this.y && game.getMaze().getTile(x, y-1).getType() == 2){
            return Direction.UP;
        }

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