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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;
import com.jsample.game.actors.Musician;

public class GameScreen2 implements Screen {

    private Game game;
    private Stage stage;
    private ParticleEffect effect;

    public GameScreen2(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setCatchBackKey(true);

        Musician musician = new Musician();
        musician.setX(Gdx.graphics.getWidth() / 12);
        stage.addActor(musician);

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("fire_note.p"), Gdx.files.internal("note.png"));
        effect.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }
        stage.act();
        stage.draw();

        effect.setEmittersCleanUpBlendFunction(false);
        stage.getBatch().begin();
        effect.draw(stage.getBatch(), delta);
        stage.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.getBatch().end();
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
        effect.dispose();
    }

}
