package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.utils.GameAssets;

public class CreditsMenu extends MenuObject{

    private final MenuManager manager;
    private Stage stage;
    private Table root;
    private Button exit;
    private List<String> creditsList;
    private ScrollPane scrollPane;

    public CreditsMenu(MenuManager manager) {
        this.manager = manager;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public void show() {
         Skin skin = GameAssets.uiSkin.getInstance();
         stage = new Stage(new ExtendViewport(
                 StarshipShooter.getInstance().guiCamera.viewportWidth,
                 StarshipShooter.getInstance().guiCamera.viewportHeight
         ));
        Gdx.input.setInputProcessor(stage);

        root = new Table();
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);

        exit = new Button(skin.get("x_menu_button",Button.ButtonStyle.class));

        creditsList = new List<>(skin);
        creditsList.setItems (
                "PhilipModDev (Lead Software Developer)",
                "OFFICIAL Biscuit_MC_YT (Lead Artist)",
                "Lolli Chan (Artist)"
        );

        scrollPane = new ScrollPane(creditsList,skin);
        scrollPane.setFadeScrollBars(false);

        //Called events.
        MenuManager.onChange(exit,() -> {
            GameAssets.hitSound.getInstance().play(0.2f);
            isVisible = false;
            manager.titleMenu.setVisible(true);
        });
        //Links to main credits.
        creditsList.addListener(new ActorGestureListener(){
          @Override
          public void tap(InputEvent event, float x, float y, int count, int button) {
              GameAssets.hitSound.getInstance().play(0.2f);
              String selectedItem = creditsList.getSelected();
              if (selectedItem.equals("PhilipModDev (Lead Developer)")){
                  Gdx.net.openURI("https://www.youtube.com/@PhilipModDev");
              }
              if (selectedItem.equals("OFFICIAL Biscuit_MC_YT (Lead Artist)")){
                  Gdx.net.openURI("https://www.youtube.com/@Biscuit_MCYT");
              }
          }
      });
        root.add(exit).expandX().center().padTop(25f).width(80).height(80);
        root.row();
        root.add(scrollPane).expand().center().width(500).height(490);
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
       stage.getViewport().update(width,height,true);
    }

    @Override
    public void hide() {
         stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
