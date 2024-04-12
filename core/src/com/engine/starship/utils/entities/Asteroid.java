package com.engine.starship.utils.entities;

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
    public Sprite target;
    private float density = 0.2f;
    public  Circle bounds;
    private boolean canMove = false;
    private boolean canRotate = true;
    public static float SPEED = 5;
    public boolean isPowerUp = false;
    public final Vector2 direction = new Vector2(-1,0);
    public Asteroid(int x, int y){
        isLiving = true;
        target = new Sprite(GameAssets.asteroid.getInstance());
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

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    //Rotates the asteroids.
    public void rotateAsteroids(){
        float degrees = 50 * Gdx.graphics.getDeltaTime();
        target.rotate(degrees);
    }
    public void update(Entity entity){
        UniverseManager manager = StarshipShooter.getInstance().universeManager;
        if (canMove){
            target.translate(direction.x * (SPEED * Gdx.graphics.getDeltaTime()),direction.y * (SPEED * Gdx.graphics.getDeltaTime()));
        }

        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds.setPosition(position);
        if (bounds.overlaps(entity.getBounds())){
            isLiving = false;
            if (entity instanceof Starship){
                Starship starship = (Starship) entity;
                if (this instanceof RogueAsteroid){
                    starship.takeDamage(10);
                } else if (this instanceof NomadAsteroid) {
                    starship.takeDamage(8);
                }else if (this instanceof CTypeAsteroid) {
                    starship.takeDamage(8);
                } else if (this instanceof STypeAsteroid) {
                    starship.takeDamage(8);
                } else if (this instanceof SalusAsteroid) {
                    SalusAsteroid salusAsteroid = (SalusAsteroid) this;
                    if (!salusAsteroid.spawnHeart){
                        starship.takeDamage(6);
                    }else starship.addHealth(2);
                }else {
                    starship.takeDamage(6);
                }
                StarshipShooter.getInstance().universeManager.onHit(entity);
                ParticleEffectPool.PooledEffect effect = manager.hitPoolEffect.obtain();
                effect.setPosition(entity.position.x,entity.position.y);
                manager.effects.add(effect);
            }
        }
        if (canRotate){
            rotateAsteroids();
        }
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
