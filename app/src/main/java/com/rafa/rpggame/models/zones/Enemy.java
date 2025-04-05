package com.rafa.rpggame.models.zones;

import com.rafa.rpggame.models.character.Stat;
import java.util.HashMap;
import java.util.Random;

public class Enemy {
    private String name;
    private int baseLevel;
    private int currentLevel;
    private HashMap<Stat, Integer> stats;
    private int currentHp;
    private int maxHp;
    private int attackPower;
    private int defense;

    public Enemy() {
        this.stats = new HashMap<>();
    }

    public Enemy clone() {
        Enemy clone = new Enemy();
        clone.name = this.name;
        clone.baseLevel = this.baseLevel;
        clone.currentLevel = this.currentLevel;
        clone.stats = new HashMap<>(this.stats);
        clone.currentHp = this.currentHp;
        clone.maxHp = this.maxHp;
        clone.attackPower = this.attackPower;
        clone.defense = this.defense;
        return clone;
    }

    public void adjustLevel(int minLevel, int maxLevel) {
        // Ajustar nivel del enemigo en el rango dado
        Random random = new Random();
        this.currentLevel = minLevel + random.nextInt(maxLevel - minLevel + 1);

        // Ajustar estadísticas basadas en el nivel
        float levelMultiplier = (float)currentLevel / baseLevel;

        // Ajustar HP
        this.maxHp = (int)(baseStats(Stat.MAX_HP) * levelMultiplier);
        this.currentHp = this.maxHp;

        // Ajustar ataque y defensa
        this.attackPower = (int)(baseStats(Stat.ATTACK_POWER) * levelMultiplier);
        this.defense = (int)(baseStats(Stat.DEFENSE) * levelMultiplier);

        // Actualizar stats
        for (Stat stat : stats.keySet()) {
            stats.put(stat, (int)(baseStats(stat) * levelMultiplier));
        }
    }

    private int baseStats(Stat stat) {
        Integer value = stats.get(stat);
        return value != null ? value : 0;
    }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    // Getters y Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return currentLevel;
    }

    public int getBaseLevel() {
        return baseLevel;
    }

    public void setBaseLevel(int baseLevel) {
        this.baseLevel = baseLevel;
        this.currentLevel = baseLevel;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public HashMap<Stat, Integer> getStats() {
        return stats;
    }

    public void setStats(HashMap<Stat, Integer> stats) {
        this.stats = stats;
    }
}