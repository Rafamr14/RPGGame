package com.rafa.rpggame.game.combat;

public class CombatAction {
    private CombatActionType type;
    private Object source;
    private Object target;
    private String text;

    public CombatAction(CombatActionType type, Object source, Object target, String text) {
        this.type = type;
        this.source = source;
        this.target = target;
        this.text = text;
    }

    // Getters
    public CombatActionType getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public Object getTarget() {
        return target;
    }

    public String getText() {
        return text;
    }
}