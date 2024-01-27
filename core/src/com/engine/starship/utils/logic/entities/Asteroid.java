package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.GameAssets;

public class Asteroid extends Entity {
    private final Sprite target;
    private float density = 0.2f;
    private final Circle bounds;
    private boolean canMove = false;
    public static float SPEED = 5;
    public boolean isPowerUp = false;
    public Asteroid(int x, int y){
        isLiving = true;
        if (UniverseManager.RANDOM_XS_128.nextBoolean()){
            target = new Sprite(GameAssets.healthAsteroid.getInstance());
            isPowerUp = true;
        }else {
            target = new Sprite(GameAssets.asteroid.getInstance());
        }
        target.setSize(1f,1f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(x,y);
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds = new Circle(position,0.6f);
        health = 1;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    //Sets and gets the density.
    public void setDensity(float density) {
        this.density = MathUtils.clamp(density,0.0f,10.0f);
    }

    public void addDensity(float density){
        setDensity(this.density + density);
    }

    //Rotates the asteroids.
    public void rotateAsteroids(){
        float degrees = 50 * Gdx.graphics.getDeltaTime();
        target.rotate(degrees);
    }
    public void update(Entity entity){
        if (canMove){
            target.translate(-SPEED * Gdx.graphics.getDeltaTime(),0);
        }
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds.setPosition(position);
        if (bounds.overlaps(entity.getBounds())){
            isLiving = false;
            if (entity instanceof Starship){
                Starship starship = (Starship) entity;
                UniverseManager manager = StarshipShooter.getInstance().universeManager;
                starship.takeDamage(1);
                StarshipShooter.getInstance().universeManager.onHit(entity);
                ParticleEffectPool.PooledEffect effect = manager.hitPoolEffect.obtain();
                effect.setPosition(entity.position.x,entity.position.y);
                manager.effects.add(effect);
            }
        }
        rotateAsteroids();
    }

    @Override
    public void update() {}

    @Override
    public void render(Batch batch) {
        if (health <= 0) isLiving = false;
        if (isLiving){
            this.target.draw(batch);
        }
    }
    @Override
    public Sprite getSprite() {
        return this.target;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public Circle getBounds() {
        return bounds;
    }
}
