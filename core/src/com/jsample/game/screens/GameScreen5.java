
package com.jsample.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jsample.game.BodyManager;
import com.jsample.game.MyGdxGame;
import com.jsample.game.model.BodyModel;
import com.jsample.game.utils.FixtureDefBuilder;
import com.jsample.game.utils.RevoluteJointDefBuilder;

public class GameScreen5 implements Screen{
    public static final String TAG = "GameScreen5";

    public static final float PXTM = 30;
    private Stage stage;
    private Stage uiStage;
    public Game game;
    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera debugCamera;

    BodyManager mBodyManager;

    Vector2 dragStartPoint, dragStopPoint, draggingPoint;

    float x, y;
    boolean isDistanceJointCreated;
    boolean isRevJoint3Destroyed;
    int count;
    float shootRadians, jointRadians;

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

        dragStartPoint = new Vector2();
        dragStopPoint = new Vector2();
        draggingPoint = new Vector2();

        mBodyManager = new BodyManager(world);

        uiStage.addCaptureListener(new MyGestureDetector());
    }

    class MyGestureDetector extends DragListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            dragStartPoint.set(0, 0);
            draggingPoint.set(0, 0);
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);
        }

        @Override
        public void dragStart(InputEvent event, float x, float y, int pointer) {
            dragStartPoint.set(x, y);
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            draggingPoint.set(x, y);
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer) {
            dragStopPoint.set(x, y);
        }
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

        if (Gdx.input.isTouched()) {
            if (!isDistanceJointCreated) {
                isDistanceJointCreated = true;
                mBodyManager.createPlatformDistanceJoint();
            }
            Gdx.app.log(TAG, "armDistanceJoint.getLength():" + mBodyManager.getArmDistanceJoint().getLength());
        }

//        if (Gdx.input.justTouched()) {
//            Gdx.app.log(TAG, "count:" + count);
//            count ++;
//            if (!isRevJoint3Destroyed && count > 3) {
//                isRevJoint3Destroyed = true;
//                world.destroyJoint(platformDistanceJoint3);
//                world.destroyJoint(platformDistanceJoint);
//                world.destroyJoint(platformDistanceJoint2);
//                leftLeg1KneeRevJoint.setLimits(-1f, 1f);
//                leftLeg2KneeRevJoint.setLimits(-1f, 1f);
//                bodyBody.applyLinearImpulse(800, 0, bodyBody.getWorldCenter().x, bodyBody.getWorldCenter().y, true);
//            }
//        }

        if (Gdx.input.isTouched() && Math.abs(draggingPoint.x - dragStartPoint.x) > 50) {
            shootRadians = MathUtils.atan2(draggingPoint.y - dragStartPoint.y, draggingPoint.x - dragStartPoint.x);
            if (shootRadians >= -MathUtils.PI && shootRadians <= MathUtils.PI / 2) {
                shootRadians += MathUtils.PI / 2;
            } else {
                shootRadians = shootRadians - MathUtils.PI * 3 / 2;
            }
            jointRadians = Math.round((mBodyManager.getLeftArmBodyRevJoint().getJointAngle()-MathUtils.PI)*10)/10.0f;
            if (shootRadians > -MathUtils.PI / 10 && shootRadians < MathUtils.PI / 2) {
                shootRadians = -MathUtils.PI / 10;
            } else if (shootRadians < -MathUtils.PI * 9 / 10 || shootRadians >= MathUtils.PI / 2) {
                shootRadians = -MathUtils.PI * 9 / 10;
            }
            shootRadians = Math.round(shootRadians * 10) / 10.0f;
        } else {
            jointRadians = 0;
            shootRadians = 0;
        }
//        Gdx.app.log(TAG, "leftArmBodyRevJoint.getJointAngle():" + jointRadians);
//        Gdx.app.log(TAG, "degreeToshootRadius:" + shootRadians);
        if (jointRadians < shootRadians) {
            y = 1;
        } else if (jointRadians > shootRadians) {
            y = -1;
        } else {
            y = 0;
        }
        mBodyManager.getLeftArmBodyRevJoint().setMotorSpeed(y);
//        rightArmBodyRevJoint.setMotorSpeed(x);

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
