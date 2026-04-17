package com.loderunner.project.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.loderunner.project.database.DatabaseGame;
import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character;

public class ClientHandler extends Thread{
    private String name;
    private Serveur serv;
    private Character c;
    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Serveur ser, Character c, Socket soc, String name) throws IOException{
        this.name = name;
        this.serv = ser;
        this.c = c;
        this.s = soc;
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
    }

    public String getNames(){
        return this.name;
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
                    case "SAVE":
                        serv.saveGame();
                        break;
                    case "LOAD":
                        serv.loadGame();
                        break;
                    case "RESTART":
                        serv.restartGame();
                        break;
                    case "GET_SCORE":
                        String score = DatabaseGame.printGames(this.serv.getGame().getMode().toString());
                        if(score.equals("Erreur")){
                            score = "Game Over";
                        }
                        sendMessage(score);
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

    public void sendMessage(String s){
        try{
            out.writeObject(s);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
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
