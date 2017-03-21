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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen4 implements Screen {

    private static final float PXTM = 30;
    private Stage stage;
    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;

    RevoluteJoint rearWheelReJoint, frontWheelReJoint;
    PrismaticJoint rearWheelPrJoint, frontWheelPrJoint;


    public GameScreen4(Game game) {
        this.game = game;
        Gdx.input.setCatchBackKey(true);
        stage = new Stage(new ScreenViewport());
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        float cameraWidth = Gdx.graphics.getWidth() / PXTM;
        float cameraHeight = Gdx.graphics.getHeight() / PXTM;
        camera = new OrthographicCamera(cameraWidth, cameraHeight);

        /** floor */
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(cameraWidth / 2, cameraHeight / 16);

        FixtureDef floorFixtureDef = new FixtureDef();
        floorFixtureDef.shape = floorShape;
        floorFixtureDef.density = 0;
        floorFixtureDef.friction = 1f;
        floorFixtureDef.restitution = 0;

        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.position.set(0, -cameraHeight / 2);
        Body floorBody = world.createBody(floorBodyDef);
        Fixture floorFixture = floorBody.createFixture(floorFixtureDef);
        floorShape.dispose();

        /** car */
        float carWidth = cameraWidth / 6;
        float carHeight = cameraWidth / 34;
        PolygonShape carShape = new PolygonShape();
        carShape.setAsBox(carWidth, carHeight);

        FixtureDef carFixtureDef = new FixtureDef();
        carFixtureDef.shape = carShape;
        carFixtureDef.density = 0.5f;
        carFixtureDef.friction = 1f;
        carFixtureDef.restitution = 0.3f;
        carFixtureDef.filter.groupIndex = -1;

        BodyDef carBodyDef = new BodyDef();
        carBodyDef.type = BodyDef.BodyType.DynamicBody;
        carBodyDef.position.set(-cameraWidth / 4, -cameraHeight / 3);
        Body carBody = world.createBody(carBodyDef);
        carBody.createFixture(carFixtureDef);
        carShape.dispose();

        /** trunk */
        PolygonShape trunkShape = new PolygonShape();
        Vector2[] trunkVector2s = new Vector2[4];
        trunkVector2s[0] = new Vector2(-carWidth, carHeight);
        trunkVector2s[1] = new Vector2(0, carHeight);
        trunkVector2s[2] = new Vector2(0, carHeight * 4);
        trunkVector2s[3] = new Vector2(-carWidth, carHeight * 4);
        trunkShape.set(trunkVector2s);
        FixtureDef trunkFixtureDef = new FixtureDef();
        trunkFixtureDef.shape = trunkShape;
        trunkFixtureDef.density = 0.5f;
        trunkFixtureDef.friction = 1f;
        trunkFixtureDef.restitution = 0.3f;
        trunkFixtureDef.filter.groupIndex = -1;
        carBody.createFixture(trunkFixtureDef);
        trunkShape.dispose();

        /** hood */
        PolygonShape hoodShape = new PolygonShape();
        Vector2[] hoodVector2s = new Vector2[3];
        hoodVector2s[0] = new Vector2(0, carHeight);
        hoodVector2s[1] = new Vector2(0, carHeight * 4);
        hoodVector2s[2] = new Vector2(carWidth, carHeight);
        hoodShape.set(hoodVector2s);
        FixtureDef hoodFixtureDef = new FixtureDef();
        hoodFixtureDef.shape = hoodShape;
        hoodFixtureDef.density = 0.5f;
        hoodFixtureDef.friction = 1f;
        hoodFixtureDef.restitution = 0.3f;
        hoodFixtureDef.filter.groupIndex = -1;
        carBody.createFixture(hoodFixtureDef);
        hoodShape.dispose();

        /** axles */
        PolygonShape axlesShape = new PolygonShape();
        axlesShape.setAsBox(carWidth / 8, carWidth / 8);
        FixtureDef axlesFixtureDef = new FixtureDef();
        axlesFixtureDef.shape = axlesShape;
        axlesFixtureDef.density = 0.5f;
        axlesFixtureDef.friction = 0.5f;
        axlesFixtureDef.restitution = 0.3f;
        axlesFixtureDef.filter.groupIndex = -1;
        BodyDef axlesBodyDef = new BodyDef();
        axlesBodyDef.type = BodyDef.BodyType.DynamicBody;
        axlesBodyDef.position.set(-cameraWidth / 4 - carWidth / 2, -cameraHeight / 3);
        Body rearAxlesBody = world.createBody(axlesBodyDef);
        rearAxlesBody.createFixture(axlesFixtureDef);

        axlesBodyDef.position.set(-cameraWidth / 4 + carWidth / 2, -cameraHeight / 3);
        Body frontAxlesBody = world.createBody(axlesBodyDef);
        frontAxlesBody.createFixture(axlesFixtureDef);

        /** wheels */
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(carWidth / 4);
        FixtureDef wheelFixtureDef = new FixtureDef();
        wheelFixtureDef.shape = wheelShape;
        wheelFixtureDef.density = 1;
        wheelFixtureDef.friction = 0.9f;
        wheelFixtureDef.restitution = 0.1f;
        wheelFixtureDef.filter.groupIndex = -1;
        BodyDef wheelBodyDef = new BodyDef();
        wheelBodyDef.type = BodyDef.BodyType.DynamicBody;
        wheelBodyDef.position.set(-cameraWidth / 4 - carWidth / 2, -cameraHeight / 3);
        Body rearWheelBody = world.createBody(wheelBodyDef);
        rearWheelBody.createFixture(wheelFixtureDef);

        wheelBodyDef.position.set(-cameraWidth / 4 + carWidth / 2, -cameraHeight / 3);
        Body frontWheelBody = world.createBody(wheelBodyDef);
        frontWheelBody.createFixture(wheelFixtureDef);

        /** axles-wheelJoint */
        RevoluteJointDef rearWheelJointDef = new RevoluteJointDef();
        rearWheelJointDef.initialize(rearAxlesBody, rearWheelBody, rearAxlesBody.getWorldCenter());
        rearWheelJointDef.enableMotor = true;
        rearWheelJointDef.maxMotorTorque = 10000;
        rearWheelReJoint = (RevoluteJoint) world.createJoint(rearWheelJointDef);

        RevoluteJointDef frontWheelJointDef = new RevoluteJointDef();
        frontWheelJointDef.initialize(frontAxlesBody, frontWheelBody, frontAxlesBody.getWorldCenter());
        frontWheelJointDef.enableMotor = true;
        frontWheelJointDef.maxMotorTorque = 10000;
        frontWheelReJoint = (RevoluteJoint) world.createJoint(frontWheelJointDef);

        /** car-axlesJoint */
        PrismaticJointDef axlesCarPriJointDef = new PrismaticJointDef();
        axlesCarPriJointDef.lowerTranslation = -carWidth / 2;
        axlesCarPriJointDef.upperTranslation = -carWidth / 8;
        axlesCarPriJointDef.enableLimit = true;
        axlesCarPriJointDef.enableMotor = true;
        axlesCarPriJointDef.initialize(carBody, frontAxlesBody, frontAxlesBody.getWorldCenter(), new Vector2(0, 1));
        frontWheelPrJoint = (PrismaticJoint) world.createJoint(axlesCarPriJointDef);

        axlesCarPriJointDef.initialize(carBody, rearAxlesBody, rearAxlesBody.getWorldCenter(), new Vector2(0, 1));
        rearWheelPrJoint = (PrismaticJoint) world.createJoint(axlesCarPriJointDef);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }

        debugRenderer.render(world, camera.combined);
        updateWheels();

        stage.act();
        stage.draw();
        world.step(1/45f, 6, 2);
    }

    private void updateWheels() {
        rearWheelReJoint.setMotorSpeed(-2);
        frontWheelReJoint.setMotorSpeed(-2);

//        frontWheelPrJoint.setMaxMotorForce(Math.abs(frontWheelPrJoint.getJointTranslation()*600));
//        frontWheelPrJoint.setMotorSpeed(frontWheelPrJoint.getMotorSpeed() - 2*frontWheelPrJoint.getJointTranslation());
//        rearWheelPrJoint.setMaxMotorForce(Math.abs(rearWheelPrJoint.getJointTranslation()*600));
//        rearWheelPrJoint.setMotorSpeed(rearWheelPrJoint.getMotorSpeed() - 2*rearWheelPrJoint.getJointTranslation());
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
