package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.GameAssets;

public class Hud implements Disposable {
    private final UniverseManager universeManager;
    private final TextureAtlas.AtlasRegion heartRegion;
    private final Camera guiCamera = StarshipShooter.getInstance().guiCamera;
    private final UIStage stage;
    private final StarshipShooter starshipShooter = StarshipShooter.getInstance();
    private  BitmapFont uiFont;
    public Hud(UniverseManager universeManager){
        this.universeManager = universeManager;
        uiFont = StarshipShooter.getInstance().gameAssets.getLocalization().getAllLanguages();
        uiFont.getData().setScale(0.95f);
        heartRegion = GameAssets.heart.getInstance();
        stage = new UIStage();
    }
    //Renders everything in the hud.
    public void render(Batch batch){
        if (GameAssets.Localization.isHindi || GameAssets.Localization.isVietnamese){
            uiFont = StarshipShooter.getInstance().gameAssets.getLocalization().getCurrentLanguage();
            uiFont.getData().setScale(0.95f);
        }
        //Sets the projection matrix.
        batch.setProjectionMatrix(starshipShooter.guiCamera.combined);
        batch.begin();
        renderUI(batch);
        renderLives(batch);
        batch.end();
    }
    //Renders the lives ui to the screen.
    private void renderLives(Batch batch){
       int lives = universeManager.getPlayer().health;
       int row = 0;
       for (int i = 0; i <= lives; i++){
           batch.draw(heartRegion,guiCamera.viewportWidth - 70 * i,guiCamera.viewportHeight - 70 - row,heartRegion.packedWidth * 5,heartRegion.packedHeight * 5);
       }
    }
    //Renders UI.
    private void renderUI(Batch batch) {
        Camera gui = starshipShooter.guiCamera;
        //Only renders if the game is over
        if (!universeManager.isGameOver()) {
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
            int x = MathUtils.floor(universeManager.getPlayer().getSprite().getX());
            int y = MathUtils.floor(universeManager.getPlayer().getSprite().getY());
            String coordinates = "X:" + x + ":Y:" + y;
            //Draws the labeled fps counter in different colors.
            uiFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 50, gui.viewportHeight - 15);
            uiFont.setColor(Color.WHITE);
            uiFont.draw(batch, GameAssets.Localization.literal("kill","kill score ") + ":" + UniverseManager.SCORE, 50, gui.viewportHeight - 50);
            uiFont.draw(batch, GameAssets.Localization.literal("Difficulty","Difficulty ") + ":" + UniverseManager.difficulty, 50, gui.viewportHeight - 80);
            if (universeManager.isDebuggingMode()) {
                uiFont.draw(batch, coordinates + "\nDraw Calls:" + StarshipShooter.getInstance().profiler.getDrawCalls(), 50, gui.viewportHeight - 50);
            }
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
        public TextButton playAgain;
        public TextButton mainMenu;
        public Label gameOverLabel;
        public Label scoreLabel;
        public boolean isPause;

        @Override
        public void show() {
            //gets the ui skin from the Game Assets.
            Skin skin = GameAssets.uiSkin.getSkin();
            stage = new Stage(new ExtendViewport(
                    StarshipShooter.getInstance().guiCamera.viewportWidth,
                    StarshipShooter.getInstance().guiCamera.viewportHeight
            ));
            Gdx.input.setInputProcessor(stage);
            root = new Table(skin);
            root.setFillParent(true);
            //Set true only for debugging reasons.
            root.setDebug(StarshipShooter.getInstance().universeManager.isDebuggingMode());
            stage.addActor(root);
            //Menu text button.
            menu = new Button(skin);;
            //Play Again text button.
            playAgain = new TextButton(GameAssets.Localization.literal("playAgain","Play Again?"),skin);
            playAgain.setVisible(false);

            scoreLabel = new Label(GameAssets.Localization.literal("score","score ") + ": ",skin);
            scoreLabel.setFontScale(2);
            scoreLabel.setColor(Color.WHITE);
            scoreLabel.setVisible(false);
            //Game over label.
            gameOverLabel = new Label(GameAssets.Localization.literal("gameOver","Game Over"),skin);
            gameOverLabel.setFontScale(3);
            gameOverLabel.setColor(Color.SCARLET);
            gameOverLabel.setVisible(false);
            //Main menu text button.
            mainMenu = new TextButton(GameAssets.Localization.literal("titleScreen", "Title Screen"),skin);
            mainMenu.setVisible(false);

            MenuManager.onChange(menu,() -> {
                Gdx.app.log("Client","Game Paused");
                isPause = true;
            });
            MenuManager.onChange(playAgain,() -> {
                Gdx.app.log("Client","Loading Level");
                if (isPause){
                    gameOverLabel.setText(GameAssets.Localization.literal("gameOver","Game Over"));
                    playAgain.setText(GameAssets.Localization.literal("playAgain","Play Again?"));
                    isPause = false;
                    setMenuVisible(false);
                }else {
                    StarshipShooter.getInstance().universeManager.reset();
                    Gdx.app.log("Client","Loading Level");
                }
            });
            MenuManager.onChange(mainMenu,() -> {
                Gdx.app.log("Client","Loading Main Menu");
                MenuManager manager = StarshipShooter.getInstance().menuManager;
                manager.setScreen(manager.titleMenu);
                StarshipShooter.getInstance().renderMenus = true;
                isPause = false;
                StarshipShooter.getInstance().universeManager.reset();
            });
            //Add components to the sense.
            root.add(menu).expandY().center().top().padTop(10).width(70).height(70);
            root.row();
            root.add(scoreLabel).expand().top();
            root.row();
            root.add(gameOverLabel).expand().top();
            root.row();
            root.add(playAgain).expand().top().center().width(300).height(90);
            root.row();
            root.add(mainMenu).expand().top().width(300).height(90);
        }
        //Renders hud.
        @Override
        public void render(float delta) {
            setMenuVisible(StarshipShooter.getInstance().universeManager.isGameOver());
            root.setDebug(StarshipShooter.getInstance().universeManager.isDebuggingMode);
            stage.act(delta);
            stage.draw();
        }

        //Set the menu visible if true.
        private void setMenuVisible(boolean visible){
            if (isPause){
                gameOverLabel.setText(GameAssets.Localization.literal("pause","Pause"));
                playAgain.setText(GameAssets.Localization.literal("resume","Resume"));
                menu.setVisible(false);
                playAgain.setVisible(isPause);
                gameOverLabel.setVisible(isPause);
                mainMenu.setVisible(isPause);
                return;
            }
            if (visible) menu.setVisible(false);
            if (!visible) menu.setVisible(true);
            //Add menu components visibility methods here.
            scoreLabel.setText(GameAssets.Localization.literal("score","score") +": "+UniverseManager.SCORE);
            playAgain.setVisible(visible);
            gameOverLabel.setVisible(visible);
            scoreLabel.setVisible(visible);
            mainMenu.setVisible(visible);
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
