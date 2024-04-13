package com.engine.starship.ui.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.GameAssets;

public class XMenuButton {

    public final Sprite xSprite;
    public static final int size = 6;

    public XMenuButton(){
        xSprite = new Sprite(GameAssets.xIcon.getInstance());
        xSprite.scale(size);
    }

    public Sprite getXSprite() {
        return xSprite;
    }

    public void setBounds(float x,float y){
        xSprite.setBounds(x,y,size,size);
    }

    public void render(Batch batch){
        xSprite.draw(batch);
    }
}
