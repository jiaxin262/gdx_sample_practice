package com.jsample.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;
import com.jsample.game.model.BaseBodyModel;
import com.jsample.game.model.CarModel;
import com.jsample.game.utils.Transform;

public class GameScreen4 implements Screen {
    public static final String TAG = "GameScreen4";
    private static final float PXTM = 30;
    private Stage stage;
    private Stage uiStage;
    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera debugCamera;
    Body carBody;
    Body rearWheelBody, frontWheelBody;

    TextureRegion carBodyTextureRegin, wheelTextureRegin;
    Touchpad touchpad;

    RevoluteJoint rearWheelReJoint, frontWheelReJoint;
    PrismaticJoint rearWheelPrJoint, frontWheelPrJoint;

    Label distanceTextLabel;
    float currentDistance;
    float zoomOffset = 0.002f;
    float x, y;
    float speed = -9;

    public GameScreen4(Game game) {
        this.game = game;
        Gdx.input.setCatchBackKey(true);
        stage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ScreenViewport());
        Box2D.init();
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        float cameraWidth = Gdx.graphics.getWidth() / PXTM;
        float cameraHeight = Gdx.graphics.getHeight() / PXTM;
        camera = (OrthographicCamera) stage.getCamera();
        debugCamera = new OrthographicCamera(cameraWidth, cameraHeight);
        carBodyTextureRegin = new TextureRegion(new Texture("car_body.png"));
        wheelTextureRegin = new TextureRegion(new Texture("wheel.png"));

        distanceTextLabel = new Label("distance:0", MyGdxGame.skin, "big");
        distanceTextLabel.setPosition(0, Gdx.graphics.getHeight() - distanceTextLabel.getHeight());
        uiStage.addActor(distanceTextLabel);
        touchpad = new Touchpad(Transform.DpToPx(10), new Touchpad.TouchpadStyle(
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_bg.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_knob.png")))));
        touchpad.setSize(Transform.DpToPx(150), Transform.DpToPx(150));
        touchpad.setPosition(Gdx.graphics.getWidth() - touchpad.getWidth() - Gdx.graphics.getWidth()/15,
                Gdx.graphics.getHeight() / 15);
        touchpad.setColor(1, 1, 0, 0.5f);
        uiStage.addActor(touchpad);

        /** floor */
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(cameraWidth * 4, cameraHeight / 8);

        FixtureDef floorFixtureDef = new FixtureDef();
        floorFixtureDef.shape = floorShape;
        floorFixtureDef.density = 0;
        floorFixtureDef.friction = 1f;
        floorFixtureDef.restitution = 0;

        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.position.set(cameraWidth / 2 * 7, -cameraHeight / 2 + cameraHeight/8);
        Body floorBody = world.createBody(floorBodyDef);
        Fixture floorFixture = floorBody.createFixture(floorFixtureDef);
        floorShape.dispose();

        Texture img = new Texture("floor.png");
        img.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion region = new TextureRegion(img);
        region.setRegion(0, 0, Gdx.graphics.getWidth() * 4, Gdx.graphics.getHeight() / 8);
        Image image = new Image(region);
        image.setSize(Gdx.graphics.getWidth() * 8, Gdx.graphics.getHeight() / 4);
        image.setPosition(0, 0);
        stage.addActor(image);

        /** slope */
        PolygonShape slopeShape = new PolygonShape();
        Vector2[] slopeVector2s = new Vector2[3];
        slopeVector2s[0] = new Vector2(0, 0);
        slopeVector2s[1] = new Vector2(cameraWidth / 4, 0);
        slopeVector2s[2] = new Vector2(cameraWidth / 4, cameraHeight / 8);
        slopeShape.set(slopeVector2s);

        FixtureDef slopeFixtureDef = new FixtureDef();
        slopeFixtureDef.shape = slopeShape;
        slopeFixtureDef.density = 0.5f;
        slopeFixtureDef.friction = 1f;
        slopeFixtureDef.restitution = 0.3f;

        BodyDef slopeBodyDef = new BodyDef();
        slopeBodyDef.position.set(0, -cameraHeight / 2 + cameraHeight / 4);
        Body slopeBody = world.createBody(slopeBodyDef);
        slopeBody.createFixture(slopeFixtureDef);
        slopeShape.dispose();

        Texture slopeTexture = new Texture("slope.png");
        Image slopeImage = new Image(slopeTexture);
        slopeImage.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/4);
        stage.addActor(slopeImage);

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
        carBodyDef.position.set(-cameraWidth / 4, 0);
        carBody = world.createBody(carBodyDef);
        Vector2 carSize = new Vector2(carWidth, carHeight);
        CarModel carModel = new CarModel();
        carModel.setSize(carSize);
        carBody.setUserData(carModel);
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
        axlesBodyDef.position.set(-cameraWidth / 4 - carWidth / 2, 0);
        Body rearAxlesBody = world.createBody(axlesBodyDef);
        rearAxlesBody.createFixture(axlesFixtureDef);

        axlesBodyDef.position.set(-cameraWidth / 4 + carWidth / 2, 0);
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
        wheelBodyDef.position.set(-cameraWidth / 4 - carWidth / 2, 0);
        rearWheelBody = world.createBody(wheelBodyDef);
        rearWheelBody.createFixture(wheelFixtureDef);

        wheelBodyDef.position.set(-cameraWidth / 4 + carWidth / 2, 0);
        frontWheelBody = world.createBody(wheelBodyDef);
        frontWheelBody.createFixture(wheelFixtureDef);
        BaseBodyModel wheelyModel = new BaseBodyModel();
        wheelyModel.setSize(new Vector2(carWidth / 2, carWidth / 2));
        frontWheelBody.setUserData(wheelyModel);

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
        axlesCarPriJointDef.lowerTranslation = -carWidth / 8;
        axlesCarPriJointDef.upperTranslation = -carWidth / 4;
        axlesCarPriJointDef.enableLimit = true;
        axlesCarPriJointDef.enableMotor = true;
        axlesCarPriJointDef.initialize(carBody, frontAxlesBody, frontAxlesBody.getWorldCenter(), new Vector2(0, 1));
        frontWheelPrJoint = (PrismaticJoint) world.createJoint(axlesCarPriJointDef);

        axlesCarPriJointDef.initialize(carBody, rearAxlesBody, rearAxlesBody.getWorldCenter(), new Vector2(0, 1));
        rearWheelPrJoint = (PrismaticJoint) world.createJoint(axlesCarPriJointDef);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(new TitleScreen(game));
        }

        if (touchpad.isTouched()) {
            x = touchpad.getKnobPercentX() * speed;
            y = touchpad.getKnobPercentY() * speed;
        } else {
            x = y = 0;
        }

//        debugRenderer.render(world, debugCamera.combined);
        MyGdxGame.batch.setProjectionMatrix(camera.combined);

        MyGdxGame.batch.begin();
        updateWheels();
        updateCar();
        MyGdxGame.batch.end();

        stage.act();
        stage.draw();
        uiStage.act();
        uiStage.draw();
        world.step(1/45f, 6, 2);
    }

    private void updateCar() {
        CarModel carModel = (CarModel) carBody.getUserData();
        if (carModel != null) {
            carModel.setPosition(Transform.mtp(carBody.getPosition().x, carBody.getPosition().y, carModel.getSize(), PXTM));
            carModel.setRotation(MathUtils.radiansToDegrees * carBody.getAngle());
            MyGdxGame.batch.draw(carBodyTextureRegin, carModel.getPosX(), carModel.getPosY(),
                    carModel.getSize().x * PXTM, carModel.getSize().y * PXTM,
                    2 * carModel.getSize().x * PXTM, 5 * carModel.getSize().y * PXTM, 1, 1, carModel.getRotation());
            if (carModel.getPosX() + carModel.getSize().x * PXTM * 2 > camera.position.x) {
//                if (camera.zoom < 0.5f) {
//                    zoomOffset = 0.01f;
//                }
//                if (camera.zoom > 1.8) {
//                    zoomOffset = -0.01f;
//                }
//                camera.zoom += zoomOffset;
                camera.position.x = carModel.getPosX() + carModel.getSize().x * PXTM * 2;
                camera.update();
            }
            if (carModel.getPosX() < camera.position.x - Gdx.graphics.getWidth() / 3) {
                camera.position.x = carModel.getPosX() + Gdx.graphics.getWidth() / 3;
                camera.update();
            }
        }
        BaseBodyModel wheelModel = (BaseBodyModel) frontWheelBody.getUserData();
        if (wheelModel != null) {
            wheelModel.setPosition(Transform.mtp(frontWheelBody.getPosition().x + wheelModel.getSize().x/2,
                    frontWheelBody.getPosition().y + wheelModel.getSize().y/2, wheelModel.getSize(), PXTM));
            wheelModel.setRotation(MathUtils.radiansToDegrees * frontWheelBody.getAngle());
            MyGdxGame.batch.draw(wheelTextureRegin, wheelModel.getPosX(), wheelModel.getPosY(),
                    wheelModel.getSize().x * PXTM / 2, wheelModel.getSize().y * PXTM / 2,
                    wheelModel.getSize().x * PXTM, wheelModel.getSize().y * PXTM, 1, 1, wheelModel.getRotation());
            wheelModel.setPosition(Transform.mtp(rearWheelBody.getPosition().x + wheelModel.getSize().x/2,
                    rearWheelBody.getPosition().y + wheelModel.getSize().y/2, wheelModel.getSize(), PXTM));
            wheelModel.setRotation(MathUtils.radiansToDegrees * rearWheelBody.getAngle());
            MyGdxGame.batch.draw(wheelTextureRegin, wheelModel.getPosX() + wheelModel.getSize().x/2, wheelModel.getPosY() + wheelModel.getSize().y/2,
                    wheelModel.getSize().x * PXTM / 2, wheelModel.getSize().y * PXTM / 2,
                    wheelModel.getSize().x * PXTM, wheelModel.getSize().y * PXTM, 1, 1, wheelModel.getRotation());
        }
        if (carBody.getPosition().x - currentDistance > 10) {
            currentDistance = carBody.getPosition().x;
            distanceTextLabel.setText("distance:" + MathUtils.floor(currentDistance));
        }
    }

    private void updateWheels() {
//        rearWheelReJoint.setMotorSpeed(-2);
//        frontWheelReJoint.setMotorSpeed(-2);

        rearWheelReJoint.setMotorSpeed(x);
        frontWheelReJoint.setMotorSpeed(x);

        frontWheelPrJoint.setMaxMotorForce(Math.abs(frontWheelPrJoint.getJointTranslation()*600));
        frontWheelPrJoint.setMotorSpeed(frontWheelPrJoint.getMotorSpeed() - 2*frontWheelPrJoint.getJointTranslation());
        rearWheelPrJoint.setMaxMotorForce(Math.abs(rearWheelPrJoint.getJointTranslation()*600));
        rearWheelPrJoint.setMotorSpeed(rearWheelPrJoint.getMotorSpeed() - 2*rearWheelPrJoint.getJointTranslation());
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
        Gdx.app.log(TAG, "dispose");
        game.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
