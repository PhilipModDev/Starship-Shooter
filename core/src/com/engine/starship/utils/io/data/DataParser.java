package com.engine.starship.utils.io.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Null;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.Player;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

public class DataParser {

    public final Gson gson;
    private final String playerData = "playerData.json";

    public DataParser(){
        gson = new Gson();
    }

    //Reads the data.
    @Null
    public void loadPlayerData(Player player) throws IOException {
        FileHandle fileHandle = Gdx.files.local(Constants.RESOURCE_LOCATION + playerData);
        if (!fileHandle.exists()) {
            PlayDataBuilder dataBuilder = new PlayDataBuilder();
            dataBuilder.setExperience(0);
            dataBuilder.setLevelRank(0);
            //Creates the roque starship.
            StarShipData starShipData = new StarShipData(20,"tier 1",true);
            starShipData.isEquip = true;
            dataBuilder.addStarShipData(starShipData.name,starShipData);
            player.updatePlayerData(dataBuilder.create());
            savePlayerData(player.getPlayerData());
        }

        Reader reader = fileHandle.reader();
        PlayerData data = gson.fromJson(reader,PlayerData.class);
        player.updatePlayerData(data);

        Set<Map.Entry<String,StarShipData>> set = player.getPlayerData().getStarShipData().entrySet();
        for (Map.Entry<String,StarShipData> entry : set) {
            StarShipData starShipData = entry.getValue();
            if (starShipData.isEquip) player.updateStarShipData(starShipData);
        }
    }
    //Writes the data.
    public void savePlayerData(PlayerData data) throws IOException {
        FileHandle fileHandle = Gdx.files.local(Constants.RESOURCE_LOCATION + playerData);
        Writer writer = fileHandle.writer(false);
        if (fileHandle.exists()){
            gson.toJson(data,writer);
            writer.close();
            return;
        }
        final String json = gson.toJson(data);
        fileHandle.writeString(json,false);
    }

    public String getPlayerDataPath() {
        return playerData;
    }
}
