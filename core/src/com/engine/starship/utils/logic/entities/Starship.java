package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.engine.starship.utils.logic.StarshipTier;
import com.engine.starship.utils.GameAssets;


public class Starship extends Entity {
    public String starshipName;
    private int shields;
    private int speed = 5;//5%
    public StarshipTier tierRank = StarshipTier.TIN;
    private final Sprite target;
    public Starship(int x,int y) {
        this.starshipName = "Galactic Starship";
        this.shields = 5;
        target = new Sprite(GameAssets.starship.getInstance());
        target.setSize(1f,1.1f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(position.x,position.y);
        position.set(x,y);
        isLiving = true;
        damage = 5;
        health = 30;
        target.getBoundingRectangle().setWidth(target.getWidth());
        target.getBoundingRectangle().setHeight(target.getHeight());
    }
    //Add the shield and speed values.
    public void addSpeed(int amount){
        setSpeed(amount + this.speed);
    }

    public void setSpeed(int speed){
        this.speed = MathUtils.clamp(speed,0,100);
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
        position.set(target.getX(),target.getY());
    }

    //This needs to be call in the begin and the end.
    @Override
    public void render(Batch batch) {
        if (health == 0) isLiving = false;
        if (isLiving){
            target.draw(batch);
        }
    }

    @Override
    public Sprite getSprite() {
        return target;
    }
}
