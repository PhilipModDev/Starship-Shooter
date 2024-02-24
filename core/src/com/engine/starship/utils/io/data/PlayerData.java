package com.engine.starship.utils.io.data;

import com.badlogic.gdx.utils.Null;
import java.util.Hashtable;

public final class PlayerData {
    private final Hashtable<String, Integer> controlKeys;
    private final int levelRank;
    private final float experience;

    public PlayerData(Hashtable<String,Integer> controlKeys,int levelRank,float experience){
        this.controlKeys = controlKeys;
        this.levelRank = levelRank;
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

    @Null
    public Integer getControlKey(String key,int value){
        if (controlKeys == null) return null;
        return controlKeys.put(key,value);
    }
}
