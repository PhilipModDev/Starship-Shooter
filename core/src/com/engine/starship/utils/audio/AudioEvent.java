package com.engine.starship.utils.audio;

import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;

public class AudioEvent implements AudioHandler{
    private final UniverseManager manager;
    private final StarshipShooter starshipShooter;

    public AudioEvent(StarshipShooter starshipShooter){
        this.starshipShooter = starshipShooter;
        this.manager = starshipShooter.universeManager;
    }
    @Override
    public void buttonPress() {

    }

    @Override
    public void enemyDamage() {

    }

    @Override
    public void playerDamage() {

    }

    @Override
    public void asteroidExploded() {

    }

    @Override
    public void shipExploded() {

    }
}
