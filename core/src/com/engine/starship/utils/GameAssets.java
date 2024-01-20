package com.engine.starship.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.engine.starship.StarshipShooter;
import com.engine.starship.config.GameConfigs;
import com.engine.starship.ui.LanguageMenu;
import com.engine.starship.utils.lang.LanguageBundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Locale;

public class GameAssets implements Disposable {
    private final AssetManager assetManager;
    public TextureAtlas gameAtlas;
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private final TextureRegion missing;
    public static AssetObject starship;
    public static AssetObject bullet;
    public static AssetObject asteroid;
    public static AssetObject healthAsteroid;
    public static AssetObject alienStarship;
    public static AssetObject heart;
    public static AssetObject uiSkin;
    public static AssetObject hitSound;
    public static AssetObject shootSound;
    public static GameConfigs gameConfigs;
    private final Localization localization = new Localization();

    public GameAssets(AssetManager manager){
        Texture texture = new Texture(Gdx.files.internal("textures/missing.png"));
        texture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
        missing = new TextureRegion(texture);
        this.assetManager = manager;
        this.assetManager.load(Constants.TEXTURE_ATLAS,TextureAtlas.class);
        this.assetManager.load("audio/hit.mp3",Sound.class);
        this.assetManager.load("audio/shooter.mp3",Sound.class);
        //Locale language support.
        localization.initLocalization(this.assetManager,LanguageMenu.getCurrentItem());
        this.assetManager .finishLoading();
    }


    public void loadConfigs(){
       try {
           FileHandle fileHandle = Gdx.files.local("configs.json");
           if (!fileHandle.exists()){
               gameConfigs = new GameConfigs();
               String json = GSON.toJson(gameConfigs);
               fileHandle.writeString(json,false);
               return;
           }
           gameConfigs = GSON.fromJson(fileHandle.reader(),GameConfigs.class);
       }catch (Exception exception){
           exception.printStackTrace();
       }
    }

    public void init(){
        //Locale language support.
        localization.initLocalization(this.assetManager,LanguageMenu.getCurrentItem());
        this.assetManager .finishLoading();

        gameAtlas = assetManager.get(Constants.TEXTURE_ATLAS);
        localization.initLanguage(assetManager);
        uiSkin = new AssetObject(localization.uiSkin);
        starship = new AssetObject(findRegion("starship"),gameAtlas.findRegions("starship"));
        asteroid = new AssetObject(findRegion("space_rock"));
        alienStarship = new AssetObject(findRegion("alien_starship"));
        bullet = new AssetObject(findRegion("bullet"));
        heart = new AssetObject(findRegion("heart"));
        healthAsteroid = new AssetObject(findRegion("health_asteroid"));
        hitSound = new AssetObject(assetManager.get("audio/hit.mp3",Sound.class));
        shootSound = new AssetObject(assetManager.get("audio/shooter.mp3",Sound.class));
        loadConfigs();
    }

    public Texture getBackground(){
        return new Texture(Gdx.files.internal("textures/space_background.png"));
    }

    //Finds the texture input by name.
    public TextureAtlas.AtlasRegion findRegion(String name){
        if (gameAtlas == null) return new TextureAtlas.AtlasRegion(missing);
        return gameAtlas.findRegion(name);
    }

    public Localization getLocalization(){
        return localization;
    }

    @Override
    public void dispose() {
        gameAtlas.dispose();
        assetManager.dispose();
        missing.getTexture().dispose();
        localization.dispose();
    }
    public boolean update(){
       return assetManager.update();
    }

    public static class AssetObject {
        private TextureAtlas.AtlasRegion atlasRegion;
        private Array<TextureAtlas.AtlasRegion> array;
        private TextureAtlas atlas;
        private Skin skin;
        private Sound sound;
        public AssetObject(TextureAtlas.AtlasRegion atlasRegion){
            this.atlasRegion = atlasRegion;
        }
        public AssetObject(TextureAtlas.AtlasRegion atlasRegion, Array<TextureAtlas.AtlasRegion> array){
            this.atlasRegion = atlasRegion;
            this.array = array;
        }
        public AssetObject(TextureAtlas.AtlasRegion atlasRegion, TextureAtlas atlas){
            this.atlasRegion = atlasRegion;
            this.atlas = atlas;
        }
        public AssetObject(Skin skin){
            this.skin = skin;
        }
        public AssetObject(Sound sound){
            this.sound = sound;
        }
        public TextureAtlas.AtlasRegion getInstance() {
            return atlasRegion;
        }

        public Skin getSkin() {
            return skin;
        }

        public Sound getSound() {
            return sound;
        }
        public Array<TextureAtlas.AtlasRegion> getArray() {
            return array;
        }
        public TextureAtlas getAtlas() {
            return atlas;
        }
    }

    public static class Localization implements Disposable {
        //Finds the player's location
        public final Locale locale = Locale.getDefault();
        private static LanguageBundle bundle = new LanguageBundle(GameAssets.GSON);
        private BitmapFont languageFont, currentLanguage;
        public static boolean canUsePixel = false;
        public static boolean isHindi = false;
        public static boolean isVietnamese = false;
        private Skin uiSkin;
        private BitmapFont pixelFont;
        private BitmapFont allLanguages;
        private String resourceLocation = "";
        private AssetManager manager;
        //Load language fonts and packs.
        public void initLocalization(AssetManager manager, String language){
            isHindi = false;
            isVietnamese = false;
            bundle = new LanguageBundle();
            this.manager = manager;
            Gdx.app.log("Game Localization","Loading country region: "+locale.getDisplayCountry() +" ["+locale.getCountry()+"]" +
                    " Language type: "+locale.getLanguage());
            manager.load(Constants.PIXEL_FONT, BitmapFont.class);
            manager.load(Constants.PIXEL_FONT_LOCAL, BitmapFont.class);

            if (StarshipShooter.getInstance().isGameLoadingUpStart){
                if (locale.equals(Locale.US)) {
                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                    loadLanguagePack("lang/lang_en.json",resourceLocation,true);
                } else if (locale.equals(Locale.GERMANY)) {
                    //Add support bundle.
                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"de.json";
                    loadLanguagePack("lang/lang_de.json",resourceLocation,true);
                } else if (locale.equals(Locale.UK)){
                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                    loadLanguagePack("n/a",resourceLocation,true);


                } else if (locale.equals(Locale.CHINA)){

                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                    loadLanguagePack("lang/lang_zh_td.json",resourceLocation,false);


                } else if (locale.equals(Locale.KOREA)){

                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                    loadLanguagePack("lang/lang_ko.json",resourceLocation,false);


                 }else if (locale.equals(Locale.FRANCE) || locale.equals(Locale.CANADA_FRENCH)){

                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                    loadLanguagePack("lang/lang_can.json",resourceLocation,false);


                } else if (locale.equals(Locale.JAPAN)){
                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                    loadLanguagePack("lang/lang_ja.json",resourceLocation,false);
                }else {
                    resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                    loadLanguagePack("n/a",resourceLocation,true);
                }
            } else {
                loadWithLanguage(language);
            }
        }
        //Reloads the language pack.
        private void loadWithLanguage(String language){
            canUsePixel = false;
            isVietnamese = false;
            isHindi = false;
            if (language.equals("English")) {
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                loadLanguagePack("lang/lang_en.json",resourceLocation,true);
            } else if (language.equals("Deutsch")) {
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"de.json";
                loadLanguagePack("lang/lang_de.json",resourceLocation,true);


             } else if (language.equals("繁體中文 (繁体中文)")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_zh_td.json",resourceLocation,false);


            } else if (language.equals("简体中文 (簡體中文)")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_zh_sm.json",resourceLocation,false);

            } else if (language.equals("Русский (язык)")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_ru.json",resourceLocation,false);


            }  else if (language.equals("українська (украинец)")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_ru_uk.json",resourceLocation,false);


            } else if (language.equals("Español")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_es.json",resourceLocation,false);


            } else if (language.equals("日本語")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_ja.json",resourceLocation,false);


            } else if (language.equals("한국인")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_ko.json",resourceLocation,false);


            }else if (language.equals("Danish")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_da.json",resourceLocation,false);


            }else if (language.equals("Hindi")){
                isHindi = true;
                manager.load(Constants.HINDI_FONT,BitmapFont.class);
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"hindi.json";
                loadLanguagePack("lang/lang_hin.json",resourceLocation,false);

            }else if (language.equals("Portuguese")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_br.json",resourceLocation,false);

            }else if (language.equals("Français")){

                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"lang.json";
                loadLanguagePack("lang/lang_can.json",resourceLocation,false);

            } else if (language.equals("Vietnamese")){
                isVietnamese = true;
                manager.load(Constants.VIETNAMESE_FONT,BitmapFont.class);
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"va.json";
                loadLanguagePack("lang/lang_va.json",resourceLocation,false);

            }else if (locale.equals(Locale.UK)){
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                loadLanguagePack("n/a",resourceLocation,true);
            } else {
                resourceLocation = Constants.SKIN_UI.substring(0,Constants.SKIN_UI.indexOf(".")) +"en.json";
                loadLanguagePack("n/a",resourceLocation,true);
            }
        }

        public void initLanguage(AssetManager manager) {
            pixelFont = manager.get(Constants.PIXEL_FONT,BitmapFont.class);
            if (canUsePixel){
                languageFont = manager.get(Constants.PIXEL_FONT,BitmapFont.class);
            }
            if (isHindi){
                currentLanguage = manager.get(Constants.HINDI_FONT,BitmapFont.class);
            }
            if (isVietnamese){
                currentLanguage = manager.get(Constants.VIETNAMESE_FONT,BitmapFont.class);
            }
            Gdx.app.log("Client","Loading skin at: "+resourceLocation);
            uiSkin = manager.get(resourceLocation,Skin.class);
            allLanguages = manager.get(Constants.PIXEL_FONT_LOCAL,BitmapFont.class);
        }

        private void loadLanguagePack(String path,String resourceLocation,boolean usePixel){
            manager.load(resourceLocation,Skin.class);
            if (!path.equalsIgnoreCase("n/a")) {
                bundle.loadResource(Gdx.files.internal(path));
            }
            canUsePixel = usePixel;
        }
        public BitmapFont getLanguageFont() {
            if (languageFont == null) return pixelFont;
            return languageFont;
        }
        public BitmapFont getAllLanguages() {
            return allLanguages;
        }
        public BitmapFont getPixelFont() {
            return pixelFont;
        }

        public BitmapFont getCurrentLanguage() {
            return currentLanguage;
        }

        public static String literal(String key, String defaultValue){
            if (bundle == null) return defaultValue;
            String text = bundle.getString(key);
            if (text == null) return defaultValue;
            return text;
        }

        @Override
        public void dispose() {
            languageFont.dispose();
            pixelFont.dispose();
            allLanguages.dispose();
        }
    }
}
