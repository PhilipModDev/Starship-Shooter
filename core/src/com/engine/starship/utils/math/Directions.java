package com.engine.starship.utils.math;

public enum Directions {
    UP(1),
    DOWN(-1),
    LEFT(-1),
    RIGHT(1);
   public final int value;
    Directions(int value) {
        this.value = value;
    }
}
