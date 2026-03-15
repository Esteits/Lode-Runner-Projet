package com.loderunner.project.entity;

public class EnemyPatrol extends Enemy {
    
    public enum AIState {
        PATROL, // Fait des allers retours ( patrouille )
        CHASE   // Fonce sur le joueur 
    }

    private AIState aiState;
    private Direction patrolDirection; // La direction de sa patrouille

    public EnemyPatrol(int x, int y) {
        super(x, y);
        this.aiState = AIState.PATROL; // Il commence toujours par patrouiller en allant a droite
        this.patrolDirection = Direction.RIGHT; 
    }

    public AIState getAiState() {
        return aiState;
    }

    public void setAiState(AIState aiState) {
        this.aiState = aiState;
    }

    public Direction getPatrolDirection() {
        return patrolDirection;
    }

    public void setPatrolDirection(Direction patrolDirection) {
        this.patrolDirection = patrolDirection;
    }
}