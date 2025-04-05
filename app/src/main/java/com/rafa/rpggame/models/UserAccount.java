package com.rafa.rpggame.models;

import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.character.CharacterRole;
import com.rafa.rpggame.models.items.Item;
import java.util.ArrayList;
import java.util.List;
import com.rafa.rpggame.models.items.Equipment;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.ConsumableItem;
import com.rafa.rpggame.models.items.Item;
import com.rafa.rpggame.models.character.Character;
import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private long id;
    private String username;
    private int accountLevel;
    private int experience;
    private int stamina;
    private int maxStamina;
    private int coins;
    private int gems;
    private List<Character> characters;
    private Character selectedCharacter;
    private List<Item> mailbox;
    private long lastStaminaRegenTime;
    private static final int STAMINA_REGEN_MINUTES = 10; // Un punto cada 10 minutos

    // --- Nuevas líneas añadidas ---
    private Equipment equipment; // Equipo general para la cuenta
    // ------------------------------

    public UserAccount(String username) {
        this.username = username;
        this.accountLevel = 1;
        this.experience = 0;
        this.maxStamina = 100;
        this.stamina = maxStamina;
        this.coins = 1000;
        this.gems = 50;
        this.characters = new ArrayList<>();
        this.mailbox = new ArrayList<>();
        this.lastStaminaRegenTime = System.currentTimeMillis();

        // --- Inicializar el equipo general ---
        this.equipment = new Equipment();
        // --------------------------------------
    }

    public void updateStamina() {
        long currentTime = System.currentTimeMillis();
        long elapsedMinutes = (currentTime - lastStaminaRegenTime) / (1000 * 60);

        if (elapsedMinutes >= STAMINA_REGEN_MINUTES) {
            int staminaToRegen = (int) (elapsedMinutes / STAMINA_REGEN_MINUTES);
            stamina = Math.min(maxStamina, stamina + staminaToRegen);
            lastStaminaRegenTime = currentTime - (elapsedMinutes % STAMINA_REGEN_MINUTES) * (1000 * 60);
        }
    }

    public void reduceStamina(int amount) {
        updateStamina(); // Actualizar primero
        stamina = Math.max(0, stamina - amount);
    }

    public void addExperience(int amount) {
        experience += amount;

        // Verificar subida de nivel
        int expRequiredForNextLevel = calculateRequiredExp(accountLevel + 1);

        while (experience >= expRequiredForNextLevel) {
            accountLevel++;
            maxStamina += 5; // Incremento de stamina máxima
            stamina = maxStamina; // Rellenar stamina al subir de nivel

            // Calcular exp para el siguiente nivel
            expRequiredForNextLevel = calculateRequiredExp(accountLevel + 1);
        }
    }

    private int calculateRequiredExp(int level) {
        return level * 100;
    }

    // En UserAccount.java
    public void addCharacter(Character character) {
        if (this.characters == null) {
            this.characters = new ArrayList<>();
        }
        this.characters.add(character);
    }

    public int getMaxCharacters() {
        // El número máximo de personajes aumenta con el nivel de cuenta
        return 1 + (accountLevel / 10);
    }

    public void addToMailbox(Item item) {
        mailbox.add(item);
    }

    public boolean removeFromMailbox(Item item) {
        return mailbox.remove(item);
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public boolean reduceCoins(int amount) {
        if (coins < amount) {
            return false;
        }

        coins -= amount;
        return true;
    }

    public void addGems(int amount) {
        this.gems += amount;
    }

    public boolean reduceGems(int amount) {
        if (gems < amount) {
            return false;
        }

        gems -= amount;
        return true;
    }

    // --- Nuevos métodos para acceder al equipo ---
    public Equipment getEquipment() {
        return equipment;
    }
    // ----------------------------------------------

    // Método para usar ítems
    public boolean useItem(Item item) {
        if (item instanceof EquipableItem) {
            EquipableItem equipItem = (EquipableItem) item;
            // Guardar el ítem anterior (si existe)
            EquipableItem oldItem = equipment.getItemInSlot(equipItem.getSlot());

            // Equipar el nuevo ítem
            equipment.equip(equipItem);

            // Remover el ítem del inventario
            if (selectedCharacter != null) {
                selectedCharacter.getInventory().remove(item);
            }

            // Si había un ítem anterior, añadirlo al inventario
            if (oldItem != null && selectedCharacter != null) {
                selectedCharacter.addToInventory(oldItem);
            }

            return true;
        } else if (item instanceof ConsumableItem) {
            // Usar ítem consumible
            if (selectedCharacter != null && item.use(selectedCharacter)) {
                selectedCharacter.getInventory().remove(item);
                return true;
            }
        }
        return false;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public int getAccountLevel() {
        return accountLevel;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getCoins() {
        return coins;
    }

    public int getGems() {
        return gems;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    public void setSelectedCharacter(Character selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
    }

    public List<Item> getMailbox() {
        return mailbox;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
