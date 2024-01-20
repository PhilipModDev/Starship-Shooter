package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public interface GameObject {
     void update();

     void render(Batch batch);
     Sprite getSprite();

     Vector2 getPosition();
}
