package com.engine.starship.utils.events;

import com.badlogic.gdx.InputAdapter;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.logic.entities.Entity;

public abstract class InputEventSystem extends InputAdapter {

    public void onHit(Entity entity){}
    public void onDeath(){}
    public void onShoot(){}

    public static class EventHandler {

        private InputEventSystem system;
        private final StarshipShooter starshipShooter;

        public EventHandler(StarshipShooter starshipShooter){
            this.starshipShooter = starshipShooter;
        }

        public void register(InputEventSystem eventSystem){
            this.system = eventSystem;
        }

        public InputEventSystem getSystem() {
            return system;
        }
    }
}
