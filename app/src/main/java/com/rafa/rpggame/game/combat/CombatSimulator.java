package com.rafa.rpggame.game.combat;

import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.character.CharacterRole;
import com.rafa.rpggame.models.character.Stat;
import com.rafa.rpggame.models.zones.Enemy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombatSimulator {
    private Character character;
    private List<Enemy> enemies;
    private Random random;
    private List<CombatAction> combatLog;
    private int turnCounter;

    public CombatSimulator(Character character, List<Enemy> enemies) {
        this.character = character;
        this.enemies = new ArrayList<>(enemies);
        this.random = new Random();
        this.combatLog = new ArrayList<>();
        this.turnCounter = 0;
    }

    public CombatResult simulate() {
        // Hacer una copia del HP inicial para restaurar después si es necesario
        int initialHp = character.getStats().get(Stat.CURRENT_HP);

        // Reiniciar el log de combate
        combatLog.clear();
        turnCounter = 0;

        // Empezar el combate
        boolean victory = false;
        int maxTurns = 30; // Límite para evitar combates infinitos

        // Registrar inicio del combate
        combatLog.add(new CombatAction(CombatActionType.COMBAT_START, null, null, "¡Combate iniciado!"));

        while (turnCounter < maxTurns) {
            turnCounter++;

            // Turno del personaje
            characterTurn();

            // Verificar si todos los enemigos están muertos
            if (areAllEnemiesDead()) {
                victory = true;
                combatLog.add(new CombatAction(CombatActionType.COMBAT_END, character, null, "¡Victoria!"));
                break;
            }

            // Turno de los enemigos
            enemiesTurn();

            // Verificar si el personaje está muerto
            if (!isCharacterAlive()) {
                combatLog.add(new CombatAction(CombatActionType.COMBAT_END, null, null, "Derrota"));
                break;
            }
        }

        // Si el combate alcanzó el límite de turnos, considerar derrota
        if (turnCounter >= maxTurns) {
            victory = false;
            combatLog.add(new CombatAction(CombatActionType.COMBAT_END, null, null,
                    "El combate ha durado demasiado y termina en derrota"));
        }

        // Calcular experiencia y recompensas
        int experienceGained = calculateExperience(victory);

        // Si es victoria, recuperar algo de HP
        if (victory) {
            int recoveryAmount = character.getStats().get(Stat.MAX_HP) / 4; // 25% del HP máximo
            int newHp = Math.min(character.getStats().get(Stat.MAX_HP),
                    character.getStats().get(Stat.CURRENT_HP) + recoveryAmount);
            character.getStats().put(Stat.CURRENT_HP, newHp);

            combatLog.add(new CombatAction(CombatActionType.HEAL, character, null,
                    "Recuperas " + recoveryAmount + " puntos de vida"));
        } else {
            // En derrota, dejar al personaje con 1 HP
            character.getStats().put(Stat.CURRENT_HP, 1);
            combatLog.add(new CombatAction(CombatActionType.STATUS, character, null,
                    "Has quedado gravemente herido con solo 1 punto de vida"));
        }

        return new CombatResult(victory, turnCounter, experienceGained, combatLog);
    }

    private void characterTurn() {
        combatLog.add(new CombatAction(CombatActionType.TURN_START, character, null,
                "Turno " + turnCounter + ": " + character.getName()));

        // Seleccionar enemigo objetivo (primero vivo)
        Enemy target = null;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                target = enemy;
                break;
            }
        }

        if (target != null) {
            // Calcular daño base
            int attackPower = calculateAttackPower(character);

            // Verificar si es un golpe crítico
            boolean isCritical = random.nextFloat() < calculateCriticalChance(character);

            // Aplicar modificador crítico si corresponde
            if (isCritical) {
                attackPower = (int)(attackPower * 1.5f);
            }

            // Calcular daño final con la defensa del enemigo
            int damage = calculateDamage(attackPower, target.getDefense());

            // Aplicar daño al enemigo
            target.takeDamage(damage);

            // Registrar la acción en el log
            String actionText = character.getName() + " ataca a " + target.getName() +
                    " causando " + damage + " puntos de daño";

            if (isCritical) {
                actionText += " (¡Golpe crítico!)";
            }

            if (!target.isAlive()) {
                actionText += " y lo derrota";
            }

            CombatActionType actionType = isCritical ? CombatActionType.CRITICAL_HIT : CombatActionType.ATTACK;
            combatLog.add(new CombatAction(actionType, character, target, actionText));
        } else {
            combatLog.add(new CombatAction(CombatActionType.STATUS, character, null,
                    "No hay enemigos para atacar"));
        }

        combatLog.add(new CombatAction(CombatActionType.TURN_END, character, null,
                "Fin del turno de " + character.getName()));
    }

    private void enemiesTurn() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                combatLog.add(new CombatAction(CombatActionType.TURN_START, enemy, null,
                        "Turno " + turnCounter + ": " + enemy.getName()));

                // Calcular daño base
                int attackPower = enemy.getAttackPower();

                // Verificar golpe crítico
                boolean isCritical = random.nextFloat() < 0.05f; // 5% para enemigos

                // Aplicar modificador crítico
                if (isCritical) {
                    attackPower = (int)(attackPower * 1.5f);
                }

                // Calcular daño final con la defensa del personaje
                int defense = calculateDefense(character);
                int damage = calculateDamage(attackPower, defense);

                // Aplicar daño al personaje
                int currentHp = character.getStats().get(Stat.CURRENT_HP);
                int newHp = Math.max(0, currentHp - damage);
                character.getStats().put(Stat.CURRENT_HP, newHp);

                // Registrar en el log
                String actionText = enemy.getName() + " ataca a " + character.getName() +
                        " causando " + damage + " puntos de daño";

                if (isCritical) {
                    actionText += " (¡Golpe crítico!)";
                }

                if (newHp <= 0) {
                    actionText += " y lo derrota";
                }

                CombatActionType actionType = isCritical ? CombatActionType.CRITICAL_HIT : CombatActionType.ATTACK;
                combatLog.add(new CombatAction(actionType, enemy, character, actionText));

                combatLog.add(new CombatAction(CombatActionType.TURN_END, enemy, null,
                        "Fin del turno de " + enemy.getName()));

                // Si el personaje muere, terminar los turnos de enemigos
                if (newHp <= 0) {
                    break;
                }
            }
        }
    }

    private int calculateAttackPower(Character character) {
        int strength = character.getStats().get(Stat.STRENGTH);
        int intelligence = character.getStats().get(Stat.INTELLIGENCE);

        // Dependiendo de la clase, usar diferentes estadísticas
        switch (character.getCharacterClass()) {
            case WARRIOR:
                return strength * 3 + character.getLevel() * 2;
            case ROGUE:
                return strength * 2 + character.getLevel() * 3;
            case MAGE:
                return intelligence * 3 + character.getLevel() * 2;
            case CLERIC:
                return intelligence * 2 + strength + character.getLevel() * 2;
            default:
                return strength + intelligence + character.getLevel() * 2;
        }
    }

    private int calculateDefense(Character character) {
        int defense = character.getStats().get(Stat.DEFENSE);

        // Los tanques reciben bonus de defensa
        if (character.getRole() == CharacterRole.TANK) {
            defense = (int)(defense * 1.2f); // +20% para tanques
        }

        return defense + character.getLevel() * 2;
    }

    private int calculateDamage(int attackPower, int defense) {
        // Fórmula base: cuanto mayor sea la defensa, menor será el daño
        float damageReduction = defense / (float)(defense + 50 + 5 * turnCounter);
        int baseDamage = Math.max(1, (int)(attackPower * (1f - damageReduction)));

        // Variación aleatoria (80%-120%)
        float randomFactor = 0.8f + random.nextFloat() * 0.4f;

        return Math.max(1, (int)(baseDamage * randomFactor));
    }

    private float calculateCriticalChance(Character character) {
        float baseCritical = 0.05f; // 5% base

        // Los pícaros tienen más probabilidad de crítico
        if (character.getCharacterClass() == CharacterClass.ROGUE) {
            baseCritical = 0.10f; // 10% base para pícaros
        }

        // Añadir bonus por agilidad
        float agilityBonus = character.getStats().get(Stat.AGILITY) * 0.002f; // 0.2% por punto

        return Math.min(0.5f, baseCritical + agilityBonus); // Máximo 50%
    }

    private boolean isCharacterAlive() {
        return character.getStats().get(Stat.CURRENT_HP) > 0;
    }

    private boolean areAllEnemiesDead() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private int calculateExperience(boolean victory) {
        if (!victory) {
            return 0; // No experiencia por derrota
        }

        int baseExp = 0;

        // Experiencia base por enemigo
        for (Enemy enemy : enemies) {
            baseExp += enemy.getLevel() * 10;
        }

        // Ajustar por diferencia de nivel
        float averageEnemyLevel = 0;
        for (Enemy enemy : enemies) {
            averageEnemyLevel += enemy.getLevel();
        }
        averageEnemyLevel /= enemies.size();

        int levelDifference = (int)averageEnemyLevel - character.getLevel();
        float levelMultiplier = 1.0f;

        if (levelDifference > 0) {
            // Bonus por enemigos de mayor nivel
            levelMultiplier = 1.0f + (levelDifference * 0.1f);
        } else if (levelDifference < 0) {
            // Penalización por enemigos de menor nivel
            levelMultiplier = Math.max(0.1f, 1.0f + (levelDifference * 0.1f));
        }

        return Math.max(1, (int)(baseExp * levelMultiplier));
    }
}