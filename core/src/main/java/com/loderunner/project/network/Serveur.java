package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import java.util.*;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Player;

public class Serveur {
    private int port;
    private Game g;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private List<Player> play = new ArrayList<>();

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
                    Player p = new Player(g.getMaze().getExit(), 1);
                    this.g.addPlayer(p);
                    ClientHandler client = new ClientHandler(this, p, soc);
                    clients.add(client);
                    client.start();
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
                    respawnAllPlayer();
                }
            }
            synchronized(clients){
                for(ClientHandler ch : clients){
                    ch.sendGame(g);
                }
            }
        }
    }

    public void movePlayer(Player p, String action){
        switch (action) {
            case "RIGHT":
                g.movePlayerRight(p);
                break;
            case "LEFT":
                g.movePlayerLeft(p);
                break;
            case "DOWN":
                g.movePlayerDown(p);
                break;
            case "UP":
                g.movePlayerUp(p);
                break;
            case "DIG":
                g.dig(p);
                break;
        }
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
            this.play.add(ch.getPlayer());
        } 
    }

    public void refreshPlayer(){
        for(int i = 0 ; i < this.play.size() ; i++) {
            ClientHandler ch = this.clients.get(i);
            Player p = this.play.get(i);
            ch.setPlayer(p);
            this.g.addPlayer(p);
        }
    }

    public void respawnAllPlayer(){
        for(ClientHandler ch : clients){
            ch.getPlayer().respawn(g.getMaze().getExit(), 1);
        }
    }

    public void restartGame(){
        addAllPlayer();
        this.g = new Game(5);
        refreshPlayer();
        respawnAllPlayer();
    }
}
