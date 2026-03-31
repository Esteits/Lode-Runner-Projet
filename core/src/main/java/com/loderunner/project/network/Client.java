package com.loderunner.project.network;

import java.io.*;
import java.net.*;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Player;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Game g;

    public Client(String host,int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        listen();
    }
   
    public Game getGame(){
        return g;
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
