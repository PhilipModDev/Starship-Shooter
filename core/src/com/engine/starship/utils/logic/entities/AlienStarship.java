package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.Utils;

import java.util.List;


public class AlienStarship extends Entity {
    public float speed = 2.0f;
    public int rankLevel = 1;// TODO: 12/18/2023 Add rank level.
    public int shields = 7;//7%
    private final Sprite target;
    private final PathFinding pathFindingAlgorithm;
    //Just for debugging.
    private final Vector2 pathFindPos = new Vector2();

    public AlienStarship(int x, int y){
        target = new Sprite(GameAssets.alienStarship.getInstance());
        target.setSize(1f,0.5f);
        target.setOrigin(target.getWidth()/2.0f,target.getHeight()/2.0f);
        position.set(x,y);
        target.setPosition(position.x,position.y);
        pathFindingAlgorithm = new PathFinding();
    }

    @Override
    public void update() {
        position.set(target.getX(),target.getY());
//        if (StarshipShooter.getInstance().universeManager.isCollision((int) target.getX(), (int) target.getY(),target.getBoundingRectangle())){
//            System.out.println(true);
//        }
        updatePathFinding();
    }

    private int index = 0;
    private PathFinding.Node current = new PathFinding.Node();
    private boolean update = true;
    private void updatePathFinding() {
        List<PathFinding.Node> list = null;
        if (update){
            list = pathFindingAlgorithm.pathFindEntity(this);
            update = false;
        }
        if (list != null && list.size() > 0){
            if (index >= list.size()) {
                update = true;
                return;
            }
            PathFinding.Node node = list.get(index);
            if (node == null) {
               update = true;
                return;
            }
            current.set(position.x,position.y);
            float distance = current.dst(node);
            float xd = node.x - current.x;
            float yd = node.y - current.y;
            float moveX = xd / distance * speed * Gdx.graphics.getDeltaTime();
            float moveY = yd / distance * speed * Gdx.graphics.getDeltaTime();
            getSprite().translate(moveX,moveY);
            if (current.equals(node)){
                update = true;
                index++;
            }
        }else {
            update = true;
        }
    }
    @Override
    public void render(Batch batch) {
        target.draw(batch);
    }

    @Override
    public Sprite getSprite() {
        return target;
    }
}
