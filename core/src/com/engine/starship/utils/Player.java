package com.engine.starship.utils;

import com.badlogic.gdx.Gdx;
import com.engine.starship.utils.io.data.DataParser;
import com.engine.starship.utils.io.data.PlayerData;
import com.engine.starship.utils.io.data.StarShipData;
import java.io.IOException;

//The player data class.
public class Player {
    //The ship it's bound to.
    private StarShipData equipStarShip;
    private PlayerData playerData;
    public Player(DataParser dataParser)  {
        Gdx.app.log("Client","Loading player data.");
        try {
            //Checks if the data is located, if not it creates a new player.
            dataParser.loadPlayerData(this);
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
        if (playerData == null) throw new NullPointerException("Player Data can't be null.");
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public StarShipData getEquipStarShip() {
        return equipStarShip;
    }


    public void levelUp(int amount){
        if (amount < 0) return;
        playerData.setLevelRank(amount + playerData.getLevelRank());
    }

    public void gainExperience(float amount){
        if (amount < 0) return;
        playerData.setExperience(getPlayerData().getExperience() + amount);
    }

    public void addGem(int amount){
        playerData.setUraniumGem(amount + playerData.getUraniumGem());
    }

    public void unLockStarShip(StarShipData data){
        if (playerData.getStarShipData().containsValue(data)) return;
        playerData.addStarShipData(data.name,data);
    }

    public void equipStarShip(String name) {
        if (playerData.getStarShipData().containsKey(name)) {
            if (equipStarShip != null && equipStarShip.isEquip)
                equipStarShip.isEquip = false;

            equipStarShip = playerData.getStarShipData().get(name);
            equipStarShip.isEquip = true;
        }
    }

    public StarShipData getStarShipData(String name) {
        if (playerData.getStarShipData().containsKey(name)) {
          return  equipStarShip = playerData.getStarShipData().get(name);
        }
        return StarShipFactory.createShipData(name);
    }

    public void updatePlayerData(PlayerData data){
        this.playerData = data;
    }

    public void updateStarShipData(StarShipData data){
        this.equipStarShip = data;
    }
}
