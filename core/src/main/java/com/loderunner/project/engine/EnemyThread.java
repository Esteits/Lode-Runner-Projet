package com.loderunner.project.engine;

public class EnemyThread extends Thread{
    
    private Game game;
    private volatile boolean running;
    private int ind;

    public EnemyThread(Game g, int ind){
        this.game = g;
        this.running = true;
        this.ind = ind;
    }

    public int getInd(){
        return this.ind;
    }

    @Override
    public void run(){
        game.getEne().get(ind).setState(true);
        while(running){
            int time = (int)(Math.random()*900)+100;
            try {
                game.moveEnemy(ind);
                this.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRun(){
        game.getEne().get(ind).setState(false);
        this.running = false;
    }
}
