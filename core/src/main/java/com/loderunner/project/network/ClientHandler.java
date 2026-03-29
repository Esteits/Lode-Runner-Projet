package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import com.loderunner.project.engine.Game;

public class ClientHandler extends Thread{
    private Serveur serv;
    private int playerId;
    private Socket s;
    private Game game;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Serveur ser, Game g, int id, Socket soc) throws IOException{
        this.serv = ser;
        this.playerId = id;
        this.s = soc;
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
        out.writeObject(playerId);
        out.flush();
    }

    public void run(){
        try{
            while(true){
                String action = (String) in.readObject();
                switch (action) {
                    case "RIGHT":
                        game.movePlayerRight(playerId);
                        break;
                    case "LEFT":
                        game.movePlayerLeft(playerId);
                        break;
                    case "DOWN":
                        game.movePlayerDown(playerId);
                        break;
                    case "UP":
                        game.movePlayerUp(playerId);
                        break;
                    case "DIG":
                        game.dig(playerId);
                        break;
                    case "LOAD":
                        serv.loadGame();
                        break;
                    case "RESTART":
                        serv.restartGame();
                        break;
                }
            }
        }
        catch(Exception e){
            System.out.println("Client deconnecté" + playerId);
        }
    }

    public void sendGame(Game game){
        try{
            this.game = game;
            out.reset();
            out.writeObject(game);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
