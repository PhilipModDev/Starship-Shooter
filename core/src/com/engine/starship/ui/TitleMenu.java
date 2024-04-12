package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.GameAssets;

public class TitleMenu extends MenuObject {

    private final MenuManager manager;
    private Stage stage;
    private final float startBlinkDelay = 0.3f;
    private float startDelaySum = 0f;
    private Button startLabel;
    private final Camera guiCamera = StarshipShooter.getInstance().guiCamera;

    public TitleMenu(MenuManager manager){
        this.manager = manager;
        isVisible = true;
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
        Table root = new Table();
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);
        //Gets the style for the label.
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = StarshipShooter.getInstance().gameAssets.getLocalization().getPixelFont();

        Image title = new Image(GameAssets.starshipLabel.getInstance());

        //Create start label.
        startLabel = new Button(skin.get("start",Button.ButtonStyle.class));

        Label version = new Label(Constants.VERSION, labelStyle);

        Label verse = new Label("John 8:32", labelStyle);



        //Loads title button.
        Button language = new Button(skin.get("Globle", Button.ButtonStyle.class));

        //Creates the settings button.
        Button settings = new Button(skin.get("settings", Button.ButtonStyle.class));

        //Creates the inventory.
        Button inventory = new Button(skin.get("inventory",Button.ButtonStyle.class));

        //Creates the credits.
        Button credit = new Button(skin.get("credits",Button.ButtonStyle.class));

        MenuManager.onChange(settings,() ->{
            //Action for when the settings button is activated.
            GameAssets.hitSound.getInstance().play(0.2f);
            setVisible(false);
            manager.settingsMenu.setVisible(true);
        });

        MenuManager.onChange(inventory,() -> {
            GameAssets.hitSound.getInstance().play(0.2f);
            manager.shopMenu.setVisible(true);
            this.isVisible = false;
        });
        MenuManager.onChange(language,() -> {
            GameAssets.hitSound.getInstance().play(0.2f);
           manager.languageMenu.setVisible(true);
           this.isVisible = false;
        });
        MenuManager.onChange(credit,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            manager.creditsMenu.setVisible(true);
            this.isVisible = false;
        });
        //Table layout.
        root.add(title).expand().center().width(500).height(50);
        root.add(inventory).width(100).height(100);
        root.row();
        root.add(startLabel).expand().center().width(300).height(100);
        root.add(credit).width(100).height(100);
        root.row();
        root.add(language).expand().center().width(132).height(108);
        root.add(settings).width(100).height(100);
        root.row();
        float sidePad = 25f;
        float bottomPad = 20f;
        root.add(version).expandX().bottom().left().padLeft(sidePad).padBottom(bottomPad);
        root.add(verse).bottom().right().padBottom(bottomPad).padRight(sidePad);
    }

    //Applies and renders the title menu.
    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        startBlink();
        checkStartCollision();
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


    private void startBlink(){
        if (startDelaySum > startBlinkDelay){
            startLabel.setVisible(!startLabel.isVisible());
            startDelaySum = 0;
        }
        startDelaySum += Gdx.graphics.getDeltaTime();
    }

    private void checkStartCollision(){
        if (!Gdx.input.isTouched(0)) return;

        Vector2 touch = new Vector2();
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();
        touch.set(x,y);

        touch = stage.getViewport().unproject(touch);
        if (touch.x > startLabel.getX() && touch.x < startLabel.getX() + startLabel.getWidth()){
            if (touch.y > startLabel.getY() && touch.y < startLabel.getY() + startLabel.getHeight()){

                GameAssets.hitSound.getInstance().play(0.2f);
                StarshipShooter.MENUS = false;
                StarshipShooter.getInstance().universeManager.reset();
                StarshipShooter.getInstance().isPressStart = true;
                manager.setScreen(manager.universeManager.hud.getStage());

            }
        }
    }
}
