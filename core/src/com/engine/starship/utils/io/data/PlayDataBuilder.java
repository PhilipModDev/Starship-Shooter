package com.engine.starship.utils.io.data;

import java.util.Hashtable;

public final class PlayDataBuilder {

    private Hashtable<String,Integer> controlKeys;
    private Hashtable<String,StarShipData> dataHashtable;
    private int levelRank;
    private float experience;
    private int uraniumGem;

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

    public void addStarShipData(String key, StarShipData data){
        dataHashtable = dataHashtable == null ? new Hashtable<>() : dataHashtable;
        dataHashtable.put(key,data);
    }
    public void setExperience(float experience) {
        this.experience = experience;
    }

    public void setLevelRank(int levelRank) {
        this.levelRank = levelRank;
    }

    public void setUraniumGem(int uraniumGem) {
        this.uraniumGem = Math.max(uraniumGem,0);
    }


    public PlayerData create(){
        return new PlayerData(controlKeys,levelRank,experience,uraniumGem,dataHashtable);
    }
}
