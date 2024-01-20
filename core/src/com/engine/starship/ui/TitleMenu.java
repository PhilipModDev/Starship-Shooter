package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
    private Table root;
    private TextButton play;
    private TextButton credits;
    private Button language;
    private TextButton quit;
    private Label title;
    private Label version;
    private Label verse;

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
        Skin skin = GameAssets.uiSkin.getSkin();
        stage = new Stage(new ExtendViewport(
                StarshipShooter.getInstance().guiCamera.viewportWidth,
                StarshipShooter.getInstance().guiCamera.viewportHeight
        ));
        Gdx.input.setInputProcessor(stage);
        root = new Table();
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);
        //Gets the style for the label.
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = StarshipShooter.getInstance().gameAssets.getLocalization().getPixelFont();
        title = new Label("Starship Shooter",labelStyle);
        title.setFontScale(3);
        title.setColor(Color.ORANGE);

        version = new Label(Constants.VERSION,labelStyle);

        verse = new Label("John 8:32",labelStyle);

        play = new TextButton(GameAssets.Localization.literal("play","Play"), skin);

        quit = new TextButton(GameAssets.Localization.literal("quit","Quit"),skin);

        credits = new TextButton(GameAssets.Localization.literal("Credits","Credits"),skin);

        //Loads image button.
        language = new Button(skin.get("Globle", Button.ButtonStyle.class));

        MenuManager.onChange(play,() ->{
            StarshipShooter.getInstance().renderMenus = false;
            StarshipShooter.getInstance().universeManager.reset();
            manager.setScreen(manager.universeManager.hud.getStage());
        });

        MenuManager.onChange(quit,() ->{
           Gdx.app.exit();
        });
        MenuManager.onChange(language,() -> {
           manager.languageMenu.setVisible(true);
           this.isVisible = false;
        });
        MenuManager.onChange(credits,() ->{
            manager.creditsMenu.setVisible(true);
            this.isVisible = false;
        });

        float tablePad = 100f;
        root.add(title).expand().bottom().padLeft(tablePad).padTop(tablePad / 2);
        root.row();
        root.add(play).expand().bottom().width(300).padLeft(tablePad);
        root.row();
        root.add(credits).expand().bottom().width(300).padLeft(tablePad);
        root.row();
        root.add(quit).expand().bottom().width(300).padLeft(tablePad);
        root.row();
        root.add(language).expand().center().width(132).height(108).padLeft(tablePad);
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
}
