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
    private List<Character> chara = new ArrayList<>();
    private boolean mode = false; //true = coop, false = adversaire

    Serveur(Game g, int p){
        this.g = g;
        this.port = p;
    }
    public static void main(String[] args) throws Exception{
        Game g = new Game(5, 0);
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
                g.sec();
                avancedToNextLevel();
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

    public void avancedToNextLevel(){
        if(g.win()){
            int sco = g.getScore();
            addAllCharacter();
            this.g = g.nextLevel(sco + 1000);
            refreshCharacter();
            respawnAllCharacter();
        }
    }

    public void loadGame(){
        g.loadFromFile();
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

    public void respawnAllCharacter(){
        for(ClientHandler ch : clients){
            if(ch.getCharacter() instanceof Player){
                ch.getCharacter().respawn(g.getMaze().getExit(), 1);
                Player p = (Player) ch.getCharacter();
                p.setHp(5);
            }else{
                ch.getCharacter().respawn(1, g.getMaze().getHeight()-2);
                Enemy e = (Enemy) ch.getCharacter();
                e.setFree(true);
                e.setState(true);
            }
        }
    }

    public void restartGame(){
        addAllCharacter();
        this.g = new Game(5, 0);
        refreshCharacter();
        respawnAllCharacter();
    }
}
