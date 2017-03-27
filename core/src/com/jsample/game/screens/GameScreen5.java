
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
            rightArmBodyRevJoint, leftLeg1_2RevJoint, rightLeg1_2RevJoint, leftArm1_2RevJoint, rightArm1_2RevJoint;
    PrismaticJoint headBodyPriJoint;

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

        /** floor */
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(cameraWidth, cameraHeight / 32);

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

        /** Body: body */
        float bodyWidth = cameraWidth / 36;
        float bodyHeight = cameraWidth / 12;
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(bodyWidth, bodyHeight);

        FixtureDef bodyFixtureDef = new FixtureDef();
        bodyFixtureDef.shape = bodyShape;
        bodyFixtureDef.density = 1f;
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
        headShape.setRadius(bodyWidth);

        FixtureDef headFixtureDef = new FixtureDef();
        headFixtureDef.shape = headShape;
        headFixtureDef.density = 1f;
        headFixtureDef.friction = 1f;
        headFixtureDef.restitution = 0.1f;
        headFixtureDef.filter.groupIndex = -1;

        BodyDef headBodyDef = new BodyDef();
        headBodyDef.type = BodyDef.BodyType.DynamicBody;
        headBodyDef.position.set(-cameraWidth / 4, bodyBody.getPosition().y + bodyHeight + bodyWidth);
        headBody = world.createBody(headBodyDef);
        headBody.createFixture(headFixtureDef);
        headShape.dispose();

        /** Joint: body-head */
        RevoluteJointDef headBodyRevJointDef = new RevoluteJointDef();
        headBodyRevJointDef.initialize(bodyBody, headBody, new Vector2(bodyBody.getPosition().x,
                bodyBody.getPosition().y + bodyHeight));
        headBodyRevJointDef.enableMotor = true;
        headBodyRevJointDef.enableLimit = true;
        headBodyRevJointDef.maxMotorTorque = 1000;
        headBodyRevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        headBodyRevJointDef.upperAngle = 0.15f * MathUtils.PI;
        headBodyRevJoint = (RevoluteJoint) world.createJoint(headBodyRevJointDef);

        /** Body: leftLeg1 */
        PolygonShape legArmShape = new PolygonShape();
        legArmShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        FixtureDef legFixtureDef = new FixtureDef();
        legFixtureDef.density = 1;
        legFixtureDef.friction = 1;
        legFixtureDef.restitution = 0.1f;
        legFixtureDef.shape = legArmShape;
        legFixtureDef.filter.groupIndex = -1;

        BodyDef legArmBodyDef = new BodyDef();
        legArmBodyDef.type = BodyDef.BodyType.DynamicBody;

        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        leftLegBody1 = world.createBody(legArmBodyDef);
        leftLegBody1.createFixture(legFixtureDef);

        /** Joint: body-leftLeg */
        RevoluteJointDef leftLegBodyRevJointDef = new RevoluteJointDef();
        leftLegBodyRevJointDef.initialize(bodyBody, leftLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight));
        leftLegBodyRevJointDef.enableMotor = true;
        leftLegBodyRevJointDef.enableLimit = true;
        leftLegBodyRevJointDef.maxMotorTorque = 1000;
        leftLegBodyRevJointDef.lowerAngle = -0.05f * MathUtils.PI;
        leftLegBodyRevJointDef.upperAngle = 0.05f * MathUtils.PI;
        leftLegBodyRevJoint = (RevoluteJoint) world.createJoint(leftLegBodyRevJointDef);
        leftLegBodyRevJoint.setMotorSpeed(-1);

        /** Body: leftLeg2 */
        legArmBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight);
        leftLegBody2 = world.createBody(legArmBodyDef);
        leftLegBody2.createFixture(legFixtureDef);

        /** Joint: leftLeg1-leftLeg2 */
        RevoluteJointDef leftLeg1_2RevJointDef = new RevoluteJointDef();
        leftLeg1_2RevJointDef.initialize(leftLegBody1, leftLegBody2,
                new Vector2(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight));
        leftLeg1_2RevJointDef.enableMotor = true;
        leftLeg1_2RevJointDef.enableLimit = true;
        leftLeg1_2RevJointDef.maxMotorTorque = 1000;
        leftLeg1_2RevJointDef.lowerAngle = -0.1f * MathUtils.PI;
        leftLeg1_2RevJointDef.upperAngle = 0.1f * MathUtils.PI;
        leftLeg1_2RevJoint = (RevoluteJoint) world.createJoint(leftLeg1_2RevJointDef);
        leftLeg1_2RevJoint.setMotorSpeed(-0.5f);

        /** Body: rightLeg1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        rightLegBody1 = world.createBody(legArmBodyDef);
        rightLegBody1.createFixture(legFixtureDef);

        /** Joint: body-rightLeg1 */
        RevoluteJointDef rightLegBodyRevJointDef = new RevoluteJointDef();
        rightLegBodyRevJointDef.initialize(bodyBody, rightLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight));
        rightLegBodyRevJointDef.enableMotor = true;
        rightLegBodyRevJointDef.enableLimit = true;
        rightLegBodyRevJointDef.maxMotorTorque = 1000;
        rightLegBodyRevJointDef.lowerAngle = -0.1f * MathUtils.PI;
        rightLegBodyRevJointDef.upperAngle = 0.1f * MathUtils.PI;
        rightLegBodyRevJoint = (RevoluteJoint) world.createJoint(rightLegBodyRevJointDef);
        rightLegBodyRevJoint.setMotorSpeed(1);

        /** Body: rightLeg2 */
        legArmBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight);
        rightLegBody2 = world.createBody(legArmBodyDef);
        rightLegBody2.createFixture(legFixtureDef);

        /** Joint: rightLeg1-rightLeg2 */
        RevoluteJointDef rightLeg1_2RevJointDef = new RevoluteJointDef();
        rightLeg1_2RevJointDef.initialize(rightLegBody1, rightLegBody2,
                new Vector2(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight));
        rightLeg1_2RevJointDef.enableMotor = true;
        rightLeg1_2RevJointDef.enableLimit = true;
        rightLeg1_2RevJointDef.maxMotorTorque = 1000;
        rightLeg1_2RevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        rightLeg1_2RevJointDef.upperAngle = 0.15f * MathUtils.PI;
        rightLeg1_2RevJoint = (RevoluteJoint) world.createJoint(rightLeg1_2RevJointDef);
//        rightLeg1_2RevJoint.setMotorSpeed(-4);

        /** Body: leftArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        leftArmBody1 = world.createBody(legArmBodyDef);
        leftArmBody1.createFixture(legFixtureDef);

        /** Joint: body-leftArm1 */
        RevoluteJointDef leftArmBodyRevJointDef = new RevoluteJointDef();
        leftArmBodyRevJointDef.initialize(bodyBody, leftArmBody1,
                new Vector2(bodyBody.getPosition().x - bodyWidth, bodyBody.getPosition().y + bodyWidth));
        leftArmBodyRevJointDef.enableMotor = true;
        leftArmBodyRevJointDef.enableLimit = true;
        leftArmBodyRevJointDef.maxMotorTorque = 1000;
        leftArmBodyRevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        leftArmBodyRevJointDef.upperAngle = 0.15f * MathUtils.PI;
        leftArmBodyRevJoint = (RevoluteJoint) world.createJoint(leftArmBodyRevJointDef);

        /** Body: leftArm2 */
        legArmBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight);
        leftArmBody2 = world.createBody(legArmBodyDef);
        leftArmBody2.createFixture(legFixtureDef);

        /** Joint: leftArm1-leftArm2 */
        RevoluteJointDef leftArm1_2RevJointDef = new RevoluteJointDef();
        leftArm1_2RevJointDef.initialize(leftArmBody1, leftArmBody2,
                new Vector2(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight));
        leftArm1_2RevJointDef.enableMotor = true;
        leftArm1_2RevJointDef.enableLimit = true;
        leftArm1_2RevJointDef.maxMotorTorque = 1000;
        leftArm1_2RevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        leftArm1_2RevJointDef.upperAngle = 0.15f * MathUtils.PI;
        leftArm1_2RevJoint = (RevoluteJoint) world.createJoint(leftArm1_2RevJointDef);

        /** Body: rightArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyWidth);
        rightArmBody1 = world.createBody(legArmBodyDef);
        rightArmBody1.createFixture(legFixtureDef);

        /** Joint: body-rightArm1 */
        RevoluteJointDef rightArmBodyRevJointDef = new RevoluteJointDef();
        rightArmBodyRevJointDef.initialize(bodyBody, rightArmBody1,
                new Vector2(bodyBody.getPosition().x + bodyWidth, bodyBody.getPosition().y + bodyWidth));
        rightArmBodyRevJointDef.enableMotor = true;
        rightArmBodyRevJointDef.enableLimit = true;
        rightArmBodyRevJointDef.maxMotorTorque = 1000;
        rightArmBodyRevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        rightArmBodyRevJointDef.upperAngle = 0.15f * MathUtils.PI;
        rightArmBodyRevJoint = (RevoluteJoint) world.createJoint(rightArmBodyRevJointDef);

        /** Body: rightArm2 */
        legArmBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight);
        rightArmBody2 = world.createBody(legArmBodyDef);
        rightArmBody2.createFixture(legFixtureDef);

        /** Joint: rightArm1-rightArm2 */
        RevoluteJointDef rightArm1_2RevJointDef = new RevoluteJointDef();
        rightArm1_2RevJointDef.initialize(rightArmBody1, rightArmBody2,
                new Vector2(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight));
        rightArm1_2RevJointDef.enableMotor = true;
        rightArm1_2RevJointDef.enableLimit = true;
        rightArm1_2RevJointDef.maxMotorTorque = 1000;
        rightArm1_2RevJointDef.lowerAngle = -0.15f * MathUtils.PI;
        rightArm1_2RevJointDef.upperAngle = 0.15f * MathUtils.PI;
        rightArm1_2RevJoint = (RevoluteJoint) world.createJoint(rightArm1_2RevJointDef);

        legArmShape.dispose();
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
//        headBodyRevJoint.setMotorSpeed(x);
//        leftLegBodyRevJoint.setMotorSpeed(x);
//        rightLegBodyRevJoint.setMotorSpeed(-x);
//        leftArmBodyRevJoint.setMotorSpeed(x);
//        rightArmBodyRevJoint.setMotorSpeed(-x);
//        leftLeg1_2RevJoint.setMotorSpeed(x);
//        rightLeg1_2RevJoint.setMotorSpeed(-x);
//        leftArm1_2RevJoint.setMotorSpeed(x);
//        rightArm1_2RevJoint.setMotorSpeed(-x);

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
