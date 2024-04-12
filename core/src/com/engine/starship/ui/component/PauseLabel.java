package com.engine.starship.ui.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.GameAssets;

public class PauseLabel {

    private final Sprite label;

    public PauseLabel(){
        this.label = new Sprite(GameAssets.pauseLabel.getInstance());
        label.setScale(15f);
    }

    public void draw(Batch batch, float x, float y){
        label.setPosition(x, y);
        label.draw(batch);
    }
}
