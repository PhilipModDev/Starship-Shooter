package com.engine.starship.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

//Registry asset system.
//By PhilipModDev.
public class RegistryAsset<T> {
    private final T asset;
    public RegistryAsset(T asset){
        this.asset = asset;
    }

    public T getInstance() {
        return asset;
    }

    public final static class TextureAsset <T extends TextureAtlas.AtlasRegion, A> extends RegistryAsset<T> {
        private final A assets;
        public TextureAsset(T region,A assets) {
            super(region);
            this.assets =  assets;
        }
        public A getAssets() {
            return assets;
        }
    }
}
