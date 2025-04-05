package com.rafa.rpggame.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.rafa.rpggame.database.converters.CharacterListConverter;
import com.rafa.rpggame.database.converters.ItemListConverter;
import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.items.Item;
import java.util.List;

@Entity(tableName = "user_accounts")
@TypeConverters({CharacterListConverter.class, ItemListConverter.class})
public class UserEntity {
    @PrimaryKey
    private long id;
    private String username;
    private int accountLevel;
    private int experience;
    private int stamina;
    private int maxStamina;
    private int coins;
    private int gems;
    private List<Character> characters;
    private long selectedCharacterId;
    private List<Item> mailbox;
    private long lastStaminaRegenTime;

    // Constructor
    public UserEntity(long id, String username, int accountLevel, int experience, int stamina, int maxStamina,
                      int coins, int gems, List<Character> characters, long selectedCharacterId,
                      List<Item> mailbox, long lastStaminaRegenTime) {
        this.id = id;
        this.username = username;
        this.accountLevel = accountLevel;
        this.experience = experience;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.coins = coins;
        this.gems = gems;
        this.characters = characters;
        this.selectedCharacterId = selectedCharacterId;
        this.mailbox = mailbox;
        this.lastStaminaRegenTime = lastStaminaRegenTime;
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getAccountLevel() { return accountLevel; }
    public void setAccountLevel(int accountLevel) { this.accountLevel = accountLevel; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }

    public int getMaxStamina() { return maxStamina; }
    public void setMaxStamina(int maxStamina) { this.maxStamina = maxStamina; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }

    public List<Character> getCharacters() { return characters; }
    public void setCharacters(List<Character> characters) { this.characters = characters; }

    public long getSelectedCharacterId() { return selectedCharacterId; }
    public void setSelectedCharacterId(long selectedCharacterId) { this.selectedCharacterId = selectedCharacterId; }

    public List<Item> getMailbox() { return mailbox; }
    public void setMailbox(List<Item> mailbox) { this.mailbox = mailbox; }

    public long getLastStaminaRegenTime() { return lastStaminaRegenTime; }
    public void setLastStaminaRegenTime(long lastStaminaRegenTime) { this.lastStaminaRegenTime = lastStaminaRegenTime; }
}