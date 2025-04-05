// Archivo: Equipment.java
package com.rafa.rpggame.models.items;

import java.util.EnumMap;

public class Equipment {
    private EnumMap<EquipmentSlot, EquipableItem> equippedItems;

    public Equipment() {
        equippedItems = new EnumMap<>(EquipmentSlot.class);
    }

    public void equip(EquipableItem item) {
        equippedItems.put(item.getSlot(), item);
    }

    public void unequip(EquipmentSlot slot) {
        equippedItems.remove(slot);
    }

    public EquipableItem getItemInSlot(EquipmentSlot slot) {
        return equippedItems.get(slot);
    }

    public EnumMap<EquipmentSlot, EquipableItem> getAllEquippedItems() {
        return equippedItems;
    }
}