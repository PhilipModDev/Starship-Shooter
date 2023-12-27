package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements GameObject {
    //The health state.
    public int health;
    //The damage state.
    public int damage;
    //The living state.
    boolean isLiving;
    public final Vector2 position = new Vector2();
}
