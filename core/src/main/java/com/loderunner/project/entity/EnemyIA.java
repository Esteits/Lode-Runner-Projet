package com.loderunner.project.entity;

import java.util.LinkedList;
import java.util.Queue;

import com.loderunner.project.engine.Game;

/**
 * Ennemi avec intelligence artificielle.
 * 
 * Utilise un algorithme de recherche en largeur (BFS)
 * pour trouver le chemin le plus court vers le joueur.
 */

public class EnemyIA extends Enemy{

    private int tick;

    public EnemyIA(int x, int y) {
        super(x, y);
        this.tick = 0;
    }

    public EnemyIA(int x, int y, boolean free, int time, boolean state) {
        super(x, y);
        this.tick = 0;
        this.setFree(free);
        this.setState(state);
        this.setTimeToRespawn(time);
    }

    /**
     * Détermine le mouvement de l'ennemi.
     * 
     * - ralentit les déplacements avec un tick
     * - cible le joueur le plus proche
     * - utilise un BFS pour trouver le chemin optimal
     */

    @Override
    public Direction mouvement(Game game) {
        
        this.tick += 3; 
        if (this.tick < 10) {
            return Direction.NONE; 
        }
        this.tick -= 10;

        Player cible = Target_Player(game);
        if (cible == null || cible.playerDead()) {
            return Direction.NONE;
        }

        return Plus_Court_Chemin(game, cible);
    }

    private Direction Plus_Court_Chemin(Game game, Player cible) {
        int largeur = game.getMaze().getWidth();
        int hauteur = game.getMaze().getHeight(); 
        boolean[][] visite = new boolean[largeur][hauteur];
        Queue<NoeudChemin> file = new LinkedList<>();
        file.add(new NoeudChemin(this.x, this.y, Direction.NONE));
        visite[this.x][this.y] = true;

        while (!file.isEmpty()) {
            NoeudChemin courant = file.poll();

            if (courant.x == cible.getX() && courant.y == cible.getY()) {
                return courant.premiereDirection; // On renvoie le tout premier pas à faire
            }
            boolean estStable = false;
            int typeActuel = game.getMaze().getTile(courant.x, courant.y).getType();
            
            if (typeActuel == 2) { 
                estStable = true;
            } 
            else if (courant.y + 1 < hauteur) {
                int typeEnDessous = game.getMaze().getTile(courant.x, courant.y + 1).getType();
                if (typeEnDessous == 1 || typeEnDessous == 3 || typeEnDessous == 2) {
                    estStable = true;
                }
            }
            if (courant.y + 1 < hauteur && !game.isWall(courant.x, courant.y + 1)) {
                Ajouter_Noeud(file, visite, courant, courant.x, courant.y + 1, Direction.DOWN);
            }
            if (!estStable) {
                continue; 
            }
            if (typeActuel == 2 && courant.y - 1 >= 0 && !game.isWall(courant.x, courant.y - 1)) {
                Ajouter_Noeud(file, visite, courant, courant.x, courant.y - 1, Direction.UP);
            }
            if (courant.x + 1 < largeur && !game.isWall(courant.x + 1, courant.y)) {
                Ajouter_Noeud(file, visite, courant, courant.x + 1, courant.y, Direction.RIGHT);
            }
            if (courant.x - 1 >= 0 && !game.isWall(courant.x - 1, courant.y)) {
                Ajouter_Noeud(file, visite, courant, courant.x - 1, courant.y, Direction.LEFT);
            }
        }
        return Direction.NONE;
    }
    private void Ajouter_Noeud(Queue<NoeudChemin> file, boolean[][] visite, NoeudChemin parent, int testX, int testY, Direction dir) {
        if (!visite[testX][testY]) {
            visite[testX][testY] = true;
            
            // on mémorise la direction direction du tout premier mouvement
            Direction dirInitiale = (parent.premiereDirection == Direction.NONE) ? dir : parent.premiereDirection;
            file.add(new NoeudChemin(testX, testY, dirInitiale));
        }
    }

    private class NoeudChemin {
        int x, y;
        Direction premiereDirection;

        public NoeudChemin(int x, int y, Direction premiereDirection) {
            this.x = x;
            this.y = y;
            this.premiereDirection = premiereDirection;
        }
    }

    private Player Target_Player(Game game) {
        if(game.getPlay().isEmpty()) return null;

        Player target = game.getPlay().get(0);
        int distanceTar = Math.abs(this.x - target.getX()) + Math.abs(this.y - target.getY());
        
        for(int i = 1 ; i < game.getPlay().size() ; i++){
            Player p = game.getPlay().get(i);
            if (!p.playerDead()) {
                int newDist = Math.abs(this.x - p.getX()) + Math.abs(this.y - p.getY());
                if(newDist < distanceTar){
                    distanceTar = newDist;
                    target = p;
                }
            }
        }
        return target;
    }
}