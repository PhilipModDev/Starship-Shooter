package com.engine.starship.utils.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.GameAssets;
import java.util.List;

public class AlienStarship extends Entity {
    public float speed = 0.5f;
    public Sprite target;
    private final PathFinding pathFindingAlgorithm;
    //Just for debugging.
    private final Circle bounds;
    public boolean isAttackUse = false;
    public int tick = 0;

    public AlienStarship(int x, int y){
        target = new Sprite(GameAssets.alienStarship.getInstance());
        target.setSize(1f,0.5f);
        position.set(x,y);
        target.setPosition(position.x,position.y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        pathFindingAlgorithm = new PathFinding();
        bounds = new Circle(x,y,0.3f);
        isLiving = true;
        health = 1;
    }

    public AlienStarship(int x, int y, int health){
        target = new Sprite(GameAssets.alienStarship.getInstance());
        target.setSize(1f,0.5f);
        position.set(x,y);
        target.setPosition(position.x,position.y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        pathFindingAlgorithm = new PathFinding();
        bounds = new Circle(x,y,0.3f);
        isLiving = true;
        this.health = health;
    }

    public void update(Entity entity) {
        if (health == 0) {
            isLiving = false;
            return;
        }
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds.setPosition(position.x,position.y);
        if (bounds.overlaps(entity.getBounds())){
            isLiving = false;
            if (entity instanceof Starship){
                Starship starship = (Starship) entity;
                UniverseManager manager = StarshipShooter.getInstance().universeManager;
                manager.onHit(entity);
                ParticleEffectPool.PooledEffect effect = manager.hitPoolEffect.obtain();
                effect.setPosition(entity.position.x,entity.position.y);
                manager.effects.add(effect);
                starship.takeDamage(6);
            }
            return;
        }
        if (isAttackUse){
            if (tick >= GameAssets.gameConfigs.bulletDelay){
                tick = 0;
                isAttackUse = false;
            } else tick++;
        }
        updatePathFinding();
    }
    private final PathFinding.Node current = new PathFinding.Node();
    private void updatePathFinding() {
        if (!this.isLiving) return;
        List<PathFinding.Node> list = pathFindingAlgorithm.pathFindEntity(this);
        if (list != null ){
            float subtractSpeed = 0;
            for (PathFinding.Node node: list) {
                current.set(position.x,position.y);
                float distance = current.dst(node);
                if (distance < 3){
                    subtractSpeed += 0.1f;
                }
                float xd = node.x - current.x;
                float yd = node.y - current.y;
                float moveX = xd / distance * (speed - subtractSpeed) * Gdx.graphics.getDeltaTime();
                float moveY = yd / distance * (speed - subtractSpeed)  * Gdx.graphics.getDeltaTime();
                if (!Float.isNaN(moveX) && !Float.isNaN(moveY)){
                    getSprite().translate(moveX,moveY);
                }
            }
        } else {
            //Move the enemy closer to the play based of the distance.
            Starship player = StarshipShooter.getInstance().universeManager.getPlayerShip();
            float moveX =  (5 + speed) * Gdx.graphics.getDeltaTime();
            if (player.position.x < getSprite().getX()){
                getSprite().translate(-moveX,0);
            }else if (player.position.x > getSprite().getX()){
                getSprite().translate(moveX,0);
            }
        }
    }

    public void setHealth(int health){
        this.health = health;
    }

    public void addHealth(int health){
        setHealth(MathUtils.clamp(this.health + health,0,5));
    }

    @Override
    public void render(Batch batch) {
        if (isLiving){
            target.draw(batch);
        }
    }

    @Override
    public Sprite getSprite() {
        return target;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public Circle getBounds() {
        return bounds;
    }

    @Override
    public void update() {

    }
}
