package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.ui.component.PixelLabel;
import com.engine.starship.utils.GameAssets;

public class GameOverMenu extends MenuObject {

    private Stage stage;
    private final MenuManager manager;
    private final Camera camera;
    private BitmapFont font;
    private PixelLabel highScore;
    private TextureAtlas.AtlasRegion region;
    public GameOverMenu(MenuManager manager){
        this.manager = manager;
        camera = StarshipShooter.getInstance().guiCamera;
    }

    /**Sets the menu visible from <code>visible</code> */
    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        manager.titleMenu.setVisible(!visible);
    }


    @Override
    public void show() {
        //Gets the new skin class registered with the assets.
        Skin skin = GameAssets.uiSkin.getInstance();
        //Gets the font and sets the color.
        font = StarshipShooter.getInstance().gameAssets.getLocalization().getPixelFont();
        font.setColor(new Color(0.22745098F, 0.9490196F, 0.72156864F,1F));
        //Creates and new stage for the menu.
        stage = new Stage(new ExtendViewport(camera.viewportWidth,camera.viewportHeight));
        Gdx.input.setInputProcessor(stage);
        //Creates a root table to add components to.
        Table root = new Table();
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);
        //Creates the new pixel label with animation for the high score if needed.
        highScore = new PixelLabel(8,GameAssets.highScoreLabel.getInstance());
        highScore.setRotation(-20);
        //Creates a new instance of button for the home menu activation.
        Button home = new Button(skin.get("home_button", Button.ButtonStyle.class));
        //Register change event when called.
        MenuManager.onChange(home,() -> {
            setVisible(false);
            UniverseManager.SCORE = 0;
            manager.universeManager.setGameOver(false);
        });
        //Creates the background image for the table.
        region = GameAssets.scoreBackground.getInstance();
        Image scoreBackground = new Image(region);
        //Adds the components the the root table.
        root.add(scoreBackground).expand().bottom().width(region.originalWidth * 6).height(region.originalHeight * 5);
        root.row();
        root.add(home).expand().bottom().padBottom(100f).width(150).height(150);
    }

    @Override
    public void render(float delta) {
        //Renders the stage.
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        //Sets the batch for the score table.
        Batch batch = stage.getBatch();
        batch.begin();
        renderScore(batch);
        batch.end();
    }

    private void renderScore(Batch batch){
        //Renders score.
        font.draw(batch, String.valueOf(manager.universeManager.getScoreCounter()),(camera.viewportWidth/2.0f),(camera.viewportHeight/2.0f) + 120 + region.originalHeight);
        //Renders kills.
        font.draw(batch, String.valueOf(manager.universeManager.getScoreCounter()),(camera.viewportWidth/2.0f),(camera.viewportHeight/2.0f) + 75 + region.originalHeight);
        //Renders gems obtain.
        font.draw(batch, String.valueOf(manager.universeManager.getScoreCounter()),(camera.viewportWidth/2.0f),(camera.viewportHeight/2.0f) + 35 + region.originalHeight);
        //Renders only if high score.
         if (manager.universeManager.scoreParser.isNewHighScore()){
             highScore.draw(batch,(camera.viewportWidth/2.0f) + + 150,(camera.viewportHeight/2.0f) +  200);
         }
    }

    @Override
    public void resize(int width, int height) {
        //Updates the stage's viewport.
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
