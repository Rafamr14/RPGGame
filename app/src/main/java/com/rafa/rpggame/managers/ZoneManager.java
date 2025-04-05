package com.rafa.rpggame.managers;

import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.character.Stat;
import com.rafa.rpggame.models.items.ConsumableItem;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.EquipmentSlot;
import com.rafa.rpggame.models.items.Item;
import com.rafa.rpggame.models.items.ItemRarity;
import com.rafa.rpggame.models.zones.Enemy;
import com.rafa.rpggame.models.zones.Zone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZoneManager {
    private static List<Zone> allZones;
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }

        allZones = new ArrayList<>();

        // Crear zonas básicas
        createStarterZones();
        createMidLevelZones();
        createHighLevelZones();

        initialized = true;
    }

    private static void createStarterZones() {
        // Zona 1: Bosque de los Novatos
        Zone zone1 = new Zone();
        zone1.setId(1);
        zone1.setName("Bosque de los Novatos");
        zone1.setDescription("Un bosque tranquilo ideal para aventureros principiantes.");
        zone1.setMinLevel(1);
        zone1.setMaxLevel(5);
        zone1.setStaminaCost(10);

        // Configurar enemigos posibles
        List<Enemy> enemies1 = new ArrayList<>();
        enemies1.add(createEnemy("Lobo Pequeño", 1, 3));
        enemies1.add(createEnemy("Bandido Novato", 1, 4));
        enemies1.add(createEnemy("Araña del Bosque", 2, 3));
        zone1.setPossibleEnemies(enemies1);

        // Configurar drops posibles
        HashMap<Item, Float> drops1 = new HashMap<>();
        drops1.put(createConsumableItem("Poción Pequeña", "Restaura 20 puntos de salud", ItemRarity.COMMON, 10), 0.4f);
        drops1.put(createEquipableItem("Espada de Hierro", "Una espada básica", ItemRarity.COMMON, 20, EquipmentSlot.MAIN_HAND), 0.2f);
        drops1.put(createEquipableItem("Túnica de Tela", "Protección ligera", ItemRarity.COMMON, 15, EquipmentSlot.CHEST), 0.2f);
        zone1.setPossibleDrops(drops1);

        allZones.add(zone1);

        // Zona 2: Cavernas Oscuras
        Zone zone2 = new Zone();
        zone2.setId(2);
        zone2.setName("Cavernas Oscuras");
        zone2.setDescription("Cavernas húmedas llenas de criaturas extrañas.");
        zone2.setMinLevel(3);
        zone2.setMaxLevel(8);
        zone2.setStaminaCost(15);

        // Configurar enemigos posibles
        List<Enemy> enemies2 = new ArrayList<>();
        enemies2.add(createEnemy("Murciélago Gigante", 3, 5));
        enemies2.add(createEnemy("Goblin Minero", 4, 6));
        enemies2.add(createEnemy("Slime de Piedra", 5, 8));
        zone2.setPossibleEnemies(enemies2);

        // Configurar drops posibles
        HashMap<Item, Float> drops2 = new HashMap<>();
        drops2.put(createConsumableItem("Poción Media", "Restaura 50 puntos de salud", ItemRarity.COMMON, 25), 0.4f);
        drops2.put(createEquipableItem("Daga Afilada", "Una daga con filo envenenado", ItemRarity.UNCOMMON, 40, EquipmentSlot.MAIN_HAND), 0.15f);
        drops2.put(createEquipableItem("Botas de Explorador", "Botas resistentes", ItemRarity.UNCOMMON, 35, EquipmentSlot.FEET), 0.15f);
        drops2.put(createConsumableItem("Antorcha", "Ilumina zonas oscuras", ItemRarity.COMMON, 5), 0.3f);
        zone2.setPossibleDrops(drops2);

        allZones.add(zone2);
    }

    private static void createMidLevelZones() {
        // Zona 3: Pantano Putrefacto
        Zone zone3 = new Zone();
        zone3.setId(3);
        zone3.setName("Pantano Putrefacto");
        zone3.setDescription("Un pantano plagado de criaturas no-muertas y enfermedades.");
        zone3.setMinLevel(8);
        zone3.setMaxLevel(15);
        zone3.setStaminaCost(20);

        // Configurar enemigos posibles
        List<Enemy> enemies3 = new ArrayList<>();
        enemies3.add(createEnemy("Zombi Pantanoso", 8, 12));
        enemies3.add(createEnemy("Lagarto Venenoso", 10, 13));
        enemies3.add(createEnemy("Espíritu de Agua", 12, 15));
        zone3.setPossibleEnemies(enemies3);

        // Configurar drops posibles
        HashMap<Item, Float> drops3 = new HashMap<>();
        drops3.put(createConsumableItem("Poción Grande", "Restaura 100 puntos de salud", ItemRarity.UNCOMMON, 50), 0.3f);
        drops3.put(createEquipableItem("Bastón de Agua", "Canaliza el poder del agua", ItemRarity.RARE, 80, EquipmentSlot.MAIN_HAND), 0.1f);
        drops3.put(createEquipableItem("Amuleto del Pantano", "Protege contra venenos", ItemRarity.RARE, 75, EquipmentSlot.NECK), 0.1f);
        drops3.put(createConsumableItem("Antídoto", "Cura efectos de veneno", ItemRarity.UNCOMMON, 30), 0.2f);
        zone3.setPossibleDrops(drops3);

        allZones.add(zone3);

        // Zona 4: Ruinas Antiguas
        Zone zone4 = new Zone();
        zone4.setId(4);
        zone4.setName("Ruinas Antiguas");
        zone4.setDescription("Restos de una civilización antigua llena de tesoros y peligros.");
        zone4.setMinLevel(12);
        zone4.setMaxLevel(20);
        zone4.setStaminaCost(25);

        // Configurar enemigos posibles
        List<Enemy> enemies4 = new ArrayList<>();
        enemies4.add(createEnemy("Guardián de Piedra", 12, 18));
        enemies4.add(createEnemy("Esqueleto Antiguo", 15, 17));
        enemies4.add(createEnemy("Mago Fantasma", 18, 20));
        zone4.setPossibleEnemies(enemies4);

        // Configurar drops posibles
        HashMap<Item, Float> drops4 = new HashMap<>();
        drops4.put(createConsumableItem("Poción de Maná", "Restaura 100 puntos de maná", ItemRarity.UNCOMMON, 55), 0.3f);
        drops4.put(createEquipableItem("Espada Rúnica", "Grabada con runas antiguas", ItemRarity.RARE, 120, EquipmentSlot.MAIN_HAND), 0.08f);
        drops4.put(createEquipableItem("Corona Ancestral", "Una reliquia de gran poder", ItemRarity.EPIC, 200, EquipmentSlot.HEAD), 0.03f);
        drops4.put(createConsumableItem("Pergamino de Identificación", "Identifica objetos mágicos", ItemRarity.RARE, 100), 0.1f);
        zone4.setPossibleDrops(drops4);

        allZones.add(zone4);
    }

    private static void createHighLevelZones() {
        // Zona 5: Montaña de Fuego
        Zone zone5 = new Zone();
        zone5.setId(5);
        zone5.setName("Montaña de Fuego");
        zone5.setDescription("Una montaña volcánica con ríos de lava y criaturas ígneas.");
        zone5.setMinLevel(20);
        zone5.setMaxLevel(30);
        zone5.setStaminaCost(30);

        // Configurar enemigos posibles
        List<Enemy> enemies5 = new ArrayList<>();
        enemies5.add(createEnemy("Elemental de Fuego", 20, 25));
        enemies5.add(createEnemy("Salamandra Ígnea", 22, 28));
        enemies5.add(createEnemy("Dragón Joven", 28, 30));
        zone5.setPossibleEnemies(enemies5);

        // Configurar drops posibles
        HashMap<Item, Float> drops5 = new HashMap<>();
        drops5.put(createConsumableItem("Poción de Resistencia al Fuego", "Otorga inmunidad temporal al fuego", ItemRarity.RARE, 150), 0.2f);
        drops5.put(createEquipableItem("Espada de Llamas", "Una espada envuelta en llamas eternas", ItemRarity.EPIC, 300, EquipmentSlot.MAIN_HAND), 0.05f);
        drops5.put(createEquipableItem("Armadura de Escamas de Dragón", "Forjada con escamas de dragón", ItemRarity.EPIC, 350, EquipmentSlot.CHEST), 0.04f);
        drops5.put(createConsumableItem("Cristal de Fuego", "Un raro cristal con propiedades mágicas", ItemRarity.RARE, 200), 0.1f);
        zone5.setPossibleDrops(drops5);

        allZones.add(zone5);

        // Zona 6: Abismo Oscuro (zona de nivel máximo)
        Zone zone6 = new Zone();
        zone6.setId(6);
        zone6.setName("Abismo Oscuro");
        zone6.setDescription("El lugar más peligroso del mundo, donde habitan las criaturas más terribles.");
        zone6.setMinLevel(30);
        zone6.setMaxLevel(40);
        zone6.setStaminaCost(50);

        // Configurar enemigos posibles
        List<Enemy> enemies6 = new ArrayList<>();
        enemies6.add(createEnemy("Demonio del Abismo", 30, 35));
        enemies6.add(createEnemy("Horror Tentacular", 33, 38));
        enemies6.add(createEnemy("Señor de la Oscuridad", 38, 40));
        zone6.setPossibleEnemies(enemies6);

        // Configurar drops posibles
        HashMap<Item, Float> drops6 = new HashMap<>();
        drops6.put(createConsumableItem("Elixir de Inmortalidad", "Revive automáticamente al caer en combate", ItemRarity.EPIC, 500), 0.1f);
        drops6.put(createEquipableItem("Guadaña del Vacío", "Arma legendaria que drena la vida", ItemRarity.LEGENDARY, 1000, EquipmentSlot.MAIN_HAND), 0.01f);
        drops6.put(createEquipableItem("Corona del Rey Olvidado", "Corona del antiguo rey del abismo", ItemRarity.LEGENDARY, 1200, EquipmentSlot.HEAD), 0.01f);
        drops6.put(createConsumableItem("Piedra del Alma", "Contiene el alma de un ser poderoso", ItemRarity.EPIC, 400), 0.05f);
        zone6.setPossibleDrops(drops6);

        allZones.add(zone6);
    }

    // Métodos auxiliares para crear enemigos y objetos
    private static Enemy createEnemy(String name, int minLevel, int maxLevel) {
        Enemy enemy = new Enemy();
        enemy.setName(name);
        enemy.setBaseLevel(minLevel);

        // Configurar estadísticas básicas según el nivel
        HashMap<Stat, Integer> stats = new HashMap<>();
        stats.put(Stat.STRENGTH, minLevel * 2);
        stats.put(Stat.DEFENSE, minLevel * 2);
        stats.put(Stat.MAX_HP, minLevel * 20);
        stats.put(Stat.ATTACK_POWER, minLevel * 3);

        enemy.setStats(stats);

        return enemy;
    }

    private static ConsumableItem createConsumableItem(String name, String description, ItemRarity rarity, int value) {
        ConsumableItem item = new ConsumableItem();
        item.setName(name);
        item.setDescription(description);
        item.setRarity(rarity);
        item.setValue(value);
        item.setRequiredLevel(calculateRequiredLevel(rarity));

        // Configurar efectos
        if (name.contains("Poción")) {
            int healAmount = 0;
            if (name.contains("Pequeña")) healAmount = 20;
            else if (name.contains("Media")) healAmount = 50;
            else if (name.contains("Grande")) healAmount = 100;

            item.addStatEffect(Stat.CURRENT_HP, healAmount);
        }

        return item;
    }

    private static EquipableItem createEquipableItem(String name, String description, ItemRarity rarity, int value, EquipmentSlot slot) {
        EquipableItem item = new EquipableItem();
        item.setName(name);
        item.setDescription(description);
        item.setRarity(rarity);
        item.setValue(value);
        item.setSlot(slot);
        item.setRequiredLevel(calculateRequiredLevel(rarity));

        // Configurar clases compatibles (simplificado)
        if (name.contains("Espada") || name.contains("Armadura")) {
            item.addCompatibleClass(CharacterClass.WARRIOR);
        }

        if (name.contains("Daga") || name.contains("Arco")) {
            item.addCompatibleClass(CharacterClass.ROGUE);
        }

        if (name.contains("Bastón") || name.contains("Túnica")) {
            item.addCompatibleClass(CharacterClass.MAGE);
            item.addCompatibleClass(CharacterClass.CLERIC);
        }

        // Si no hay clases específicas, hacerlo compatible con todas
        if (item.getStatBonuses().isEmpty()) {
            for (CharacterClass characterClass : CharacterClass.values()) {
                item.addCompatibleClass(characterClass);
            }
        }

        // Añadir bonificaciones de stats según rareza y tipo
        int statBonus = (int)(5 * rarity.getStatMultiplier());

        if (slot == EquipmentSlot.MAIN_HAND) {
            // Arma
            item.addStatBonus(Stat.STRENGTH, statBonus);
            if (name.contains("Bastón") || name.contains("Vara")) {
                item.addStatBonus(Stat.INTELLIGENCE, statBonus * 2);
            }
        } else if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS) {
            // Armadura
            item.addStatBonus(Stat.DEFENSE, statBonus);
            if (name.contains("Túnica") || name.contains("Manto")) {
                item.addStatBonus(Stat.INTELLIGENCE, statBonus / 2);
            }
        }

        return item;
    }

    private static int calculateRequiredLevel(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 1;
            case UNCOMMON:
                return 5;
            case RARE:
                return 15;
            case EPIC:
                return 25;
            case LEGENDARY:
                return 35;
            default:
                return 1;
        }
    }

    public static List<Zone> getAvailableZones(int playerLevel) {
        if (!initialized) {
            initialize();
        }

        List<Zone> availableZones = new ArrayList<>();

        for (Zone zone : allZones) {
            if (playerLevel >= zone.getMinLevel()) {
                availableZones.add(zone);
            }
        }

        return availableZones;
    }

    public static Zone getZoneById(long id) {
        if (!initialized) {
            initialize();
        }

        for (Zone zone : allZones) {
            if (zone.getId() == id) {
                return zone;
            }
        }

        return null;
    }
}