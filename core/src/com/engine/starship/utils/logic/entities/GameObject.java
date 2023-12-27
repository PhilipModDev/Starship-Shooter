package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public interface GameObject {
     void update();

     void render(Batch batch);
     Sprite getSprite();
}
