package com.rafa.rpggame.models.items;

import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.character.Stat;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class EquipableItem extends Item {
    private EquipmentSlot slot;
    private Set<CharacterClass> compatibleClasses;
    private EnumMap<Stat, Integer> statBonuses;

    public EquipableItem() {
        this.compatibleClasses = new HashSet<>();
        this.statBonuses = new EnumMap<>(Stat.class);
    }

    public boolean canBeEquippedBy(CharacterClass characterClass) {
        return compatibleClasses.contains(characterClass);
    }

    public EnumMap<Stat, Integer> getStatBonuses() {
        return statBonuses;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public void setSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public void addCompatibleClass(CharacterClass characterClass) {
        this.compatibleClasses.add(characterClass);
    }

    public void addStatBonus(Stat stat, int value) {
        this.statBonuses.put(stat, value);
    }

    @Override
    public boolean use(Character character) {
        try {
            character.equip(this);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}