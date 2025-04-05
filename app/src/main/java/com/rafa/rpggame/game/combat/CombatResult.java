package com.rafa.rpggame.game.combat;

import java.util.List;

public class CombatResult {
    private boolean victory;
    private int turns;
    private int experienceGained;
    private List<CombatAction> combatLog;

    public CombatResult(boolean victory, int turns, int experienceGained, List<CombatAction> combatLog) {
        this.victory = victory;
        this.turns = turns;
        this.experienceGained = experienceGained;
        this.combatLog = combatLog;
    }

    // Constructor simple para casos donde no se necesita el log completo
    public CombatResult(boolean victory, int turns, int experienceGained) {
        this.victory = victory;
        this.turns = turns;
        this.experienceGained = experienceGained;
        this.combatLog = null;
    }

    // Getters
    public boolean isVictory() {
        return victory;
    }

    public int getTurns() {
        return turns;
    }

    public int getExperienceGained() {
        return experienceGained;
    }

    public List<CombatAction> getCombatLog() {
        return combatLog;
    }
}