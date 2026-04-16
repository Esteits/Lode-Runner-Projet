package com.loderunner.project.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.loderunner.project.entity.Character;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.EnemyIA;
import com.loderunner.project.entity.EnemyNormal;
import com.loderunner.project.entity.EnemyPatrouilleur;
import com.loderunner.project.entity.EnemyPlayer;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Treasure;
import com.loderunner.project.map.Maze;
import com.loderunner.project.map.Tiles;


public class Game implements Serializable{
    private int score;
    private Maze maze;
    private List<Player> play;
    private List<Enemy> ene;
    private List<Treasure> tre;
    private int lvl;
    private int id;
    private Mode modeJeu;

    public enum Mode{
        COOP,
        VERSUS
    };

    public Game(){  
        this.score = 0;
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
    }

    public Game(int nbrTreasure, int score, int lvl, Mode mJeu){
        this.score = score;
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
        this.modeJeu = mJeu;
        int i = 1;
        while(this.ene.size() < lvl){
            switch (i % 3) {
                case 1:
                    EnemyNormal en = new EnemyNormal(this.maze.getExit(), 1);
                    this.ene.add(en);
                    break;
                case 2:
                    EnemyPatrouilleur ep = new EnemyPatrouilleur((int)(Math.random() * (this.maze.getWidth() - 1)) , (int)(Math.random() * (this.maze.getHeight() - 1)));
                    while(ep.getY() <= 1 || ep.getX() < 1 || (this.maze.getTile(ep.getX(), ep.getY() - 1).getType() != 1 && this.maze.getTile(ep.getX(), ep.getY() - 1).getType() != 3)){
                        ep.setX((int)(Math.random() * (this.maze.getWidth() - 1)));
                        ep.setY((int)(Math.random() * (this.maze.getHeight() - 1)));
                    }
                    this.ene.add(ep);
                    break;
                case 0:
                    EnemyIA eIA = new EnemyIA(this.maze.getExit(), 1);
                    this.ene.add(eIA);
                    break;
            }
            i++;
        }
        while(tre.size() < nbrTreasure){
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

    public int getLvl(){
        return this.lvl;
    }
    public void setLvl(int l){
        this.lvl = l;
    }

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

    public Mode getMode(){
        return this.modeJeu;
    }
    public void setMode(Mode jeu){
        this.modeJeu = jeu;
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
        movementEnemy();
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
            tile.setRespawn(65);
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
                e.setTimeToRespawn(25);
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
            if (e.getFree() && e.getState()) {
                
                Direction intention = e.mouvement(this); 
                
                if (intention == Direction.RIGHT) {
                    moveCharacterRight(e);
                } 
                else if (intention == Direction.LEFT) {
                    moveCharacterLeft(e);
                } 
                else if (intention == Direction.UP) {
                    moveCharacterUp(e);
                } 
                else if (intention == Direction.DOWN) {
                    moveCharacterDown(e);
                }
            }
        }
    }

    public void activEnemy(){
        boolean activ = false;
        int i = 0;
        while(i < this.getEne().size() && !activ){
            if(!this.getEne().get(i).getState()){
                this.getEne().get(i).setState(true);
                activ = true;
            }
            i++;
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
                    if(e instanceof EnemyPatrouilleur){
                        e.respawn(e.getX(), e.getY() - 1);
                        e.setFree(true);
                    }else{
                        e.respawn(maze.getExit(), 0);
                        e.setFree(true);
                    }
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
                if(e instanceof EnemyPatrouilleur){
                    e.respawn(e.getX(), e.getY() - 1);
                }else{
                    e.respawn(maze.getExit(), 0);
                }
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
                                p.setTimeInve(10);
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

    public Game nextLevel(int score, int lvl, Mode mJeu){
        Game g = new Game(5, score, lvl, mJeu);
        g.setLvl(lvl);
        return g;
    }

    public void saveToFile(){
        try (BufferedWriter b = new BufferedWriter(new FileWriter("level.txt"))){
            b.write(maze.getWidth() + " " + maze.getHeight() + " " + maze.getExit() + " " + this.getScore() + " " + this.getLvl() + " " + this.getId() + " " + this.getMode());
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
                if(e instanceof EnemyNormal){
                    b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState() + " " + 1);
                }
                if(e instanceof EnemyPatrouilleur){
                    b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState() + " " + 2);
                }
                if(e instanceof EnemyIA){
                    b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState() + " " + 3);
                }
                if(e instanceof EnemyPlayer){
                    b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState() + " " + 4);
                }else{
                    b.write(e.getX() + " " + e.getY() + " " + e.getFree() + " " + e.getTimeToRespawn() + " " + e.getState() + " " + 1);
                }
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

    public Game loadFromFile(){
        Game g = new Game();
        try (BufferedReader b = new BufferedReader(new FileReader("level.txt"))){
            String[] dims = b.readLine().split(" ");
            g.maze = new Maze(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
            g.score = Integer.parseInt(dims[3]);
            g.lvl = Integer.parseInt(dims[4]);
            g.id = Integer.parseInt(dims[5]);
            g.modeJeu = Mode.valueOf(dims[6]);
            for(int y = 0 ; y < g.maze.getHeight() ; y++){
                String line = b.readLine();
                for(int x = 0 ; x < g.maze.getWidth() ; x++){
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
                    g.maze.getTile(x, y).setType(type);
                }
            }

            b.readLine();
            g.play = new ArrayList<>();
            String line = b.readLine();
            while(!line.contentEquals("Ene")){
                String[] caractereP = line.split(" ");
                Player p = new Player(Integer.parseInt(caractereP[0]), Integer.parseInt(caractereP[1]));
                p.setHp(Integer.parseInt(caractereP[2]));
                g.addPlayer(p);
                line = b.readLine();
            }
            g.ene = new ArrayList<>();
            line = b.readLine();
            while(!line.contentEquals("Tre")){
                String[] caractereE = line.split(" ");
                if(Integer.parseInt(caractereE[5]) == 2){
                    EnemyPatrouilleur e = new EnemyPatrouilleur(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]), Boolean.parseBoolean(caractereE[2]), Integer.parseInt(caractereE[3]), Boolean.parseBoolean(caractereE[4]));
                    g.addEnemy(e);
                }else if(Integer.parseInt(caractereE[5]) == 3){
                    EnemyIA e = new EnemyIA(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]), Boolean.parseBoolean(caractereE[2]), Integer.parseInt(caractereE[3]), Boolean.parseBoolean(caractereE[4]));
                    g.addEnemy(e);
                }else if(Integer.parseInt(caractereE[5]) == 4){
                    EnemyPlayer e = new EnemyPlayer(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]), Boolean.parseBoolean(caractereE[2]), Integer.parseInt(caractereE[3]), Boolean.parseBoolean(caractereE[4]));
                    g.addEnemy(e);
                }else{
                    EnemyNormal e = new EnemyNormal(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]), Boolean.parseBoolean(caractereE[2]), Integer.parseInt(caractereE[3]), Boolean.parseBoolean(caractereE[4]));
                    g.addEnemy(e);
                }
                line = b.readLine();
            }

            g.tre = new ArrayList<>();
            line = b.readLine();
            while(line != null){
                String[] caractereT = line.split(" ");
                Treasure t = new Treasure(Integer.parseInt(caractereT[0]), Integer.parseInt(caractereT[1]));
                t.setCollect(Boolean.parseBoolean(caractereT[2]));
                g.addTreasure(t);
                line = b.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return g;
    }
}