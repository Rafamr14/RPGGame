package com.rafa.rpggame.models.zones;

import com.rafa.rpggame.game.combat.CombatResult;
import com.rafa.rpggame.game.combat.CombatSimulator;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.Stat;
import com.rafa.rpggame.models.items.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Exploration {
    private Zone zone;
    private Character character;
    private List<Enemy> enemies;
    private List<Item> drops;
    private ExplorationType explorationType;
    private ExplorationStatus status;
    private int experienceGained;

    public Exploration(Zone zone, Character character) {
        this.zone = zone;
        this.character = character;
        this.enemies = generateEnemies();
        this.drops = new ArrayList<>();
        this.status = ExplorationStatus.STARTED;
        this.explorationType = ExplorationType.NORMAL;
    }

    private List<Enemy> generateEnemies() {
        // Generar enemigos según la zona
        List<Enemy> generatedEnemies = new ArrayList<>();
        Random random = new Random();
        int enemyCount = random.nextInt(3) + 1; // 1-3 enemigos

        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = zone.getRandomEnemy();
            if (enemy != null) {
                // Ajustar nivel del enemigo
                enemy.adjustLevel(zone.getMinLevel(), zone.getMaxLevel());
                generatedEnemies.add(enemy);
            }
        }

        return generatedEnemies;
    }

    public void startCombat() {
        status = ExplorationStatus.IN_COMBAT;
        // Iniciar combate automático
        CombatSimulator combat = new CombatSimulator(character, enemies);
        CombatResult result = combat.simulate();

        if (result.isVictory()) {
            status = ExplorationStatus.VICTORY;
            handleVictory(result);
        } else {
            status = ExplorationStatus.DEFEAT;
            handleDefeat();
        }
    }

    private void handleVictory(CombatResult result) {
        // Guardar experiencia ganada
        experienceGained = result.getExperienceGained();

        // Otorgar experiencia
        character.addExperience(experienceGained);

        // Generar drops
        drops = zone.generateDrops();

        // Añadir drops al inventario
        for (Item item : drops) {
            if (!character.addToInventory(item)) {
                // Inventario lleno
                break;
            }
        }
    }

    private void handleDefeat() {
        // Penalización por derrota
        character.getStats().put(Stat.CURRENT_HP, 1); // Reducir HP a 1
        experienceGained = 0;
    }

    // Getters

    public Zone getZone() {
        return zone;
    }

    public Character getCharacter() {
        return character;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Item> getDrops() {
        return drops;
    }

    public ExplorationStatus getStatus() {
        return status;
    }

    public int getExperienceGained() {
        return experienceGained;
    }

    public ExplorationType getExplorationType() {
        return explorationType;
    }

    public void setExplorationType(ExplorationType explorationType) {
        this.explorationType = explorationType;
    }
}