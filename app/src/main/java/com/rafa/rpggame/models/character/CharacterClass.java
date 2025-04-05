package com.rafa.rpggame.models.character;

public enum CharacterClass {
    WARRIOR(2, 1, 0.5f, 0.2f),
    MAGE(0.2f, 0.5f, 1, 2),
    ROGUE(1, 0.5f, 2, 0.5f),
    CLERIC(0.5f, 1.5f, 0.5f, 1.5f);

    private float strengthPerLevel;
    private float defensePerLevel;
    private float agilityPerLevel;
    private float intelligencePerLevel;

    CharacterClass(float str, float def, float agi, float intel) {
        this.strengthPerLevel = str;
        this.defensePerLevel = def;
        this.agilityPerLevel = agi;
        this.intelligencePerLevel = intel;
    }

    public float getStrengthPerLevel() {
        return strengthPerLevel;
    }

    public float getDefensePerLevel() {
        return defensePerLevel;
    }

    public float getAgilityPerLevel() {
        return agilityPerLevel;
    }

    public float getIntelligencePerLevel() {
        return intelligencePerLevel;
    }
}