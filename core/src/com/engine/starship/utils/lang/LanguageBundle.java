package com.engine.starship.utils.lang;


import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;

public class LanguageBundle {
    private final Gson gson;
    private FileHandle fileHandle;
    private final HashMap<String,String> resourceData;
    public LanguageBundle(FileHandle fileHandle){
        this.fileHandle = fileHandle;
        gson = new GsonBuilder().disableHtmlEscaping().create();
        resourceData = new HashMap<>();
    }
    public LanguageBundle(){
        gson = new GsonBuilder().disableHtmlEscaping().create();
        resourceData = new HashMap<>();
    }
    public LanguageBundle(Gson gson){
        this.gson = gson;
        resourceData = new HashMap<>();
    }

    public void loadResource(){
      Bundle[] data = gson.fromJson(fileHandle.reader("UTF-8"), Bundle[].class);
        for (Bundle bundle : data) {
            String key = bundle.key;
            String value = bundle.value;
            resourceData.put(key, value);
        }
    }

    public void loadResource(FileHandle fileHandle){
        Bundle[] data = gson.fromJson(fileHandle.reader("UTF-8"), Bundle[].class);
        for (Bundle bundle : data) {
            String key = bundle.key;
            String value = bundle.value;
            resourceData.put(key, value);
        }
    }
    public String getString(String key){
        return resourceData.get(key);
    }
}
