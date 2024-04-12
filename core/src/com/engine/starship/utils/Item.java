package com.engine.starship.utils;

import com.badlogic.gdx.graphics.g2d.Batch;

public abstract class Item {

    public boolean isRare;
    public boolean render;

    public abstract void render(Batch batch);

    public abstract void update();
}
