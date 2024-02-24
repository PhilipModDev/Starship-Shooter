package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.engine.starship.utils.GameAssets;

public class NomadAsteroid extends Asteroid{
    public NomadAsteroid(int x, int y) {
        super(x, y);
        target = new Sprite(GameAssets.bigAsteroidZero.getInstance());
        target.setSize(4f,4f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(x,y);
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds = new Circle(position,2.8f);
        health = 4;
    }
}
