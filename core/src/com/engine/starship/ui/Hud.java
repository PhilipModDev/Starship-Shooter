package com.engine.starship.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.ui.component.PixelLabel;
import com.engine.starship.ui.component.PauseLabel;
import com.engine.starship.ui.component.StatusBar;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.UraniumGem;
import com.engine.starship.utils.entities.Starship;

public class Hud implements Disposable {
    private final UniverseManager universeManager;
    private final StatusBar healthBar;
    private final StatusBar shieldBar;
    private final TextureAtlas.AtlasRegion shieldRegion;
    private final TextureAtlas.AtlasRegion heartRegion;
    private final Camera guiCamera = StarshipShooter.getInstance().guiCamera;
    private final UIStage stage;
    private final StarshipShooter starshipShooter = StarshipShooter.getInstance();
    private final TextureAtlas.AtlasRegion uraniumGem;
    private BitmapFont uiFont;
    public Hud(UniverseManager universeManager){
        this.universeManager = universeManager;
        uiFont = StarshipShooter.getInstance().gameAssets.getLocalization().getAllLanguages();
        uiFont.getData().setScale(0.95f);
        stage = new UIStage(guiCamera);
        healthBar = GameAssets.healthBar.getInstance();
        shieldBar = GameAssets.shieldBar.getInstance();
        shieldRegion = GameAssets.shield.getInstance();
        heartRegion = GameAssets.heart.getInstance();
        uraniumGem = GameAssets.uraniumGem.getInstance();
    }

    public void init(){
        Starship player = universeManager.getPlayerShip();
        healthBar.setStatusBarProperties(player.health,50);
        healthBar.addCurrentStatus(player.health);
        shieldBar.setStatusBarProperties(player.getShields(),50);
        shieldBar.setCurrentStatus(player.getShields());
    }
    //Renders everything in the hud.
    public void render(Batch batch, float delta){
        if (GameAssets.Localization.isHindi || GameAssets.Localization.isVietnamese){
            uiFont = StarshipShooter.getInstance().gameAssets.getLocalization().getCurrentLanguage();
            uiFont.getData().setScale(0.95f);
        }
        //Sets the projection matrix.
        batch.setProjectionMatrix(starshipShooter.guiCamera.combined);
        batch.begin();
        renderUI(batch);
        renderLives(batch);
        stage.render(batch,delta);
        batch.end();
    }
    //Renders the lives ui to the screen.
    private void renderLives(Batch batch){
        Starship player = universeManager.getPlayerShip();
        healthBar.setCurrentStatus(player.health);
        healthBar.drawStatusBar(batch,guiCamera.viewportWidth - 290,guiCamera.viewportHeight - 70);
        //Rendering heart icon.
        batch.draw(heartRegion,guiCamera.viewportWidth - 50,guiCamera.viewportHeight - 70,
                heartRegion.originalWidth *  3, heartRegion.originalHeight * 3);
        shieldBar.setCurrentStatus(player.getShields());
        shieldBar.drawStatusBar(batch,guiCamera.viewportWidth - 290,guiCamera.viewportHeight - 100);
        //Rendering shield icon.
        batch.draw(shieldRegion,guiCamera.viewportWidth - 50,guiCamera.viewportHeight - 100,
                heartRegion.originalWidth *  3, heartRegion.originalHeight * 3);
    }
    //Renders UI.
    private void renderUI(Batch batch) {
        Camera gui = starshipShooter.guiCamera;
        //Only renders if the game is not over.
        if (!universeManager.isGameOver()) {
            uiFont.getData().setScale(0.95f);
            //Gets the camera and fps counter.
            int fps = Gdx.graphics.getFramesPerSecond();
            //Checks if the fps is greater or equal to 0 but less than or equal to 30.
            //Same principle applies for the rest.
            if (fps >= 0 && fps <= 30) {
                uiFont.setColor(Color.RED);
            }
            if (fps >= 31 && fps <= 49) {
                uiFont.setColor(Color.ORANGE);
            }
            if (fps >= 50 && fps < 60) {
                uiFont.setColor(Color.GOLD);
            }
            if (fps >= 60) {
                uiFont.setColor(Color.GREEN);
            }
            //Gets the current pos.
            int x = MathUtils.floor(universeManager.getPlayerShip().getSprite().getX());
            int y = MathUtils.floor(universeManager.getPlayerShip().getSprite().getY());
            String coordinates = "X:" + x + ":Y:" + y;
            //Draws the labeled fps counter in different colors.
            uiFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 50, gui.viewportHeight - 15);
            uiFont.setColor(Color.WHITE);
            uiFont.draw(batch, GameAssets.Localization.translate("kill","kill score") + ":" + UniverseManager.SCORE, 50, gui.viewportHeight - 50);
            uiFont.draw(batch, GameAssets.Localization.translate("Difficulty","Difficulty") + ":" +
                            StarshipShooter.getInstance().universeManager.level.getLevelDifficulty(),
                    50, gui.viewportHeight - 80);
            if (universeManager.isDebuggingMode()) {
                uiFont.draw(batch, coordinates + "\nDraw Calls:" + StarshipShooter.getInstance().profiler.getDrawCalls(), 50, gui.viewportHeight - 50);
            }
            //Draws the uranium gem for the coins.
            uiFont.getData().setScale(2);
            batch.draw(uraniumGem,50,gui.viewportHeight/1.35f,uraniumGem.originalWidth * 6f,uraniumGem.originalHeight * 6f);
            uiFont.draw(batch," "+universeManager.collectedGems,90,gui.viewportHeight - 120);
        }
    }
    //Checks if the menu button is pressed.
    public UIStage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        uiFont.dispose();
        stage.dispose();
    }

    public static class UIStage extends ScreenAdapter {
        public Stage stage;
        public Table root;
        public Button menu;
        public Button rocket;
        public Button playAgain;
        public Button home;
        public PauseLabel pauseLabel;
        public PixelLabel gameOverLabel;
        private final Camera guiCamera;
        private final UniverseManager universeManager = StarshipShooter.getInstance().universeManager;
        public UIStage(Camera guiCamera){
            this.guiCamera = guiCamera;
        }

        @Override
        public void show() {
            //gets the ui skin from the Game Assets.
            Skin skin = GameAssets.uiSkin.getInstance();
            stage = new Stage(new ExtendViewport(
                    StarshipShooter.getInstance().guiCamera.viewportWidth,
                    StarshipShooter.getInstance().guiCamera.viewportHeight
            ));
            Gdx.input.setInputProcessor(stage);
            root = new Table(skin);
            root.setFillParent(true);
            //Set true only for debugging reasons.
            stage.addActor(root);
            //Menu text button.
            menu = new Button(skin);
            rocket = new Button(skin.get("rocket_button", Button.ButtonStyle.class));
            if (UniverseManager.checkAppType && Gdx.app.getType() == Application.ApplicationType.Desktop) {
                rocket.setVisible(false);
            }
            //Play Again text button.
            playAgain = new Button(skin.get("right_arrow",Button.ButtonStyle.class));
            playAgain.setVisible(false);
            //Main menu text button.
            home = new Button(skin.get("home_button",Button.ButtonStyle.class));
            home.setVisible(false);

            pauseLabel = new PauseLabel();
            gameOverLabel = new PixelLabel(12);

            MenuManager.onChange(menu,() -> {
                Gdx.app.log("Client","Game Paused");
                StarshipShooter.GAME_PAUSE = true;
                universeManager.cancelPlayerMovement = false;
                GameAssets.hitSound.getInstance().play(0.2f);
            });

            menu.addListener(new InputListener(){
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    universeManager.cancelPlayerMovement = true;
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    universeManager.cancelPlayerMovement = false;
                }
            });
            MenuManager.onChange(playAgain,() -> {
                GameAssets.hitSound.getInstance().play(0.2f);
                Gdx.app.log("Client","Loading Level");
                if ( StarshipShooter.GAME_PAUSE){
                    StarshipShooter.GAME_PAUSE = false;
                }else {
                    universeManager.reset();
                    Gdx.app.log("Client","Loading Level");
                }
                universeManager.scoreParser.updateHighScore();
            });
            MenuManager.onChange(home,() -> {
                Gdx.app.log("Client","Loading Main Menu");
                GameAssets.hitSound.getInstance().play(0.2f);
                MenuManager manager = StarshipShooter.getInstance().menuManager;
                manager.setScreen(manager.gameOverMenu);
                StarshipShooter.MENUS = true;
                StarshipShooter.GAME_PAUSE = false;
                StarshipShooter.getInstance().isPressStart = false;
                universeManager.scoreParser.updateHighScore();
                universeManager.reset();
            });

            //Add rocket fire.
            MenuManager.onChange(rocket, universeManager::firePlayerRocket);

            //Add components to the sense.
            root.add(menu).expandY().right().top().padTop(10).width(40).height(40);
            root.row();
            root.add(playAgain).expand().bottom().right().width(60).height(100).spaceRight(100);
            root.add(home).expand().bottom().left().width(100).height(100);
            root.row();
            root.add(rocket).width(100).height(100).left().padLeft(20f);
        }
        //Renders hud.
        public void render(Batch batch,float delta) {
            root.setDebug(universeManager.isDebuggingMode);
            stage.getViewport().apply();
            stage.act(delta);
            stage.draw();
            //Centers the label.
            if (StarshipShooter.GAME_PAUSE && !universeManager.isGameOver())  pauseLabel.draw(batch,guiCamera.viewportWidth/2.0f,(guiCamera.viewportHeight/2.0f) + 150);
            playAgain.setVisible(StarshipShooter.GAME_PAUSE);
            home.setVisible(StarshipShooter.GAME_PAUSE);
            menu.setVisible(!StarshipShooter.GAME_PAUSE);

            //Display game over label.
            if (universeManager.isGameOver()){
                gameOverLabel.draw(batch,guiCamera.viewportWidth/2.0f,(guiCamera.viewportHeight/2.0f) + 100);
            }
            if (UniverseManager.checkAppType && Gdx.app.getType() == Application.ApplicationType.Desktop) {
                rocket.setVisible(false);
            }
        }

        @Override
        public void resize(int width, int height) {
            stage.getViewport().update(width, height,true);
        }

        @Override
        public void hide() {
           stage.dispose();
        }
    }
}
