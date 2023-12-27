package com.engine.starship;

import static com.badlogic.gdx.Gdx.gl30;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.engine.starship.ui.MainMenu;
import com.engine.starship.utils.CameraUtils;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.GameAssets;

public class Starship extends Game {
	private UniverseManager universeManager;
    public GameAssets gameAssets;
	
	@Override
	public void create () {
		//Assets.
		gameAssets = new GameAssets();
		universeManager = new UniverseManager(this);
		universeManager.init();
	}

	@Override
	public void render () {
		//loads all assets.
		if (gameAssets.update(17)){
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
	}


	@Override
	public void resize(int width, int height) {
		//Resizes the universe.
		universeManager.resize(width, height);
	}
	
	@Override
	public void dispose () {
		//Disposes to free up memory.
		universeManager.dispose();
		gameAssets.dispose();
	}
}
