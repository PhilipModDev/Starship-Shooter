package com.engine.starship.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
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
import com.engine.starship.ui.component.StatusBar;
import com.engine.starship.utils.lang.LanguageBundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Locale;

public class GameAssets implements Disposable {
    private final AssetManager assetManager;
    public TextureAtlas gameAtlas;
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private final TextureRegion missing;
    public static RegistryAsset.MultiRegistryAsset<TextureAtlas.AtlasRegion,Array<TextureAtlas.AtlasRegion>> starship;
    public static RegistryAsset<TextureAtlas.AtlasRegion> bullet;
    public static RegistryAsset<TextureAtlas.AtlasRegion> asteroid;
    public static RegistryAsset<TextureAtlas.AtlasRegion> healthAsteroid;
    public static RegistryAsset<TextureAtlas.AtlasRegion> alienStarship;
    public static RegistryAsset<TextureAtlas.AtlasRegion> tankAlienStarship;
    public static RegistryAsset<TextureAtlas.AtlasRegion> heart;
    public static RegistryAsset<TextureAtlas.AtlasRegion> shield;
    public static RegistryAsset<TextureAtlas.AtlasRegion> deadHeart;
    public static RegistryAsset<TextureAtlas.AtlasRegion> deadShield;
    public static RegistryAsset<TextureAtlas.AtlasRegion> bigAsteroidZero;
    public static RegistryAsset<TextureAtlas.AtlasRegion> colossalAsteroidZero;
    public static RegistryAsset<Texture> starshipLabel;
    public static RegistryAsset<TextureAtlas.AtlasRegion> gameOverLabel;
    public static RegistryAsset<TextureAtlas.AtlasRegion> pauseLabel;
    public static RegistryAsset<TextureAtlas.AtlasRegion> scoreBackground;
    public static RegistryAsset<TextureAtlas.AtlasRegion> background;
    public static RegistryAsset<TextureAtlas.AtlasRegion> highScoreLabel;
    public static RegistryAsset<TextureAtlas.AtlasRegion> audioIcon;
    public static RegistryAsset<TextureAtlas.AtlasRegion> particleIcon;
    public static RegistryAsset<Skin> uiSkin;
    public static RegistryAsset<Sound> hitSound;
    public static RegistryAsset<Sound> shootSound;
    public static RegistryAsset<Music> backVoid;
    public static RegistryAsset<Music> lightSpeed;
    public static RegistryAsset<StatusBar> healthBar;
    public static RegistryAsset<StatusBar> shieldBar;
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
        this.assetManager.load("audio/black_void.mp3",Music.class);
        this.assetManager.load("audio/light_speed.mp3",Music.class);
        TextureLoader.TextureParameter parameter = new TextureLoader.TextureParameter();
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        this.assetManager.load("textures/starship_shooter_label.png",Texture.class,parameter);
        //Locale language support.
        localization.initLocalization(this.assetManager,LanguageMenu.getCurrentItem());
        this.assetManager .finishLoading();
    }

    public void init(){
        //Locale language support.
        localization.initLocalization(this.assetManager,LanguageMenu.getCurrentItem());
        this.assetManager .finishLoading();

        gameAtlas = assetManager.get(Constants.TEXTURE_ATLAS);
        localization.initLanguage(assetManager);
        uiSkin = new RegistryAsset<>(localization.uiSkin);
        starship = new RegistryAsset.MultiRegistryAsset<>(findRegion("starship"),gameAtlas.findRegions("starship"));
        healthBar = new RegistryAsset<>(() -> new StatusBar(findRegion("status_bar"),findRegion("health_status")));
        shieldBar = new RegistryAsset<>(() -> new StatusBar(findRegion("status_bar"),findRegion("shield_status")));
        particleIcon = new RegistryAsset<>(findRegion("particle_icon"));
        audioIcon = new RegistryAsset<>(findRegion("audio_icon"));
        highScoreLabel = new RegistryAsset<>(findRegion("high_score_label"));
        scoreBackground = new RegistryAsset<>(findRegion("score_background"));
        background = new RegistryAsset<>(findRegion("background"));
        pauseLabel = new RegistryAsset<>(findRegion("pause_label"));
        gameOverLabel = new RegistryAsset<>(findRegion("game_over_label"));
        asteroid = new RegistryAsset<>(findRegion("space_rock"));
        bigAsteroidZero = new RegistryAsset<>(findRegion("big_asteroid0"));
        colossalAsteroidZero = new RegistryAsset<>(findRegion("collossal_asteroid0"));
        alienStarship = new RegistryAsset<>(findRegion("alien_starship"));
        tankAlienStarship = new RegistryAsset<>(findRegion("tank_alien"));
        bullet = new RegistryAsset<>(findRegion("bullet"));
        heart = new RegistryAsset<>(findRegion("heart"));
        shield = new RegistryAsset<>(findRegion("shield"));
        deadHeart = new RegistryAsset<>(findRegion("dead_heart"));
        deadShield = new RegistryAsset<>(findRegion("dead_shield"));
        healthAsteroid = new RegistryAsset<>(findRegion("health_asteroid"));
        hitSound = new RegistryAsset<>(assetManager.get("audio/hit.mp3",Sound.class));
        shootSound = new RegistryAsset<>(assetManager.get("audio/shooter.mp3",Sound.class));
        backVoid = new RegistryAsset<>(assetManager.get("audio/black_void.mp3",Music.class));
        lightSpeed = new RegistryAsset<>(assetManager.get("audio/light_speed.mp3",Music.class));
        starshipLabel = new RegistryAsset<>(assetManager.get("textures/starship_shooter_label.png",Texture.class));
        loadConfigs();
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
            BitmapFontLoader.BitmapFontParameter bitmapFontParameter = new BitmapFontLoader.BitmapFontParameter();
            bitmapFontParameter.magFilter = Texture.TextureFilter.Nearest;
            bitmapFontParameter.minFilter = Texture.TextureFilter.Nearest;
            manager.load(Constants.PIXEL_FONT, BitmapFont.class,bitmapFontParameter);
            manager.load(Constants.PIXEL_FONT_LOCAL, BitmapFont.class,bitmapFontParameter);

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

        public static String translate(String key, String defaultValue){
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
