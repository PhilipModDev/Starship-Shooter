package com.engine.starship.utils;


//Registry asset system.
//By PhilipModDev.
public class RegistryAsset<T> {
    private final T asset;
    public RegistryAsset(T asset){
        this.asset = asset;
    }

    public RegistryAsset(Provider<T> provider){
        this.asset = provider.provide();
    }

    public T getInstance() {
        return asset;
    }

    public final static class MultiRegistryAsset<T, A> extends RegistryAsset<T> {
        private final A assets;
        public MultiRegistryAsset(T region, A assets) {
            super(region);
            this.assets =  assets;
        }
        public A getAssets() {
            return assets;
        }
    }
}
