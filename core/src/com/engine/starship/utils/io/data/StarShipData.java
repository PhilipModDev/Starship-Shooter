package com.engine.starship.utils.io.data;

public class StarShipData {
    public int maxAttackDelay = 20;
    public int speed;
    public final String name;
    public final boolean isAnimated;
    public boolean isEquip;
    public boolean unlock;

    public StarShipData(int speed, String name, boolean isAnimated) {
        this.speed = speed;
        this.name = name;
        this.isAnimated = isAnimated;
    }
}
