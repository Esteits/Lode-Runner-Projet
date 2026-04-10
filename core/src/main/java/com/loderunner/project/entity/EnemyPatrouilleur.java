package com.loderunner.project.entity;

import java.io.Serializable;

import com.loderunner.project.engine.Game;
import com.loderunner.project.entity.Character.Direction;
import com.loderunner.project.map.Tiles;

public class EnemyPatrouilleur extends Enemy implements Serializable {

    public enum Etat { PATROUILLE, POURSUIT }

    private Etat etat;
    private Direction directionActuelle; 
    private int tick;

    public EnemyPatrouilleur(int x, int y) {
        super(x, y);
        this.setState(true);
        this.etat = Etat.PATROUILLE;
        this.directionActuelle = Direction.RIGHT; 
        this.tick = 0;
    }
    
    public EnemyPatrouilleur(int x, int y, boolean free, int time, boolean state) {
        super(x, y);
        this.tick = 0;
        this.setFree(free);
        this.setState(state);
        this.setTimeToRespawn(time);
        this.etat = Etat.PATROUILLE;
        this.directionActuelle = Direction.RIGHT;  
    }

    @Override
    public Direction mouvement(Game game) {   
        this.tick += 3;
        if (this.tick < 10) {
            return Direction.NONE; 
        }
        this.tick -= 10;

        Player cible = Search_Player(game);
        if (cible != null) {
            Mode_Poursuite(cible);
        } else {
            Mode_Patrouille(game);
        }
        return this.directionActuelle; 
    }

    private void Mode_Poursuite(Player cible) {
        this.etat = Etat.POURSUIT;
        if (cible.getX() > this.x) {
            this.directionActuelle = Direction.RIGHT;
        } else if (cible.getX() < this.x) {
            this.directionActuelle = Direction.LEFT;
        }
    }

    private void Mode_Patrouille(Game game) {
        this.etat = Etat.PATROUILLE;
        if (obstacle(game) || videDevant(game)) {
            Demi_Tour();
        }
    }

    private void Demi_Tour() {
        if (this.directionActuelle == Direction.RIGHT) {
            this.directionActuelle = Direction.LEFT;
        } else {
            this.directionActuelle = Direction.RIGHT;
        }
    }

    private boolean obstacle(Game game) {
        int caseDevantX = Next_Case_X();
        return game.isWall(caseDevantX, this.y);
    }

    private boolean videDevant(Game game) {
        int caseDevantX = Next_Case_X();
        if (caseDevantX < 0 || caseDevantX >= game.getMaze().getWidth()) { // bord de map 
            return true; 
        }
        Tiles caseEnDessous = game.getMaze().getTile(caseDevantX, this.y + 1);
        boolean estDuVide = (caseEnDessous.getType() == 0);
        boolean estUneEchelle = (game.getMaze().getTile(caseDevantX, this.y).getType() == 2);
        return estDuVide && !(estUneEchelle);
    }

    private int Next_Case_X() {
        if (this.directionActuelle == Direction.RIGHT) {
            return this.x + 1;
        } else {
            return this.x - 1;
        }
    }

    private Player Search_Player(Game game) {
        for (Player joueur : game.getPlay()) {
            if (joueur.getY() == this.y && !joueur.playerDead()) {
                if (aucunMurEntre(this.x, joueur.getX(), this.y, game)) {
                    return joueur;
                }
            }
        }
        return null;
    }

    private boolean aucunMurEntre(int x1, int x2, int y, Game game) {
        int debut = Math.min(x1, x2);
        int fin = Math.max(x1, x2);
        for (int x = debut; x <= fin; x++) {
            if (game.isWall(x, y)) {
                return false;
            }
        }
        return true;
    }

    public Etat getPEtat() { return etat; }
    public void setPEtat(Etat etat) { this.etat = etat; }
    public Direction getPDirection() { return directionActuelle; }
    public void setPDirection(Direction directionActuelle) { this.directionActuelle = directionActuelle; }
}