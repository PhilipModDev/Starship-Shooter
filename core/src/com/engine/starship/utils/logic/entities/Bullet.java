package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.engine.starship.utils.GameAssets;

public class Bullet extends Entity implements Pool.Poolable {
    public static final float SPEED = 12.8f;
    public static final short MAX_LENGTH = 230;
    public final Vector2 direction = new Vector2();
    private final Sprite target;
    private short stepsCounter = 0;
    public boolean isEnemy = false;
    public boolean fireRight = false;

    public Bullet(float x, float y){
        target = new Sprite(GameAssets.bullet.getInstance());
        target.setSize(0.07f,0.2f);
        target.rotate(270);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        position.x = x;
        position.y = y;
        isLiving = true;
    }
    public void init(float x, float y){
        position.x = x;
        position.y = y;
        isLiving = true;
    }


    @Override
    public void update() {
        if (stepsCounter > MAX_LENGTH){
            stepsCounter = 0;
            isLiving = false;
            return;
        }
        if (!isEnemy){
            position.add(SPEED * Gdx.graphics.getDeltaTime(),0);
            target.setPosition(position.x,position.y);
        } else {
            if (fireRight){
                position.add(SPEED * Gdx.graphics.getDeltaTime(),0);
                target.setPosition(position.x,position.y);
            }else {
                position.add(-SPEED * Gdx.graphics.getDeltaTime(),0);
                target.setPosition(position.x,position.y);
            }
        }
        stepsCounter++;
    }

    @Override
    public void render(Batch batch) {
        if (isLiving) target.draw(batch);
    }

    @Override
    public Sprite getSprite() {
        return target;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void reset() {
        direction.set(0,0);
        position.set(0,0);
        isLiving = true;
        isEnemy = false;
        fireRight = false;
        stepsCounter = 0;
    }
}
