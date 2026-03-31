package com.loderunner.project.network;

import java.io.*;
import java.net.*;
import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Player;

public class ClientHandler extends Thread{
    private Serveur serv;
    private Player player;
    private Socket s;
    private Game g;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Serveur ser, Player p, Socket soc) throws IOException{
        this.serv = ser;
        this.player = p;
        this.s = soc;
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
    }

    public Player getPlayer(){
        return this.player;
    }
    public void setPlayer(Player p){
        this.player = p;
    }
    
    public void run(){
        try{
            while(true){
                String action = (String) in.readObject();
                switch (action) {
                    case "LOAD":
                        serv.loadGame();
                        break;
                    case "RESTART":
                        serv.restartGame();
                        break;
                    default:
                        serv.movePlayer(player, action);
                }
            }
        }
        catch(Exception e){
            System.out.println("Client deconnecté");
        }
    }

    public void sendGame(Game game){
        try{
            this.g = game;
            out.reset();
            out.writeObject(game);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
