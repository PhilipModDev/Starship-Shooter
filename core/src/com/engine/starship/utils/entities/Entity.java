package com.engine.starship.utils.entities;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements GameObject {
    //The health state.
    public int health;
    //The damage state.
    public int damage;
    //The living state.
    public boolean isLiving;
    public final Vector2 position = new Vector2();

    public Circle getBounds(){ return null;};
   public abstract void update();
}
