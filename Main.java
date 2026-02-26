import map.Maze;

public class Main {

    public static void main(String[] args) {
        //for(int i = 0 ; i<10 ; i++){
            System.out.println();
            System.out.println("Creation");
            Maze maze = Maze.generation();
            maze.saveToFile();
            Maze maze2 = new Maze(maze.getWidth(), maze.getHeight(), maze.getExit());
            maze2.loadFromFile();
            for (int y = 0; y < maze2.getHeight(); y++) {
                for (int x = 0; x < maze2.getWidth(); x++) {
                    int type = maze2.getTile(x, y).getType();
                    switch (type) {
                        case 0: System.out.print(' '); break;
                        case 1: System.out.print('#'); break;
                        case 2: System.out.print('H'); break;
                        case 3: System.out.print('='); break;
                    }
                }
                System.out.println();
            }
        //}
    }
}