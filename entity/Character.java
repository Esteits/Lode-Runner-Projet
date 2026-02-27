package entity;

public abstract class Character extends Entity{
    protected Direction dir;
    protected boolean state; //true = normal false = pieger

    public enum Direction {
    LEFT,
    RIGHT
    }

    public Character(int x, int y){
        super(x, y);
        this.dir = Direction.RIGHT;
        this.state = true ;
    }

    public boolean getState(){
        return this.state;
    }
    public void setState(boolean s){
        this.state = s;
    }

    public Direction getDirection(){
        return this.dir;
    }
    public void setDirection(Direction d){
        this.dir = d;
    }

    public void right(){
        this.x++;
        this.dir = Direction.RIGHT;
    }

    public void left(){
        this.x--;
        this.dir = Direction.LEFT;
    }

    public void up(){
        this.y--;
    }

    public void down(){
            this.y++;
    }

    public abstract void respawn(int x, int y);
}
