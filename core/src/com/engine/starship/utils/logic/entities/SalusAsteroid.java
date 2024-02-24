package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.GameAssets;

public class SalusAsteroid extends Asteroid {
    public boolean spawnHeart = false;
    public SalusAsteroid(int x, int y) {
        super(x, y);
        this.target = new Sprite(GameAssets.healthAsteroid.getInstance());
        target.setSize(1f,1f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(x,y);
        isPowerUp = true;
    }
    //Changes the state of the asteroid to heart.
    public void changeState(){
        this.target = new Sprite(GameAssets.heart.getInstance());
        target.setSize(0.6f,0.6f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(position.x,position.y);
        spawnHeart = true;
        setCanRotate(false);
    }
    //Checks of the asteroid is dead. If true, method will be executed.
    public void checkDeath(Runnable method){
        if (spawnHeart){
           method.run();
           isLiving = false;
           health = 0;
        }
    }
}
