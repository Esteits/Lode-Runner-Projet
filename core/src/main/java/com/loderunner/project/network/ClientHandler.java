package com.loderunner.project.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character;

public class ClientHandler extends Thread{
    private Serveur serv;
    private Character c;
    private Socket s;
    private Game g;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Serveur ser, Character c, Socket soc) throws IOException{
        this.serv = ser;
        this.c = c;
        this.s = soc;
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
    }

    public Character getCharacter(){
        return this.c;
    }
    public void setCharacter(Character c){
        this.c = c;
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
                        serv.moveCharacter(c, action);
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
