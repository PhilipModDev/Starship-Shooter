package com.engine.starship.utils.io.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Null;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.io.data.PlayerData;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class DataParser {

    public final Gson gson;
    private final String playerData = "player_data.json";

    public DataParser(){
        gson = new Gson();
    }

    //Reads the data.
    @Null
    public PlayerData loadPlayerData() {
        FileHandle fileHandle = Gdx.files.local(Constants.RESOURCE_LOCATION + playerData);
        if (!fileHandle.exists()) return null;
        Reader reader = fileHandle.reader();
        return gson.fromJson(reader,PlayerData.class);
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
