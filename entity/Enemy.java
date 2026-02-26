package entity;

public class Enemy extends Character{
    private int timetorespawn;

    public Enemy(int x, int y){
        super(x, y);
        this.timetorespawn = 0;
    }

    public int getTimeToRespawn(){
        return this.timetorespawn;
    }
    public void setTimeToRespawn(int time){
        this.timetorespawn = time ;
    }

    public void respawn(int x, int y){
        this.x = x;
        this.y = y;
        this.timetorespawn = 0;
        this.state = true;
    }

}
