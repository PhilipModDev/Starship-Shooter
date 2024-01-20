package com.engine.starship;

import static com.badlogic.gdx.Gdx.gl30;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.engine.starship.ui.MenuManager;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.events.InputEventSystem;
import com.engine.starship.utils.logic.entities.Entity;

public class StarshipShooter extends Game {
	private static StarshipShooter STARSHIP_SHOOTER;
	public UniverseManager universeManager;
	public OrthographicCamera guiCamera;
    public GameAssets gameAssets;
	public boolean isGamePaused = false;
	public boolean renderMenus = true;
	public boolean reloadGame = false;
	public boolean isGameLoadingUpStart = false;
	public GLProfiler profiler;
	public MenuManager menuManager;
	private InputEventSystem.EventHandler eventHandler;

	@Override
	public void create () {
		//Assets.
		isGameLoadingUpStart = true;
		STARSHIP_SHOOTER = this;
		gameAssets = new GameAssets(new AssetManager());
		gameAssets.init();
		guiCamera = new OrthographicCamera(Constants.GUI_VIEWPORT_WIDTH,Constants.GUI_VIEWPORT_HEIGHT);
		guiCamera.position.set(0,0,0);
		guiCamera.update();

		universeManager = new UniverseManager(this) {
			@Override
			public void onHit(Entity entity) {
				GameAssets.hitSound.getSound().play();
			}

			@Override
			public void onShoot() {
				GameAssets.shootSound.getSound().play(0.3f);
			}
		};
		universeManager.init();
		//Register events.
		eventHandler = new InputEventSystem.EventHandler(this);
		eventHandler.register(universeManager);
		menuManager = new MenuManager(universeManager);
		menuManager.create();
	}

	@Override
	public void render () {
		if (isGamePaused) return;
		if (reloadGame){
			isGameLoadingUpStart = false;
			gameAssets.init();
			menuManager.create();
			reloadGame = false;
		}
		if (universeManager.isDebuggingMode){
			profiler.reset();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.F3) && !universeManager.isDebuggingMode){
			profiler = new GLProfiler(Gdx.graphics);
			profiler.enable();
			universeManager.isDebuggingMode = true;
		} else {
			if (Gdx.input.isKeyJustPressed(Input.Keys.F3) && universeManager.isDebuggingMode){
				universeManager.isDebuggingMode = false;
				profiler = null;
			}
		}
		//loads all assets.
		if (universeManager.isObjectsLoaded() && gameAssets.update()){
			//Updates the universe.
			universeManager.update();

			if (Gdx.input.isKeyJustPressed(Input.Keys.F11) && Gdx.graphics.isFullscreen() ){
				Gdx.graphics.setWindowedMode(1080,720);
			}else {
				if (Gdx.input.isKeyJustPressed(Input.Keys.F11)){
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				}
			}
			//Call OpenGL functions.
			gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
			//Render the universe.
			universeManager.renderObjects(Gdx.graphics.getDeltaTime());
			if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
				universeManager.reset();
			}
		}
	}

	@Override
	public void resume() {
		isGamePaused = false;
	}

	@Override
	public void pause() {
		isGamePaused = true;
	}

	@Override
	public void resize(int width, int height) {
		//Resizes the universe.
		universeManager.resize(width, height);
		guiCamera.viewportHeight = Constants.GUI_VIEWPORT_HEIGHT;
		guiCamera.viewportWidth = ((float) Constants.GUI_VIEWPORT_HEIGHT / height) * width;
        guiCamera.position.set(guiCamera.viewportWidth/2.0f,guiCamera.viewportHeight/2.0f,0);
		guiCamera.update();
	}
	
	@Override
	public void dispose () {
		//Disposes to free up memory.
		universeManager.dispose();
		gameAssets.dispose();
		menuManager.dispose();
	}

	public static StarshipShooter getInstance(){
		return STARSHIP_SHOOTER;
	}

	public InputEventSystem.EventHandler getEventHandler() {
		return eventHandler;
	}
}
