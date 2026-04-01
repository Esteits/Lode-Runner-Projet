package com.loderunner.project.engine;

import com.loderunner.project.map.Maze;
import com.loderunner.project.map.Tiles;
import com.loderunner.project.entity.*;
import com.loderunner.project.entity.Character;
import com.loderunner.project.entity.Character.Direction;

import java.io.*;
import java.util.*;

public class Game implements Serializable{
    private int score;
    private Maze maze;
    private List<Player> play;
    private List<Enemy> ene;
    private List<Treasure> tre;

    public Game(){  
        this.score = 0;
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
    }

    public Game(int nbrTreasure){
        this.score = 0;
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
        for(int i = 1 ; i<5 ; i++){
            Enemy e = new EnemyNormal(this.maze.getExit(), 0); // Pour l'instant on va faire apparaitre 5 ennemy normal parce que je n'ai pas comment faire pour en importer plusieur de chaque 
            this.ene.add(e);
        }
        while(tre.size()<nbrTreasure){
            Treasure t = new Treasure((int)(Math.random() * (this.maze.getWidth() - 1)) , (int)(Math.random() * (this.maze.getHeight() - 1)));
            if (maze.getTile(t.getX(), t.getY() + 1).getType()==1){
                boolean already = false;
                for(Treasure treas : this.tre){
                    if(treas.getX() == t.getX() && treas.getY() == t.getY()){
                        already = true;
                    }
                }
                if(!already){
                    this.tre.add(t);
                }
            }
        }
    }
    
    public int getScore(){
        return this.score;
    }
    public void setScore(int s){
        this.score=s;
    }

    public Maze getMaze(){
        return this.maze;
    }

    public List<Player> getPlay(){
        return this.play;
    }

    public List<Enemy> getEne(){
        return this.ene;
    }

    public List<Treasure> getTre(){
        return this.tre;
    }
    
    public void addPlayer(Player p){
        this.play.add(p);
    }

    public void addEnemy(Enemy e){
        this.ene.add(e);
    }

    public void addTreasure(Treasure t){
        this.tre.add(t);
    }
    
    public void sec(){
        gravity();
        playerEnemyCol();
        playerTreasureCol();
        decrementPlayerInvTimer();
        decrementEnemyTimer();
        decrementTileTimer();
        canEscape();
    }

    public boolean isPlayer(int x, int y){
        for (Player pl : play){
            if(pl.getX() == x && pl.getY() == y){
                return true;
            }
        }
        return false;
    }

    public boolean isWall(int x, int y){
        Tiles t = maze.getTile(x, y);
        if((t.getType() == 1 && t.getState() == true) || t.getType() == 3){
            return true;
        }return false;
    }
    public void moveCharacterRight(Character character){
        if (!isWall(character.getX()+1, character.getY()) && (!isPlayer(character.getX()+1, character.getY()) || character instanceof Enemy)){
            character.right();
        }
    }

    public void moveCharacterLeft(Character character){
        if (!isWall(character.getX()-1, character.getY()) && (!isPlayer(character.getX()-1, character.getY()) || character instanceof Enemy)){
            character.left();
        }
    }

    public void moveCharacterUp(Character character){
        if (maze.getTile(character.getX(), character.getY()).getType()==2 && (!isPlayer(character.getX(), character.getY()-1) || character instanceof Enemy)){
            if(character instanceof Player && maze.getCanEscape() || character.getY()-1 != 0){
                character.up();
            }
        }
    }
    
    public void moveCharacterDown(Character character){
        if (maze.getTile(character.getX(), character.getY()+1).getType()==2 && (!isPlayer(character.getX(), character.getY()+1) || character instanceof Enemy)){
            character.down();
        }
    }

    public synchronized void moveEnemy(int ind){
        Enemy e = this.ene.get(ind);
        if(e.getFree()){
        Player p = this.play.get(0);
            if(p.getY()>e.getY()){
                e.down();
            }else{
                if(p.getX()>e.getX()){
                    e.right();
                }else{
                    e.left();
                }
            }
        }
    }

    public void dig(Player player){
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
            tile.setRespawn(50);
        }
    }

    public void fall(Character c){
        if(c.getY()+1 >= maze.getHeight()){
            return;
        }

        for (Enemy e: ene){
            if(c.getX() == e.getX() && c.getY() == e.getY()-1 && e.getFree()==false){
                return;
            }
        }

        if (c instanceof Enemy){
            Enemy e = (Enemy) c;
            if(maze.getTile(c.getX(), c.getY()).getType()==1 && maze.getTile(c.getX(), c.getY()).getState()==false && e.getFree()){
                this.score += 10;
                e.setFree(false);
                e.setTimeToRespawn(10);
                return;
            }
        }
        Tiles tileUnder = maze.getTile(c.getX(), c.getY()+1);
        if((tileUnder.getType()==0 || (tileUnder.getType()==1 && tileUnder.getState()==false))){
            c.down();
        }
    }
    

    public synchronized void gravity(){
        for(Player p : play){
            if(!p.playerDead()){
                fall(p);
            }
        }
        for(Enemy e : ene){
            if(e.getFree() && e.getState()){
                fall(e);
            }    
        }
    }

    public void movementEnemy(){
        for(Enemy e : this.ene){
            e.mouvement(this);
        }
    }

    public void decrementPlayerInvTimer(){
        for(Player p : play){
            if(p.getInvin()){
                p.setTimeInve(p.getTimeInv() - 1);
            }if(p.getTimeInv() <= 0){
                p.setInvin(false);
            }
        }
    }

    public void decrementEnemyTimer(){
        for(Enemy e : ene){
            if(!e.getFree()){
                e.setTimeToRespawn(e.getTimeToRespawn() - 1);
                if(e.getTimeToRespawn() <= 0){
                    e.respawn(maze.getExit(), 0);
                    e.setFree(true);
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
                e.setFree(true);
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
            if (!p.playerDead() && !p.getInvin()){
                for (Enemy e: ene){
                    if (e.getFree() && e.getState()){
                        if (p.getX()==e.getX() && p.getY()==e.getY()){
                            p.loseHp();
                            if(!p.playerDead()){
                                p.setInvin(true);
                                p.setTimeInve(20);
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
                            if(!t.getCollect()){
                                this.score+=100;
                            }
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

    public Game nextLevel(){
        return new Game(5);
    }

    public void saveToFile(){
        try (BufferedWriter b = new BufferedWriter(new FileWriter("level.txt"))){
            b.write(maze.getWidth() + " " + maze.getHeight() + " " + maze.getExit());
            b.newLine();

            for(int y = 0 ; y < maze.getHeight() ; y++){
                for(int x =  0 ; x < maze.getWidth() ; x++){

                    int type = maze.getTile(x, y).getType();

                    switch(type){
                        case 0 :
                            b.write(' ');
                            break ;

                        case 1:
                            b.write('#');
                            break;

                        case 2:
                            b.write('H');
                            break;
                        
                        case 3:
                            b.write('=');
                            break;

                        default:
                            b.write('?');
                    }
                }
                b.newLine();
            }

            b.write("Play");
            b.newLine();
            for (Player p: play){
                b.write(p.getX() + " " + p.getY() + " " + p.getHp());
                b.newLine();
            }

            b.write("Ene");
            b.newLine();
            for (Enemy e: ene){
                b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState());
                b.newLine();
            }

            b.write("Tre");
            b.newLine();
            for(Treasure t: tre){
                b.write(t.getX() + " " + t.getY() + " " + t.getCollect());
                b.newLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadFromFile(){
        try (BufferedReader b = new BufferedReader(new FileReader("level.txt"))){
            String[] dims = b.readLine().split(" ");
            this.maze = new Maze(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
            for(int y = 0 ; y < maze.getHeight() ; y++){
                String line = b.readLine();
                for(int x = 0 ; x < maze.getWidth() ; x++){
                    int type;
                    char c = line.charAt(x);
                        switch (c) {
                            case ' ':
                                type = 0;
                                break;

                            case '#':
                                type = 1;
                                break;

                            case 'H':
                                type = 2;
                                break;

                            case '=':
                                type = 3 ;
                                break;

                            default:
                                type = 0 ;
                        }
                    maze.getTile(x, y).setType(type);
                }
            }

            b.readLine();
            this.play = new ArrayList<>();
            String line = b.readLine();
            while(!line.contentEquals("Ene")){
                String[] caractereP = line.split(" ");
                Player p = new Player(Integer.parseInt(caractereP[0]), Integer.parseInt(caractereP[1]));
                p.setHp(Integer.parseInt(caractereP[2]));
                play.add(p);
                line = b.readLine();
            }

            this.ene = new ArrayList<>();
            line = b.readLine();
            while(!line.contentEquals("Tre")){
                String[] caractereE = line.split(" ");
                Enemy e = new EnemyNormal(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]));
                e.setFree(Boolean.parseBoolean(caractereE[2]));
                e.setTimeToRespawn(Integer.parseInt(caractereE[3]));
                e.setState(Boolean.parseBoolean(caractereE[4]));
                ene.add(e);
                line = b.readLine();
            }

            this.tre = new ArrayList<>();
            line = b.readLine();
            while(line != null){
                String[] caractereT = line.split(" ");
                Treasure t = new Treasure(Integer.parseInt(caractereT[0]), Integer.parseInt(caractereT[1]));
                t.setCollect(Boolean.parseBoolean(caractereT[2]));
                tre.add(t);
                line = b.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}