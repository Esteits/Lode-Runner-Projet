package entity;

public class Player extends Character {
    private int hp ;
    private boolean canescape;


    public Player(int x, int y){
        super(x, y);
        this.hp = 5;
        this.canescape=true;
    }

    public int getHp(){
        return this.hp;
    }
    public void setHp(int h){
        this.hp=h;
    }
    
    public boolean getCanEscape(){
        return this.canescape;
    }
    public void setCanEscape(boolean escape){
        this.canescape=escape;
    }

    public boolean playerDead(){
        return this.hp<=0;
    }

    public void loseHp(){
        this.hp--;
    }

    public void respawn(int x, int y){
        this.x = x;
        this.y = y;
        this.state = true;
    }
}