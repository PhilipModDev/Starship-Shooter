package com.engine.starship.utils.events;

import com.badlogic.gdx.InputAdapter;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.entities.Entity;

public abstract class EventSystem extends InputAdapter {

    public void onHit(Entity entity){}
    public void onDeath(){}
    public void onShoot(){}
    public void onGameOver(){}
    public void onOptionUpdate(){}


    public static class EventHandler {

        private EventSystem system;
        private final StarshipShooter starshipShooter;

        public EventHandler(StarshipShooter starshipShooter){
            this.starshipShooter = starshipShooter;
        }

        public void register(EventSystem eventSystem){
            this.system = eventSystem;
        }

        public EventSystem getSystem() {
            return system;
        }
    }
}
