package com.engine.starship.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class GameAssets implements Disposable {
    private final AssetManager assetManager;
    private TextureAtlas gameAtlas;
    private final TextureRegion missing;

    public static AssetObject starship;
    public static AssetObject asteroid;
    public static AssetObject alienStarship;
    public GameAssets(AssetManager manager){
        Texture texture = new Texture(Gdx.files.internal("textures/missing.png"));
        missing = new TextureRegion(texture);
        this.assetManager = manager;
        this.assetManager .load(Constants.TEXTURE_ATLAS,TextureAtlas.class);
        BitmapFontLoader.BitmapFontParameter parameter = new BitmapFontLoader.BitmapFontParameter();
        parameter.flip = false;
        this.assetManager .load("ui/font/default.fnt", BitmapFont.class,parameter);
        this.assetManager .finishLoading();

    }
    public void init(){
        gameAtlas = assetManager.get(Constants.TEXTURE_ATLAS);
        starship = new AssetObject(findRegion("starship"));
        asteroid = new AssetObject(findRegion("space_rock"));
        alienStarship = new AssetObject(findRegion("alien_starship"));
    }
    public Texture getBackground(){
        return new Texture(Gdx.files.internal("textures/space_background.png"));
    }

    //Finds the texture input by name.
    public TextureAtlas.AtlasRegion findRegion(String name){
        if (gameAtlas == null) return new TextureAtlas.AtlasRegion(missing);
        return gameAtlas.findRegion(name);
    }

    public <T> T getAsset(String name, Class<T> tClass){
        if (tClass == BitmapFont.class){
            String fontPath = Constants.FONTS_DIRECTORY+ "/" + name + ".fnt";
            return assetManager.get(fontPath,tClass);
        }
        return null;
    }

    @Override
    public void dispose() {
        gameAtlas.dispose();
        assetManager.dispose();
        missing.getTexture().dispose();
    }

    public static class AssetObject {
        private final TextureAtlas.AtlasRegion atlasRegion;
        public AssetObject(TextureAtlas.AtlasRegion atlasRegion){
            this.atlasRegion = atlasRegion;
        }
        public TextureAtlas.AtlasRegion getInstance() {
            return atlasRegion;
        }
    }
}
