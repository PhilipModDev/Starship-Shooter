package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.logic.Material;
import com.engine.starship.utils.GameAssets;

public class Asteroid extends Entity {
    private final Sprite target;
    public Material material = Material.STONE;
    private float density = 0.2f;

    public Asteroid(int x, int y){
        position.set(x,y);
        target = new Sprite(GameAssets.asteroid.getInstance());
        target.setPosition(position.x,position.y);
        target.setSize(1,1);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
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

    @Override
    public void update() {
        //Add the update code.

       rotateAsteroids();
    }

    @Override
    public void render(Batch batch) {
       this.target.draw(batch);
    }

    @Override
    public Sprite getSprite() {
        return this.target;
    }
}
