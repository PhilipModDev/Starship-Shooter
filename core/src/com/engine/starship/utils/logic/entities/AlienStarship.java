package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.utils.GameAssets;
import java.util.List;

public class AlienStarship extends Entity {
    public float speed = 0.5f;
    public int rankLevel = 1;// TODO: 12/18/2023 Add rank level.
    public int shields = 7;//7%
    private final Sprite target;
    private final PathFinding pathFindingAlgorithm;
    //Just for debugging.
    private final Circle bounds;

    public AlienStarship(int x, int y){
        target = new Sprite(GameAssets.alienStarship.getInstance());
        target.setSize(1f,0.5f);
        position.set(x,y);
        target.setPosition(position.x,position.y);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        pathFindingAlgorithm = new PathFinding();
        bounds = new Circle(x,y,0.3f);
        isLiving = true;
        health = 2;
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

    @Override
    public void update() {
        if (health == 0) isLiving = false;
        position.set(target.getX() + target.getOriginX() ,target.getY() + target.getOriginY());
        bounds.setPosition(position.x,position.y);
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
}
