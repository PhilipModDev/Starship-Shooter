package com.engine.starship.utils.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.utils.StarShipFactory;
import com.engine.starship.utils.StarshipTier;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.io.data.StarShipData;


public class Starship extends Entity {
    public String starshipName;
    private int shields;
    public StarshipTier tierRank = StarshipTier.TIN;
    private final Sprite target;
    private final Circle bounds;
    private Animation<TextureAtlas.AtlasRegion> animation;
    public boolean isAttackUse = false;
    private int shieldTick;
    private int shieldDelay = 1000;//1000 ticks.
    public int tick = 0;
    public static final int MAX_HEALTH = 20;
    public static final int MAX_SHIELD = 20;
    private StarShipData data;
    public Starship(int x, int y, StarShipData data) {
        this.data = data;
        this.starshipName = data.name;

        target = StarShipFactory.getStarshipTexture(this.starshipName);
        target.rotate(270);
        target.setPosition(x,y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        position.set(target.getX() + target.getOriginX(),target.getY() + target.getOriginY());
        isLiving = true;
        damage = 5;
        health = MAX_HEALTH;
        this.shields = MAX_SHIELD;
        bounds = new Circle(position.x,position.y,0.6f);
        animation = new Animation<>(0.080f,GameAssets.starship.getAssets());
    }
    //Add the shield and speed values.
    public void addSpeed(int amount){
        setSpeed(amount + this.data.speed);
    }

    public void setSpeed(int speed){
        this.data.speed = MathUtils.clamp(speed,0,100);
    }
    public void addHealth(int amount){
        setHealth(amount + this.health);
    }

    public void takeDamage(int amount){
        if (amount >= shields) {
            addHealth(-(amount - shields));
            shields = 0;
        }else {
            addShields(-amount);
        }
    }

    public void setHealth(int health){
        this.health = MathUtils.clamp(health,0,MAX_HEALTH);
    }

    public void setShields(int shields){
        this.shields = MathUtils.clamp(shields,0,MAX_SHIELD);
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
        return data.speed;
    }

    @Override
    public void update() {
        position.set(target.getX() + target.getOriginX(),target.getY() + target.getOriginY());
        bounds.setPosition(position.x,position.y);
        if (isAttackUse){
            if (tick >= data.maxAttackDelay){
                tick = 0;
                isAttackUse = false;
            } else tick++;
        }
        if (shieldTick > shieldDelay ){
            shieldTick = 0;
            addShields(3);
        }else if (shields < MAX_SHIELD) shieldTick ++;
    }

    //This needs to be call in the begin and the end.
    private float state = 0;

    @Override
    public void render(Batch batch) {
        if (health == 0) isLiving = false;
        if (isLiving){
            if (data.isAnimated) {
                state += Gdx.graphics.getDeltaTime();
                TextureAtlas.AtlasRegion frame = animation.getKeyFrame(state, true);
                target.setRegion(frame);
            }
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

    public StarShipData getData() {
        return data;
    }

    public void setData(StarShipData data) {
        this.data = data;
    }
}
