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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.actors.Musician;
import com.jsample.game.utils.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameScreen2 implements Screen {
    public static final String TAG = "GameScreen2";

    private Game game;
    private Stage stage;
    private ParticleEffect effect;
    private ParticleEffectPool particleEffectPool;
    private List<ParticleEffect> effects;
    Touchpad touchpad;
    float x, y;
    float speed = 9;
    Musician musician;

    boolean bScarce;
    int randomHighMin;
    int randomHighMax;
    //创建粒子系统
    //放大系数
    float scale_lowMin;
    float scale_lowMax;
    float scale_highMin;
    float scale_highMax;
    //移动系数
    float move_lowMin;
    float move_lowMax;
    float move_highMin;
    float move_highMax;
    int randomX = 0;
    int randomY = 0;

    public GameScreen2(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setCatchBackKey(true);

        musician = new Musician();
        musician.setX(Gdx.graphics.getWidth() / 10);
        stage.addActor(musician);

        effect = new ParticleEffect();
        //effect.load(Gdx.files.internal("fire_note.p"), Gdx.files.internal("note.png"));
        effect.load(Gdx.files.internal("heartballoon.p"), Gdx.files.internal("balloon/heart.png"));
        effect.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        particleEffectPool = new ParticleEffectPool(effect, 3, 3);
        effects = new ArrayList<ParticleEffect>();
        for (int i = 1; i < 8; i++) {
            effect.loadEmitterImages(Gdx.files.internal("balloon/" + i + ".png"));

            bScarce = (int) (Math.random() * 10.0f) < 2;
            randomHighMin = bScarce ? 180 : 80;
            randomHighMax = bScarce ? 200 : 145;
            //创建粒子系统
            //放大系数
            scale_lowMin = 0;
            scale_lowMax = 0;
            scale_highMin = Transform.DpToPx(26);
            scale_highMax = Transform.DpToPx(28);
            //移动系数
            move_lowMin = Transform.DpToPx(15);
            move_lowMax = Transform.DpToPx(25);
            move_highMin = Transform.DpToPx(randomHighMin);
            move_highMax = Transform.DpToPx(randomHighMax);
            effect.getEmitters().get(0).getScale().setLow(scale_lowMin, scale_lowMax);
            effect.getEmitters().get(0).getScale().setHigh(scale_highMin, scale_highMax);
            effect.getEmitters().get(0).getVelocity().setLow(move_lowMin, move_lowMax);
            effect.getEmitters().get(0).getVelocity().setHigh(move_highMin, move_highMax);
            ParticleEffect effectTmp = particleEffectPool.obtain();

            randomX = (int) (Math.random() * 20) + (Gdx.graphics.getWidth() / 2);
            randomY = 100;
            effectTmp.setPosition(randomX, randomY);
            effects.add(effectTmp);
        }

        touchpad = new Touchpad(Transform.DpToPx(10), new Touchpad.TouchpadStyle(
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_bg.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_knob.png")))));
        touchpad.setSize(Transform.DpToPx(150), Transform.DpToPx(150));
        touchpad.setPosition(Gdx.graphics.getWidth() * 2 / 3, Gdx.graphics.getHeight() / 3);
        stage.addActor(touchpad);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }
        if (touchpad.isTouched()) {
            Gdx.app.log(TAG, "touchpad.getKnobPercentX(),touchpad.getKnobPercentY():" + touchpad.getKnobPercentX() + "," + touchpad.getKnobPercentY());
            x = touchpad.getKnobPercentX() * speed;
            y = touchpad.getKnobPercentY() * speed;
        } else {
            x = y = 0;
        }
        musician.setPosition(musician.getX() + x, musician.getY() + y);
        stage.act();
        stage.draw();

        //effect.setEmittersCleanUpBlendFunction(false);
        stage.getBatch().begin();
        for (ParticleEffect effect : effects) {
            effect.draw(stage.getBatch(), delta);
        }

        //stage.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
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
