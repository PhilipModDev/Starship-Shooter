package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.GameAssets;

public class TankAlienStarship extends AlienStarship {
    //The tank alien.
    public TankAlienStarship(int x, int y) {
        super(x, y);
        target = new Sprite(GameAssets.tankAlienStarship.getInstance());
        target.setSize(1f,0.5f);
        position.set(x,y);
        target.setPosition(position.x,position.y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        isLiving = true;
        health = 2;
        speed = 0.7f;
    }
}
