
package com.jsample.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;

public class TitleScreen implements Screen {
    public static final String TAG = "TitleScreen";

    private Stage stage;
    private Game game;
    private Table table;

    public TitleScreen(final Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setCatchBackKey(false);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);

        Label label = new Label("This is Title Screen!", MyGdxGame.skin, "big-black");
        label.setAlignment(Align.center);
        table.add(label).pad(10);
        //stage.addActor(label);

        TextButton textButton = new TextButton("play", MyGdxGame.skin);
        textButton.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.row();
        table.add(textButton).spaceTop(10);
        //stage.addActor(textButton);

        TextButton textButton3 = new TextButton("play3", MyGdxGame.skin);
        textButton3.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen3(game));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(textButton3).padTop(10);

        TextButton textButton2 = new TextButton("play2", MyGdxGame.skin);
        textButton2.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen2(game));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.row();
        table.add(textButton2).spaceTop(10);
        //stage.addActor(textButton2);

        TextButton textButton4 = new TextButton("play4", MyGdxGame.skin);
        textButton4.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen4(game));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.add(textButton4).padTop(10);

        Touchpad touchpad = new Touchpad(20f, new Touchpad.TouchpadStyle(
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_bg.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_knob.png")))));
        table.add(touchpad).width(Gdx.graphics.getDensity()*120).height(Gdx.graphics.getDensity()*120);
        //stage.addActor(touchpad);

        TextButton textButton5 = new TextButton("play5", MyGdxGame.skin);
        textButton5.addListener(new InputListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen5(game));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        table.row();
        table.add(textButton5).padTop(10);


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }
		stage.act();
		stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
