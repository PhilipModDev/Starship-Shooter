package com.engine.starship.ui;

import static com.badlogic.gdx.Gdx.gl30;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MainMenu extends ScreenAdapter {

    private Stage stage;
    private Table table;
    private Skin skin;
    private Button button;

    @Override
    public void show() {

        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);

        skin = new Skin(Gdx.files.internal("ui/button_component.json"));
        button = new Button(skin);
        table.add(button).center();
        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
       stage.getViewport().update(width,height,true);
    }

    @Override
    public void render(float delta) {
       gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

       stage.act(Gdx.graphics.getDeltaTime());
       stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
