package com.rafa.rpggame.models.character;

import com.rafa.rpggame.models.items.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character {
    private long id;
    private String name;
    private CharacterClass characterClass;
    private CharacterRole role;
    private int level;
    private int experience;
    private HashMap<Stat, Integer> stats;
    private Equipment equipment;
    private List<Item> inventory;
    private int maxInventorySize;

    public Character(String name, CharacterClass characterClass, CharacterRole role) {
        this.id = System.currentTimeMillis(); // Generar ID por defecto
        this.name = name;
        this.characterClass = characterClass;
        this.role = role;
        this.level = 1;
        this.experience = 0;
        this.stats = initStats();
        this.equipment = new Equipment();
        this.inventory = new ArrayList<>();
        this.maxInventorySize = 20;
    }
    private HashMap<Stat, Integer> initStats() {
        HashMap<Stat, Integer> baseStats = new HashMap<>();

        // Inicializar estadísticas base según la clase
        switch (characterClass) {
            case WARRIOR:
                baseStats.put(Stat.STRENGTH, 10);
                baseStats.put(Stat.DEFENSE, 8);
                baseStats.put(Stat.AGILITY, 5);
                baseStats.put(Stat.INTELLIGENCE, 3);
                break;
            case MAGE:
                baseStats.put(Stat.STRENGTH, 3);
                baseStats.put(Stat.DEFENSE, 4);
                baseStats.put(Stat.AGILITY, 6);
                baseStats.put(Stat.INTELLIGENCE, 12);
                break;
            case ROGUE:
                baseStats.put(Stat.STRENGTH, 7);
                baseStats.put(Stat.DEFENSE, 5);
                baseStats.put(Stat.AGILITY, 12);
                baseStats.put(Stat.INTELLIGENCE, 6);
                break;
            case CLERIC:
                baseStats.put(Stat.STRENGTH, 6);
                baseStats.put(Stat.DEFENSE, 7);
                baseStats.put(Stat.AGILITY, 4);
                baseStats.put(Stat.INTELLIGENCE, 10);
                break;
        }

        // Modificar según el rol
        if (role == CharacterRole.TANK) {
            baseStats.put(Stat.DEFENSE, baseStats.get(Stat.DEFENSE) + 2);
        } else if (role == CharacterRole.DPS) {
            baseStats.put(Stat.STRENGTH, baseStats.get(Stat.STRENGTH) + 2);
        }

        // Añadir estadísticas derivadas
        baseStats.put(Stat.MAX_HP, calculateMaxHp(baseStats));
        baseStats.put(Stat.CURRENT_HP, baseStats.get(Stat.MAX_HP));

        return baseStats;
    }

    private int calculateMaxHp(HashMap<Stat, Integer> stats) {
        return 100 + (stats.get(Stat.DEFENSE) * 5) + (stats.get(Stat.STRENGTH) * 2) + (level * 10);
    }

    public void levelUp() {
        level++;

        // Incrementar estadísticas base con conversión explícita a int
        stats.put(Stat.STRENGTH, stats.get(Stat.STRENGTH) + (int)characterClass.getStrengthPerLevel());
        stats.put(Stat.DEFENSE, stats.get(Stat.DEFENSE) + (int)characterClass.getDefensePerLevel());
        stats.put(Stat.AGILITY, stats.get(Stat.AGILITY) + (int)characterClass.getAgilityPerLevel());
        stats.put(Stat.INTELLIGENCE, stats.get(Stat.INTELLIGENCE) + (int)characterClass.getIntelligencePerLevel());

        // Recalcular HP máximo
        stats.put(Stat.MAX_HP, calculateMaxHp(stats));
        stats.put(Stat.CURRENT_HP, stats.get(Stat.MAX_HP)); // Curar al subir de nivel
    }

    public boolean addToInventory(Item item) {
        if (inventory.size() >= maxInventorySize) {
            return false;
        }
        inventory.add(item);
        return true;
    }

    public void equip(EquipableItem item) {
        if (item.canBeEquippedBy(characterClass)) {
            equipment.equip(item);
            inventory.remove(item);
            // Actualizar estadísticas según el equipo
            updateStatsFromEquipment();
        }
    }

    public void unequip(EquipmentSlot slot) {
        EquipableItem item = equipment.getItemInSlot(slot);
        if (item != null && inventory.size() < maxInventorySize) {
            equipment.unequip(slot);
            addToInventory(item);
            // Actualizar estadísticas
            updateStatsFromEquipment();
        }
    }

    private void updateStatsFromEquipment() {
        // Resetear a las estadísticas base
        stats = initStats();

        // Aplicar bonificaciones del equipo
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            EquipableItem item = equipment.getItemInSlot(slot);
            if (item != null) {
                for (Map.Entry<Stat, Integer> bonus : item.getStatBonuses().entrySet()) {
                    stats.put(bonus.getKey(), stats.get(bonus.getKey()) + bonus.getValue());
                }
            }
        }

        // Actualizar HP y otros valores derivados
        int newMaxHp = calculateMaxHp(stats);
        int currentHp = stats.get(Stat.CURRENT_HP);
        float healthPercentage = (float) currentHp / stats.get(Stat.MAX_HP);
        stats.put(Stat.MAX_HP, newMaxHp);
        stats.put(Stat.CURRENT_HP, Math.min(currentHp, (int) (newMaxHp * healthPercentage)));
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public CharacterRole getRole() {
        return role;
    }

    public HashMap<Stat, Integer> getStats() {
        return stats;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addExperience(int amount) {
        experience += amount;
        int expForNextLevel = calculateExpForNextLevel();

        while (experience >= expForNextLevel) {
            levelUp();
            expForNextLevel = calculateExpForNextLevel();
        }
    }

    private int calculateExpForNextLevel() {
        return level * 100;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
        

    public void setName(String name) {
        this.name = name;
    }


    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }


    public void setRole(CharacterRole role) {
        this.role = role;
    }


    public void setLevel(int level) {
        this.level = level;
    }


    public void setStats(HashMap<Stat, Integer> stats) {
        this.stats = stats;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }
}