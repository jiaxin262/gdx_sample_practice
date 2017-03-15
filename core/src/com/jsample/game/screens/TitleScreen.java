/************
 * Copyright (C) 2004 - 2017 UCWeb Inc. All Rights Reserved.
 * Description :
 * <p>
 * Creation    : 2017/3/14
 * Author      : jiaxin, jx124336@alibaba-inc.com
 */
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

    public TitleScreen(final Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setCatchBackKey(false);

        Label label = new Label("This is Title Screen!", MyGdxGame.skin, "big-black");
        label.setAlignment(Align.center);
        label.setWidth(Gdx.graphics.getWidth());
        label.setY(Gdx.graphics.getHeight() * 2 / 3);
        stage.addActor(label);

        TextButton textButton = new TextButton("play", MyGdxGame.skin);
        textButton.setWidth(Gdx.graphics.getWidth() / 3);
        textButton.setPosition(Gdx.graphics.getWidth() / 2 - textButton.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - textButton.getHeight() / 2);
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
        stage.addActor(textButton);

        TextButton textButton2 = new TextButton("play2", MyGdxGame.skin);
        textButton2.setWidth(Gdx.graphics.getWidth() / 3);
        textButton2.setPosition(Gdx.graphics.getWidth() / 2 - textButton2.getWidth() / 2,
                Gdx.graphics.getHeight() / 3 - textButton2.getHeight() / 2);
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
        stage.addActor(textButton2);

        Touchpad touchpad = new Touchpad(20f, new Touchpad.TouchpadStyle(
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_bg.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_knob.png")))));
        touchpad.setPosition(100, 30);
        touchpad.setSize(400, 400);
        stage.addActor(touchpad);


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
