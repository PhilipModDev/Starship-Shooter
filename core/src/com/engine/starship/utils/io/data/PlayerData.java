package com.engine.starship.utils.io.data;

import com.badlogic.gdx.utils.Null;
import java.util.Hashtable;

public final class PlayerData {
    private Hashtable<String, Integer> controlKeys;
    private Hashtable<String,StarShipData> data;
    private int levelRank;
    private float experience;
    private int uraniumGem;


    public PlayerData(Hashtable<String,Integer> controlKeys, int levelRank, float experience, int uraniumGem, Hashtable<String, StarShipData> data){
        this.controlKeys = controlKeys;
        this.levelRank = levelRank;
        this.experience = experience;
        this.uraniumGem = uraniumGem;
        this.data = data;
    }

    public Integer addControls(String key, int value){
        controlKeys = controlKeys == null ? new Hashtable<>() : controlKeys;
        controlKeys.put(key,value);
        return value;
    }

    public void addStarShipData(String key, StarShipData starShipData){
        data = data == null ? new Hashtable<>() : data;
        data.put(key,starShipData);
    }

    public void setUraniumGem(int uraniumGem) {
        this.uraniumGem = Math.max(uraniumGem,0);
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public float getExperience() {
        return experience;
    }

    public int getLevelRank() {
        return levelRank;
    }

    public Hashtable<String, Integer> getControlKeys() {
        return controlKeys;
    }

    public int getUraniumGem(){
        return uraniumGem;
    }

    public Hashtable<String, StarShipData> getStarShipData() {
        return data;
    }


    @Null
    public Integer getControlKey(String key,int value){
        if (controlKeys == null) return null;
        return controlKeys.put(key,value);
    }
}
