package com.loderunner.project.engine;

import java.util.*;

public class StartEnemyThread extends Thread {
    private List<EnemyThread> ennThread;
    private boolean run;

    public StartEnemyThread(){
        this.ennThread = new ArrayList<>();
        this.run = true ;
    }

    public void addThreadEnemy(EnemyThread et){
        ennThread.add(et);
    }

    @Override
    public void run(){
        while(run){
            for(int i = 0 ; i < ennThread.size() ; i++){
                try{
                    sleep(2000 + 1000 * i);
                    ennThread.get(i).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.run = false;
        }
    }
}

