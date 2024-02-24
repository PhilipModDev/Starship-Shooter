package com.engine.starship;

import static com.badlogic.gdx.Gdx.gl;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.engine.starship.ui.MenuManager;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.events.EventSystem;
import com.engine.starship.utils.io.data.DataParser;
import com.engine.starship.utils.logic.entities.Entity;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;


public class StarshipShooter extends Game {
	private static StarshipShooter STARSHIP_SHOOTER;
	public UniverseManager universeManager;
	public Preferences preferences;
	private final DataParser dataParser = new DataParser();
	public OrthographicCamera guiCamera;
    public GameAssets gameAssets;
	public static boolean GAME_PAUSE = false;
	public static boolean MENUS = true;
	public boolean reloadGame = false;
	public boolean isPressStart = false;
	public boolean isGameLoadingUpStart = false;
	public boolean isAudioMute = false;
	public GLProfiler profiler;
	public MenuManager menuManager;
	private EventSystem.EventHandler eventHandler;
	private int timerCounter = 0;
	private int gameOverTick = 0;

	@Override
	public void create () {
		preferences = Gdx.app.getPreferences("options.dat");
		isAudioMute = preferences.getBoolean("audio");
		//Assets.
		isGameLoadingUpStart = true;
		STARSHIP_SHOOTER = this;
		gameAssets = new GameAssets(new AssetManager());
		gameAssets.init();

		guiCamera = new OrthographicCamera(Constants.GUI_VIEWPORT_WIDTH,Constants.GUI_VIEWPORT_HEIGHT);
		guiCamera.position.set(0,0,0);
		guiCamera.update();

		//Gets the audio to be register on load.
		Music backVoid = GameAssets.backVoid.getInstance();
		Music lightSpeed = GameAssets.lightSpeed.getInstance();

		universeManager = new UniverseManager(this) {
			@Override
			public void onHit(Entity entity) {
				GameAssets.hitSound.getInstance().play();
			}

			@Override
			public void onShoot() {
				GameAssets.shootSound.getInstance().play(0.3f);
			}
            //Fires the game over event in order to display the game over menu.
			@Override
			public void onGameOver() {
				MENUS = true;
				isPressStart = false;
				menuManager.gameOverMenu.setVisible(true);
			}
            //Only fires when options are changed.
			@Override
			public void onOptionUpdate() {
				//For the audio update.
				preferences.putBoolean("audio",isAudioMute);
				preferences.flush();
				if (isAudioMute){
					backVoid.setVolume(0);
					lightSpeed.setVolume(0);
				}else {
					backVoid.setVolume(1);
					lightSpeed.setVolume(1);
				}
			}
		};

		universeManager.init();
		//Register events.
		eventHandler = new EventSystem.EventHandler(this);
		eventHandler.register(universeManager);
		menuManager = new MenuManager(universeManager);
		menuManager.create();

        //Play music on start.
		if (!isAudioMute) {
			if (UniverseManager.RANDOM_XS_128.nextInt(100) < 50) backVoid.play();
			else lightSpeed.play();
		}
	}

	@Override
	public void render () {
		//Reloads the game when resources or a pack needs.
		if (reloadGame){
			isGameLoadingUpStart = false;
			gameAssets.init();
			menuManager.create();
			reloadGame = false;
		}
		//Only debug.
		if (universeManager.isDebuggingMode){
			profiler.reset();
		}
		//Only debug.
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
			if (isPressStart) universeManager.update();

			if (Gdx.input.isKeyJustPressed(Input.Keys.F11) && Gdx.graphics.isFullscreen() ){
				Gdx.graphics.setWindowedMode(1080,720);
			}else {
				if (Gdx.input.isKeyJustPressed(Input.Keys.F11)){
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				}
			}
			//Call OpenGL functions.
			gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
			//Render the universe.
			universeManager.renderObjects(Gdx.graphics.getDeltaTime());
			if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
				universeManager.reset();
			}
			//20000 ticks.
			if (timerCounter == 20000) {
                timerCounter = 0;
				int number = UniverseManager.RANDOM_XS_128.nextInt(10);
				if (number < 5){
					GameAssets.backVoid.getInstance().play();
				} else if (number < 8){
					GameAssets.lightSpeed.getInstance().play();
				}
			}
			timerCounter++;
			//Updates the game over menu time stamp.
			if (universeManager.isGameOver()){
				if (gameOverTick >= 100){
					gameOverTick = 0;
					universeManager.onGameOver();
				} else if (!menuManager.gameOverMenu.isVisible) gameOverTick += 1;
			}

			//Only called when f2 is pressed.
			if (Gdx.input.isKeyJustPressed(Input.Keys.F2)){
				takeScreenShot();
			}
		}
	}

	@Override
	public void resume() {
		GAME_PAUSE = false;
	}

	@Override
	public void pause() {
		GAME_PAUSE = true;
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

	public DataParser getDataParser() {
		return dataParser;
	}

	private void takeScreenShot(){
		Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		ByteBuffer pixels = pixmap.getPixels();
          // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
		int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
		for (int i = 3; i < size; i += 4) {
			pixels.put(i, (byte) 255);
		}
		PixmapIO.writePNG(Gdx.files.local(Constants.RESOURCE_LOCATION+
						"screenshots/screenshot"+UniverseManager.RANDOM_XS_128.nextInt()+".png"),
				pixmap, Deflater.DEFAULT_COMPRESSION, true);
		pixmap.dispose();
	}
}
