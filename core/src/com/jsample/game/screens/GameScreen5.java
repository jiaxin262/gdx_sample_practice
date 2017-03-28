
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
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.MyGdxGame;
import com.jsample.game.model.BodyModel;
import com.jsample.game.utils.Transform;

public class GameScreen5 implements Screen{

    private static final float PXTM = 30;
    private Stage stage;
    private Stage uiStage;
    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera debugCamera;
    Body bodyBody, headBody, leftArmBody1, leftArmBody2, rightArmBody1, rightArmBody2,
        leftLegBody1, leftLegBody2, rightLegBody1, rightLegBody2;
    RevoluteJoint headBodyRevJoint, leftLegBodyRevJoint, rightLegBodyRevJoint, leftArmBodyRevJoint,
            rightArmBodyRevJoint, leftLeg1KneeRevJoint, rightLeg1KneeRevJoint,
            leftLeg2KneeRevJoint, rightLeg2KneeRevJoint, leftArm1ElbowRevJoint, rightArm1ElbowRevJoint,
            leftArm2ElbowRevJoint, rightArm2ElbowRevJoint;
    PrismaticJoint headBodyPriJoint;
    DistanceJoint platformDistanceJoint;

    Touchpad touchpad;

    float x, y;
    float speed = -9;

    public GameScreen5(Game game) {
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

        touchpad = new Touchpad(Transform.DpToPx(10), new Touchpad.TouchpadStyle(
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_bg.png"))),
                new TextureRegionDrawable(new TextureRegion(new Texture("touchpad_knob.png")))));
        touchpad.setSize(Transform.DpToPx(150), Transform.DpToPx(150));
        touchpad.setPosition(Gdx.graphics.getWidth() - touchpad.getWidth() - Gdx.graphics.getWidth()/15,
                Gdx.graphics.getHeight() / 15);
        touchpad.setColor(1, 1, 0, 0.5f);
        uiStage.addActor(touchpad);

        /** Body: floor */
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(cameraWidth, cameraHeight / 32);

        FixtureDef floorFixtureDef = new FixtureDef();
        floorFixtureDef.shape = floorShape;
        floorFixtureDef.density = 0;
        floorFixtureDef.friction = 0.1f;
        floorFixtureDef.restitution = 0;

        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.position.set(0, -cameraHeight / 2);
        Body floorBody = world.createBody(floorBodyDef);
        Fixture floorFixture = floorBody.createFixture(floorFixtureDef);
        floorShape.dispose();

        /** Body: body */
        float bodyWidth = cameraWidth / 36;
        float bodyHeight = cameraWidth / 12;
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(bodyWidth, bodyHeight);

        FixtureDef bodyFixtureDef = new FixtureDef();
        bodyFixtureDef.shape = bodyShape;
        bodyFixtureDef.density = 0.5f;
        bodyFixtureDef.friction = 1f;
        bodyFixtureDef.restitution = 0.1f;
        bodyFixtureDef.filter.groupIndex = -1;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-cameraWidth / 4, 0);
        bodyBody = world.createBody(bodyDef);
        Vector2 bodySize = new Vector2(bodyWidth, bodyHeight);
        BodyModel bodyModel = new BodyModel();
        bodyModel.setSize(bodySize);
        bodyBody.setUserData(bodyModel);
        bodyBody.createFixture(bodyFixtureDef);
        bodyShape.dispose();

        /** Body: head */
        CircleShape headShape = new CircleShape();
        headShape.setRadius(bodyWidth * 2);

        FixtureDef headFixtureDef = new FixtureDef();
        headFixtureDef.shape = headShape;
        headFixtureDef.density = 0.5f;
        headFixtureDef.friction = 1f;
        headFixtureDef.restitution = 0.1f;
        headFixtureDef.filter.groupIndex = -1;

        BodyDef headBodyDef = new BodyDef();
        headBodyDef.type = BodyDef.BodyType.DynamicBody;
        headBodyDef.position.set(-cameraWidth / 4, bodyBody.getPosition().y + bodyHeight + bodyWidth * 2);
        headBody = world.createBody(headBodyDef);
        headBody.createFixture(headFixtureDef);
        headShape.dispose();

        /** Joint: body-head */
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef = configRevJointDef(revoluteJointDef, bodyBody, headBody,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight),
                true, true, 1000, -0.15f, 0.15f);
        headBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftLeg1 */
        PolygonShape legArmShape = new PolygonShape();
        legArmShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        FixtureDef legFixtureDef = new FixtureDef();
        legFixtureDef.density = 0.5f;
        legFixtureDef.friction = 0.1f;
        legFixtureDef.restitution = 0.1f;
        legFixtureDef.shape = legArmShape;
        legFixtureDef.filter.groupIndex = -1;

        BodyDef legArmBodyDef = new BodyDef();
        legArmBodyDef.type = BodyDef.BodyType.DynamicBody;

        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        leftLegBody1 = world.createBody(legArmBodyDef);
        leftLegBody1.createFixture(legFixtureDef);

        /** Joint: body-leftLeg */
        revoluteJointDef = configRevJointDef(revoluteJointDef, bodyBody, leftLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight),
                true, true, 1000, -0.07f, 0.07f);
        leftLegBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        leftLegBodyRevJoint.setMotorSpeed(-0.5f);

        /** Body: leftKnee */
        CircleShape kneeShape = new CircleShape();
        kneeShape.setRadius(bodyWidth / 2);

        FixtureDef kneeFixtureDef = new FixtureDef();
        kneeFixtureDef.shape = kneeShape;
        kneeFixtureDef.friction = 0.1f;
        kneeFixtureDef.density = 0.5f;
        kneeFixtureDef.restitution = 0.1f;
        kneeFixtureDef.filter.groupIndex = -1;

        BodyDef kneeBodyDef = new BodyDef();
        kneeBodyDef.type = BodyDef.BodyType.DynamicBody;
        kneeBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight / 2);
        Body leftKneeBody = world.createBody(kneeBodyDef);
        leftKneeBody.createFixture(kneeFixtureDef);

        /** Joint: leftLeg1-knee */
        revoluteJointDef = configRevJointDef(revoluteJointDef, leftLegBody1, leftKneeBody,
                leftKneeBody.getWorldCenter(), true, true, 1000, 0, 0);
        leftLeg1KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        leftLeg1KneeRevJoint.setMotorSpeed(-0.5f);

        /** Body: leftLeg2 */
        legArmBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight);
        leftLegBody2 = world.createBody(legArmBodyDef);
        leftLegBody2.createFixture(legFixtureDef);

        /** Joint: leftLeg2-knee */
        revoluteJointDef = configRevJointDef(revoluteJointDef, leftLegBody2, leftKneeBody,
                leftKneeBody.getWorldCenter(), true, true, 1000, -0.07f, 0.07f);
        leftLeg2KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        leftLeg2KneeRevJoint.setMotorSpeed(0.5f);

        /** Body: rightLeg1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        rightLegBody1 = world.createBody(legArmBodyDef);
        rightLegBody1.createFixture(legFixtureDef);

        /** Joint: body-rightLeg1 */
        revoluteJointDef = configRevJointDef(revoluteJointDef, bodyBody, rightLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight),
                true, true, 1000, -0.1f, 0.1f);
        rightLegBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        rightLegBodyRevJoint.setMotorSpeed(0.5f);

        /** Body: rightKnee */
        kneeBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight / 2);
        Body rightKneeBody = world.createBody(kneeBodyDef);
        rightKneeBody.createFixture(kneeFixtureDef);

        /** Joint: rightLeg1-knee */
        revoluteJointDef = configRevJointDef(revoluteJointDef, rightLegBody1, rightKneeBody,
                rightKneeBody.getWorldCenter(), true, true, 1000, 0, 0);
        rightLeg1KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightLeg1KneeRevJoint.setMotorSpeed(-1);

        /** Body: rightLeg2 */
        legArmBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight);
        rightLegBody2 = world.createBody(legArmBodyDef);
        rightLegBody2.createFixture(legFixtureDef);

        /** Joint: rightLeg2-knee */
        revoluteJointDef = configRevJointDef(revoluteJointDef, rightLegBody2, rightKneeBody,
                rightKneeBody.getWorldCenter(), true, true, 1000, -0.07f, 0.07f);
        rightLeg2KneeRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        rightLeg2KneeRevJoint.setMotorSpeed(0.5f);

        /** Body: leftArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        leftArmBody1 = world.createBody(legArmBodyDef);
        leftArmBody1.createFixture(legFixtureDef);

        /** Joint: body-leftArm1 */
        revoluteJointDef = configRevJointDef(revoluteJointDef, bodyBody, leftArmBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight - bodyWidth / 2),
                true, true, 1000, 0.05f, 0.5f);
        leftArmBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftElbow */
        kneeBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight / 2);
        Body leftElbowBody = world.createBody(kneeBodyDef);
        leftElbowBody.createFixture(kneeFixtureDef);

        /** Joint: leftArm1-elbow */
        revoluteJointDef = configRevJointDef(revoluteJointDef, leftArmBody1, leftElbowBody,
                leftElbowBody.getWorldCenter(), true, true, 1000, 0, 0);
        leftArm1ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: leftArm2 */
        legArmBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight);
        leftArmBody2 = world.createBody(legArmBodyDef);
        leftArmBody2.createFixture(legFixtureDef);

        /** Joint: leftArm2-elbow */
        revoluteJointDef = configRevJointDef(revoluteJointDef, leftArmBody2, leftElbowBody,
                leftElbowBody.getWorldCenter(), true, true, 1000, 0, 0);
        leftArm2ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: rightArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        rightArmBody1 = world.createBody(legArmBodyDef);
        rightArmBody1.createFixture(legFixtureDef);

        /** Joint: body-rightArm1 */
        revoluteJointDef = configRevJointDef(revoluteJointDef, bodyBody, rightArmBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight - bodyWidth / 2),
                true, true, 1000, -0.5f, 0.1f);
        rightArmBodyRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightArmBodyRevJoint.setMotorSpeed(-0.5f);

        /** Body: rightElbow */
        kneeBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight / 2);
        Body rightElbowBody = world.createBody(kneeBodyDef);
        rightElbowBody.createFixture(kneeFixtureDef);

        /** Joint: rightArm1-elbow */
        revoluteJointDef = configRevJointDef(revoluteJointDef, rightArmBody1, rightElbowBody,
                rightElbowBody.getWorldCenter(), true, true, 1000, 0, 0);
        rightArm1ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        /** Body: rightArm2 */
        legArmBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight);
        rightArmBody2 = world.createBody(legArmBodyDef);
        rightArmBody2.createFixture(legFixtureDef);

        /** Joint: rightArm2-elbow */
        revoluteJointDef = configRevJointDef(revoluteJointDef, rightArmBody2, rightElbowBody,
                rightElbowBody.getWorldCenter(), true, true, 3000, -0.8f, 0.1f);
        rightArm2ElbowRevJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
//        rightArm2ElbowRevJoint.setMotorSpeed(-1f);

        /** Body: platform */
        PolygonShape platformShape = new PolygonShape();
        platformShape.setAsBox(cameraWidth / 8, cameraHeight / 64);

        FixtureDef platformFixtureDef = new FixtureDef();
        platformFixtureDef.shape = platformShape;
        platformFixtureDef.density = 0;
        platformFixtureDef.friction = 0.1f;
        platformFixtureDef.restitution = 0;

        BodyDef platformBodyDef = new BodyDef();
        platformBodyDef.position.set(bodyBody.getPosition().x, rightLegBody2.getPosition().y - bodyHeight / 2);
        Body platformBody = world.createBody(platformBodyDef);
        platformBody.createFixture(platformFixtureDef);
        platformShape.dispose();

        /** Joint: platformDistanceJoint */
//        DistanceJointDef distanceJointDef = new DistanceJointDef();
//        distanceJointDef.initialize(platformBody, bodyBody, platformBody.getWorldCenter(), bodyBody.getWorldCenter());
//        platformDistanceJoint = (DistanceJoint) world.createJoint(distanceJointDef);

        kneeShape.dispose();
        legArmShape.dispose();
    }

    private RevoluteJointDef configRevJointDef(RevoluteJointDef revoluteJointDef, Body body1, Body body2,
                                               Vector2 anchor, boolean enableMotor, boolean enableLimit,
                                               float maxMotorTorque, float lowerAngle, float upperAngle) {
        revoluteJointDef.initialize(body1, body2, anchor);
        revoluteJointDef.enableMotor = enableMotor;
        revoluteJointDef.enableLimit = enableLimit;
        revoluteJointDef.maxMotorTorque = maxMotorTorque;
        revoluteJointDef.lowerAngle = lowerAngle * MathUtils.PI;
        revoluteJointDef.upperAngle = upperAngle * MathUtils.PI;
        return revoluteJointDef;
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
                debugRenderer.render(world, debugCamera.combined);
        MyGdxGame.batch.setProjectionMatrix(camera.combined);

//        MyGdxGame.batch.begin();
//        MyGdxGame.batch.end();

        if (touchpad.isTouched()) {
            x = touchpad.getKnobPercentX() * speed;
            y = touchpad.getKnobPercentY() * speed;
        } else {
            x = y = 0;
        }
        leftArmBodyRevJoint.setMotorSpeed(y);
        rightArmBodyRevJoint.setMotorSpeed(x);

        stage.act();
        stage.draw();
        uiStage.act();
        uiStage.draw();
        world.step(1/45f, 6, 2);
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
