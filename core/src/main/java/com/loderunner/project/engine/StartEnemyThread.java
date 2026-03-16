package com.loderunner.project.engine;

import java.util.*;

public class StartEnemyThread extends Thread {
    private List<EnemyThread> ennThread;
    private boolean running;

    public StartEnemyThread(){
        this.ennThread = new ArrayList<>();
        this.running = true ;
    }

    public void addThreadEnemy(EnemyThread et){
        ennThread.add(et);
    }

    @Override
    public void run(){
        while(running){
            for(int i = 0 ; i < ennThread.size() ; i++){
                try{
                    sleep(2000 + 1000 * i);
                    ennThread.get(i).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.running = false;
        }
    }

    public void stopAll(){
        for(int i = 0 ; i < ennThread.size() ; i++){
            ennThread.get(i).stopRun();
        }
        ennThread.clear();
    }
}

