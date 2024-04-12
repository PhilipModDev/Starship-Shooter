package com.engine.starship.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.GameAssets;
import com.engine.starship.utils.Player;
import com.engine.starship.utils.StarShipFactory;
import com.engine.starship.utils.io.data.StarShipData;
import java.util.HashMap;
import java.util.Objects;

public class ShopMenu extends MenuObject {

    private final MenuManager manager;
    private Stage stage;
    private final Camera gui = StarshipShooter.getInstance().guiCamera;
    private final HashMap<String, TextureAtlas.AtlasRegion> shopList;
    private int selectIndex = 0;
    private BitmapFont font;
    private final TextureAtlas.AtlasRegion uraniumGem;
    public ShopMenu(MenuManager manager){
        this.manager = manager;

        uraniumGem = GameAssets.uraniumGem.getInstance();
        //Creates the starships the the play can buy or equip.
        shopList = new HashMap<>();
        shopList.put("tier 1",GameAssets.tierOneStarShip.getInstance());
        shopList.put("tier 2",GameAssets.tierTwoStarShip.getInstance());
        shopList.put("tier 3",GameAssets.tierThreeStarShip.getInstance());
        shopList.put("tier 4", GameAssets.tierFourStarShip.getInstance());
    }
    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }


    @Override
    public void show() {
        font = new BitmapFont(Gdx.files.internal("ui/font/pixel.fnt"));
        font.getData().scale(0.5f);
        Skin skin = GameAssets.uiSkin.getInstance();

        stage = new Stage(new ExtendViewport(gui.viewportWidth,gui.viewportHeight));
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.setDebug(false);
        stage.addActor(root);
        //Creates the home button.
        Button home = new Button(skin.get("home_button",Button.ButtonStyle.class));
        //Adds the check button.
        Button check = new Button(skin.get("check",Button.ButtonStyle.class));
        //Creates the right arrow button.
        Button rightArrow = new Button(skin.get("right_arrow",Button.ButtonStyle.class));
        //Creates new left arrow button.
        Button leftArrow = new Button(skin.get("left_arrow",Button.ButtonStyle.class));

        MenuManager.onChange(home,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            manager.titleMenu.setVisible(true);
            this.isVisible = false;
        });

        MenuManager.onChange(rightArrow,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            setSelectIndex(1);
        });
        MenuManager.onChange(leftArrow,() ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            setSelectIndex(-1);
        });
        MenuManager.onChange(check, () ->{
            GameAssets.hitSound.getInstance().play(0.2f);
            equipItem();
        });
        //Adds the components to the root table.
        int width = 60, height = 100;
        root.add(leftArrow).width(width).height(height).expand().left().bottom().padLeft(width);
        root.add(rightArrow).width(width).height(height).expand().right().bottom().padRight(width);
        root.row();
        root.add(home).width(150).height(150).expand().left().bottom().padLeft(10).padBottom(10);
        root.add(check).width(150).height(150).expand().right().bottom().padRight(10).padBottom(10);
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }


    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        renderSelectedStarShip();
        renderUraniumGems();
    }

    private void renderSelectedStarShip() {
        StarShipData data = manager.universeManager.getPlayer().getEquipStarShip();
        String name = data.name;
        if (name == null && !data.isEquip) return;
        TextureAtlas.AtlasRegion region = null;
        if (selectIndex == 0) region = shopList.get("tier 1");
        if (selectIndex == 1) region = shopList.get("tier 2");
        if (selectIndex == 2) region = shopList.get("tier 3");
        if (selectIndex == 3) region = shopList.get("tier 4");
        if (region == null) return;
        Batch batch = stage.getBatch();
        float scale = 2;
        batch.begin();
        batch.draw(region,
                (gui.viewportWidth/2.0f) - (region.originalWidth + 120),(gui.viewportHeight/2.0f) - (region.originalHeight + 150),
                (region.originalWidth + 130) * scale,(region.originalHeight + 150) * scale);
        batch.end();
    }

    private void renderUraniumGems(){
        Batch batch = stage.getBatch();
        batch.begin();
        batch.draw(uraniumGem,50,gui.viewportHeight - 80,
                uraniumGem.originalWidth * 8,
                uraniumGem.originalHeight * 8
        );
        font.draw(batch, String.valueOf(manager.universeManager.getTotalUraniumGems()), 130, gui.viewportHeight - 40);
        StarShipData data = getSelectedItem();
        if (!data.unlock && !data.name.equals("tier 1")){
            if (data.name.equals("tier 2")){
                batch.draw(uraniumGem,gui.viewportWidth/2.3f,gui.viewportHeight - 100,
                        uraniumGem.originalWidth * 10,
                        uraniumGem.originalHeight * 10
                );
                font.draw(batch, String.valueOf(10), gui.viewportWidth/1.9f, gui.viewportHeight - 45);
            }
            if (data.name.equals("tier 3")){
                batch.draw(uraniumGem,gui.viewportWidth/2.3f,gui.viewportHeight - 100,
                        uraniumGem.originalWidth * 10,
                        uraniumGem.originalHeight * 10
                );
                font.draw(batch, String.valueOf(20), gui.viewportWidth/1.9f, gui.viewportHeight - 45);
            }
            if (data.name.equals("tier 4")){
                batch.draw(uraniumGem,gui.viewportWidth/2.3f,gui.viewportHeight - 100,
                        uraniumGem.originalWidth * 10,
                        uraniumGem.originalHeight * 10
                );
                font.draw(batch, String.valueOf(30), gui.viewportWidth/1.9f, gui.viewportHeight - 45);
            }
        }
        batch.end();
    }

    public void setSelectIndex(int amount) {
        int index = selectIndex + amount;
        int max = shopList.size() - 1;
        if (index < 0) {
            selectIndex = max;
            return;
        }
        if (index > max) {
            selectIndex = 0;
            return;
        }
        this.selectIndex = index;
    }

    public StarShipData getSelectedItem(){
        UniverseManager universeManager = manager.universeManager;
        Player player = universeManager.getPlayer();
        if (selectIndex == 0){
            return player.getStarShipData("tier 1");
        }
        if (selectIndex == 1){
            return player.getStarShipData("tier 2");
        }
        if (selectIndex == 2){
            return player.getStarShipData("tier 3");
        }
        if (selectIndex == 3){
            return player.getStarShipData("tier 4");
        }
        return null;
    }

    private void equipItem(){
        UniverseManager universeManager = manager.universeManager;
        Player player = universeManager.getPlayer();
        if (selectIndex == 0){
            StarShipData data = player.getStarShipData("tier 1");
            if (data == null) return;
            player.unLockStarShip(data);
            player.equipStarShip(data.name);
        }
        if (selectIndex == 1){
            System.out.println(true);
            StarShipData data = player.getStarShipData("tier 2");
            if (data == null) return;
            if (!data.unlock) {
                if (player.getPlayerData().getUraniumGem() >= 10){
                    player.addGem(-10);
                    data.unlock = true;
                } else return;
            }
            player.unLockStarShip(data);
            player.equipStarShip(data.name);
        }
        if (selectIndex == 2){
            //Displays the selected shop items.
            StarShipData data = StarShipFactory.createShipData("tier 3");
            if (data == null) return;
            if (!data.unlock) {
                if (player.getPlayerData().getUraniumGem() >= 20){
                    player.addGem(-20);
                    data.unlock = true;
                } else return;
            }
            player.unLockStarShip(data);
            player.equipStarShip(data.name);
        }
        if (selectIndex == 3){
            StarShipData data = StarShipFactory.createShipData("tier 4");
            if (data == null) return;
            if (!data.unlock) {
                if (player.getPlayerData().getUraniumGem() >= 30){
                    player.addGem(-30);
                    data.unlock = true;
                } else return;
            }
            player.unLockStarShip(data);
            player.equipStarShip(data.name);
        }
        universeManager.reloadPlayer();
    }

    @Override
    public void hide() {
        font.dispose();
        stage.dispose();
    }


    @Override
    public void dispose() {
        font.dispose();
        stage.dispose();
    }

    public HashMap<String, TextureAtlas.AtlasRegion> getShopList() {
        return shopList;
    }
}
