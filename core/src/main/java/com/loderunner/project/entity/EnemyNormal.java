package com.loderunner.project.entity;

import com.loderunner.project.engine.Game;
import com.loderunner.project.map.Tiles;

public class EnemyNormal extends Enemy {

    private Direction directionActuelle; 
    private boolean enRechercheEchelle; 
    private int tick;

    public EnemyNormal(int x, int y) {
        super(x, y);
        this.directionActuelle = Direction.NONE;
        this.enRechercheEchelle = false; 
        this.tick =0;
    }

    public EnemyNormal(int x, int y, boolean free, int time, boolean state) {
        super(x, y);
        this.tick = 0;
        this.setFree(free);
        this.setState(state);
        this.setTimeToRespawn(time);
        this.directionActuelle = Direction.NONE;
        this.enRechercheEchelle = false; 
    }

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
        if (cible.getY() == this.y) {
            return Poursuivre(cible);
        }
        if (cible.getY() < this.y) {
            return Chercher_A_Monter(game);
        } else {
            return Chercher_A_Descendre(game);
        }
    }

    private Direction Poursuivre(Player cible) {
        this.enRechercheEchelle = false; 
        if (cible.getX() > this.x) {
            this.directionActuelle = Direction.RIGHT;
        } else if (cible.getX() < this.x) {
            this.directionActuelle = Direction.LEFT;
        }
        return this.directionActuelle;
    }

    private Direction Chercher_A_Monter(Game game) {
        if (Est_Sur_Une_Echelle(game)) {
            this.enRechercheEchelle = false; 
            return Direction.UP;
        }
        return Trouver_Echelle(game);
    }

    private Direction Chercher_A_Descendre(Game game) {
        if (Echelle_En_Dessous(game)) {
            this.enRechercheEchelle = false;
            return Direction.DOWN;
        }
        return Trouver_Echelle(game);
    }

    private Direction Trouver_Echelle(Game game) {
        if (this.enRechercheEchelle == false) {
            this.directionActuelle = Pile_Face();
            this.enRechercheEchelle = true; 
        }
        if (Obstacle_Devant(game) || Vide_Devant(game)) {
            Faire_Demi_Tour();
        }
        return this.directionActuelle;
    }

    private Direction Pile_Face() {
        if (Math.random() > 0.5) {
            return Direction.RIGHT;
        } else {
            return Direction.LEFT;
        }
    }

    private void Faire_Demi_Tour() {
        if (this.directionActuelle == Direction.RIGHT) {
            this.directionActuelle = Direction.LEFT;
        } else {
            this.directionActuelle = Direction.RIGHT;
        }
    }

    private boolean Est_Sur_Une_Echelle(Game game) {
        return game.getMaze().getTile(this.x, this.y).getType() == 2;
    }

    private boolean Echelle_En_Dessous(Game game) {
        if (this.y + 1 >= game.getMaze().getHeight()) {
            return false;
        }
        return game.getMaze().getTile(this.x, this.y + 1).getType() == 2;
    }

    private boolean Obstacle_Devant(Game game) {
        int caseDevantX = Next_Case_X();
        return game.isWall(caseDevantX, this.y);
    }

    private boolean Vide_Devant(Game game) {
        int caseDevantX = Next_Case_X();
        if (caseDevantX < 0 || caseDevantX >= game.getMaze().getWidth()) {
            return true; 
        }
        Tiles caseEnDessous = game.getMaze().getTile(caseDevantX, this.y + 1);
        boolean estDuVideNaturel = (caseEnDessous.getType() == 0);
        
        boolean echelleEnDessous = (caseEnDessous.getType() == 2);
        boolean echelleDevant = (game.getMaze().getTile(caseDevantX, this.y).getType() == 2);

        return estDuVideNaturel && !(echelleDevant) && !(echelleEnDessous);
    }

    private int Next_Case_X() {
        if (this.directionActuelle == Direction.RIGHT) {
            return this.x + 1;
        } else {
            return this.x - 1;
        }
    }

    private Player Target_Player(Game game) {
        if (game.getPlay().isEmpty()) {
            return null;
        }
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