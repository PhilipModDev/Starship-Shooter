package com.engine.starship.utils.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.GameAssets;

public class Rocket extends Bullet {

    public Sprite sprite;
    public static final int SPEED = 10;
    public static final int boundSize = 1;

    public Rocket(float x, float y){
        super(x, y);
        sprite = new Sprite(GameAssets.rocket.getInstance());
        sprite.setScale(0.07f);
        sprite.rotate90(true);
        sprite.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        position.set(x + sprite.getOriginX(), y + sprite.getOriginY());
        isLiving = true;
    }

    @Override
    public void update() {
        position.add(SPEED * Gdx.graphics.getDeltaTime(),0);
        sprite.setPosition(position.x, position.y);
    }

    public boolean hitBounds(float x,float y){
        if (x <= position.x + boundSize && x >= position.x - boundSize){
            return y <= position.y + boundSize && y >= position.y - boundSize;
        }
       return false;
    }

    @Override
    public void init(float x, float y) {
        //For offset on the ship.
        super.init(x, y - 0.3f);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

}
