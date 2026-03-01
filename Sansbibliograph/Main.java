import entity.*;

public class Main {

    public static void main(String[] args) {
        //for(int i = 0 ; i<10 ; i++){
            System.out.println();
            System.out.println("Creation");
            Game g = new Game();
            Enemy e = new Enemy(10, 10);
            Enemy e1 = new Enemy(10, 13);
            g.addEnemy(e);
            g.addEnemy(e1);
            Player p = new Player(10, 1);
            Player p1 = new Player(20, 14);
            g.addPlayer(p);
            g.addPlayer(p1);
            Treasure t = new Treasure(10, 10);
            Treasure t1 = new Treasure(10, 13);
            g.addTreasure(t);
            g.addTreasure(t1);
            g.saveToFile();
            g.loadFromFile();
        //}
    }
}