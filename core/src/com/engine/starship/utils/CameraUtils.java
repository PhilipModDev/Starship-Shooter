package com.engine.starship.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraUtils extends OrthographicCamera {

    public static final float MAX_ZOOM_IN = 0.45f;
    public static final float MAX_ZOOM_OUT =  3f;
    private Sprite targetSprite;
    private boolean hasTarget;

    private Viewport viewport;

    public CameraUtils(int x, int y){
        viewportWidth = Constants.VIEWPORT_WIDTH;
        viewportHeight = Constants.VIEWPORT_HEIGHT;
        viewport = new ExtendViewport(viewportWidth,viewportHeight,this);
        position.set(x,y,0);
        update();
    }

    public void resizeViewport(float width,float height){
        viewport.update((int) width, (int) height,true);
    }


    @Override
    public void update() {
        if (hasTarget()){
            if (targetSprite.getX() + targetSprite.getWidth() > viewportWidth + 1){
                targetSprite.setPosition(viewportWidth - 3,targetSprite.getY() + targetSprite.getOriginY());
            }
            if (targetSprite.getY() + targetSprite.getHeight() > viewportHeight + 1){
                targetSprite.setPosition(targetSprite.getY() + targetSprite.getOriginY(),viewportHeight - 3);
            }
            position.x = targetSprite.getX() + targetSprite.getOriginX();
            position.y = targetSprite.getY() + targetSprite.getOriginY();
        }
        super.update();
    }

    public void setTarget(Sprite sprite){
        if (sprite != null) {
            this.targetSprite = sprite;
            hasTarget = true;
            return;
        }
        hasTarget = false;
    }
    public void setZoom(float value){
        zoom =  MathUtils.clamp(value,MAX_ZOOM_IN,MAX_ZOOM_OUT);
    }

    public boolean hasTarget() {
        return hasTarget;
    }
    public boolean hasTarget(Sprite sprite) {
        return hasTarget && this.targetSprite.equals(sprite);
    }

    public void addZoom(float value){
        setZoom(value + zoom);
    }

    public Viewport getViewport() {
        return viewport;
    }
}
