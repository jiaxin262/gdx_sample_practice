/************
 * Copyright (C) 2004 - 2017 UCWeb Inc. All Rights Reserved.
 * Description :
 * <p>
 * Creation    : 2017/3/16
 * Author      : jiaxin, jx124336@alibaba-inc.com
 */
package com.jsample.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameScreen3 implements Screen {
    public static final String TAG = "GameScreen3";
    private static final float PXTM = 30;

    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    Body body2;

    public GameScreen3(Game game) {
        this.game = game;
        Gdx.input.setCatchBackKey(true);
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        float cameraWidth = Gdx.graphics.getWidth() / PXTM;
        float cameraHeight = Gdx.graphics.getHeight() / PXTM;
        camera = new OrthographicCamera(cameraWidth, cameraHeight);
        camera = new OrthographicCamera(cameraWidth, cameraHeight);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 30.0f / PXTM);
        Body body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(2.0f, 2.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        addWalls();

        BodyDef kinematicBodyDef = new BodyDef();
        kinematicBodyDef.type = BodyDef.BodyType.KinematicBody;
        kinematicBodyDef.position.set(new Vector2(0.5f, -3f));
        Body kinematicBody = world.createBody(kinematicBodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(1f);
        kinematicBody.createFixture(shape, 0);
        kinematicBody.setLinearVelocity(2.0f, 0.0f);
        shape.dispose();

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.DynamicBody;
        bodyDef2.position.set(-cameraWidth / 2 + 5, -cameraHeight / 2 + 3.5f);
        body2 = world.createBody(bodyDef2);
        PolygonShape polygonShape2 = new PolygonShape();
        polygonShape2.setAsBox(2.0f, 2.0f);
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = polygonShape2;
        fixtureDef2.density = 0.3f;
        fixtureDef2.friction = 0.1f;
        fixtureDef2.restitution = 0.5f;
        body2.createFixture(fixtureDef2);
        polygonShape2.dispose();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }
        //body2.applyLinearImpulse(0.5f, 0.5f, body2.getPosition().x - 0.5f, body2.getPosition().y + 0.5f, false);
        //body2.applyForceToCenter(10.0f, 0f, true);
        body2.applyForce(40f, 40f, body2.getPosition().x - 0.5f, body2.getPosition().y + 0.5f, false);

        debugRenderer.render(world, camera.combined);
        world.step(1/45f, 6, 2);
    }

    private void addWalls() {
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(0, -camera.viewportHeight/2 + 1));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth / 2, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        groundBody.createFixture(fixtureDef);
        groundBox.dispose();

        BodyDef leftWallBodyDef =new BodyDef();
        leftWallBodyDef.position.set(new Vector2(-camera.viewportWidth / 2 + 1, 0));
        Body leftWallBody = world.createBody(leftWallBodyDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(0.5f, camera.viewportHeight / 2);
        leftWallBody.createFixture(leftWallBox, 0.5f);
        leftWallBox.dispose();

        BodyDef rightWallBodyDef =new BodyDef();
        rightWallBodyDef.position.set(new Vector2(camera.viewportWidth / 2 - 1, 0));
        Body rightWallBody = world.createBody(rightWallBodyDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0.5f, camera.viewportHeight / 2);
        rightWallBody.createFixture(rightWallBox, 0.5f);
        rightWallBox.dispose();

        BodyDef topWallBodyDef =new BodyDef();
        topWallBodyDef.position.set(new Vector2(0, camera.viewportHeight/2 - 1));
        Body topWallBody = world.createBody(topWallBodyDef);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(camera.viewportWidth / 2, 0.5f);
        FixtureDef topWallDef = new FixtureDef();
        topWallDef.shape = topWallBox;
        topWallBody.createFixture(topWallDef);
        topWallBox.dispose();
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
        game.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
