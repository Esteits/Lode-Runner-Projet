package com.loderunner.project.network;

import java.io.*;
import java.net.*;

import com.loderunner.project.engine.Game;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Game g;
    private String score;
    private String name;

    public Client(String host,int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        listen();
    }
   
    public Game getGame(){
        return g;
    }

    public String getScore(){
        return this.score;
    }

    public String getName(){
        return this.name;
    }

    public void listen(){
        new Thread(() -> {
            try{
                while(true){
                    Object obj = in.readObject();
                    if(obj instanceof Game){
                        g = (Game) obj;
                    }else{
                        if(obj instanceof String){
                            String msg = (String) obj;
                            if(msg.startsWith("GAME") || msg.startsWith("ERREUR")){
                                this.score = msg;
                            }else{
                                this.name = msg;
                            }
                        }
                    }
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
