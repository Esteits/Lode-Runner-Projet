package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import java.util.*;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.EnemyPlayer;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Character;

public class Serveur {
    private int port;
    private Game g;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private List<Player> play = new ArrayList<>();
    private boolean mode = false; //true = coop, false = adversaire

    Serveur(Game g, int p){
        this.g = g;
        this.port = p;
    }
    public static void main(String[] args) throws Exception{
        Game g = new Game(5);
        Serveur serv = new Serveur(g, 8080);
        serv.start();
    }

    public void start() throws Exception{
        ServerSocket s = new ServerSocket(port);
        System.out.println("Serveur lancer");

        new Thread(()->{
            while(true) {
                try {
                    Socket soc = s.accept();
                    if(!mode && this.clients.size()>0){
                        Enemy e = new EnemyPlayer(1, this.g.getMaze().getHeight()-2) ;
                        this.g.addEnemy(e);
                        ClientHandler client = new ClientHandler(this, e, soc);
                        clients.add(client);
                        client.start();
                    }else{
                        Player p = new Player(g.getMaze().getExit(), 1);
                        this.g.addPlayer(p);
                        ClientHandler client = new ClientHandler(this, p, soc);
                        clients.add(client);
                        client.start();
                    }
                    System.out.println("Client connecté");
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
        gameLoop();
    }

    public void gameLoop(){
        while(true){
            try{
                Thread.sleep(50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            synchronized(this){
                g.sec();
                if(g.win()){
                    int sco = g.getScore();
                    addAllPlayer();
                    this.g = g.nextLevel();
                    refreshPlayer();
                    g.setScore(sco + 1000);
                    respawnAllCharacter();
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
    }

    public void moveEnemy(Enemy e, String action){
        
    }

    public void loadGame(){
        g.loadFromFile();
        for(ClientHandler ch : clients) {
        ch.sendGame(g);
        }
    }

    public void addAllPlayer(){
        this.play.clear();
        for(ClientHandler ch : clients) {
            if(ch.getCharacter() instanceof Player){
                Player p = (Player) ch.getCharacter();
                this.play.add(p);  
            }
        } 
    }

    public void refreshPlayer(){
        for(int i = 0 ; i < this.play.size() ; i++) {
            ClientHandler ch = this.clients.get(i);
            Player p = this.play.get(i);
            ch.setCharacter(p);
            this.g.addPlayer(p);
        }
    }

    public void respawnAllCharacter(){
        for(ClientHandler ch : clients){
            if(ch.getCharacter() instanceof Player){
                ch.getCharacter().respawn(g.getMaze().getExit(), 1);
            }else{
                ch.getCharacter().respawn(1, g.getMaze().getHeight()-2);
            }
        }
    }

    public void restartGame(){
        addAllPlayer();
        this.g = new Game(5);
        refreshPlayer();
        respawnAllCharacter();
    }
}
