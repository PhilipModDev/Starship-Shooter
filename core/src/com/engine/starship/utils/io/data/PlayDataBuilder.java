package com.engine.starship.utils.io.data;

import java.util.Hashtable;

public class PlayDataBuilder {

    private Hashtable<String,Integer> controlKeys;
    private int levelRank;
    private float experience;

    public PlayDataBuilder(PlayerData data){
        controlKeys = data.getControlKeys();
        levelRank = data.getLevelRank();
        experience = data.getExperience();
    }

    public PlayDataBuilder(){}

    public Integer addControls(String key, int value){
        controlKeys = controlKeys == null ? new Hashtable<>() : controlKeys;
        controlKeys.put(key,value);
        return value;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    public PlayerData create(){
        return new PlayerData(controlKeys,levelRank,experience);
    }
}
