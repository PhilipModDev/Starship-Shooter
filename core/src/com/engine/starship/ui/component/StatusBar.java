package com.engine.starship.ui.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class StatusBar {

    public final static int SIZE = 5;
    private final TextureAtlas.AtlasRegion bar;
    private final TextureAtlas.AtlasRegion status;
    private int maxStatus;
    private int maxBars;
    private int currentStatus;

    public StatusBar(TextureAtlas.AtlasRegion bar,TextureAtlas.AtlasRegion status){
        this.bar = bar;
        this.status = status;
    }

    public void setStatusBarProperties(int maxStatus , int maxBars){
        this.maxStatus = maxStatus;
        this.maxBars = maxBars;
    }

    public int getMaxBars() {
        return maxBars;
    }

    public int getMaxStatus() {
        return maxStatus;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = Math.min(Math.max(currentStatus,0),maxStatus - 4);
    }

    public void addCurrentStatus(int amount){
        setCurrentStatus(Math.min(amount + currentStatus,maxStatus - 4));
    }

    public void drawStatusBar(Batch batch, float x, float y){
        double ratio = (double)this.currentStatus / maxStatus;
        int width = (int) (ratio * maxBars);
        batch.draw(status,x,y + 5,width * SIZE,status.packedHeight * SIZE);
        batch.draw(bar,x,y,bar.packedWidth * SIZE,bar.packedHeight * SIZE);
    }
}
