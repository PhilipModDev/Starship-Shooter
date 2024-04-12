package com.engine.starship.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.engine.starship.UniverseManager;

public class MenuManager extends Game {

    public final TitleMenu titleMenu;
    public final LanguageMenu languageMenu;
    public final UniverseManager universeManager;
    public final CreditsMenu creditsMenu;
    public final GameOverMenu gameOverMenu;
    public final SettingsMenu settingsMenu;
    public final ShopMenu shopMenu;

    public MenuManager(UniverseManager universeManager){
        this.universeManager = universeManager;
        titleMenu = new TitleMenu(this);
        languageMenu = new LanguageMenu(this);
        creditsMenu = new CreditsMenu(this);
        gameOverMenu = new GameOverMenu(this);
        settingsMenu = new SettingsMenu(this);
        shopMenu = new ShopMenu(this);
    }

    @Override
    public void create() {
        if (titleMenu.isVisible){
            setScreen(titleMenu);
        }
        if (languageMenu.isVisible){
            setScreen(languageMenu);
        }
        if (creditsMenu.isVisible){
            setScreen(creditsMenu);
        }
        if (gameOverMenu.isVisible){
            setScreen(gameOverMenu);
        }
        if (settingsMenu.isVisible){
            setScreen(settingsMenu);
        }
        if (shopMenu.isVisible){
            setScreen(shopMenu);
        }
    }

    @Override
    public void resize(int width, int height) {
          screen.resize(width, height);
    }
    //Registers and listens for change events from buttons. (May change this Method)
    public static void onChange(Actor actor, final Runnable runnable){
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }


    public static void onTouchDown(Actor actor, final Runnable runnable){
        actor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                runnable.run();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    //Registers and listens for change events from buttons. (May change this Method)
    @Override
    public void render() {
        if (titleMenu.isVisible && screen != titleMenu){
            Gdx.app.log("Client","Loading Title Menu.");
            setScreen(titleMenu);
        }
        if (languageMenu.isVisible && screen != languageMenu){
            Gdx.app.log("Client","Loading Language Menu.");
            setScreen(languageMenu);
        }
        if (creditsMenu.isVisible && screen != creditsMenu){
            Gdx.app.log("Client","Loading Credits Menu.");
            setScreen(creditsMenu);
        }
        if (gameOverMenu.isVisible && screen != gameOverMenu){
            Gdx.app.log("Client","Loading Game Over Menu.");
            setScreen(gameOverMenu);
        }
        if (settingsMenu.isVisible && screen != settingsMenu){
            Gdx.app.log("Client","Loading Game Over Settings Menu.");
            setScreen(settingsMenu);
        }
        if (shopMenu.isVisible && screen != shopMenu){
            Gdx.app.log("Client","Loading Game Over Settings Menu.");
            setScreen(shopMenu);
        }
        screen.render(Gdx.graphics.getDeltaTime());
    }
}
