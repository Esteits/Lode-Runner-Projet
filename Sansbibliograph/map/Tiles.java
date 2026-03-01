package map;

public class Tiles {
    private int type;
    private int x;
    private int y;
    private boolean state; //true =  visible false = invisible
    private int respawn ;

    public Tiles(int t, int x, int y){
        this.type = t;
        this.x = x;
        this.y = y;
        this.state = true;
        this.respawn = 0 ;
    }

    public int getType(){
        return this.type;
    }
    public void setType(int t){
        this.type = t;
    }

    public int getX(){
        return this.x;
    }
    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }
    public void setY(int y){
        this.y = y;
    }

    public boolean getState(){
        return this.state;
    }
    public void setState(boolean s){
        if (this.type==1){
            this.state = s;
        }
    }

    public int getRespawn(){
        return this.respawn;
    }
    public void setRespawn(int time){
        this.respawn = time;
    }
}