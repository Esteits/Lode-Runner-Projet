package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import com.loderunner.project.engine.Game;

public class ClientHandler extends Thread{
    private Game game;
    private int playerId;
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Game g, int id, Socket soc) throws IOException{
        this.game = g;
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
                }
            }
        }
        catch(Exception e){
            System.out.println("Client deconnecté" + playerId);
        }
    }

    public void sendGame(Game game){
        try{
            out.reset();
            out.writeObject(game);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
