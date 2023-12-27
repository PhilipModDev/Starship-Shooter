package com.engine.starship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.engine.starship.utils.logic.entities.AlienStarship;
import com.engine.starship.utils.logic.entities.Asteroid;
import com.engine.starship.utils.logic.entities.Entity;
import com.engine.starship.utils.logic.entities.GameObject;
import com.engine.starship.utils.logic.entities.Starship;
import com.engine.starship.utils.CameraUtils;
import com.engine.starship.utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UniverseManager extends InputAdapter implements Disposable {
    private final StarshipShooter starshipShooter;
    public HashMap<Vector2,Asteroid> asteroidsMap = new HashMap<>();
    public Array<GameObject> aliens;
    private Starship player;
    public CameraUtils cameraUtils;
    private SpriteBatch batch;
    private Sprite worldBackground;
    private BitmapFont uiFont;
    private boolean isObjectsLoaded = false;
    private ShapeRenderer renderer;


    public UniverseManager(StarshipShooter starshipShooter){
        this.starshipShooter = starshipShooter;
    }

    public void init(){
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
        cameraUtils = new CameraUtils(0,0);
        cameraUtils.setZoom(1);
        renderer = new ShapeRenderer();
        renderer.setColor(Color.RED);
        renderer.setAutoShapeType(true);
    }

    public void loadObjects(){
        RandomXS128 randomXS128 = new RandomXS128();
        aliens = new Array<>();
        player = new Starship(0,0);
        int maxRocks = randomXS128.nextInt(1000);
        int rockCount = maxRocks / 2;
        for (int i = 0; i < rockCount; i++) {
            int x , y;
            x = randomXS128.nextInt(i + 1);
            y = randomXS128.nextInt(i + 1);

            Asteroid asteroid = new Asteroid(x,y);
            asteroidsMap.put(asteroid.position,asteroid);
        }

        for (int i = 0; i < rockCount; i++) {
            int x , y;
            x = randomXS128.nextInt(i + 1);
            y = randomXS128.nextInt(i + 1);
            x *= -1;
            y *= -1;

            Asteroid asteroid = new Asteroid(x,y);
            asteroidsMap.put(asteroid.position,asteroid);
        }
        worldBackground = new Sprite(starshipShooter.gameAssets.getBackground());
        worldBackground.setSize(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
        worldBackground.setOrigin(Gdx.graphics.getWidth()/2.0f,Gdx.graphics.getHeight()/2.0f);
        isObjectsLoaded = true;

        uiFont = starshipShooter.gameAssets.getAsset("default",BitmapFont.class);
        uiFont.getData().setScale(0.75f);

        AlienStarship alienStarship = new AlienStarship((int) player.position.x + -5, (int) player.position.y + 10);
        aliens.add(alienStarship);
    }

    public void renderObjects() {
        batch.setProjectionMatrix(cameraUtils.combined);
        batch.begin();
        worldBackground.draw(batch);
        //Render asteroids
        renderAsteroids(batch);
        //Render aliens.
        if (!aliens.isEmpty()){
            for (GameObject object : aliens) {
                object.update();
                object.render(batch);
            }
        }
        player.render(batch);
        batch.end();
        renderUI(batch);
    }

    private void renderAsteroids(Batch batch){
        renderer.setProjectionMatrix(cameraUtils.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        if (!asteroidsMap.isEmpty()) {
            Set<Map.Entry<Vector2, Asteroid>> asteroidSet = asteroidsMap.entrySet();
            for (Map.Entry<Vector2, Asteroid> entry : asteroidSet) {
                Asteroid asteroid = entry.getValue();
                Rectangle rectangle = asteroid.getSprite().getBoundingRectangle();
                asteroid.update();
                asteroid.render(batch);
                renderer.rect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
            }
        }
        renderer.end();
    }

    //update universe logic.
    public void update(){
        handleInputs();
        player.update();
        if (isCollision(player.position.x,player.position.y)){
            System.out.println(player.getSprite().getX()+":"+player.getSprite().getY());
        }
        cameraUtils.setTarget(player.getSprite());
        cameraUtils.update();
        updateBackground();
        updateTouchControls();
    }

    //Updates the background.
    private void updateBackground(){
        worldBackground.setCenter(cameraUtils.position.x,cameraUtils.position.y);
    }

    //Moves the selected gameObject.
    public void moveSelectedSprite(float x, float y){
        Sprite sprite = player.getSprite();
        sprite.translate(x,y);
    }

    //handles key input events.
    private void handleInputs(){
        if (Gdx.app.getType() == Application.ApplicationType.Desktop){
            //Keyboard controls.
            if (Gdx.input.isKeyPressed(Input.Keys.W)){
                float y = 5 * Gdx.graphics.getDeltaTime();
                moveSelectedSprite(0,y);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)){
                float y = 5 * Gdx.graphics.getDeltaTime();
                moveSelectedSprite(0,-y);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)){
                float x = 5 * Gdx.graphics.getDeltaTime();
                moveSelectedSprite(-x,0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)){
                float x = 5 * Gdx.graphics.getDeltaTime();
                moveSelectedSprite(x,0);
            }
            //Zoom in-out
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
                cameraUtils.addZoom(-0.5f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)){
                cameraUtils.addZoom(0.5f);
            }
            //handles fullscreen events.
            if (Gdx.input.isKeyJustPressed(Input.Keys.F11) && Gdx.graphics.isFullscreen() ){
                Gdx.graphics.setWindowedMode(1080,720);
            }else {
                if (Gdx.input.isKeyJustPressed(Input.Keys.F11)){
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                }
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        //Resets the game world.
        //Only debug.
        if (keycode == Input.Keys.R){
            init();
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.app.debug("Debug","Resetting game world.");
        }
        //Only debug.
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }
        return false;
    }

    //Resizes the camera viewport.

    public void resize(int width, int height){
        cameraUtils.resizeViewport(width,height);
    }

    //Dispose the resources to free up memory.
    @Override
    public void dispose() {
        batch.dispose();
        uiFont.dispose();
        renderer.dispose();
    }

    //Checks if objects is loaded.
    public boolean isObjectsLoaded() {
        if (!isObjectsLoaded) loadObjects();
        return isObjectsLoaded;
    }

    //Renders UI.
    private void renderUI(Batch batch){
        //Sets the projection matrix.
        batch.setProjectionMatrix(starshipShooter.guiCamera.combined);
        //Gets the camera and fps counter.
        Camera gui = starshipShooter.guiCamera;
        int fps = Gdx.graphics.getFramesPerSecond();
        //Checks if the fps is greater or equal to 0 but less than or equal to 30.
        //Same principle applies for the rest.
        if (fps >= 0 && fps <= 30){
            uiFont.setColor(Color.RED);
        }
        if (fps >= 31 && fps <= 49){
            uiFont.setColor(Color.ORANGE);
        }
        if (fps >= 50 && fps < 60){
            uiFont.setColor(Color.GOLD);
        }
        if (fps >= 60){
            uiFont.setColor(Color.GREEN);
        }
        //Gets the current pos.
        int x = MathUtils.floor(player.getSprite().getX());
        int y = MathUtils.floor(player.getSprite().getY());
        String coordinates = "X:"+x+":Y:"+y;
        //Draws the labeled fps counter in different colors.
        batch.begin();
        uiFont.draw(batch,"FPS: "+Gdx.graphics.getFramesPerSecond(), 50 ,gui.viewportHeight - 15);
        uiFont.draw(batch,coordinates, 50 ,gui.viewportHeight - 50);
        batch.end();
    }

    //Update touch controls.
    private void updateTouchControls(){
        //Only fires event when isTouch on screen.
        if (Gdx.input.isTouched(0)){
            //Gets the pixels xy coordinates.
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //Converts the touch coordinates to world coordinates.
            Vector2 touchPoint = new Vector2(xTouchPixels,yTouchPixels);
            touchPoint = cameraUtils.getViewport().unproject(touchPoint);
            Sprite sprite = player.getSprite();
            Vector2 playerLocation = new Vector2(
                    sprite.getX()+sprite.getOriginX(),
                    sprite.getY()+sprite.getOriginY()
            );
            //Calculates the distance between touchPoint and playerLocation.
            float touchDistance = touchPoint.dst(playerLocation);
            //If the touchDistance is greater than the max touch movement length, than move.
            if (touchDistance > Constants.TOUCH_MOVEMENT_SPEED){
                //Gets the xy differences.
                float xTouchDifference = touchPoint.x - playerLocation.x;
                float yTouchDifference = touchPoint.y - playerLocation.y;
                //Calculates the rotational direction for the ship.
                Vector2 direction = new Vector2(xTouchDifference,yTouchDifference);
                float rotateDegrees = direction.angleDeg();
                updateShipRotation(rotateDegrees);
                //Calculates the speed values for xy.
                float xMove = xTouchDifference / touchDistance * player.getSpeed() * Gdx.graphics.getDeltaTime();
                float yMove = yTouchDifference / touchDistance * player.getSpeed() * Gdx.graphics.getDeltaTime();
                //Moves the player toward the move position.
                Sprite playerSprite = player.getSprite();
                playerSprite.translate(xMove,yMove);
            }
        }
    }

    //rotates the ship with additional 270 degrees as for the offset.
    private void updateShipRotation(float degrees){
        player.getSprite().setRotation(degrees + 270);
    }

    public Starship getPlayer() {
        return player;
    }

    public boolean isCollision(float x, float y){
        x = MathUtils.floor(x);
        y = MathUtils.floor(y);
        Asteroid asteroid = asteroidsMap.get(new Vector2(x, y));
        if (asteroid == null) return false;
        return asteroid.getSprite().getBoundingRectangle().contains(x, y);
    }


    public boolean isCollisionTest(float x, float y) {
        Set<Map.Entry<Vector2, Asteroid>> asteroidSet = asteroidsMap.entrySet();
        for (Map.Entry<Vector2, Asteroid> entry : asteroidSet) {
            Asteroid asteroid = entry.getValue();
            Rectangle rectangle = asteroid.getSprite().getBoundingRectangle();
               
        }
        return false;
    }
}
