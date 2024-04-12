package com.engine.starship.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class UraniumGem extends Item {
    private final Vector2 position = new Vector2();
    private final TextureRegion textureRegion;

    public UraniumGem(int x,int y){
       isRare = true;
       render = true;
       position.set(x,y);
       textureRegion = GameAssets.uraniumGem.getInstance();
    }

    @Override
    public void render(Batch batch) {
        float size = 1f;
        batch.draw(textureRegion,position.x,position.y, size + .2f, size);
    }


    @Override
    public void update() {

    }
}
