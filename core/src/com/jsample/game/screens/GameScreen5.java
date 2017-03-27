
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
import com.badlogic.gdx.physics.box2d.Shape;
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
            rightArmBodyRevJoint, leftLeg1_2BodyRevJoint, rightLeg1_2BodyRevJoint, leftArm1_2BodyRevJoint, rightArm1_2BodyRevJoint;
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

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef = configFixtureDef(fixtureDef, 0, 1, 0, floorShape, (short)0);

        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.position.set(0, -cameraHeight / 2);
        Body floorBody = world.createBody(floorBodyDef);
        Fixture floorFixture = floorBody.createFixture(fixtureDef);
        floorShape.dispose();

        /** Body: body */
        float bodyWidth = cameraWidth / 36;
        float bodyHeight = cameraWidth / 12;
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(bodyWidth, bodyHeight);

        fixtureDef = configFixtureDef(fixtureDef, 1, 1, 0.3f, bodyShape, (short)-1);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(-cameraWidth / 4, cameraHeight / 2);
        bodyBody = world.createBody(bodyDef);
        Vector2 bodySize = new Vector2(bodyWidth, bodyHeight);
        BodyModel bodyModel = new BodyModel();
        bodyModel.setSize(bodySize);
        bodyBody.setUserData(bodyModel);
        bodyBody.createFixture(fixtureDef);
        bodyShape.dispose();

        /** Body: head */
        CircleShape headShape = new CircleShape();
        headShape.setRadius(bodyWidth);

        fixtureDef = configFixtureDef(fixtureDef, 1f, 1f, 0.3f, headShape, (short)-1);

        BodyDef headBodyDef = new BodyDef();
        headBodyDef.type = BodyDef.BodyType.DynamicBody;
        headBodyDef.position.set(-cameraWidth / 4, bodyBody.getPosition().y + bodyHeight + bodyWidth);
        headBody = world.createBody(headBodyDef);
        headBody.createFixture(fixtureDef);
        headShape.dispose();

        RevoluteJointDef revJointDef = new RevoluteJointDef();
        /** Joint: body-head */
        revJointDef = configRevoluteJointDef(revJointDef, bodyBody, headBody,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y + bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        headBodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: leftLeg1 */
        PolygonShape legArmShape = new PolygonShape();
        legArmShape.setAsBox(bodyWidth / 2, bodyHeight / 2);

        fixtureDef = configFixtureDef(fixtureDef, 1f, 1f, 0.3f, headShape, (short)-1);

        BodyDef legArmBodyDef = new BodyDef();
        legArmBodyDef.type = BodyDef.BodyType.DynamicBody;
        legArmBodyDef.position.set(bodyBody.getPosition().x - bodyWidth, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        leftLegBody1 = world.createBody(legArmBodyDef);
        leftLegBody1.createFixture(fixtureDef);

        /** Joint: body-leftLeg */
        revJointDef = configRevoluteJointDef(revJointDef, bodyBody, leftLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        leftLegBodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: leftLeg2 */
        legArmBodyDef.position.set(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight);
        leftLegBody2 = world.createBody(legArmBodyDef);
        leftLegBody2.createFixture(fixtureDef);

        /** Joint: leftLeg1-leftLeg2 */
        revJointDef = configRevoluteJointDef(revJointDef, leftLegBody1, leftLegBody2,
                new Vector2(leftLegBody1.getPosition().x, leftLegBody1.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        leftLeg1_2BodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: rightLeg1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x + bodyWidth, bodyBody.getPosition().y - bodyHeight - bodyHeight / 2);
        rightLegBody1 = world.createBody(legArmBodyDef);
        rightLegBody1.createFixture(fixtureDef);

        /** Joint: body-rightLeg1 */
        revJointDef = configRevoluteJointDef(revJointDef, bodyBody, rightLegBody1,
                new Vector2(bodyBody.getPosition().x, bodyBody.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        rightLegBodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: rightLeg2 */
        legArmBodyDef.position.set(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight);
        rightLegBody2 = world.createBody(legArmBodyDef);
        rightLegBody2.createFixture(fixtureDef);

        /** Joint: rightLeg1-rightLeg2 */
        revJointDef = configRevoluteJointDef(revJointDef, rightLegBody1, rightLegBody2,
                new Vector2(rightLegBody1.getPosition().x, rightLegBody1.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        rightLeg1_2BodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: leftArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x - bodyWidth - bodyWidth / 2, bodyBody.getPosition().y + bodyWidth);
        leftArmBody1 = world.createBody(legArmBodyDef);
        leftArmBody1.createFixture(fixtureDef);

        /** Joint: body-leftArm1 */
        revJointDef = configRevoluteJointDef(revJointDef, bodyBody, leftArmBody1,
                new Vector2(bodyBody.getPosition().x - bodyWidth, bodyBody.getPosition().y + bodyWidth),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        leftArmBodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: leftArm2 */
        legArmBodyDef.position.set(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight);
        leftArmBody2 = world.createBody(legArmBodyDef);
        leftArmBody2.createFixture(fixtureDef);

        /** Joint: leftArm1-leftArm2 */
        revJointDef = configRevoluteJointDef(revJointDef, leftArmBody1, leftArmBody2,
                new Vector2(leftArmBody1.getPosition().x, leftArmBody1.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        leftArm1_2BodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: rightArm1 */
        legArmBodyDef.position.set(bodyBody.getPosition().x + bodyWidth + bodyWidth / 2, bodyBody.getPosition().y + bodyWidth);
        rightArmBody1 = world.createBody(legArmBodyDef);
        rightArmBody1.createFixture(fixtureDef);

        /** Joint: body-rightArm1 */
        revJointDef = configRevoluteJointDef(revJointDef, bodyBody, rightArmBody1,
                new Vector2(bodyBody.getPosition().x + bodyWidth, bodyBody.getPosition().y + bodyWidth),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        rightArmBodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        /** Body: rightArm2 */
        legArmBodyDef.position.set(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight);
        rightArmBody2 = world.createBody(legArmBodyDef);
        rightArmBody2.createFixture(fixtureDef);

        /** Joint: rightArm1-rightArm2 */
        revJointDef = configRevoluteJointDef(revJointDef, rightArmBody1, rightArmBody2,
                new Vector2(rightArmBody1.getPosition().x, rightArmBody1.getPosition().y - bodyHeight),
                true, true, 10000, -0.15f * MathUtils.PI, 0.15f * MathUtils.PI);
        rightArm1_2BodyRevJoint = (RevoluteJoint) world.createJoint(revJointDef);

        legArmShape.dispose();
    }

    private RevoluteJointDef configRevoluteJointDef(RevoluteJointDef revoluteJointDef, Body body1, Body body2,
                                                    Vector2 anchor, boolean enableMotor, boolean enableLimit,
                                                    float maxMotorTorque, float lowerAngle, float upperAngle) {
        revoluteJointDef.initialize(body1, body2, anchor);
        revoluteJointDef.enableMotor = enableMotor;
        revoluteJointDef.enableLimit = enableLimit;
        revoluteJointDef.maxMotorTorque = maxMotorTorque;
        revoluteJointDef.lowerAngle = lowerAngle;
        revoluteJointDef.upperAngle = upperAngle;
        return revoluteJointDef;
    }

    private FixtureDef configFixtureDef(FixtureDef fixtureDef, float density, float friction,
                                        float restitution, Shape shape, short groupIndex) {
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;
        fixtureDef.filter.groupIndex = groupIndex;
        return fixtureDef;
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
        headBodyRevJoint.setMotorSpeed(x);
        leftLegBodyRevJoint.setMotorSpeed(x);
        rightLegBodyRevJoint.setMotorSpeed(-x);
        leftArmBodyRevJoint.setMotorSpeed(x);
        rightArmBodyRevJoint.setMotorSpeed(-x);
        leftLeg1_2BodyRevJoint.setMotorSpeed(x);
        rightLeg1_2BodyRevJoint.setMotorSpeed(-x);
        leftArm1_2BodyRevJoint.setMotorSpeed(x);
        rightArm1_2BodyRevJoint.setMotorSpeed(-x);

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
