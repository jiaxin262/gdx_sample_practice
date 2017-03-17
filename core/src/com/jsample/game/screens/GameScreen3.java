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
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;
import com.jsample.game.actors.Musician;
import com.jsample.game.model.GreenFace;
import com.jsample.game.utils.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameScreen3 implements Screen{
    public static final String TAG = "GameScreen3";
    private static final float PXTM = 30;

    private Stage stage;
    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    Body body;
    Body body2;
    Body groundBody;
    List<Body> bodyList = new ArrayList<Body>();
    DistanceJoint joint;

    TextureRegion texture;
    ParticleEffect effect;

    public GameScreen3(Game game) {
        this.game = game;
        Gdx.input.setCatchBackKey(true);
        stage = new Stage(new ScreenViewport());

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("musician.p"), MyGdxGame.textureAtlas);

        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new MyContactListener());
        debugRenderer = new Box2DDebugRenderer();
        float cameraWidth = Gdx.graphics.getWidth() / PXTM;
        float cameraHeight = Gdx.graphics.getHeight() / PXTM;
        camera = new OrthographicCamera(cameraWidth, cameraHeight);

        texture = new TextureRegion(new Texture("1111.png"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 8);
        body = world.createBody(bodyDef);
        Vector2 size = new Vector2(2.0f, 2.0f);
        GreenFace greenFace = new GreenFace();
        greenFace.setSize(size);
        body.setUserData(greenFace);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(size.x, size.y);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        bodyList.add(body);
        polygonShape.dispose();

        addWalls();

        BodyDef kinematicBodyDef = new BodyDef();
        kinematicBodyDef.type = BodyDef.BodyType.KinematicBody;
        kinematicBodyDef.position.set(new Vector2(0f, 0f));
        Body kinematicBody = world.createBody(kinematicBodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(1f);
        kinematicBody.createFixture(shape, 0);
        kinematicBody.setLinearVelocity(2.0f, 0.0f);
        shape.dispose();

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.DynamicBody;
        bodyDef2.position.set(-cameraWidth / 2 + 5, cameraHeight / 2 - 3.5f);
        bodyDef2.linearVelocity.set(25f, 15f);
        bodyDef2.angularVelocity = 5f;
        body2 = world.createBody(bodyDef2);
        body2.setUserData(new GreenFace());
        Vector2 sizeVector = new Vector2(2.0f, 2.0f);
        GreenFace greenFace2 = new GreenFace();
        greenFace2.setSize(sizeVector);
        body2.setUserData(greenFace2);
        PolygonShape polygonShape2 = new PolygonShape();
        polygonShape2.setAsBox(sizeVector.x, sizeVector.y);
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = polygonShape2;
        fixtureDef2.density = 1f;
        fixtureDef2.friction = 0.3f;
        fixtureDef2.restitution = 0.9f;
        body2.createFixture(fixtureDef2);
        bodyList.add(body2);
        polygonShape2.dispose();

        DistanceJointDef distanceJointDef = new DistanceJointDef();
        distanceJointDef.initialize(kinematicBody, body, new Vector2(0, 0), new Vector2(0, 0));
        joint = (DistanceJoint) world.createJoint(distanceJointDef);
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
        //body2.applyLinearImpulse(0.5f, 0.5f, body2.getPosition().x - 0.5f, body2.getPosition().y + 0.5f, false);
        body2.applyForceToCenter(40.0f, 10f, true);
        //body2.applyForce(40f, 40f, body2.getPosition().x - 0.5f, body2.getPosition().y + 0.5f, false);
        updateGreenFaces();

        debugRenderer.render(world, camera.combined);
        stage.act();
        stage.draw();
        world.step(1/45f, 6, 2);
    }

    private void updateGreenFaces() {
        MyGdxGame.batch.begin();
        for (int i = 0; i < bodyList.size(); i++) {
            Body body = bodyList.get(i);
            GreenFace greenFace = (GreenFace) body.getUserData();
            if (greenFace != null) {
                Vector2 pos = Transform.mtp(body.getPosition().x, body.getPosition().y, greenFace.getSize(), PXTM);
                greenFace.setPosition(pos);
                greenFace.setRotation(MathUtils.radiansToDegrees * body.getAngle());
                MyGdxGame.batch.draw(texture, greenFace.getPosX(), greenFace.getPosY(),
                        texture.getTexture().getWidth() / 2, texture.getTexture().getHeight() / 2,
                        texture.getTexture().getWidth(), texture.getTexture().getHeight(), 1, 1, greenFace.getRotation());
                effect.setPosition(greenFace.getPosX(),greenFace.getPosY());
                effect.draw(MyGdxGame.batch, 1/45f);
            }
        }
        MyGdxGame.batch.end();
    }

    private void addWalls() {
        BodyDef groundBodyDef =new BodyDef();
        groundBodyDef.position.set(new Vector2(0, -camera.viewportHeight/2));
        groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(camera.viewportWidth / 2, 0.1f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundBox;
        groundBody.createFixture(fixtureDef);
        groundBox.dispose();

        BodyDef leftWallBodyDef =new BodyDef();
        leftWallBodyDef.position.set(new Vector2(-camera.viewportWidth / 2, 0));
        Body leftWallBody = world.createBody(leftWallBodyDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(0.1f, camera.viewportHeight / 2);
        leftWallBody.createFixture(leftWallBox, 0.5f);
        leftWallBox.dispose();

        BodyDef rightWallBodyDef =new BodyDef();
        rightWallBodyDef.position.set(new Vector2(camera.viewportWidth / 2, 0));
        Body rightWallBody = world.createBody(rightWallBodyDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0.1f, camera.viewportHeight / 2);
        rightWallBody.createFixture(rightWallBox, 0.5f);
        rightWallBox.dispose();

        BodyDef topWallBodyDef =new BodyDef();
        topWallBodyDef.position.set(new Vector2(0, camera.viewportHeight/2));
        Body topWallBody = world.createBody(topWallBodyDef);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(camera.viewportWidth / 2, 0.1f);
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

    class MyContactListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            GreenFace greenFaceA = (GreenFace) contact.getFixtureA().getBody().getUserData();
            GreenFace greenFaceB = (GreenFace) contact.getFixtureB().getBody().getUserData();
            if (greenFaceA != null) {
                Gdx.app.log(TAG, "a-x,y:" + greenFaceA.getPosX() + "," + greenFaceA.getPosY());
            }
            if (greenFaceB != null) {
                Gdx.app.log(TAG, "b-x,y:" + greenFaceB.getPosX() + "," + greenFaceB.getPosY());
            }

        }

        @Override
        public void endContact(Contact contact) {
            GreenFace greenFaceA = (GreenFace) contact.getFixtureA().getBody().getUserData();
            GreenFace greenFaceB = (GreenFace) contact.getFixtureB().getBody().getUserData();
            if (greenFaceA != null) {
                Gdx.app.log(TAG, "end-a-x,y:" + greenFaceA.getPosX() + "," + greenFaceA.getPosY());
            }
            if (greenFaceB != null) {
                Gdx.app.log(TAG, "end-b-x,y:" + greenFaceB.getPosX() + "," + greenFaceB.getPosY());
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
