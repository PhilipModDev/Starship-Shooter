package com.engine.starship.ui.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.Provider;

public class PixelLabel {

    private Sprite label;
    public float counter = 0;
    public boolean increase = true;
    public boolean done = false;
    private final int size;
    private int popUpDuration = 20;
    public PixelLabel(int size){
        this.size = size;
        this.label = new Sprite(GameAssets.gameOverLabel.getInstance());
    }

    public PixelLabel(int size, TextureAtlas.AtlasRegion region){
        this.size = size;
        this.label = new Sprite(region);
    }


    public void setRotation(float degrees){
        label.setRotation(degrees);
    }

    public void setPopUpDuration(int popUpDuration) {
        this.popUpDuration = popUpDuration;
    }

    public void defineProperties(Provider<Sprite> provider){
        this.label = provider.provide();
    }

    public void draw(Batch batch,float x,float y){
        label.setPosition(x, y);
        label.setScale(counter);
        label.draw(batch);

        if (done) return;

        if (increase) {
            if (counter > size) increase = false;
            else counter += popUpDuration * Gdx.graphics.getDeltaTime();
        }
        if (!increase){
            if (counter < size - 1) done = true;
            counter -= 6 * Gdx.graphics.getDeltaTime();
        }
    }
}
