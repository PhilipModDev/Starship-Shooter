package com.engine.starship.utils;

import com.engine.starship.UniverseManager;
import com.engine.starship.utils.logic.entities.AlienStarship;
import com.engine.starship.utils.logic.entities.Asteroid;

import static com.engine.starship.UniverseManager.RANDOM_XS_128;
import static com.engine.starship.utils.GameAssets.gameConfigs;

public class Level {

    private float spawnFrequency = 5;
    private final UniverseManager manager;
    private float counter = 0;
    private int levelDifficulty = 0;
    private boolean isSpawnOneEntity = true;

    public Level(UniverseManager manager){
        this.manager = manager;
    }

    public void spawnEntities(){
        if (isSpawnOneEntity) {
            if (RANDOM_XS_128.nextBoolean()){
                spawnAsteroids();
                return;
            }
            if (RANDOM_XS_128.nextBoolean()){
                spawnAliens();
                return;
            }
        }
        spawnAliens();
        spawnAsteroids();
    }


    //Spawns Asteroids.
    private void spawnAsteroids(){
       int frequency =  RANDOM_XS_128.nextInt(gameConfigs.maxSpawnRatio);
        if (frequency <= spawnFrequency){
            int newWidth = (int) (manager.cameraUtils.viewportWidth + 3);
            int newHeight = RANDOM_XS_128.nextInt((int) manager.cameraUtils.viewportHeight);

            Asteroid asteroid = new Asteroid(newWidth, newHeight);
            asteroid.setCanMove(true);
            manager.asteroids.add(asteroid);
        }
    }

    //Spawn Alien
    private void spawnAliens(){
        int frequency = RANDOM_XS_128.nextInt(gameConfigs.maxSpawnRatio);
        if (frequency <= spawnFrequency){
            int newWidth = (int) (manager.cameraUtils.viewportWidth + 3);
            int newHeight = RANDOM_XS_128.nextInt((int) manager.cameraUtils.viewportHeight);

            AlienStarship alienStarship = new AlienStarship(newWidth,newHeight);
            manager.aliens.add(alienStarship);
        }
    }
    //Update the level.
    public void update(float delta){
        if (counter >= gameConfigs.levelTick){
            counter = 0;
            spawnFrequency += 1;
            Asteroid.SPEED = Math.min(Asteroid.SPEED + 0.1f,gameConfigs.maxAsteroidSpeed);
            gameConfigs.bulletDelay = Math.max(gameConfigs.bulletDelay - 1,10);
            levelDifficulty += 1;
            if (levelDifficulty >= 10) isSpawnOneEntity = false;
        }
        counter += 2 * delta;
    }
    //Resets the level.
    public void reset(){
        spawnFrequency = 0;
        counter = 0;
        Asteroid.SPEED = 5;
        levelDifficulty = 0;
        isSpawnOneEntity = true;
    }
    //Gets the level.
    public int getLevelDifficulty() {
        return levelDifficulty;
    }
}
