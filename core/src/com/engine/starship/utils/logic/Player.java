package com.engine.starship.utils.logic;

import com.badlogic.gdx.Gdx;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.io.data.DataParser;
import com.engine.starship.utils.io.data.PlayDataBuilder;
import com.engine.starship.utils.io.data.PlayerData;
import com.engine.starship.utils.logic.entities.Starship;
import java.io.IOException;

//The player data class.
public final class Player {
    private int sensitively = 20;
    //The ship it's bound to.
    private Starship playerShip;
    private PlayerData playerData;
    private final PlayDataBuilder playDataBuilder;

    public Player(PlayDataBuilder playDataBuilder, DataParser dataParser){
        //Checks if the data is located.
        if (!Gdx.files.local(Constants.RESOURCE_LOCATION + dataParser.getPlayerDataPath()).exists()) {
            playDataBuilder.setExperience(0);
            playDataBuilder.setLevelRank(0);
            this.playerData = playDataBuilder.create();
            try {
                dataParser.savePlayerData(this.playerData);
            }catch (IOException exception){
                exception.printStackTrace();
            }
            Gdx.app.log("Client","Creating player data.");
        }
        Gdx.app.log("Client","Loading player data.");
        this.playDataBuilder = playDataBuilder;
        this.playerData = dataParser.loadPlayerData();
    }

    public void setBoundShip(Starship playerShip){
        this.playerShip = playerShip;
    }

    public Starship getBoundShip() {
        return playerShip;
    }

    public int getSensitively() {
        return sensitively;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public PlayDataBuilder getPlayDataBuilder() {
        return playDataBuilder;
    }

    public void levelUp(int amount){
        if (amount < 0) return;
        playDataBuilder.setLevelRank(amount + playerData.getLevelRank());
        playerData = playDataBuilder.create();
    }

    public void gainExperience(float amount){
        if (amount < 0) return;
        playDataBuilder.setExperience(getPlayerData().getExperience() + amount);
        playerData = playDataBuilder.create();
    }
}
