package com.engine.starship.utils;

import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.logic.Player;
import com.engine.starship.utils.logic.entities.AlienStarship;
import com.engine.starship.utils.logic.entities.Asteroid;
import com.engine.starship.utils.logic.entities.NomadAsteroid;
import com.engine.starship.utils.logic.entities.RogueAsteroid;
import com.engine.starship.utils.logic.entities.SalusAsteroid;
import com.engine.starship.utils.logic.entities.TankAlienStarship;

import static com.engine.starship.UniverseManager.RANDOM_XS_128;
import static com.engine.starship.utils.GameAssets.gameConfigs;

public class Level {

    //Controls the max spawn weight for enemies.
    public static final int MAX_SPAWN = 100;
    private int maxEnemySpawns = 130;
    private final UniverseManager manager;
    private float counter = 0;
    private int levelDifficulty = 0;
    private final AsteroidSpawnSettings asteroidSpawnSettings;
    private final AlienSpawnSettings alienSpawnSettings;
    private final Player player;

    public Level(UniverseManager manager){
        this.manager = manager;
        asteroidSpawnSettings = new AsteroidSpawnSettings(this);
        alienSpawnSettings = new AlienSpawnSettings(this);
        player = this.manager.getPlayer();
    }

    public void spawnEntities(){
            asteroidSpawnSettings.updateFeatures();
            alienSpawnSettings.updateFeatures();
    }


    //Spawns Asteroids.
    protected void spawnAsteroids() {
        int newWidth = (int) (manager.cameraUtils.viewportWidth + 3);
        int newHeight = RANDOM_XS_128.nextInt((int) manager.cameraUtils.viewportHeight - 3);

        Asteroid asteroid;
        //Random checks whether to spawn a health asteroid.
        if (RANDOM_XS_128.nextBoolean()) {
            asteroid = new SalusAsteroid(newWidth, newHeight);
        } else if (RANDOM_XS_128.nextBoolean()) {
            asteroid = new RogueAsteroid(newWidth, newHeight);
        } else if (RANDOM_XS_128.nextBoolean()) {
            asteroid = new NomadAsteroid(newWidth, newHeight);
        } else {
            asteroid = new Asteroid(newWidth, newHeight);
        }
        asteroid.setCanMove(true);
        manager.asteroids.add(asteroid);
    }


    //Spawn Alien
    private void spawnAliens(){
        int newX = (int) (manager.cameraUtils.viewportWidth + 3);
        int newY = RANDOM_XS_128.nextInt((int) manager.cameraUtils.viewportHeight);
        if (newY <= 4) newY = 6;
        AlienStarship alien;
        if (RANDOM_XS_128.nextBoolean()){
            alien = new TankAlienStarship(newX,newY);
        }else {
            alien = new AlienStarship(newX,newY);
        }
        manager.aliens.add(alien);
    }
    //Update the level.
    public void update(float delta){
        if (counter >= gameConfigs.levelTick){
            counter = 0;
            Asteroid.SPEED = Math.min(Asteroid.SPEED + 0.1f,gameConfigs.maxAsteroidSpeed);
            gameConfigs.bulletDelay = Math.max(gameConfigs.bulletDelay - 5,10);
            levelDifficulty += 1;
            maxEnemySpawns += 1;
            
            if (levelDifficulty == 1 && player.getPlayerData().getLevelRank() < 1){
                player.levelUp(1);
            }
        }
        counter += 2 * delta;
    }
    //Resets the level.
    public void reset(){
        counter = 0;
        Asteroid.SPEED = 5;
        levelDifficulty = 0;
        maxEnemySpawns = 130;
        alienSpawnSettings.resetFeatures();
    }
    //Gets the level.
    public int getLevelDifficulty() {
        return levelDifficulty;
    }

    public static class AsteroidSpawnSettings implements SpawnSettings {
        private float tick = MAX_SPAWN;
        //Main level.
        public final Level level;

        public AsteroidSpawnSettings(Level level){
            this.level = level;
        }

        @Override
        public void updateFeatures() {
            if (tick == MAX_SPAWN){
                tick = 0;
                //Spawn Asteroids random.
                level.spawnAsteroids();
            }
            tick += 0.5;
        }

        @Override
        public void resetFeatures() {

        }
    }

    public static class AlienSpawnSettings implements SpawnSettings {
        //3 ticks.
        private float tick = 0;
        private float weight = MAX_SPAWN;
        //Main level.
        public final Level level;

        public AlienSpawnSettings(Level level){
            this.level = level;
        }

        @Override
        public void updateFeatures() {
            if (tick >= weight){
                tick = 0;
                if (level.levelDifficulty > 5){
                    weight = Math.max(weight - 1,10);
                }
                //Spawn aliens random.
                if (StarshipShooter.getInstance().universeManager.aliens.size > 6) return;
                if (RANDOM_XS_128.nextInt(MAX_SPAWN) <= level.maxEnemySpawns) level.spawnAliens();
            }
            tick += 0.5f;
        }

        @Override
        public void resetFeatures() {
            weight = MAX_SPAWN;
            tick = 0;
        }
    }
}
