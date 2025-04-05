package com.rafa.rpggame.models.items;

import com.rafa.rpggame.models.character.Character;

public abstract class Item {
    protected long id;
    protected String name;
    protected String description;
    protected ItemRarity rarity;
    protected int value; // Valor monetario
    protected int requiredLevel;

    // Getters y Setters
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

    public ItemRarity getRarity() {
        return rarity;
    }

    public void setRarity(ItemRarity rarity) {
        this.rarity = rarity;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    // Método para usar el objeto
    public abstract boolean use(Character character);
}