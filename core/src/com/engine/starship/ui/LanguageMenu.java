package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.GameAssets;
import java.util.Locale;

public class LanguageMenu extends MenuObject  {

    private final MenuManager manager;
    private Stage stage;
    private Table root;
    private List<String> languageList;
    private ScrollPane scrollPane;
    private Button exit;
    private static String currentItem = "";

    public LanguageMenu(MenuManager manager){
        this.manager = manager;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void show() {
        Skin skin = GameAssets.uiSkin.getInstance();

        stage = new Stage(new ExtendViewport(
                StarshipShooter.getInstance().guiCamera.viewportWidth,
                StarshipShooter.getInstance().guiCamera.viewportHeight
        ));
        Gdx.input.setInputProcessor(stage);
        root = new Table();
        root.setFillParent(true);
        //Debug only.
        root.setDebug(false);
        stage.addActor(root);

        exit = new Button(skin.get("x_menu_button",Button.ButtonStyle.class));
        MenuManager.onChange(exit,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            isVisible = false;
            manager.titleMenu.setVisible(true);
        });

        languageList = new List<>(skin);
        languageList.getStyle().font = StarshipShooter.getInstance().gameAssets.getLocalization().getAllLanguages();
        Array<String> items = new Array<>();
        items.addAll("Deutsch",
                "English",
                "Español",//Español
                "繁體中文 (繁体中文)", //Traditional Chinese (Traditional Chinese)
                "简体中文 (簡體中文)", //Simplified Chinese (Simplified Chinese)
                "Русский (язык)", //Russian language
                "українська (украинец)",//Ukrainian (Ukrainian)
                "日本語",//Japanese.
                "한국인",// Korean.
                "Danish",//Denmark.
                "Hindi",//India
                "Portuguese",//Brazil  br
                "Français", //France
                "Vietnamese" // Vietnam vi
        );
        languageList.setItems(items);
        if (currentItem.isBlank()) {
            currentItem = Locale.getDefault().getDisplayLanguage();
        }
        languageList.setSelected(currentItem);
        scrollPane = new ScrollPane(languageList,skin);
        scrollPane.setFadeScrollBars(false);
        addListEvents(languageList);

        root.add(exit).expandX().center().padTop(25f).width(80).height(80);
        root.row();
        root.add(scrollPane).expand().center().width(470).height(450);
    }

    //Applies and renders the title menu.
    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public static String getCurrentItem() {
        return currentItem;
    }

    private void addListEvents(List<String> list){
        list.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                GameAssets.hitSound.getInstance().play(0.2f);
                currentItem = list.getSelected();
                if (currentItem.equals("English")){
                    //Load English Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Deutsch")){
                    //Load German Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Русский (язык)")) {
                    //Load Russian Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("українська (украинец)")){
                    //Load Russian Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("简体中文 (簡體中文)")){
                    //Load Mandarin Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("繁體中文 (繁体中文)")){
                    //Load 繁體中文 (繁体中文) Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Español")){
                    //Load Mandarin Language.
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("日本語")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("한국인")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Danish")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Hindi")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Portuguese")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Français")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                if (currentItem.equals("Vietnamese")){
                    StarshipShooter.getInstance().reloadGame = true;
                }
                isVisible = false;
                manager.titleMenu.setVisible(true);
            }
        });
    }
}
