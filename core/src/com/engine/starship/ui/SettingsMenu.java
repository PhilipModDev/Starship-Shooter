package com.engine.starship.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.GameAssets;

//The settings the menu for the game.
public class SettingsMenu extends MenuObject {

    private Stage stage;
    private final MenuManager manager;
    private final Camera camera;
    private final StarshipShooter starshipShooter = StarshipShooter.getInstance();

    public SettingsMenu(MenuManager menuManager){
        this.manager = menuManager;
        this.camera = StarshipShooter.getInstance().guiCamera;
    }

    @Override
    public void setVisible(boolean visible) {
        //Set the scene visible.
        this.isVisible = visible;
    }

    @Override
    public void show() {
        //Gets the skin for the ui.
        Skin skin = GameAssets.uiSkin.getInstance();
        //Creates the stage.
        stage = new Stage(new ExtendViewport(camera.viewportWidth,camera.viewportHeight));
        Gdx.input.setInputProcessor(stage);
        //Creating the table.
        Table root = new Table();
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(GameAssets.background.getInstance());
        root.setBackground(backgroundDrawable);
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);

        //Tells whether if can create the controls option if on PC.
        Button controls = createControlsButton(skin);

        //Creating home button.
        Button home = new Button(skin.get("home_button",Button.ButtonStyle.class));

        //Creates the check button particles.
        Button particleCheck = new Button(skin.get("particles",Button.ButtonStyle.class));
        //Only sets it when the particles is true.
        if (!manager.universeManager.particles) particleCheck.setDisabled(true);

        //Creates the audio button.
        Button audio = new Button(skin.get("volume_button",Button.ButtonStyle.class));
        if (starshipShooter.isAudioMute) audio.setDisabled(true);

        //Registers the event for the home button.
        MenuManager.onChange(home,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            setVisible(false);
            manager.titleMenu.setVisible(true);
            Gdx.app.log("Options","Saving options.");
            manager.universeManager.onOptionUpdate();
        });

        //Registers the particle check button.
        particleCheck.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssets.hitSound.getInstance().play(0.2f);
                if (particleCheck.isDisabled()){
                    particleCheck.setDisabled(false);
                    manager.universeManager.particles = true;
                    manager.universeManager.onOptionUpdate();
                    return super.touchDown(event, x, y, pointer, button);
                }
                //do stuff here.
                particleCheck.setDisabled(true);
                manager.universeManager.particles = false;
                manager.universeManager.onOptionUpdate();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        //Registers the audio check button.
        audio.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameAssets.hitSound.getInstance().play(0.2f);
                if (audio.isDisabled()){
                    audio.setDisabled(false);
                    starshipShooter.isAudioMute = false;
                    manager.universeManager.onOptionUpdate();
                    System.out.println("Disabled");
                    return super.touchDown(event, x, y, pointer, button);
                }
                //do stuff here.
                audio.setDisabled(true);
                starshipShooter.isAudioMute = true;
                manager.universeManager.onOptionUpdate();
                System.out.println("Enabled");
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        //Table Layouts.
        root.row();
        root.add(audio).expand().center().right().width(150).height(150).spaceRight(100);
        root.add(particleCheck).expand().center().left().width(150).height(150);
        root.row();
        root.add(home).expand().center().left().width(150).height(150).padLeft(100);
        if (controls != null) root.add(controls).left().width(150).height(150);
    }

    //Resizes the stage.
    @Override
    public void resize(int width, int height) {
        //Resizes the viewport to the correct dimensions.
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void render(float delta) {
        //Renders the the viewport.
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Null
    private Button createControlsButton(Skin skin){
        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
            return new Button(
                skin.get("controls_button",Button.ButtonStyle.class
                ));
        return null;
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
