package com.loderunner.project.engine;

public class EnemyThread extends Thread{
    
    private Game game;

    public EnemyThread(Game g){
        this.game = g;
    }

    @Override
    public void run(){
            int time = (int)(Math.random()*900)+100;
            try {
                game.moveEnemy();
                this.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
