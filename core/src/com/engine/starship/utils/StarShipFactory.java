package com.engine.starship.utils;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.engine.starship.utils.io.data.StarShipData;

public final class StarShipFactory {

    public static StarShipData createShipData(String name){
        if (name.equals("tier 1")) return new StarShipData(20,name,true);
        if (name.equals("tier 2")) return new StarShipData(20,name,false);
        if (name.equals("tier 3")) return new StarShipData(20,name,false);
        if (name.equals("tier 4")) return new StarShipData(20,name,false);
        return null;
    }

    public static Sprite getStarshipTexture(String name){
        Sprite sprite;
        if (name.equals("tier 2")) {
            sprite = new Sprite(GameAssets.tierTwoStarShip.getInstance());
            sprite.setSize(1.2f,1.2f);
            return sprite;
        }
        if (name.equals("tier 3")) {
            sprite = new Sprite(GameAssets.tierThreeStarShip.getInstance());
            sprite.setSize(1.2f,1.2f);
            return sprite;
        }
        if (name.equals("tier 4")) {
           sprite = new Sprite(GameAssets.tierFourStarShip.getInstance());
            sprite.setSize(1.2f,1.2f);
            return sprite;
        }
        sprite = new Sprite(GameAssets.starship.getInstance());
        sprite.setSize(1f,1.3f);
        return sprite;
    }
}
