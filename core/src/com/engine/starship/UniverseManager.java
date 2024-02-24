package com.engine.starship;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.engine.starship.ui.Hud;
import com.engine.starship.utils.Level;
import com.engine.starship.utils.events.EventSystem;
import com.engine.starship.utils.io.data.DataParser;
import com.engine.starship.utils.io.data.PlayDataBuilder;
import com.engine.starship.utils.io.ScoreParser;
import com.engine.starship.utils.logic.Player;
import com.engine.starship.utils.logic.entities.AlienStarship;
import com.engine.starship.utils.logic.entities.Asteroid;
import com.engine.starship.utils.logic.entities.Bullet;
import com.engine.starship.utils.logic.entities.Entity;
import com.engine.starship.utils.logic.entities.SalusAsteroid;
import com.engine.starship.utils.CameraUtils;
import com.engine.starship.utils.Constants;
import com.engine.starship.utils.logic.entities.Starship;

public class UniverseManager extends EventSystem implements Disposable {
    private final StarshipShooter starshipShooter;
    public static final RandomXS128 RANDOM_XS_128 = new RandomXS128();
    private Texture[] backgrounds;
    private final float[] offsetBackgrounds = {0,0};
    public Array<AlienStarship> aliens;
    public Array<Asteroid> asteroids;
    public Array<Bullet> bullets;
    public Array<ParticleEffectPool.PooledEffect> effects;
    public ScoreParser scoreParser;
    private Starship playerShip;
    private Player player;
    private DataParser dataParser;
    public CameraUtils cameraUtils;
    public SpriteBatch batch;
    private Sprite worldBackground;
    private boolean isObjectsLoaded = false;
    public boolean isDebuggingMode = false;
    private boolean isGameOver = false;
    public boolean fireBullet = false;
    public boolean autoFire = true;
    public boolean particles = true;
    public static int SCORE = 0;
    private int scoreCounter = 0;
    private ShapeRenderer renderer;
    private Viewport viewport;
    public Level level;
    public Hud hud;
    //A pool for all the bullets.
    public final Pool<Bullet> bulletPool = new Pool<Bullet>(1,5) {
        @Override
        protected Bullet newObject() {
            return new Bullet(playerShip.position.x, playerShip.position.y);
        }
    };

    public ParticleEffectPool hitPoolEffect;
    public ParticleEffectPool rocketPoolEffect;
    public ParticleEffectPool pointPoolEffect;
    private ParticleEffectPool.PooledEffect rocketEffect;

    public UniverseManager(StarshipShooter starshipShooter){
        this.starshipShooter = starshipShooter;
        dataParser = starshipShooter.getDataParser();
        PlayDataBuilder playDataBuilder = new PlayDataBuilder();
        player = new Player(playDataBuilder,dataParser);
    }


    public void init(){
        scoreParser = new ScoreParser();
//        ShaderProgram shaderProgram = new ShaderProgram(
//                Gdx.files.internal("shaders/vertex.glsl"),
//                Gdx.files.internal("shaders/fragment.glsl")
//        );
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
        cameraUtils = new CameraUtils(0,0);
        cameraUtils.setZoom(1);
        renderer = new ShapeRenderer();
        renderer.setColor(Color.RED);
        renderer.setAutoShapeType(true);
        //Init the background data.
        backgrounds = new Texture[2];
        backgrounds[0] = new Texture(Gdx.files.internal("textures/star_background_0.png"));
        backgrounds[1] = new Texture(Gdx.files.internal("textures/star_background_1.png"));

        viewport = new StretchViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
        hud = new Hud(this);
        hud.getStage().show();
        level = new Level(this);
        player.setBoundShip(playerShip);
    }

    public void loadObjects(){

        //Load all effects.
         ParticleEffect hit = new ParticleEffect();
         hit.setDuration(1);
         hit.load(Gdx.files.internal("particles/exploded.p"), StarshipShooter.getInstance().gameAssets.gameAtlas);
         hit.scaleEffect(0.03f);

         ParticleEffect rocket = new ParticleEffect();
         rocket.setDuration(1);
         rocket.load(Gdx.files.internal("particles/rocket_charge.p"), StarshipShooter.getInstance().gameAssets.gameAtlas);
         rocket.scaleEffect(0.03f);

         ParticleEffect point = new ParticleEffect();
         point.setDuration(1);
         point.load(Gdx.files.internal("particles/point.p"), StarshipShooter.getInstance().gameAssets.gameAtlas);
         point.scaleEffect(0.05f);

        hitPoolEffect = new ParticleEffectPool(hit,1,4);
        rocketPoolEffect = new ParticleEffectPool(rocket,1,2);
        pointPoolEffect = new ParticleEffectPool(point,1,3);

        aliens = new Array<>();
        asteroids = new Array<>();
        bullets = new Array<>();
        effects = new Array<>();

        playerShip = new Starship((int) (cameraUtils.viewportWidth/2.0f), (int) (cameraUtils.viewportHeight/2.0f),player);
        worldBackground = new Sprite(starshipShooter.gameAssets.getBackground());
        worldBackground.setSize(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
        worldBackground.setOrigin(Gdx.graphics.getWidth()/2.0f,Gdx.graphics.getHeight()/2.0f);
        isObjectsLoaded = true;

        rocketEffect = rocketPoolEffect.obtain();
        rocketEffect.setPosition(playerShip.position.x, playerShip.position.y);
        effects.add(rocketEffect);
        hud.init();
    }

    public void renderObjects(float delta) {
        renderWorldBackground(batch);
      if (!isGameOver && !StarshipShooter.MENUS){
          if (isDebuggingMode){
              //Debugging only.
              starshipShooter.profiler.reset();
          }
          //Render asteroids
          batch.setProjectionMatrix(cameraUtils.combined);
          batch.begin();

          renderAsteroids(batch);
          //Render aliens.
          if (!aliens.isEmpty()){
              for (int i = 0; i < aliens.size; i++) {
                  AlienStarship alienStarship = aliens.get(i);
                  if (alienStarship.isLiving){
                      if (!StarshipShooter.GAME_PAUSE) {
                          fireEnemyBullet(alienStarship);
                          if (autoFire) firePlayerBullet(alienStarship);
                          alienStarship.update(playerShip);
                      }
                      alienStarship.render(batch);
                  }else {
                      aliens.removeIndex(i);
                  }
              }
          }
          //Render bullets if any.
          if (!bullets.isEmpty()){
              for(int i = 0; i < bullets.size; i++){
                  Bullet bullet = bullets.get(i);
                  if (bullet.isLiving){
                      if (!StarshipShooter.GAME_PAUSE) bullet.update();
                      bullet.render(batch);
                  } else {
                      bullets.removeIndex(i);
                      bullet.reset();
                      if (bullets.size <= bulletPool.max){
                          bulletPool.free(bullet);
                      }
                  }
              }
          }
          //updates the effect pool.
          if (!effects.isEmpty() && particles){
              for (int i = 0; i < effects.size; i++) {
                  ParticleEffectPool.PooledEffect effect = effects.get(i);
                  effect.draw(batch,delta);
                  if (effect.isComplete()){
                      effect.free();
                      effects.removeIndex(i);
                  }
              }
          }
          rocketEffect.setPosition(playerShip.position.x, playerShip.position.y);
          playerShip.render(batch);
          batch.end();
      }
      if (!StarshipShooter.MENUS) {
          hud.render(batch,delta);
      }
        if (StarshipShooter.MENUS){
            starshipShooter.menuManager.render();
        }
    }

    public void reset(){
        try {
            dataParser.savePlayerData(player.getPlayerData());
        }catch (Exception exception){
            exception.printStackTrace();
        }
        scoreParser.updateHighScore();
        bullets.clear();
        asteroids.clear();
        aliens.clear();
        bulletPool.clear();
        RANDOM_XS_128.setSeed(RANDOM_XS_128.nextLong());
        playerShip = new Starship((int) (cameraUtils.viewportWidth/2.0f), (int) (cameraUtils.viewportHeight/2.0f),player);
        isGameOver = false;
        isDebuggingMode = false;
        level.reset();
        scoreCounter = 0;
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).free();
        }
    }
    private void renderWorldBackground(Batch batch){
        // TODO: 2/5/2024 Add the background with depth.
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
            offsetBackgrounds[0] = offsetBackgrounds[0] + 0.01f;
            offsetBackgrounds[1] = offsetBackgrounds[1] + 0.02f;
          for (int i = 0; i < backgrounds.length; i++) {
              if (offsetBackgrounds[i] > cameraUtils.viewportWidth) offsetBackgrounds[i] = 0;
              batch.draw(backgrounds[i], -offsetBackgrounds[i],0, cameraUtils.viewportWidth, cameraUtils.viewportHeight);
              batch.draw(backgrounds[i], -offsetBackgrounds[i] + cameraUtils.viewportWidth, 0, cameraUtils.viewportWidth, cameraUtils.viewportHeight);
          }

        batch.end();
    }

    //Renders the asteroids.
    private void renderAsteroids(Batch batch){
        if (isDebuggingMode){
            renderer.setProjectionMatrix(cameraUtils.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
        }
        //If there's any asteroids to render.
        if (!asteroids.isEmpty()) {
            for (int i = 0; i < asteroids.size; i++) {
                Asteroid asteroid = asteroids.get(i);
                //Checks whether of the asteroid is living.
                if (asteroid.isLiving){
                    if (asteroid instanceof SalusAsteroid){
                        //Gets the salus asteroid.
                        SalusAsteroid salusAsteroid = (SalusAsteroid) asteroid;
                        //Checks if the particles is true.
                        if (salusAsteroid.spawnHeart && particles){
                            ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                            effect.setPosition(asteroid.position.x,asteroid.position.y);
                            effects.add(effect);
                        }
                    }
                    if (!StarshipShooter.GAME_PAUSE){
                        asteroid.update(playerShip);
                        if (autoFire) firePlayerBullet(asteroid);
                    }
                    asteroid.render(batch);
                    //Debug only.
                    if (isDebuggingMode) {
                        Circle circle = asteroid.getBounds();
                        Circle player = getPlayerShip().getBounds();
                        for (AlienStarship alienStarship : aliens) {
                            Circle alien = alienStarship.getBounds();
                            renderer.circle(alien.x, alien.y, alien.radius);
                        }
                        renderer.circle(circle.x, circle.y, circle.radius);
                        renderer.circle(player.x, player.y, player.radius);
                    }
                    //If out of screen remove it from the hashMap.
                    if (asteroid.position.x < -4){
                        asteroids.removeIndex(i);
                    }
                }else {
                    asteroids.removeIndex(i);
                }
            }
        }
        if (isDebuggingMode){
            renderer.end();
        }
    }

    //update universe logic.
    public void update(){
        if (isGameOver || StarshipShooter.GAME_PAUSE) {
            scoreParser.updateHighScore();
            return;
        }

        fireBullet();
        handleInputs();
        checkBulletCollision();
        playerShip.update();
        //Checks if the player has been killed.
        if (!playerShip.isLiving){
            isGameOver = true;
        }
        cameraUtils.update();
        updateBackground();
        updateTouchControls();
        spawnObjects();
        if (!StarshipShooter.GAME_PAUSE){
           level.update(Gdx.graphics.getDeltaTime());
       }

        //Debug
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)){
            isGameOver = true;
        }
    }

    private void fireBullet(){
        if (!playerShip.isAttackUse){
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)){
                Bullet bullet = bulletPool.obtain();
                bullet.init(playerShip.position.x, playerShip.position.y);
                bullets.add(bullet);
                onShoot();
                playerShip.isAttackUse = true;
            }
            if (fireBullet){
                Bullet bullet = bulletPool.obtain();
                bullet.init(playerShip.position.x, playerShip.position.y);
                bullets.add(bullet);
                onShoot();
                playerShip.isAttackUse = true;
            }else {
                if (Gdx.input.isTouched(1)) {
                    Bullet bullet = bulletPool.obtain();
                    bullet.init(playerShip.position.x, playerShip.position.y);
                    bullets.add(bullet);
                    onShoot();
                    playerShip.isAttackUse = true;
                }
            }
        }
    }

    private void fireEnemyBullet(AlienStarship alienStarship){
        if (!alienStarship.isAttackUse){
            float py = playerShip.getPosition().y;
            float px = playerShip.getPosition().x;
            float ey = alienStarship.getPosition().y;
            float ex = alienStarship.getPosition().x;
            if (py + 2 >= ey && py + 2 <= ey + 2){
                if (bullets.size < bulletPool.max) {
                    Bullet bullet = bulletPool.obtain();
                    bullet.init(alienStarship.position.x,alienStarship.position.y);
                    bullet.isEnemy = true;
                    if (px > ex) {
                        bullet.fireRight = true;
                    }
                    bullets.add(bullet);
                    alienStarship.isAttackUse = true;
                }
            }
        }
    }

    private void firePlayerBullet(Entity entity){
        if (!playerShip.isAttackUse){
            float py = playerShip.getPosition().y;
            float px = playerShip.getPosition().x;
            float ey = entity.getPosition().y;
            float ex = entity.getPosition().x;
            if (ey <= py + 2 && ey >= py - 2 && px <= ex){
                Bullet bullet = bulletPool.obtain();
                bullet.init(playerShip.position.x, playerShip.position.y);
                bullet.fireRight = true;
                bullets.add(bullet);
                onShoot();
                playerShip.isAttackUse = true;
            }
        }
    }

    private void checkBulletCollision(){
        for (Bullet bullet : bullets) {
            Vector2 pos = bullet.getPosition();
            for (Asteroid asteroid: asteroids) {
              if (asteroid.getBounds().contains(pos)) {
                  if (particles){
                      ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                      effect.setPosition(bullet.position.x,bullet.position.y);
                      effects.add(effect);
                  }
                  //Checks if the bullet collied with a power up asteroid.
                  if (asteroid instanceof SalusAsteroid) {
                      SalusAsteroid salusAsteroid = (SalusAsteroid) asteroid;
                      if (salusAsteroid.spawnHeart){
                          salusAsteroid.checkDeath(() -> {
                              asteroid.isPowerUp = false;
                              playerShip.addHealth(2);
                          });
                      }else {
                          salusAsteroid.changeState();
                      }
                      bullet.isLiving = false;
                      onHit(asteroid);
                      continue;
                  }

                  bullet.isLiving = false;
                  asteroid.health +=  - 1;
                  onHit(asteroid);
                }
            }
            //If the bullet wasn't fired by enemy.
            if (!bullet.isEnemy) {
                for (AlienStarship alienStarship: aliens) {
                    if (alienStarship.getBounds().contains(pos)) {
                        if (particles){
                            ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                            effect.setPosition(bullet.position.x,bullet.position.y);
                            effects.add(effect);
                        }
                        bullet.isLiving = false;
                        alienStarship.addHealth(-1);
                        //Calculates the score.
                        if (alienStarship.health == 0){
                            SCORE += 1;
                            if (particles){
                                ParticleEffectPool.PooledEffect point = pointPoolEffect.obtain();
                                point.setPosition(bullet.position.x,bullet.position.y);
                                effects.add(point);
                            }
                        }
                        onHit(alienStarship);
                    }
                }
            }
            if (bullet.isEnemy){
                if (playerShip.getBounds().contains(bullet.position.x,bullet.position.y)){

                    if (particles){
                        ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                        effect.setPosition(bullet.position.x,bullet.position.y);
                        effects.add(effect);
                    }

                    bullet.isLiving = false;
                    playerShip.takeDamage(6);
                    onHit(playerShip);
                }
            }
        }
    }

    private void spawnObjects(){
        level.spawnEntities();
    }
    //Updates the background.
    private void updateBackground(){
        worldBackground.setCenter(cameraUtils.position.x,cameraUtils.position.y);
    }


    //handles key input events.
    private void handleInputs(){
        if (Gdx.app.getType() == Application.ApplicationType.Desktop){
            //Keyboard controls.
            //Zoom in-out

            if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
                cameraUtils.addZoom(-0.5f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.O)){
                cameraUtils.addZoom(0.5f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)){
                float yMove = 1f * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = playerShip.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(0,yMove);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)){
                float yMove = 1f * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = playerShip.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(0,-yMove);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)){
                float xMove = 1f * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = playerShip.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(-xMove,0);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)){
                float xMove = 1f * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = playerShip.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(xMove,0);
                }
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
        //Only debug.
        if (keycode == Input.Keys.F3){
            if (isDebuggingMode){
                isDebuggingMode = false;
                 starshipShooter.profiler.disable();
                starshipShooter. profiler = null;
            }else {
                isDebuggingMode = true;
                starshipShooter.profiler = new GLProfiler(Gdx.graphics);
                starshipShooter. profiler.enable();
            }
        }
        return false;
    }

    //Resizes the camera viewport.

    public void resize(int width, int height){
        viewport.update(width,height,true);
        cameraUtils.resizeViewport(width,height);
        hud.getStage().resize(width, height);
        starshipShooter.menuManager.resize(width, height);
        if (starshipShooter.MENUS){
            starshipShooter.menuManager.resize(width, height);
        }
    }

    //Dispose the resources to free up memory.
    @Override
    public void dispose() {
        batch.dispose();
        hud.dispose();
        renderer.dispose();
    }

    //Checks if objects is loaded.
    public boolean isObjectsLoaded() {
        if (!isObjectsLoaded) loadObjects();
        return isObjectsLoaded;
    }
    //Update touch controls.
    private void updateTouchControls(){
        //Only fires event when isTouch on screen.
        if (Gdx.input.isTouched(0)){
            //Gets the pixels xy coordinates.
            float offset;
            if (Gdx.app.getType() == Application.ApplicationType.Android){
                offset = 150f;
            }else {
                offset = 130f;
            }
            float xTouchPixels = Gdx.input.getX() + offset;
            float yTouchPixels = Gdx.input.getY();

            //Converts the touch coordinates to world coordinates.
            Vector2 touchPoint = new Vector2(xTouchPixels,yTouchPixels);
            touchPoint = cameraUtils.getViewport().unproject(touchPoint);
            Sprite sprite = playerShip.getSprite();
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

//                updateShipRotation(rotateDegrees);

                //Calculates the speed values for xy.
                float xMove = xTouchDifference / touchDistance * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                float yMove = yTouchDifference / touchDistance * playerShip.getSpeed() * Gdx.graphics.getDeltaTime();
                //Moves the player toward the move position.
                Sprite playerSprite = playerShip.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(xMove,yMove);
                }
            }
        }
        updateSpriteScreen(playerShip.getSprite());
    }

    //Checks if the current sprite is out of the screen coordinates.
    public boolean isOutScreen(Sprite sprite){
         float px = sprite.getX() + sprite.getWidth();
         float py = sprite.getY() + sprite.getHeight();
         int offset = 1;
         if (px > cameraUtils.viewportWidth) {
              sprite.translate(-0.01f,0);
             return true;
         }
         if (py > cameraUtils.viewportHeight) {
             sprite.translate(0,-0.01f);
             return true;
         }
         if (px - offset < 0){
             sprite.translate(0.01f,0);
             return true;
         }
        if (py - offset < 0){
            sprite.translate(0,0.01f);
            return true;
        }
         return false;
    }

    //Keeps the sprite on screen.
    public void updateSpriteScreen(Sprite sprite){
        if (sprite.getX() + sprite.getWidth() > cameraUtils.viewportWidth + 1){
            sprite.setPosition(cameraUtils.viewportWidth - 3,sprite.getY() + sprite.getOriginY());
        }
        if (sprite.getY() + sprite.getHeight() > cameraUtils.viewportHeight + 1){
            sprite.setPosition(sprite.getY() + sprite.getOriginY(),cameraUtils.viewportHeight - 3);
        }
        if (sprite.getX() + sprite.getWidth() < -1){
            sprite.setPosition(0,sprite.getY() + sprite.getOriginY());
        }
        if (sprite.getY() + sprite.getHeight() < -1){
            sprite.setPosition(sprite.getY() + sprite.getOriginY(),0);
        }
    }

    //rotates the ship with additional 270 degrees for the offset.
    private void updateShipRotation(float degrees){
        playerShip.getSprite().setRotation(degrees + 270);
    }

    public Starship getPlayerShip() {
        return playerShip;
    }

    public boolean isCollisionTest(Circle bounds) {
        for (Asteroid asteroid : asteroids) {
            Circle circle = asteroid.getBounds();
            if (circle.overlaps(bounds)) return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isDebuggingMode() {
        return isDebuggingMode;
    }

    public int getScoreCounter() {
        if (scoreCounter < SCORE) scoreCounter ++;
        return scoreCounter;
    }

    public Player getPlayer() {
        return player;
    }
}
