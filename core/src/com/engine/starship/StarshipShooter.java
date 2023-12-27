package com.engine.starship;

import static com.badlogic.gdx.Gdx.gl30;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.GameAssets;

public class StarshipShooter extends Game {
	private static StarshipShooter STARSHIP_SHOOTER;
	public UniverseManager universeManager;
	public OrthographicCamera guiCamera;
    public GameAssets gameAssets;


	@Override
	public void create () {
		//Assets.
		STARSHIP_SHOOTER = this;
		gameAssets = new GameAssets(new AssetManager());
		gameAssets.init();
		guiCamera = new OrthographicCamera(Constants.GUI_VIEWPORT_WIDTH,Constants.GUI_VIEWPORT_HEIGHT);
		guiCamera.position.set(0,0,0);

		guiCamera.update();
		universeManager = new UniverseManager(this);
		universeManager.init();
	}

	@Override
	public void render () {
		//loads all assets.
		if (universeManager.isObjectsLoaded()){
			//Updates the universe.
			universeManager.update();
			//Call OpenGL functions.
			gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
			gl30.glClearColor(0.1f,0.1f,0.1f,0.0f);
			//Render the universe.
			universeManager.renderObjects();
		}
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
	}

	public static StarshipShooter getInstance(){
		return STARSHIP_SHOOTER;
	}
}
