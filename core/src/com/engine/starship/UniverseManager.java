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
import com.engine.starship.utils.events.InputEventSystem;
import com.engine.starship.utils.logic.entities.AlienStarship;
import com.engine.starship.utils.logic.entities.Asteroid;
import com.engine.starship.utils.logic.entities.Bullet;
import com.engine.starship.utils.logic.entities.Starship;
import com.engine.starship.utils.CameraUtils;
import com.engine.starship.utils.Constants;

public class UniverseManager extends InputEventSystem implements Disposable {
    private final StarshipShooter starshipShooter;
    public static final RandomXS128 RANDOM_XS_128 = new RandomXS128();
    public Array<AlienStarship> aliens;
    public Array<Asteroid> asteroids;
    public Array<Bullet> bullets;
    public Array<ParticleEffectPool.PooledEffect> effects;
    private Starship player;
    public CameraUtils cameraUtils;
    private SpriteBatch batch;
    private Sprite worldBackground;
    private Texture background;
    private float backgroundOffset;
    private boolean isObjectsLoaded = false;
    public boolean isDebuggingMode = false;
    private boolean isGameOver = false;
    public boolean fireBullet = false;
    public static int SCORE = 0;
    private ShapeRenderer renderer;
    private Viewport viewport;
    public Level level;
    public Hud hud;
    //A pool for all the bullets.
    public final Pool<Bullet> bulletPool = new Pool<Bullet>(1,5) {
        @Override
        protected Bullet newObject() {
            return new Bullet(player.position.x, player.position.y);
        }
    };

    public ParticleEffectPool hitPoolEffect;
    public ParticleEffectPool rocketPoolEffect;
    private ParticleEffectPool.PooledEffect rocketEffect;

    public UniverseManager(StarshipShooter starshipShooter){
        this.starshipShooter = starshipShooter;
    }

//    private ParticleEffect particleEffect;

    public void init(){
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
        cameraUtils = new CameraUtils(0,0);
        cameraUtils.setZoom(1);
        renderer = new ShapeRenderer();
        renderer.setColor(Color.RED);
        renderer.setAutoShapeType(true);
        background = new Texture(Gdx.files.internal("textures/star_background_0.png"));
        viewport = new StretchViewport(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
        hud = new Hud(this);
        hud.getStage().show();
        level = new Level(this);
    }

    public void loadObjects(){

        //Effect just for test
         ParticleEffect hit = new ParticleEffect();
         hit.setDuration(1);
         hit.load(Gdx.files.internal("particles/exploded.p"), StarshipShooter.getInstance().gameAssets.gameAtlas);
         hit.scaleEffect(0.03f);

         ParticleEffect rocket = new ParticleEffect();
         rocket.setDuration(1);
         rocket.load(Gdx.files.internal("particles/rocket_charge.p"), StarshipShooter.getInstance().gameAssets.gameAtlas);
         rocket.scaleEffect(0.03f);
         hitPoolEffect = new ParticleEffectPool(hit,1,4);
         rocketPoolEffect = new ParticleEffectPool(rocket,1,2);


        aliens = new Array<>();
        asteroids = new Array<>();
        bullets = new Array<>();
        effects = new Array<>();

        player = new Starship((int) (cameraUtils.viewportWidth/2.0f), (int) (cameraUtils.viewportHeight/2.0f));
        worldBackground = new Sprite(starshipShooter.gameAssets.getBackground());
        worldBackground.setSize(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
        worldBackground.setOrigin(Gdx.graphics.getWidth()/2.0f,Gdx.graphics.getHeight()/2.0f);
        isObjectsLoaded = true;

        rocketEffect = rocketPoolEffect.obtain();
        rocketEffect.setPosition(player.position.x,player.position.y);
        effects.add(rocketEffect);
    }

    public void renderObjects(float delta) {
        renderWorldBackground(batch);
      if (!isGameOver && !hud.getStage().isPause && !starshipShooter.renderMenus){
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
                      fireEnemyBullet(alienStarship);
                      alienStarship.update(player);
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
                      bullet.update();
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
          rocketEffect.setPosition(player.position.x,player.position.y);
          //updates the effect pool.
          if (!effects.isEmpty()){
              for (int i = 0; i < effects.size; i++) {
                  ParticleEffectPool.PooledEffect effect = effects.get(i);
                  effect.draw(batch,delta);
                  if (effect.isComplete()){
                      effect.free();
                      effects.removeIndex(i);
                  }
              }
          }
          player.render(batch);
          batch.end();
      }
      if (!starshipShooter.renderMenus) {
          hud.render(batch);
          hud.getStage().render(delta);
      }
        if (starshipShooter.renderMenus){
            starshipShooter.menuManager.render();
        }
    }

    public void reset(){
        bullets.clear();
        asteroids.clear();
        aliens.clear();
        bulletPool.clear();
        RANDOM_XS_128.setSeed(RANDOM_XS_128.nextLong());
        player = new Starship((int) (cameraUtils.viewportWidth/2.0f), (int) (cameraUtils.viewportHeight/2.0f));
        isGameOver = false;
        isDebuggingMode = false;
        level.reset();
        SCORE = 0;
        for (int i = 0; i < effects.size; i++) {
            effects.get(i).free();
        }
    }
    private void renderWorldBackground(Batch batch){
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        backgroundOffset += 0.01;
        if (backgroundOffset > cameraUtils.viewportHeight){
            backgroundOffset = 0;
        }
        batch.draw(background,0,-backgroundOffset,cameraUtils.viewportWidth ,cameraUtils.viewportHeight);
        batch.draw(background,0,-backgroundOffset + cameraUtils.viewportHeight,cameraUtils.viewportWidth ,cameraUtils.viewportHeight);
        batch.end();
    }

    //Renders the asteroids.
    private void renderAsteroids(Batch batch){
        if (isDebuggingMode){
            renderer.setProjectionMatrix(cameraUtils.combined);
            renderer.begin(ShapeRenderer.ShapeType.Line);
        }
        if (!asteroids.isEmpty()) {
            for (int i = 0; i < asteroids.size; i++) {
                Asteroid asteroid = asteroids.get(i);
                if (asteroid.isLiving){
                    asteroid.update(player);
                    asteroid.render(batch);
                    //Debug only.
                    if (isDebuggingMode) {
                        Circle circle = asteroid.getBounds();
                        Circle player = getPlayer().getBounds();
                        for (AlienStarship alienStarship : aliens) {
                            Circle alien = alienStarship.getBounds();
                            renderer.circle(alien.x, alien.y, alien.radius);
                        }
                        renderer.circle(circle.x, circle.y, circle.radius);
                        renderer.circle(player.x, player.y, player.radius);
                    }
                    //If out of screen remove it from the hashMap.
                    if (asteroid.position.x < 0){
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
        if (isGameOver || starshipShooter.isGamePaused || hud.getStage().isPause) return;

        fireBullet();
        handleInputs();
        checkBulletCollision();
        player.update();
        //Checks if the player has been killed.
        if (!player.isLiving){
            isGameOver = true;
        }
        cameraUtils.update();
        updateBackground();
        updateTouchControls();
        spawnObjects();
        if (!hud.getStage().isPause){
           level.update(Gdx.graphics.getDeltaTime());
       }
    }

    private void fireBullet(){
        if (!player.isAttackUse){
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)){
                Bullet bullet = bulletPool.obtain();
                bullet.init(player.position.x,player.position.y);
                bullets.add(bullet);
                onShoot();
                player.isAttackUse = true;
            }
            if (fireBullet){
                Bullet bullet = bulletPool.obtain();
                bullet.init(player.position.x, player.position.y);
                bullets.add(bullet);
                onShoot();
                player.isAttackUse = true;
            }else {
                if (Gdx.input.isTouched(1)) {
                    Bullet bullet = bulletPool.obtain();
                    bullet.init(player.position.x, player.position.y);
                    bullets.add(bullet);
                    onShoot();
                    player.isAttackUse = true;
                }
            }
        }
    }

    private void fireEnemyBullet(AlienStarship alienStarship){
        if (!alienStarship.isAttackUse){
            float py = player.getPosition().y;
            float px = player.getPosition().x;
            float ey = alienStarship.getPosition().y;
            float ex = alienStarship.getPosition().x;
            if (py + 1 >= ey && py + 1 <= ey + 1){
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

//    private void firePlayerBullet(Entity entity){
//        if (!player.isAttackUse){
//            float py = player.getPosition().y;
//            float px = player.getPosition().x;
//            float ey = entity.getPosition().y;
//            float ex = entity.getPosition().x;
//            if (ey <= py + 1 && ey >= py - 1 && px <= ex){
//                Bullet bullet = bulletPool.obtain();
//                bullet.init(player.position.x,player.position.y);
//                bullet.fireRight = true;
//                bullets.add(bullet);
//                onShoot();
//                player.isAttackUse = true;
//            }
//        }
//    }

    private void checkBulletCollision(){
        for (Bullet bullet : bullets) {
            Vector2 pos = bullet.getPosition();
            for (Asteroid asteroid: asteroids) {
              if (asteroid.getBounds().contains(pos)) {
                  ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                  effect.setPosition(bullet.position.x,bullet.position.y);
                  effects.add(effect);
                  if (asteroid.isPowerUp){
                      asteroid.isPowerUp = false;
                      player.addHealth(1);
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
                        ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                        effect.setPosition(bullet.position.x,bullet.position.y);
                        effects.add(effect);
                        bullet.isLiving = false;
                        alienStarship.addHealth(-1);
                        SCORE += 1;
                        onHit(alienStarship);
                    }
                }
            }
            if (bullet.isEnemy){
                if (player.getBounds().contains(bullet.position.x,bullet.position.y)){
                    ParticleEffectPool.PooledEffect effect = hitPoolEffect.obtain();
                    effect.setPosition(bullet.position.x,bullet.position.y);
                    effects.add(effect);
                    bullet.isLiving = false;
                    player.takeDamage(1);
                    onHit(player);
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
                float yMove = 1f * player.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = player.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(0,yMove);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)){
                float yMove = 1f * player.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = player.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(0,-yMove);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)){
                float xMove = 1f * player.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = player.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(-xMove,0);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)){
                float xMove = 1f * player.getSpeed() * Gdx.graphics.getDeltaTime();
                Sprite playerSprite = player.getSprite();
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
        if (starshipShooter.renderMenus){
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

//                updateShipRotation(rotateDegrees);


                //Calculates the speed values for xy.
                float xMove = xTouchDifference / touchDistance * player.getSpeed() * Gdx.graphics.getDeltaTime();
                float yMove = yTouchDifference / touchDistance * player.getSpeed() * Gdx.graphics.getDeltaTime();
                //Moves the player toward the move position.
                Sprite playerSprite = player.getSprite();
                if (!isOutScreen(playerSprite)){
                    playerSprite.translate(xMove,yMove);
                }
            }
        }
        updateSpriteScreen(player.getSprite());
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
        player.getSprite().setRotation(degrees + 270);
    }

    public Starship getPlayer() {
        return player;
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

    public boolean isDebuggingMode() {
        return isDebuggingMode;
    }
}
