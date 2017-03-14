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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;

public class GameScreen implements Screen {
    public static final String TAG = "GameScreen";

    private Game game;
    private Stage stage;
    private Stage uiStage;
    private OrthographicCamera camera;
    private Slider slider;
    private InputMultiplexer multiplexer;

    private float currentX, currentY, currentZ;
    private float imgWidth, imgHeight;
    private float zoomOffset = 0.01f;
    private float lastDistance, lastInitalDistance;
    private float minZoom;

    public GameScreen(Game game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(stage);

        camera = (OrthographicCamera) stage.getCamera();
        currentX = camera.position.x;
        currentY = camera.position.y;
        currentZ = camera.zoom;

        Image image = new Image(new Texture(Gdx.files.internal("map.jpg")));
        imgWidth = image.getWidth();
        imgHeight = image.getHeight();
        minZoom = imgWidth / Gdx.graphics.getWidth();
        image.addListener(new ActorGestureListener() {
            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                //Gdx.app.log(TAG, "deltaX:" + deltaX + ",deltaY:" + deltaY);
            }
        });
        stage.addActor(image);

        slider = new Slider(0.3f, minZoom, 0.01f, true, MyGdxGame.skin);
        slider.setAnimateInterpolation(Interpolation.smooth);
        slider.setHeight(Gdx.graphics.getHeight()*0.8f);
        slider.setWidth(slider.getWidth() * 2);
        slider.setPosition(20, Gdx.graphics.getHeight()/10);
        slider.setValue((0.3f + minZoom) / 2);
        currentZ = slider.getValue();
        slider.addListener(new InputListener(){
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                camera.zoom = 0.3f + minZoom - slider.getValue();
                currentZ = camera.zoom;
                camera.update();
                Gdx.app.log(TAG,"dragged-slider Value:"+slider.getValue());
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "up-slider Value:"+slider.getValue());
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log(TAG, "down-slider Value:"+slider.getValue());
                return true;
            }
        });
        uiStage.addActor(slider);

    }

    @Override
    public void show() {
        //Gdx.input.setInputProcessor(new GestureDetector(new MapInputListener()));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        moveCamera();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.log(TAG, "back-pressed");
            game.setScreen(new TitleScreen(game));
        }
        uiStage.act();
        stage.act();
        stage.draw();
        uiStage.draw();
    }

    private void moveCamera() {
        camera.position.x = currentX;
        camera.position.y = currentY;
        //camera.zoom = currentZ;
        camera.update();
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
        uiStage.dispose();
    }

    class MapInputListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Gdx.app.log(TAG, "touchDown");
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Gdx.app.log(TAG, "tap");
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            Gdx.app.log(TAG, "longPress");
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            Gdx.app.log(TAG, "fling");
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            currentX -= deltaX * currentZ;
            currentY += deltaY * currentZ;
            if (currentX < Gdx.graphics.getWidth() / 2 * currentZ) {
                currentX = Gdx.graphics.getWidth() / 2 * currentZ;
            }
            if (currentX > imgWidth - Gdx.graphics.getWidth() / 2 * currentZ) {
                currentX = imgWidth - Gdx.graphics.getWidth() / 2 * currentZ;
            }
            if (currentY < Gdx.graphics.getHeight() / 2 * currentZ) {
                currentY = Gdx.graphics.getHeight() / 2 * currentZ;
            }
            if (currentY > imgHeight - Gdx.graphics.getHeight() / 2 * currentZ) {
                currentY = imgHeight - Gdx.graphics.getHeight() / 2 * currentZ;
            }
            return true;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            Gdx.app.log(TAG, "panStop");
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            if (lastDistance == 0 || lastInitalDistance != initialDistance) {
                lastDistance = initialDistance;
            }
            currentZ -= (distance - lastDistance) / 10 * zoomOffset;
            if (currentZ < 0.3) {
                currentZ = 0.3f;
            }
            if (currentZ > minZoom) {
                currentZ = minZoom;
            }
            lastDistance = distance;
            lastInitalDistance = initialDistance;
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            Gdx.app.log(TAG, "pinch");
            return false;
        }

        @Override
        public void pinchStop() {
            Gdx.app.log(TAG, "pinchStop");
        }
    }

}
