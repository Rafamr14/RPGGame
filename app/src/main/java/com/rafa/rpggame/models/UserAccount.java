package com.rafa.rpggame.models;

import android.util.Log;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo simplificado de cuenta de usuario
 */
public class UserAccount {
    private static final String TAG = "UserAccount";

    // Constantes
    private static final int STAMINA_REGEN_MINUTES = 10;

    // Datos básicos de la cuenta
    private long id;
    private String username;
    private int accountLevel;
    private int experience;

    // Recursos
    private int stamina;
    private int maxStamina;
    private int coins;
    private int gems;

    // Personajes
    private List<Character> characters;
    private Character selectedCharacter;

    // Otros
    private List<Item> mailbox;
    private long lastStaminaRegenTime;

    /**
     * Constructor que inicializa una cuenta con valores por defecto
     */
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
    }

    /**
     * Actualiza la stamina según el tiempo transcurrido
     */
    public void updateStamina() {
        try {
            long currentTime = System.currentTimeMillis();
            long elapsedMinutes = (currentTime - lastStaminaRegenTime) / (1000 * 60);

            if (elapsedMinutes >= STAMINA_REGEN_MINUTES) {
                int staminaToRegen = (int) (elapsedMinutes / STAMINA_REGEN_MINUTES);
                stamina = Math.min(maxStamina, stamina + staminaToRegen);
                lastStaminaRegenTime = currentTime - (elapsedMinutes % STAMINA_REGEN_MINUTES) * (1000 * 60);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al actualizar stamina", e);
        }
    }

    /**
     * Reduce la stamina en la cantidad especificada
     */
    public void reduceStamina(int amount) {
        try {
            updateStamina(); // Actualizar primero
            stamina = Math.max(0, stamina - amount);
        } catch (Exception e) {
            Log.e(TAG, "Error al reducir stamina", e);
        }
    }

    /**
     * Añade experiencia y verifica subida de nivel
     */
    public void addExperience(int amount) {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error al añadir experiencia", e);
        }
    }

    /**
     * Calcula la experiencia requerida para un nivel
     */
    private int calculateRequiredExp(int level) {
        return level * 100;
    }

    /**
     * Método mejorado para añadir un personaje a la cuenta
     */
    public void addCharacter(Character character) {
        if (character == null) {
            Log.e(TAG, "Error: Intento de añadir un personaje nulo");
            return;
        }

        Log.d(TAG, "Añadiendo personaje: " + character.getName() + " (ID: " + character.getId() + ")");

        // Asegurarse de que la lista de personajes existe
        if (this.characters == null) {
            this.characters = new ArrayList<>();
            Log.d(TAG, "Lista de personajes creada");
        }

        // Verificar si el personaje ya existe (por ID)
        boolean exists = false;
        for (Character existingChar : this.characters) {
            if (existingChar.getId() == character.getId()) {
                exists = true;
                Log.d(TAG, "El personaje ya existe en la cuenta (mismo ID)");
                break;
            }
        }

        // Verificar por nombre, clase y rol (redundante, pero por seguridad)
        if (!exists) {
            for (Character existingChar : this.characters) {
                if (existingChar.getName().equals(character.getName()) &&
                        existingChar.getCharacterClass() == character.getCharacterClass() &&
                        existingChar.getRole() == character.getRole()) {

                    exists = true;
                    Log.d(TAG, "El personaje ya existe en la cuenta (mismo nombre/clase/rol)");
                    break;
                }
            }
        }

        // Añadir el personaje si no existe
        if (!exists) {
            this.characters.add(character);
            Log.d(TAG, "Personaje añadido. Total personajes: " + this.characters.size());
            for (Character c : this.characters) {
                Log.d(TAG, "Personaje en cuenta: " + c.getName() + " (ID: " + c.getId() + ")");
            }

            // Si no hay personaje seleccionado, seleccionar este automáticamente
            if (selectedCharacter == null) {
                selectedCharacter = character;
                Log.d(TAG, "Personaje seleccionado automáticamente: " + character.getName());
            }
        }
    }

    /**
     * Método para obtener un personaje por su ID
     */
    public Character getCharacterById(long id) {
        if (characters == null) {
            return null;
        }
        for (Character character : characters) {
            if (character.getId() == id) {
                return character;
            }
        }
        return null;
    }

    /**
     * Obtiene el número máximo de personajes permitidos
     */
    public int getMaxCharacters() {
        // El número máximo de personajes aumenta con el nivel de cuenta
        return 1 + (accountLevel / 10);
    }

    /**
     * Añade un item al buzón
     */
    public void addToMailbox(Item item) {
        if (mailbox == null) {
            mailbox = new ArrayList<>();
        }
        mailbox.add(item);
    }

    /**
     * Elimina un item del buzón
     */
    public boolean removeFromMailbox(Item item) {
        if (mailbox == null) {
            return false;
        }
        return mailbox.remove(item);
    }

    /**
     * Añade monedas
     */
    public void addCoins(int amount) {
        this.coins += amount;
    }

    /**
     * Reduce monedas si hay suficientes
     */
    public boolean reduceCoins(int amount) {
        if (coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }

    /**
     * Añade gemas
     */
    public void addGems(int amount) {
        this.gems += amount;
    }

    /**
     * Reduce gemas si hay suficientes
     */
    public boolean reduceGems(int amount) {
        if (gems < amount) {
            return false;
        }
        gems -= amount;
        return true;
    }

    // Getters y Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(int accountLevel) {
        this.accountLevel = accountLevel;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public List<Character> getCharacters() {
        if (characters == null) {
            characters = new ArrayList<>();
        }
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public Character getSelectedCharacter() {
        // Si no hay personaje seleccionado pero hay personajes disponibles, seleccionar el primero
        if (selectedCharacter == null && characters != null && !characters.isEmpty()) {
            selectedCharacter = characters.get(0);
            Log.d(TAG, "Auto-seleccionado el primer personaje: " + selectedCharacter.getName());
        }
        return selectedCharacter;
    }

    public void setSelectedCharacter(Character selectedCharacter) {
        if (selectedCharacter != null) {
            Log.d(TAG, "Estableciendo personaje seleccionado: " + selectedCharacter.getName());
            this.selectedCharacter = selectedCharacter;
        } else {
            Log.w(TAG, "Intento de establecer un personaje seleccionado nulo");
        }
    }

    public List<Item> getMailbox() {
        if (mailbox == null) {
            mailbox = new ArrayList<>();
        }
        return mailbox;
    }

    public void setMailbox(List<Item> mailbox) {
        this.mailbox = mailbox;
    }

    public long getLastStaminaRegenTime() {
        return lastStaminaRegenTime;
    }

    public void setLastStaminaRegenTime(long lastStaminaRegenTime) {
        this.lastStaminaRegenTime = lastStaminaRegenTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
