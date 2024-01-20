package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.utils.logic.StarshipTier;
import com.engine.starship.utils.GameAssets;


public class Starship extends Entity {
    public String starshipName;
    private int shields;
    private int speed = 7;//5%
    public StarshipTier tierRank = StarshipTier.TIN;
    private final Sprite target;
    private final Circle bounds;
    private Animation<TextureAtlas.AtlasRegion> animation;
    public Starship(int x,int y) {
        this.starshipName = "Galactic Starship";
        this.shields = 5;
        target = new Sprite(GameAssets.starship.getInstance());
        target.setSize(1f,1.3f);
        target.rotate(270);
        target.setPosition(x,y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        position.set(target.getX() + target.getOriginX(),target.getY() + target.getOriginY());
        isLiving = true;
        damage = 5;
        health = 5;
        bounds = new Circle(position.x,position.y,0.6f);
        animation = new Animation<>(0.080f,GameAssets.starship.getArray());
    }
    //Add the shield and speed values.
    public void addSpeed(int amount){
        setSpeed(amount + this.speed);
    }

    public void setSpeed(int speed){
        this.speed = MathUtils.clamp(speed,0,100);
    }
    public void addHealth(int amount){
        setHealth(amount + this.health);
    }

    public void setHealth(int health){
        this.health = MathUtils.clamp(health,0,5);
    }

    public void setShields(int shields){
        this.shields = MathUtils.clamp(shields,0,100);
    }


    public void addShields(int amount){
        setShields(amount + this.shields);
    }

    private void setTierRank(StarshipTier tierRank){
        this.tierRank = tierRank;
    }

    public int getShields() {
        return shields;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void update() {
        position.set(target.getX() + target.getOriginX(),target.getY() + target.getOriginY());
        bounds.setPosition(position.x,position.y);
    }

    //This needs to be call in the begin and the end.
    private float state = 0;
    @Override
    public void render(Batch batch) {
        if (health == 0) isLiving = false;
        if (isLiving){
            state += Gdx.graphics.getDeltaTime();
            TextureAtlas.AtlasRegion frame = animation.getKeyFrame(state,true);
            target.setRegion(frame);
            target.draw(batch);
            if (state > 10) state = 0;
        }
    }

    @Override
    public Sprite getSprite() {
        return target;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public Circle getBounds() {
        return bounds;
    }
}
