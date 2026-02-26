import map.Maze;
import map.Tiles;
import entity.*;
import entity.Character;
import entity.Character.Direction;

import java.util.*;

public class Game {
    private Maze maze;
    private Player play;
    private List<Enemy> ene;

    public Game(){
        this.maze = Maze.generation();
        this.play = new Player(maze.getExit(), 1);
        this.ene = new ArrayList<>();
    }

    public void movePlayerRight(){
        if (maze.getTile(play.getX()+1, play.getY()).getType()!=1){
            play.right();
        }
    }

    public void movePlayerLeft(){
        if (maze.getTile(play.getX()-1, play.getY()).getType()!=1){
            play.left();
        }
    }

    public void movePlayerUp(){
        if (maze.getTile(play.getX(), play.getY()-1).getType()==2){
            play.up();
        }
    }
    
    public void movePlayerDown(){
        if (maze.getTile(play.getX(), play.getY()+1).getType()==2){
            play.down();;
        }
    }

    public void dig(){
        int digX = play.getX() ;
        int digY = play.getY()+1;
        if (play.getDirection() == Direction.RIGHT){
            digX++;
        }else{
            digX--;
        }
        Tiles tile = maze.getTile(digX, digY);
        if(tile.getType()==1 && digX != maze.getWidth()-1 && digX != 0){
            tile.setState(false);
            tile.setRespawn(10);
        }
    }

    public void fall(Character c){
        if(c.getY()+1 >= maze.getHeight()){
            return;
        }

        for (Enemy e: ene){
            if(c.getX() == e.getX() && c.getY() == e.getY()-1 && e.getState()==false){
                return;
            }
        }
        Tiles tileUnder = maze.getTile(c.getX(), c.getY()+1);
        if((tileUnder.getType()==0 || (tileUnder.getType()==1 && tileUnder.getState()==false))){
            c.fallOne();
        }
        if (c instanceof Enemy){
            Enemy e = (Enemy) c;
            if(maze.getTile(c.getX(), c.getY()).getType()==1 && maze.getTile(c.getX(), c.getY()).getState()==false){
                e.setState(false);
                e.setTimeToRespawn(10);
            }
        }
    }
}

// ajoute classe tresor, interaction 