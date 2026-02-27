import map.Maze;
import map.Tiles;
import entity.*;
import entity.Character;
import entity.Character.Direction;

import java.util.*;

public class Game {
    private Maze maze;
    private List<Player> play;
    private List<Enemy> ene;
    private List<Treasure> tre;

    public Game(){
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
    }
    
    public boolean isPlayer(int x, int y, Player p){
        for (Player pl : play){
            if(pl != p && pl.getX() == x && pl.getY() == y){
                return true;
            }
        }
        return false;
    }

    public void movePlayerRight(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX()+1, player.getY()).getType()!=3 && maze.getTile(player.getX()+1, player.getY()).getType()!=1 && !isPlayer(player.getX()+1, player.getY(), player)){
            player.right();
        }
    }

    public void movePlayerLeft(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX()-1, player.getY()).getType()!=3 && maze.getTile(player.getX()-1, player.getY()).getType()!=1 && !isPlayer(player.getX()-1, player.getY(), player)){
            player.left();
        }
    }

    public void movePlayerUp(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX(), player.getY()-1).getType()==2 && !isPlayer(player.getX(), player.getY()-1, player)){
            if(player.getY()-1 != 0 || maze.getCanEscape()){
                player.up();
            }
        }
    }
    
    public void movePlayerDown(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX(), player.getY()+1).getType()==2 && !isPlayer(player.getX(), player.getY()+1, player)){
            player.down();
        }
    }

    public void dig(int ind){
        Player player = play.get(ind);
        int digX = player.getX() ;
        int digY = player.getY()+1;
        if (player.getDirection() == Direction.RIGHT){
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
            if (c instanceof Player){
                Player p = (Player) c;
                if(isPlayer(p.getX(), p.getY()+1, p)){
                    return;
                }
            }
            c.down();
        }

        if (c instanceof Enemy){
            Enemy e = (Enemy) c;
            if(maze.getTile(c.getX(), c.getY()).getType()==1 && maze.getTile(c.getX(), c.getY()).getState()==false){
                e.setState(false);
                e.setTimeToRespawn(10);
            }
        }
    }

    public void gravity(){
        for(Player p : play){
            if(!p.playerDead()){
                fall(p);
            }
        }
        for(Enemy e : ene){
            if(e.getState()){
                fall(e);
            }    
        }
    }

    public void decrementEnemyTimer(){
        for(Enemy e : ene){
            if(!e.getState()){
                e.setTimeToRespawn(e.getTimeToRespawn()-1);
                if(e.getTimeToRespawn() <= 0){
                    e.respawn(maze.getExit(), 0);
                    e.setState(true);
                }
            }
        }
    }

    public void kill(int x, int y){
        for(Player p : play){
            if (p.getX()==x && p.getY()==y){
                p.loseHp();
                if(!p.playerDead()){
                    p.respawn(maze.getExit(), 1);
                }
            }
        }

        for(Enemy e : ene){
            if (e.getX()==x && e.getY()==y){
                e.setState(true);
                e.respawn(maze.getExit(), 0);
            }
        }
    }

    public void decrementTileTimer(){
        for(int y = 0 ; y < maze.getHeight() ; y++){
            for(int x = 0 ; x < maze.getWidth() ; x++){
                Tiles t = maze.getTile(x, y);
                if(!t.getState()){
                    t.setRespawn(t.getRespawn()-1);
                    if(t.getRespawn()<=0){
                        t.setState(true);
                        kill(x, y);
                    }
                }
            }
        }
    }

    public void playerEnemyCol(){
        for(Player p : play){
            if (!p.playerDead()){
                for (Enemy e: ene){
                    if (e.getState()){
                        if (p.getX()==e.getX() && p.getY()==e.getY()){
                            p.loseHp();
                            if(!p.playerDead()){
                                p.respawn(maze.getExit(), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void playerTreasureCol(){
        for(Player p : play){
            if(!p.playerDead()){
                for (Treasure t: tre){
                    if (!t.getCollect()){
                        if (p.getX()==t.getX() && p.getY()==t.getY()){
                            t.setCollect(true);
                        }
                    }
                }
            }
        }
    }

    public void canEscape(){
        for(Treasure t : tre){
            if(!t.getCollect()){
                return;
            }
        }
        maze.setCanEscape(true);
    }

    public boolean win(){
        for(Player p : play){
            if(p.getX() == maze.getExit() && p.getY() == 0 && !p.playerDead()){
                return true;
            }
        }
        return false;
    }

    public boolean gameOver(){
        for(Player p : play){
            if(!p.playerDead()){
                return false;
            }
        }
        return true;
    }
}
