package com.rafa.rpggame.models.zones;

import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Zone {
    private long id;
    private String name;
    private String description;
    private int minLevel;
    private int maxLevel;
    private List<Enemy> possibleEnemies;
    private HashMap<Item, Float> possibleDrops; // Item -> Probabilidad
    private int staminaCost;

    public Zone() {
        this.possibleEnemies = new ArrayList<>();
        this.possibleDrops = new HashMap<>();
    }

    // Getters y Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(int staminaCost) {
        this.staminaCost = staminaCost;
    }

    public List<Enemy> getPossibleEnemies() {
        return possibleEnemies;
    }

    public void setPossibleEnemies(List<Enemy> possibleEnemies) {
        this.possibleEnemies = possibleEnemies;
    }

    public HashMap<Item, Float> getPossibleDrops() {
        return possibleDrops;
    }

    public void setPossibleDrops(HashMap<Item, Float> possibleDrops) {
        this.possibleDrops = possibleDrops;
    }

    public Enemy getRandomEnemy() {
        if (possibleEnemies == null || possibleEnemies.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(possibleEnemies.size());
        return possibleEnemies.get(index).clone(); // Clonar para no modificar el original
    }

    public Exploration startExploration(Character character, UserAccount account) {
        if (character.getLevel() < minLevel) {
            return null; // Nivel insuficiente
        }

        if (account.getStamina() < staminaCost) {
            return null; // Stamina insuficiente
        }

        account.reduceStamina(staminaCost);

        // Crear una nueva exploración
        Exploration exploration = new Exploration(this, character);
        exploration.startCombat();

        return exploration;
    }

    public List<Item> generateDrops() {
        List<Item> drops = new ArrayList<>();
        Random random = new Random();

        for (Map.Entry<Item, Float> possibleDrop : possibleDrops.entrySet()) {
            if (random.nextFloat() <= possibleDrop.getValue()) {
                drops.add(possibleDrop.getKey());
            }
        }

        return drops;
    }
}