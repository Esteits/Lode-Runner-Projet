package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import java.util.*;

import com.loderunner.project.engine.Game;

public class Serveur {
    private int port;
    private Game g;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private int nextIdPlayer = 0;

    Serveur(Game game, int p){
        this.g = game;
        this.port = p;
    }
    public static void main(String[] args) throws Exception{
        Game game = new Game(5, 1);
        Serveur serv = new Serveur(game, 8080);
        serv.start();
    }

    public void start() throws Exception{
        ServerSocket s = new ServerSocket(port);
        System.out.println("Serveur lancer");

        new Thread(()->{
            while(true) {
                try {
                    Socket soc = s.accept();
                    ClientHandler client = new ClientHandler(this, g, nextIdPlayer, soc);
                    clients.add(client);
                    client.start();
                    nextIdPlayer++;
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
            g.sec();
            if(g.win()){
                int sco = g.getScore();
                this.g = g.nextLevel();
                g.setScore(sco + 1000);
            }
            synchronized(clients){
                for(ClientHandler ch : clients){
                    ch.sendGame(g);
                }
            }
        }
    }

    public void loadGame(){
        g.loadFromFile();
        for(ClientHandler ch : clients) {
        ch.sendGame(g);
        }
    }

    public void restartGame(){
        this.g = new Game(5, 2);
        for(ClientHandler ch : clients) {
            ch.sendGame(g);
        }
    }
}
