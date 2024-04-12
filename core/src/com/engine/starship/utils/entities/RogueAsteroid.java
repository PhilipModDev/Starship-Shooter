package com.engine.starship.utils.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.GameAssets;

public class RogueAsteroid extends Asteroid {

    private static final int MAX_ASTEROIDS_ON_DEATH = 10;

    public RogueAsteroid(int x, int y) {
        super(x, y);
        target = new Sprite(GameAssets.colossalAsteroidZero.getInstance());
        target.setSize(7f,7f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        target.setPosition(x,y);
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds = new Circle(position,3.8f);
        health = 6;
    }

    private void onDeath(UniverseManager manager){
        int x = (int) (UniverseManager.RANDOM_XS_128.nextInt((int) Math.ceil(bounds.radius)) + position.x);
        int y = (int) (UniverseManager.RANDOM_XS_128.nextInt((int) Math.ceil(bounds.radius)) + position.y);
        if (bounds.contains(x,y)){
            Asteroid asteroid = new Asteroid(x,y);
            asteroid.setCanMove(true);
            boolean value = UniverseManager.RANDOM_XS_128.nextBoolean();
            if (value) {
                asteroid.direction.set(-x,2);
            } else {
                asteroid.direction.set(-x,-2);
            }
            asteroid.direction.nor();
            manager.asteroids.add(asteroid);
        }
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        if (health == 0 || !isLiving){
            for (int i = 0; i <= MAX_ASTEROIDS_ON_DEATH; i++) {
                onDeath(StarshipShooter.getInstance().universeManager);
            }
        }
    }
}
