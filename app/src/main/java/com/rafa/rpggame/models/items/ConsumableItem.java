package com.rafa.rpggame.models.items;

import com.rafa.rpggame.models.character.Character;
import com.rafa.rpggame.models.character.Stat;
import java.util.EnumMap;
import java.util.Map;

public class ConsumableItem extends Item {
    private EnumMap<Stat, Integer> statEffects;
    private boolean isTemporary;
    private int duration; // En turnos si es temporal

    public ConsumableItem() {
        this.statEffects = new EnumMap<>(Stat.class);
        this.isTemporary = false;
        this.duration = 0;
    }

    public void addStatEffect(Stat stat, int value) {
        this.statEffects.put(stat, value);
    }

    public void setTemporary(boolean temporary) {
        isTemporary = temporary;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean use(Character character) {
        // Aplicar efectos al personaje
        for (Map.Entry<Stat, Integer> effect : statEffects.entrySet()) {
            Stat stat = effect.getKey();
            int value = effect.getValue();

            if (stat == Stat.CURRENT_HP) {
                // Restaurar HP
                int currentHp = character.getStats().get(Stat.CURRENT_HP);
                int maxHp = character.getStats().get(Stat.MAX_HP);
                int newHp = Math.min(maxHp, currentHp + value);
                character.getStats().put(Stat.CURRENT_HP, newHp);
            } else if (!isTemporary) {
                // Aplicar efecto permanente (ejemplo: aumentar estadística)
                int currentValue = character.getStats().get(stat);
                character.getStats().put(stat, currentValue + value);
            } else {
                // Aplicar efecto temporal (se implementaría en sistema de efectos)
                // Esto requeriría un sistema adicional para rastrear efectos temporales
            }
        }
        return true;
    }
}