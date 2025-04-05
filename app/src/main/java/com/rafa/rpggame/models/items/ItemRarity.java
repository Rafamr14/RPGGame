package com.rafa.rpggame.models.items;

public enum ItemRarity {
    COMMON(1.0f),
    UNCOMMON(1.5f),
    RARE(2.0f),
    EPIC(3.0f),
    LEGENDARY(5.0f);

    private float statMultiplier;

    ItemRarity(float multiplier) {
        this.statMultiplier = multiplier;
    }

    public float getStatMultiplier() {
        return statMultiplier;
    }
}