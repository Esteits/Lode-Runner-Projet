package com.loderunner.project.network;

import java.io.*;
import java.net.*;

import com.loderunner.project.engine.Game;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Game g;
    private int playerId;

    public Client(String host,int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        try{
            playerId = (int) in.readObject();
            System.out.println("joueur" + playerId);
        }catch(Exception e){
            e.printStackTrace();
        }
        listen();
    }
   
    public Game getGame(){
        return g;
    }
    public int getId(){
        return playerId;
    }

    public void listen(){
        new Thread(() -> {
            try{
                while(true){
                    g = (Game) in.readObject();
                }
            }catch (Exception e){
                System.out.println("Au revoir");
            }
        }).start();
    }

    public void action(String action){
        try{
            out.writeObject(action);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
