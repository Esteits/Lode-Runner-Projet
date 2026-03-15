package com.loderunner.project.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.loderunner.project.entity.Character;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.entity.Enemy;
import com.loderunner.project.entity.EnemyArcade;
import com.loderunner.project.entity.EnemyPatrol;
import com.loderunner.project.entity.Player;
import com.loderunner.project.entity.Treasure;
import com.loderunner.project.map.Maze;
import com.loderunner.project.map.Tiles;

public class Game {
    private Maze maze;
    private List<Player> play;
    private List<Enemy> ene;
    private List<Treasure> tre;
    private int tick = 0; // Compteur de tours pour rendre les ennemies plus lents

    public Game(){  
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
    }

    public Game(int nbrTreasure){
        this.maze = Maze.generation();
        this.play = new ArrayList<>();
        this.ene = new ArrayList<>();
        this.tre = new ArrayList<>();
        
        // 4 ennemis Arcade répartis en bas du niveau 
        for(int i = 0 ; i < 4 ; i++){
            // Ils apparaissent en bas espacés les uns des autres 
            this.ene.add(new EnemyArcade(2 + (i * 2), this.maze.getHeight() - 2));
        }
        
        // 1 ennemi Patrouilleur au milieu en bas
        this.ene.add(new EnemyPatrol(this.maze.getWidth() / 2, this.maze.getHeight() - 2));
      

        while(tre.size()<nbrTreasure){
            Treasure t = new Treasure((int)(Math.random() * (this.maze.getWidth() - 1)) , (int)(Math.random() * (this.maze.getHeight() - 1)));
            if (maze.getTile(t.getX(), t.getY() + 1).getType()==1){
                boolean already = false;
                for(Treasure treas : this.tre){
                    if(treas.getX() == t.getX() && treas.getY() == t.getY()){
                        already = true;
                    }
                }
                if(!already){
                    this.tre.add(t);
                }
            }
        }
    }   

    public Maze getMaze(){
        return this.maze;
    }

    public List<Player> getPlay(){
        return this.play;
    }

    public List<Enemy> getEne(){
        return this.ene;
    }

    public List<Treasure> getTre(){
        return this.tre;
    }
    
    public void addPlayer(Player p){
        this.play.add(p);
    }

    public void addEnemy(Enemy e){
        this.ene.add(e);
    }

    public void addTreasure(Treasure t){
        this.tre.add(t);
    }
    
    public void sec(){
        moveAllEnemies();
        gravity();
        playerEnemyCol();
        playerTreasureCol();
        decrementEnemyTimer();
        decrementTileTimer();
        canEscape();
    }

    private boolean canMove(int targetX, int targetY, Enemy self) {
        int type = maze.getTile(targetX, targetY).getType();
        if (type == 1 || type == 3) return false;

        for (Enemy other : ene) {
            if (other != self && other.getState() && other.getX() == targetX && other.getY() == targetY) {
                return false;
            }
        }
        return true;
    }

    public boolean isPlayer(int x, int y, Player p){
        for (Player pl : play){
            if(pl != p && pl.getX() == x && pl.getY() == y){
                return true;
            }
        }
        return false;
    }

    public void movePlayerRight(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX()+1, player.getY()).getType()!=3 && maze.getTile(player.getX()+1, player.getY()).getType()!=1 && !isPlayer(player.getX()+1, player.getY(), player)){
            player.right();
        }
    }

    public void movePlayerLeft(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX()-1, player.getY()).getType()!=3 && maze.getTile(player.getX()-1, player.getY()).getType()!=1 && !isPlayer(player.getX()-1, player.getY(), player)){
            player.left();
        }
    }

    public void movePlayerUp(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX(), player.getY()).getType()==2 && !isPlayer(player.getX(), player.getY()-1, player)){
            if(player.getY()-1 != 0 || maze.getCanEscape()){
                player.up();
            }
        }
    }
    
    public void movePlayerDown(int ind){
        Player player = play.get(ind);
        if (maze.getTile(player.getX(), player.getY()+1).getType()==2 && !isPlayer(player.getX(), player.getY()+1, player)){
            player.down();
        }
    }

    public void moveAllEnemies() { 
        tick++;

        for (int i = 0; i < ene.size(); i++) {
            Enemy e = ene.get(i);

            if ((tick+i) % 2 == 0) { // Les ennemis passe 1 tour sur 2.
                continue; // On passe au prochain ennemi sans le faire bouger
            }
            moveEnemy(i); 
        }

    }

    public void moveEnemy(int ind) {
        Enemy e = this.ene.get(ind);
        if (!e.getState()) return; // Si l'ennemi est piégé ou mort, il ne bouge pas 

        if (e.getY() + 1 < maze.getHeight()) {
            Tiles tileUnder = maze.getTile(e.getX(), e.getY() + 1);
            if (tileUnder.getType() == 0 || (tileUnder.getType() == 1 && !tileUnder.getState())) {
                
                // l'echelle n'est pas consideré comme "tomber dans le vide"
                if (maze.getTile(e.getX(), e.getY()).getType() != 2) {
                    return; // Pas de déplacement horizontal possible en l'air.
                }
            }
        }

        Player p = this.play.get(0);

        if (e instanceof EnemyArcade) { // On vérifie l'identité de l'ennemi
            moveArcade((EnemyArcade) e, p);
        } else if (e instanceof EnemyPatrol) {
            movePatrol((EnemyPatrol) e, p);
        }
    }

    // --- NOTE A MOI MEME --- Empecher les ennemies de pouvoir sauter dans le vide naturel 
    private void moveArcade(EnemyArcade e, Player p) {
        // Si meme hauteur on le chasse
        if (e.getY() == p.getY()) {
            if (p.getX() > e.getX() && canMove(e.getX() + 1, e.getY(), e)) {
                e.right();
            } else if (p.getX() < e.getX() && canMove(e.getX() - 1, e.getY(), e)) {
                e.left();
            }
            return; // Fin du tour
        }

        // Si pas meme Y on cherche à prendre une échelle
        int currentTile = maze.getTile(e.getX(), e.getY()).getType();
        int tileBelow = maze.getTile(e.getX(), e.getY() + 1).getType();

        // echelle trouver on monte ( si le player est plus Haut )
        if (p.getY() < e.getY() && currentTile == 2 && canMove(e.getX(), e.getY() - 1, e)) {
            e.up();
            return;
        }
        // echelle trouver on descend ( si le player est plus Bas )
        else if (p.getY() > e.getY() && tileBelow == 2 && canMove(e.getX(), e.getY() + 1, e)) {
            e.down();
            return;
        }

        // Si pas d'echelle on balaye la zone
        if (e.getDirection() == Direction.RIGHT) {
            if (canMove(e.getX() + 1, e.getY(), e)) { // S'il peut aller à droite, il y va
                e.right();
            } else {
                // Sinon, il fait demi-tour pour chercher ailleurs
                e.setDirection(Direction.LEFT); 
            }
        } else { // S'il balaye vers la gauche
            if (canMove(e.getX() - 1, e.getY(), e)) {
                e.left();
            } else {
                // Mur ou ennemies à gauche, on repart à droite 
                e.setDirection(Direction.RIGHT); 
            }
        }
    }

private void movePatrol(EnemyPatrol e, Player p) {
        // Le patrouilleur voit il le joueur 
        boolean voitLeJoueur = false;
        if (e.getY() == p.getY()) {
            voitLeJoueur = true;
            int minX = Math.min(e.getX(), p.getX());
            int maxX = Math.max(e.getX(), p.getX());
            for (int x = minX + 1; x < maxX; x++) {
                if (maze.getTile(x, e.getY()).getType() == 1 || maze.getTile(x, e.getY()).getType() == 3) {
                    voitLeJoueur = false;  // Un mur bloque la vue ?
                    break; 
                }
            }
        }

        // On met à jour son état chasseur / patrouilleur
        if (voitLeJoueur) {
            e.setAiState(EnemyPatrol.AIState.CHASE);
        } else {
            e.setAiState(EnemyPatrol.AIState.PATROL);
        }

        if (e.getAiState() == EnemyPatrol.AIState.CHASE) {
           // Mode Chasse, il fonce sur le joueur
            if (p.getX() > e.getX()) {
                int typeDroite = maze.getTile(e.getX() + 1, e.getY()).getType();
                if (typeDroite != 1 && typeDroite != 3) e.right();
            } else if (p.getX() < e.getX()) {
                int typeGauche = maze.getTile(e.getX() - 1, e.getY()).getType();
                if (typeGauche != 1 && typeGauche != 3) e.left();
            }
        } else {
            // S'il voit un mur, il fait demi-tour
            int nextX = (e.getPatrolDirection() == Direction.RIGHT) ? e.getX() + 1 : e.getX() - 1;
            boolean faceDanger = false;

            // Sortie de la carte ?
            if (nextX <= 0 || nextX >= maze.getWidth() - 1) {
                faceDanger = true;
            } else {
                // Mur devant lui ?
                int typeDevant = maze.getTile(nextX, e.getY()).getType();
                if (typeDevant == 1 || typeDevant == 3) {
                    faceDanger = true;
                } else {
                    // Vide sous ses pieds ?
                    Tiles tuileEnBas = maze.getTile(nextX, e.getY() + 1);
    
                    // Ffait demi-tour que si c'est le vide naturel de la map 
                    if (tuileEnBas.getType() == 0) {
                        faceDanger = true;
                    }
                }
            }

            // S'il y a un danger (mur ou trou), on inverse juste sa direction
            if (faceDanger) {
                if (e.getPatrolDirection() == Direction.RIGHT) e.setPatrolDirection(Direction.LEFT);
                else e.setPatrolDirection(Direction.RIGHT);
            } else {
                // S'il n'y a pas de danger, il avance sereinement
                if (e.getPatrolDirection() == Direction.RIGHT) e.right();
                else e.left();
            }
        }
    }

    public void dig(int ind){
        Player player = play.get(ind);
        int digX = player.getX() ;
        int digY = player.getY()+1;
        if (player.getDirection() == Direction.RIGHT){
            digX++;
        }else{
            digX--;
        }
        Tiles tile = maze.getTile(digX, digY);
        if(tile.getType()==1 && digX != maze.getWidth()-1 && digX != 0){
            tile.setState(false);
            tile.setRespawn(500);
        }
    }

    public void fall(Character c){
        if(c.getY()+1 >= maze.getHeight()){
            return;
        }

        for (Enemy e: ene){
            if(c.getX() == e.getX() && c.getY() == e.getY()-1 && e.getState()==false){
                return;
            }
        }

        Tiles tileUnder = maze.getTile(c.getX(), c.getY()+1);
        if((tileUnder.getType()==0 || (tileUnder.getType()==1 && tileUnder.getState()==false))){
            if (c instanceof Player){
                Player p = (Player) c;
                if(isPlayer(p.getX(), p.getY()+1, p)){
                    return;
                }
            }
            c.down();
        }

        if (c instanceof Enemy){
            Enemy e = (Enemy) c;
            if(maze.getTile(c.getX(), c.getY()).getType()==1 && maze.getTile(c.getX(), c.getY()).getState()==false){
                e.setState(false);
                e.setTimeToRespawn(250);
            }
        }
    }

    public void gravity(){
        for(Player p : play){
            if(!p.playerDead()){
                fall(p);
            }
        }
        for(Enemy e : ene){
            if(e.getState()){
                fall(e);
            }    
        }
    }

    public void decrementEnemyTimer(){
        for(Enemy e : ene){
            if(!e.getState()){
                e.setTimeToRespawn(e.getTimeToRespawn()-1);
                if(e.getTimeToRespawn() <= 0){
                    e.respawn(maze.getExit(), 0);
                    e.setState(true);
                }
            }
        }
    }

    public void kill(int x, int y){
        for(Player p : play){
            if (p.getX()==x && p.getY()==y){
                p.loseHp();
                if(!p.playerDead()){
                    p.respawn(maze.getExit(), 1);
                }
            }
        }

        for(Enemy e : ene){
            if (e.getX()==x && e.getY()==y){
                e.setState(true);
                e.respawn(maze.getExit(), 0);
            }
        }
    }

    public void decrementTileTimer(){
        for(int y = 0 ; y < maze.getHeight() ; y++){
            for(int x = 0 ; x < maze.getWidth() ; x++){
                Tiles t = maze.getTile(x, y);
                if(!t.getState()){
                    t.setRespawn(t.getRespawn()-1);
                    if(t.getRespawn()<=0){
                        t.setState(true);
                        kill(x, y);
                    }
                }
            }
        }
    }

    public void playerEnemyCol(){
        for(Player p : play){
            if (!p.playerDead()){
                for (Enemy e: ene){
                    if (e.getState()){
                        if (p.getX()==e.getX() && p.getY()==e.getY()){
                            p.loseHp();
                            if(!p.playerDead()){
                                p.respawn(maze.getExit(), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void playerTreasureCol(){
        for(Player p : play){
            if(!p.playerDead()){
                for (Treasure t: tre){
                    if (!t.getCollect()){
                        if (p.getX()==t.getX() && p.getY()==t.getY()){
                            t.setCollect(true);
                        }
                    }
                }
            }
        }
    }

    public void canEscape(){
        for(Treasure t : tre){
            if(!t.getCollect()){
                return;
            }
        }
        maze.setCanEscape(true);
    }

    public boolean win(){
        for(Player p : play){
            if(p.getX() == maze.getExit() && p.getY() == 0 && !p.playerDead()){
                return true;
            }
        }
        return false;
    }

    public boolean gameOver(){
        for(Player p : play){
            if(!p.playerDead()){
                return false;
            }
        }
        return true;
    }

    public void saveToFile(){
        try (BufferedWriter b = new BufferedWriter(new FileWriter("level.txt"))){
            b.write(maze.getWidth() + " " + maze.getHeight() + " " + maze.getExit());
            b.newLine();

            for(int y = 0 ; y < maze.getHeight() ; y++){
                for(int x =  0 ; x < maze.getWidth() ; x++){

                    int type = maze.getTile(x, y).getType();

                    switch(type){
                        case 0 :
                            b.write(' ');
                            break ;

                        case 1:
                            b.write('#');
                            break;

                        case 2:
                            b.write('H');
                            break;
                        
                        case 3:
                            b.write('=');
                            break;

                        default:
                            b.write('?');
                    }
                }
                b.newLine();
            }

            b.write("Play");
            b.newLine();
            for (Player p: play){
                b.write(p.getX() + " " + p.getY() + " " + p.getHp());
                b.newLine();
            }

            b.write("Ene");
            b.newLine();
            for (Enemy e: ene){
                b.write(e.getX() + " " + e.getY() + " " + e.getState() + " " + e.getTimeToRespawn());
                b.newLine();
            }

            b.write("Tre");
            b.newLine();
            for(Treasure t: tre){
                b.write(t.getX() + " " + t.getY() + " " + t.getCollect());
                b.newLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadFromFile(){
        try (BufferedReader b = new BufferedReader(new FileReader("level.txt"))){
            String[] dims = b.readLine().split(" ");
            this.maze = new Maze(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
            for(int y = 0 ; y < maze.getHeight() ; y++){
                String line = b.readLine();
                for(int x = 0 ; x < maze.getWidth() ; x++){
                    int type;
                    char c = line.charAt(x);
                        switch (c) {
                            case ' ':
                                type = 0;
                                break;

                            case '#':
                                type = 1;
                                break;

                            case 'H':
                                type = 2;
                                break;

                            case '=':
                                type = 3 ;
                                break;

                            default:
                                type = 0 ;
                        }
                    maze.getTile(x, y).setType(type);
                }
            }

            b.readLine();
            this.play = new ArrayList<>();
            String line = b.readLine();
            while(!line.contentEquals("Ene")){
                String[] caractereP = line.split(" ");
                Player p = new Player(Integer.parseInt(caractereP[0]), Integer.parseInt(caractereP[1]));
                p.setHp(Integer.parseInt(caractereP[2]));
                play.add(p);
                line = b.readLine();
            }

            this.ene = new ArrayList<>();
            line = b.readLine();
            while(!line.contentEquals("Tre")){
                String[] caractereE = line.split(" ");
                Enemy e = new Enemy(Integer.parseInt(caractereE[0]), Integer.parseInt(caractereE[1]));
                e.setState(Boolean.parseBoolean(caractereE[2]));
                e.setTimeToRespawn(Integer.parseInt(caractereE[3]));
                ene.add(e);
                line = b.readLine();
            }

            this.tre = new ArrayList<>();
            line = b.readLine();
            while(line != null){
                String[] caractereT = line.split(" ");
                Treasure t = new Treasure(Integer.parseInt(caractereT[0]), Integer.parseInt(caractereT[1]));
                t.setCollect(Boolean.parseBoolean(caractereT[2]));
                tre.add(t);
                line = b.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
