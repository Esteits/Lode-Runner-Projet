package com.loderunner.project.engine;

public class EnemyThread extends Thread{
    
    private Game game;
    private boolean run;
    private int ind;

    public EnemyThread(Game g, int ind){
        this.game = g;
        this.run = true;
        this.ind = ind;
    }

    public int getINd(){
        return this.ind;
    }

    @Override
    public void run(){
        while(run){
            int time = (int)(Math.random()*900)+100;
            try {
                game.moveEnemy(ind);
                this.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
