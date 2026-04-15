package com.loderunner.project.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.EnemyPlayer;
import com.loderunner.project.entity.Player;
import com.loderunner.project.database.DatabaseManager;
import com.loderunner.project.database.DatabasePlayer;
import com.loderunner.project.database.DatabaseGame;

public class Serveur {
    private int port;
    private Game g;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private List<Character> chara = new ArrayList<>();
    private int tick;
    private int lvl = 1;
    private int id;

    Serveur(Game g, int p){
        this.g = g;
        this.port = p;
    }
    public static void main(String[] args) throws Exception{
        DatabaseManager.init();
        Game g = new Game(5, 0, 1, Game.Mode.COOP);
        Serveur serv = new Serveur(g, 8080);
        serv.start();
    }

    public void start() throws Exception{
        ServerSocket s = new ServerSocket(port);
        System.out.println("Serveur lancer");
        Game.Mode mode = Game.Mode.COOP;
        this.id = DatabaseGame.createGame(mode.toString());
        this.g.setId(this.id);
        new Thread(()->{
            while(true) {
                try {
                    Socket soc = s.accept();
                    if(this.g.getMode() == Game.Mode.VERSUS && this.clients.size() > 0){
                        Enemy e = new EnemyPlayer(g.getMaze().getExit(), 1) ;
                        this.g.addEnemy(e);
                        ClientHandler client = new ClientHandler(this, e, soc, "Enemy" + this.clients.size());
                        clients.add(client);
                        client.start();
                    }else{
                        Player p = new Player(g.getMaze().getExit(), 1);
                        this.g.addPlayer(p);
                        ClientHandler client = new ClientHandler(this, p, soc, "Player " + this.clients.size());
                        clients.add(client);
                        DatabaseGame.addPlayerToGame(this.g.getId(), client.getNames());
                        client.start();
                    }
                    System.out.println("Client connecté");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    gameLoop();
    s.close();
    }

    public void gameLoop(){
        while(true){
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            synchronized(this){
                if(this.clients.size() >= 1){
                    tick ++;
                    if(tick % 2 == 1){
                        g.sec();
                    }
                    if(tick >= 35){
                        g.activEnemy();
                        tick = 0;
                    }
                    avancedToNextLevel();
                }
                
            }
            synchronized(clients){
                for(ClientHandler ch : clients){
                    ch.sendGame(g);
                }
            }
        }
    }

    public void moveCharacter(Character c, String action){
        switch (action) {
            case "RIGHT":
                g.moveCharacterRight(c);
                break;
            case "LEFT":
                g.moveCharacterLeft(c);
                break;
            case "DOWN":
                g.moveCharacterDown(c);
                break;
            case "UP":
                g.moveCharacterUp(c);
                break;
            case "DIG":
                if(c instanceof Player){
                    Player p = (Player) c;
                    g.dig(p);
                }
                break;
        }
        if (c instanceof Player) {
            g.playerTreasureCol(); 
            g.playerEnemyCol();    
            g.canEscape();         
        }
    }

    public void avancedToNextLevel(){
        if(g.win()){
            int sco = g.getScore() + 1000;
            this.lvl++;
            addAllCharacter();
            Game.Mode m = this.g.getMode();
            this.g = g.nextLevel(sco, this.lvl, m);
            this.g.setId(this.id);
            DatabaseGame.refreshScore(this.id, sco);
            refreshCharacter();
            respawnAllCharacter(0);
            DatabaseGame.printGames();
        }
    }

    public void saveGame(){
        g.saveToFile();
    }

    public void loadGame(){
        Game gameLoad = g.loadFromFile();
        for (int i = 0 ;  i < this.clients.size() ; i++){
            if(this.clients.get(i).getCharacter() instanceof Player){
                if(0 != gameLoad.getPlay().size()){
                    this.clients.get(i).setCharacter(gameLoad.getPlay().get(0));
                    gameLoad.getPlay().remove(0);
                }else{
                    this.clients.get(i).setCharacter(new Player(gameLoad.getMaze().getExit(), 1));
                }
            }else{
                if(0 != gameLoad.getEne().size()){
                    EnemyPlayer ep = new EnemyPlayer(gameLoad.getEne().get(0));
                    this.clients.get(i).setCharacter(ep);
                    gameLoad.getEne().remove(0);
                }else{
                    this.clients.get(i).setCharacter(new EnemyPlayer(1, gameLoad.getMaze().getHeight() - 2));
                }
            }
        }
        addAllCharacter();
        this.g = gameLoad;
        refreshCharacter();
        for(ClientHandler ch : clients) {
            ch.sendGame(g);
        }
    }

    public void addAllCharacter(){
        this.chara.clear();
        for(ClientHandler ch : clients) {
            this.chara.add(ch.getCharacter());  
        }
    } 

    public void refreshCharacter(){
        for(int i = this.g.getEne().size() - 1 ; i >= 0 ; i--){
            Enemy e = this.g.getEne().get(i);
            if(e instanceof EnemyPlayer){
                this.g.getEne().remove(i);
            }
        }
        this.g.getPlay().clear();
        for(int i = 0 ; i < this.chara.size() ; i++){
            ClientHandler ch = this.clients.get(i);
            Character c = this.chara.get(i);
            ch.setCharacter(c);
            if(c instanceof Player){
                Player p = (Player) c;
                this.g.addPlayer(p);
            }else{
                Enemy e = (Enemy) c;
                this.g.addEnemy(e);
            }
        }
    }

    public void respawnAllCharacter(int a){ // a = 1 == restart
        for(ClientHandler ch : clients){
            ch.getCharacter().respawn(g.getMaze().getExit(), 1);
            if(ch.getCharacter() instanceof Player){
                if(a == 1){
                    Player p = (Player) ch.getCharacter();
                    p.setHp(5);
                }else{
                    Player p = (Player) ch.getCharacter();
                    p.setHp(p.getHp() + 1);
                }
            }
        }
    }

    public void restartGame(){
        synchronized(this){
            addAllCharacter();
            this.lvl = 1;
            this.tick = 0;
            Game.Mode m = this.g.getMode();
            this.g = new Game(5, 0, this.lvl, m);
            this.id = DatabaseGame.createGame(m.toString());
            this.g.setId(id);
            for(ClientHandler ch : clients){
                DatabaseGame.addPlayerToGame(this.g.getId(), ch.getNames());
            }
            refreshCharacter();
            respawnAllCharacter(1);
        }
    }        
}
